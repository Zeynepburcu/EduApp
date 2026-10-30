package prz.rutedu.app.data

import androidx.compose.ui.graphics.Color
import prz.rutedu.app.locale.getCurrentLanguage
import prz.rutedu.app.math.MathShape
import prz.rutedu.app.math.MathViewport
import prz.rutedu.app.math.Pt
import prz.rutedu.app.models.Hint
import prz.rutedu.app.models.Question
import prz.rutedu.app.models.Question.FractionAnswer
import prz.rutedu.app.models.Question.GraphSelectFromList
import prz.rutedu.app.models.Question.GraphTypeAnswer
import prz.rutedu.app.models.Question.SelectFromList
import prz.rutedu.app.models.Question.TypeAnswer
import kotlin.math.abs
import kotlin.random.Random
import kotlin.math.pow

/**
 * Procedural question set for "Matematyka rozszerzona" (Extended Mathematics).
 *
 * Texts are available in Polish, English and German. Polish is used when the app
 * language is Polish, German when it is German, and English for every other language.
 */
object ExtendedMathQuestionGenerator {

    /** Picks the text matching the current app language (pl / de, otherwise en). */
    private fun t(pl: String, en: String, de: String): String = when (getCurrentLanguage()) {
        "pl" -> pl
        "de" -> de
        else -> en
    }

    /** Localized word for "or" used in answer options. */
    private fun or(): String = t("lub", "or", "oder")

    /**
     * Generates a procedural set of questions for a given "Matematyka rozszerzona" lesson.
     *
     * @param lessonId The identifier of the lesson (e.g., "mat_roz_1_1").
     * @param seed The random seed used to shuffle and generate procedural values.
     * @param excludeIds Set of question IDs to exclude (already answered).
     * @return List of generated [Question] objects.
     */
    fun generateFor(
        lessonId: String,
        seed: Long,
        excludeIds: Set<Int> = emptySet()
    ): List<Question> {
        val all = when (lessonId) {
            "mat_roz_1_1" -> functionsWithParameter(seed)
            "mat_roz_1_2" -> graphTransformations(seed)
            "mat_roz_1_3" -> absoluteValueFunctions(seed)
            "mat_roz_2_1" -> polynomialFactorization(seed)
            "mat_roz_2_2" -> polynomialEquations(seed)
            "mat_roz_2_3" -> rationalFunctions(seed)
            "mat_roz_3_1" -> trigonometricIdentities(seed)
            "mat_roz_3_2" -> trigonometricEquations(seed)
            "mat_roz_3_3" -> trigonometricGraphs(seed)
            "mat_roz_4_1" -> lineAndCircle(seed)
            "mat_roz_4_2" -> pointLineDistance(seed)
            "mat_roz_4_3" -> vectors(seed)
            "mat_roz_5_1" -> sequences(seed)
            "mat_roz_5_2" -> sequenceLimits(seed)
            "mat_roz_5_3" -> geometricSeries(seed)
            "mat_roz_6_1" -> combinatorics(seed)
            "mat_roz_6_2" -> bernoulli(seed)
            "mat_roz_6_3" -> conditionalProbability(seed)
            "mat_roz_7_1" -> derivativeRate(seed)
            "mat_roz_7_2" -> extrema(seed)
            "mat_roz_7_3" -> tangent(seed)
            "mat_roz_8_1" -> logProperties(seed)
            "mat_roz_8_2" -> logEquations(seed)
            "mat_roz_8_3" -> logPowers(seed)
            "mat_roz_9_1" -> absEquations(seed)
            "mat_roz_9_2" -> absInequalities(seed)
            "mat_roz_9_3" -> absDistance(seed)
            "mat_roz_10_1" -> cubeDiagonals(seed)
            "mat_roz_10_2" -> prismVolume(seed)
            "mat_roz_10_3" -> pyramidVolume(seed)

            else -> emptyList()
        }
        return if (excludeIds.isEmpty()) all else all.filter { it.id !in excludeIds }
    }

    /**
     * Returns the total number of questions available for a given lesson.
     *
     * @param lessonId The identifier of the lesson.
     * @return Total count of questions.
     */
    fun totalFor(lessonId: String): Int = generateFor(lessonId, seed = 0L).size

    private fun select(
        id: Int,
        prompt: String,
        correct: String,
        wrong: List<String>,
        rng: Random,
        hint: Hint = Hint("")
    ): SelectFromList {
        val options = (listOf(correct) + wrong).distinct().shuffled(rng)
        return SelectFromList(id, prompt, options, setOf(options.indexOf(correct)), hint = hint)
    }

    private fun functionsWithParameter(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()

        repeat(8) { i ->
            val a = rng.nextInt(-4, 5).let { if (it == 0) 2 else it }
            val b = rng.nextInt(-6, 7)
            val x = rng.nextInt(-3, 4)
            val value = a * x + b
            questions += TypeAnswer(
                10100 + i,
                t(
                    "Dla f(x) = ${a}x + ($b) oblicz f($x).",
                    "For f(x) = ${a}x + ($b), calculate f($x).",
                    "Für f(x) = ${a}x + ($b) berechne f($x)."
                ),
                value,
                hint = Hint(
                    t(
                        "Podstaw $x w miejsce x.",
                        "Substitute $x for x.",
                        "Setze $x für x ein."
                    ),
                    steps = listOf("f($x) = $a * $x + ($b)", "f($x) = $value")
                )
            )
        }

        return questions.shuffled(rng)
    }

    private fun graphTransformations(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()

        repeat(6) { i ->
            val p = rng.nextInt(-3, 4)
            val q = rng.nextInt(-3, 4)
            val shapes = listOf(
                MathShape.FunctionPlot(
                    f = { x -> (x - p) * (x - p) + q },
                    color = Color(0xFFE53935)
                ),
                MathShape.PointMark(
                    Pt(p.toDouble(), q.toDouble()),
                    label = "W",
                    color = Color(0xFFE53935)
                )
            )
            questions += GraphSelectFromList(
                10200 + i,
                t(
                    "Wskaż wierzchołek paraboli y = (x - $p)^2 + ($q).",
                    "Select the vertex of the parabola y = (x - $p)^2 + ($q).",
                    "Wähle den Scheitelpunkt der Parabel y = (x - $p)^2 + ($q)."
                ),
                shapes,
                MathViewport(xMin = -6.0, xMax = 6.0, yMin = -4.0, yMax = 10.0),
                listOf("($p, $q)", "(${-p}, $q)", "($p, ${-q})").distinct().shuffled(rng),
                emptySet(),
                hint = Hint(
                    t(
                        "Postać y = (x - p)^2 + q ma wierzchołek w punkcie (p, q).",
                        "The form y = (x - p)^2 + q has its vertex at the point (p, q).",
                        "Die Form y = (x - p)^2 + q hat ihren Scheitelpunkt im Punkt (p, q)."
                    )
                )
            ).let { qn -> qn.copy(correctIndices = setOf(qn.options.indexOf("($p, $q)"))) }
        }

        return questions.shuffled(rng)
    }

    private fun absoluteValueFunctions(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()

        repeat(8) { i ->
            val a = rng.nextInt(-5, 6)
            val b = rng.nextInt(-3, 4)
            val x = rng.nextInt(-6, 7)
            val value = abs(x - a) + b
            questions += TypeAnswer(
                10300 + i,
                t(
                    "Dla f(x) = |x - ($a)| + ($b) oblicz f($x).",
                    "For f(x) = |x - ($a)| + ($b), calculate f($x).",
                    "Für f(x) = |x - ($a)| + ($b) berechne f($x)."
                ),
                value,
                hint = Hint(
                    t(
                        "Najpierw oblicz wartość w module, potem dodaj przesunięcie.",
                        "First calculate the value inside the absolute value, then add the shift.",
                        "Berechne zuerst den Wert im Betrag und addiere dann die Verschiebung."
                    ),
                    steps = listOf("|$x - ($a)| = ${abs(x - a)}", "f($x) = $value")
                )
            )
        }

        return questions.shuffled(rng)
    }

    private fun polynomialFactorization(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()

        repeat(8) { i ->
            val p = rng.nextInt(1, 7)
            val q = rng.nextInt(1, 7)
            val b = p + q
            val c = p * q
            val correct = "(x + $p)(x + $q)"
            questions += select(
                20100 + i,
                t(
                    "Rozłóż na czynniki: x^2 + ${b}x + $c",
                    "Factor: x^2 + ${b}x + $c",
                    "Faktorisiere: x^2 + ${b}x + $c"
                ),
                correct,
                listOf("(x - $p)(x - $q)", "(x + $b)(x + $c)", "(x + $p)(x - $q)"),
                rng,
                Hint(
                    t(
                        "Szukamy dwóch liczb o sumie $b i iloczynie $c.",
                        "Look for two numbers whose sum is $b and whose product is $c.",
                        "Gesucht sind zwei Zahlen mit der Summe $b und dem Produkt $c."
                    )
                )
            )
        }

        return questions.shuffled(rng)
    }

    private fun polynomialEquations(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()

        repeat(8) { i ->
            val r1 = rng.nextInt(-5, 0)
            val r2 = (1..5).filter { it != -r1 }.random(rng)
            val b = -(r1 + r2)
            val c = r1 * r2
            val middle = if (b >= 0) "+ ${b}x" else "- ${-b}x"
            val end = if (c >= 0) "+ $c" else "- ${-c}"
            val correct = "x = $r1 ${or()} x = $r2"
            questions += select(
                20200 + i,
                t(
                    "Rozwiąż równanie: x^2 $middle $end = 0",
                    "Solve the equation: x^2 $middle $end = 0",
                    "Löse die Gleichung: x^2 $middle $end = 0"
                ),
                correct,
                listOf(
                    "x = ${-r1} ${or()} x = ${-r2}",
                    "x = ${r1 + r2}",
                    t("brak rozwiązań rzeczywistych", "no real solutions", "keine reellen Lösungen")
                ),
                rng,
                Hint(
                    t(
                        "Równanie ma postać (x - r1)(x - r2) = 0.",
                        "The equation has the form (x - r1)(x - r2) = 0.",
                        "Die Gleichung hat die Form (x - r1)(x - r2) = 0."
                    )
                )
            )
        }

        return questions.shuffled(rng)
    }

    private fun rationalFunctions(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()

        repeat(6) { i ->
            val a = rng.nextInt(-4, 5).let { if (it == 0) 1 else it }
            val shapes = listOf(
                MathShape.FunctionPlot(
                    f = { x -> 1.0 / (x - a) },
                    color = Color(0xFFE53935)
                )
            )
            questions += GraphSelectFromList(
                20300 + i,
                t(
                    "Dla f(x) = 1/(x - ($a)) wskaż asymptotę pionową.",
                    "For f(x) = 1/(x - ($a)), select the vertical asymptote.",
                    "Wähle für f(x) = 1/(x - ($a)) die senkrechte Asymptote."
                ),
                shapes,
                MathViewport(xMin = -7.0, xMax = 7.0, yMin = -6.0, yMax = 6.0),
                listOf("x = $a", "y = $a", "x = ${-a}", "y = 0").distinct().shuffled(rng),
                emptySet(),
                hint = Hint(
                    t(
                        "Mianownik zeruje się dla x = $a, więc tam jest asymptota pionowa.",
                        "The denominator is zero at x = $a, so the vertical asymptote is there.",
                        "Der Nenner wird bei x = $a null, dort liegt also die senkrechte Asymptote."
                    )
                )
            ).let { qn -> qn.copy(correctIndices = setOf(qn.options.indexOf("x = $a"))) }
        }

        return questions.shuffled(rng)
    }

    private fun trigonometricIdentities(seed: Long): List<Question> {
        val rng = Random(seed)

        return listOf(
            select(
                30100,
                t(
                    "Która tożsamość jest prawdziwa?",
                    "Which identity is true?",
                    "Welche Identität ist richtig?"
                ),
                "sin^2(x) + cos^2(x) = 1",
                listOf("sin(x) + cos(x) = 1", "tg(x) = cos(x)/sin(x)", "sin^2(x) - cos^2(x) = 1"),
                rng
            ),
            select(
                30101,
                t(
                    "Czemu równa się tg(x)?",
                    "What is tan(x) equal to?",
                    "Wem ist tan(x) gleich?"
                ),
                "sin(x)/cos(x)",
                listOf("cos(x)/sin(x)", "sin(x)*cos(x)", "1/sin(x)"),
                rng
            ),
            select(
                30102,
                t(
                    "Jeśli sin(x)=3/5 i x jest ostry, to cos(x) wynosi:",
                    "If sin(x)=3/5 and x is acute, then cos(x) equals:",
                    "Wenn sin(x)=3/5 und x spitz ist, dann ist cos(x):"
                ),
                "4/5",
                listOf("3/4", "5/4", "2/5"),
                rng
            ),
            select(
                30103,
                t(
                    "W trójkącie prostokątnym sin kąta ostrego to:",
                    "In a right triangle, the sine of an acute angle is:",
                    "Im rechtwinkligen Dreieck ist der Sinus eines spitzen Winkels:"
                ),
                t(
                    "przyprostokątna naprzeciw / przeciwprostokątna",
                    "opposite side / hypotenuse",
                    "Gegenkathete / Hypotenuse"
                ),
                listOf(
                    t(
                        "przyprostokątna przy kącie / przeciwprostokątna",
                        "adjacent side / hypotenuse",
                        "Ankathete / Hypotenuse"
                    ),
                    t(
                        "przeciwprostokątna / przyprostokątna",
                        "hypotenuse / leg",
                        "Hypotenuse / Kathete"
                    ),
                    t(
                        "suma przyprostokątnych",
                        "sum of the legs",
                        "Summe der Katheten"
                    )
                ),
                rng
            )
        ).shuffled(rng)
    }

    private fun trigonometricEquations(seed: Long): List<Question> {
        val rng = Random(seed)

        return listOf(
            select(
                30200,
                t(
                    "Rozwiązania równania sin(x)=0 w przedziale <0, 2π> to:",
                    "The solutions of sin(x)=0 in the interval [0, 2π] are:",
                    "Die Lösungen von sin(x)=0 im Intervall [0, 2π] sind:"
                ),
                "0, π, 2π",
                listOf("π/2", "0, π/2", "π/2, 3π/2"),
                rng
            ),
            select(
                30201,
                t(
                    "Rozwiązania równania cos(x)=0 w przedziale <0, 2π> to:",
                    "The solutions of cos(x)=0 in the interval [0, 2π] are:",
                    "Die Lösungen von cos(x)=0 im Intervall [0, 2π] sind:"
                ),
                "π/2, 3π/2",
                listOf("0, π", "π", "0, 2π"),
                rng
            ),
            select(
                30202,
                t(
                    "Rozwiązania równania sin(x)=1 w przedziale <0, 2π> to:",
                    "The solutions of sin(x)=1 in the interval [0, 2π] are:",
                    "Die Lösungen von sin(x)=1 im Intervall [0, 2π] sind:"
                ),
                "π/2",
                listOf("0", "π", "3π/2"),
                rng
            ),
            select(
                30203,
                t(
                    "Rozwiązania równania cos(x)=1 w przedziale <0, 2π> to:",
                    "The solutions of cos(x)=1 in the interval [0, 2π] are:",
                    "Die Lösungen von cos(x)=1 im Intervall [0, 2π] sind:"
                ),
                "0, 2π",
                listOf("π/2", "π", "π/2, 3π/2"),
                rng
            )
        ).shuffled(rng)
    }

    private fun trigonometricGraphs(seed: Long): List<Question> {
        val rng = Random(seed)

        return listOf(
            GraphSelectFromList(
                30300,
                t(
                    "Który wykres jest narysowany?",
                    "Which graph is shown?",
                    "Welcher Graph ist dargestellt?"
                ),
                listOf(MathShape.FunctionPlot({ x -> kotlin.math.sin(x) }, Color(0xFFE53935))),
                MathViewport(xMin = -6.5, xMax = 6.5, yMin = -1.5, yMax = 1.5, gridStep = 1.0),
                listOf("sin(x)", "cos(x)", "tg(x)"),
                setOf(0),
                hint = Hint(
                    t(
                        "Sinus przechodzi przez punkt (0, 0).",
                        "The sine passes through the point (0, 0).",
                        "Der Sinus verläuft durch den Punkt (0, 0)."
                    )
                )
            ),
            GraphSelectFromList(
                30301,
                t(
                    "Który wykres jest narysowany?",
                    "Which graph is shown?",
                    "Welcher Graph ist dargestellt?"
                ),
                listOf(MathShape.FunctionPlot({ x -> kotlin.math.cos(x) }, Color(0xFFE53935))),
                MathViewport(xMin = -6.5, xMax = 6.5, yMin = -1.5, yMax = 1.5, gridStep = 1.0),
                listOf("cos(x)", "sin(x)", "tg(x)"),
                setOf(0),
                hint = Hint(
                    t(
                        "Cosinus dla x=0 ma wartość 1.",
                        "The cosine has the value 1 at x=0.",
                        "Der Kosinus hat bei x=0 den Wert 1."
                    )
                )
            )
        ).shuffled(rng)
    }

    private fun lineAndCircle(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()

        repeat(8) { i ->
            if (rng.nextBoolean()) {
                val a = rng.nextInt(-4, 5).let { if (it == 0) 1 else it }
                val b = rng.nextInt(-5, 6)
                questions += TypeAnswer(
                    40100 + i,
                    t(
                        "Jaki jest współczynnik kierunkowy prostej y = ${a}x + ($b)?",
                        "What is the slope of the line y = ${a}x + ($b)?",
                        "Wie groß ist die Steigung der Geraden y = ${a}x + ($b)?"
                    ),
                    a,
                    hint = Hint(
                        t(
                            "W równaniu y = ax + b współczynnik kierunkowy to a.",
                            "In the equation y = ax + b, the slope is a.",
                            "In der Gleichung y = ax + b ist die Steigung a."
                        )
                    )
                )
            } else {
                val r = rng.nextInt(2, 8)
                questions += TypeAnswer(
                    40100 + i,
                    t(
                        "Okrąg ma równanie (x-a)^2 + (y-b)^2 = ${r * r}. Podaj promień.",
                        "A circle has the equation (x-a)^2 + (y-b)^2 = ${r * r}. Give the radius.",
                        "Ein Kreis hat die Gleichung (x-a)^2 + (y-b)^2 = ${r * r}. Gib den Radius an."
                    ),
                    r,
                    hint = Hint(
                        t(
                            "Prawa strona równania okręgu to r^2.",
                            "The right-hand side of the circle equation is r^2.",
                            "Die rechte Seite der Kreisgleichung ist r^2."
                        )
                    )
                )
            }
        }

        return questions.shuffled(rng)
    }

    private fun pointLineDistance(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()

        repeat(8) { i ->
            val point = rng.nextInt(-6, 7)
            val line = rng.nextInt(-4, 5)
            val vertical = rng.nextBoolean()
            val distance = abs(point - line)
            val prompt = if (vertical) {
                t(
                    "Oblicz odległość punktu P($point, 2) od prostej x = $line.",
                    "Calculate the distance from the point P($point, 2) to the line x = $line.",
                    "Berechne den Abstand des Punktes P($point, 2) von der Geraden x = $line."
                )
            } else {
                t(
                    "Oblicz odległość punktu P(2, $point) od prostej y = $line.",
                    "Calculate the distance from the point P(2, $point) to the line y = $line.",
                    "Berechne den Abstand des Punktes P(2, $point) von der Geraden y = $line."
                )
            }
            questions += TypeAnswer(
                40200 + i,
                prompt,
                distance,
                hint = Hint(
                    t(
                        "Dla prostej pionowej lub poziomej wystarczy różnica odpowiednich współrzędnych.",
                        "For a vertical or horizontal line, the difference of the matching coordinates is enough.",
                        "Bei einer senkrechten oder waagerechten Geraden genügt die Differenz der passenden Koordinaten."
                    )
                )
            )
        }

        return questions.shuffled(rng)
    }

    private fun vectors(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()

        repeat(8) { i ->
            val ax = rng.nextInt(-4, 5)
            val ay = rng.nextInt(-4, 5)
            val bx = rng.nextInt(-4, 5)
            val by = rng.nextInt(-4, 5)
            val askX = rng.nextBoolean()
            val answer = if (askX) ax + bx else ay + by
            val coord = if (askX) t("pierwszą", "first", "erste") else t("drugą", "second", "zweite")
            questions += TypeAnswer(
                40300 + i,
                t(
                    "Dane są wektory u=($ax,$ay), v=($bx,$by). Podaj $coord współrzędną u+v.",
                    "Given the vectors u=($ax,$ay), v=($bx,$by). Give the $coord coordinate of u+v.",
                    "Gegeben sind die Vektoren u=($ax,$ay), v=($bx,$by). Gib die $coord Koordinate von u+v an."
                ),
                answer,
                hint = Hint(
                    t(
                        "Wektory dodajemy współrzędna po współrzędnej.",
                        "Vectors are added coordinate by coordinate.",
                        "Vektoren werden Koordinate für Koordinate addiert."
                    )
                )
            )
        }

        return questions.shuffled(rng)
    }

    private fun sequences(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()

        repeat(8) { i ->
            val a1 = rng.nextInt(1, 8)
            val r = rng.nextInt(2, 6)
            val n = rng.nextInt(4, 9)
            val value = a1 + (n - 1) * r
            questions += TypeAnswer(
                50100 + i,
                t(
                    "Ciąg arytmetyczny: a1=$a1, r=$r. Oblicz a$n.",
                    "Arithmetic sequence: a1=$a1, d=$r. Calculate a$n.",
                    "Arithmetische Folge: a1=$a1, d=$r. Berechne a$n."
                ),
                value,
                hint = Hint(
                    t(
                        "Wzór: an = a1 + (n-1)r.",
                        "Formula: an = a1 + (n-1)d.",
                        "Formel: an = a1 + (n-1)d."
                    )
                )
            )
        }

        return questions.shuffled(rng)
    }

    private fun sequenceLimits(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()

        repeat(8) { i ->
            val c = rng.nextInt(1, 5)
            val limit = rng.nextInt(1, 6)
            val a = c * limit

            val b = rng.nextInt(-5, 6)
            val d = rng.nextInt(-5, 6)

            val signB = if (b >= 0) "+ $b" else "- ${-b}"
            val signD = if (d >= 0) "+ $d" else "- ${-d}"

            questions += TypeAnswer(
                50200 + i,
                t(
                    "Oblicz granicę ciągu dla n dążącego do nieskończoności: a_n = (${a}n $signB) / (${c}n $signD)",
                    "Calculate the limit of the sequence as n approaches infinity: a_n = (${a}n $signB) / (${c}n $signD)",
                    "Berechne den Grenzwert der Folge für n gegen unendlich: a_n = (${a}n $signB) / (${c}n $signD)"
                ),
                limit,
                hint = Hint(
                    t(
                        "Granica ilorazu wielomianów tego samego stopnia to po prostu iloraz liczb stojących przy 'n', czyli $a podzielone przez $c.",
                        "The limit of a quotient of polynomials of the same degree is simply the quotient of the coefficients of 'n', that is $a divided by $c.",
                        "Der Grenzwert eines Quotienten von Polynomen gleichen Grades ist einfach der Quotient der Koeffizienten vor 'n', also $a geteilt durch $c."
                    )
                )
            )
        }

        return questions.shuffled(rng)
    }

    private fun geometricSeries(seed: Long): List<Question> {
        val rng = Random(seed)

        return listOf(
            TypeAnswer(
                50300,
                t(
                    "Suma nieskończonego szeregu geometrycznego: a1=1, q=1/2.",
                    "Sum of the infinite geometric series: a1=1, q=1/2.",
                    "Summe der unendlichen geometrischen Reihe: a1=1, q=1/2."
                ),
                2,
                hint = Hint("S = a1/(1-q) = 1/(1-1/2) = 2.")
            ),
            FractionAnswer(
                50301,
                t(
                    "Suma nieskończonego szeregu geometrycznego: a1=3, q=1/3.",
                    "Sum of the infinite geometric series: a1=3, q=1/3.",
                    "Summe der unendlichen geometrischen Reihe: a1=3, q=1/3."
                ),
                9,
                2,
                hint = Hint("S = 3/(1-1/3) = 3/(2/3) = 9/2.")
            ),
            TypeAnswer(
                50302,
                t(
                    "Suma szeregu: a1=2, q=0.5.",
                    "Sum of the series: a1=2, q=0.5.",
                    "Summe der Reihe: a1=2, q=0.5."
                ),
                4,
                hint = Hint("S = 2/(1-0.5) = 4.")
            ),
            select(
                50303,
                t(
                    "Kiedy istnieje suma nieskończonego szeregu geometrycznego?",
                    "When does the sum of an infinite geometric series exist?",
                    "Wann existiert die Summe einer unendlichen geometrischen Reihe?"
                ),
                "|q| < 1",
                listOf("q > 1", "q = 1", "|q| > 1"),
                rng
            )
        ).shuffled(rng)
    }

    private fun combinatorics(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()

        repeat(4) { i ->
            val n = rng.nextInt(3, 6)
            val silnia = (1..n).reduce { acc, j -> acc * j }
            questions += TypeAnswer(
                60100 + i,
                t(
                    "Na ile sposobów można ustawić w szeregu $n różnych osób?",
                    "In how many ways can $n different people be arranged in a row?",
                    "Auf wie viele Arten kann man $n verschiedene Personen in einer Reihe aufstellen?"
                ),
                silnia,
                hint = Hint(
                    t(
                        "Musisz policzyć silnię, czyli pomnożyć liczby do $n włącznie (1 * 2 * ... * $n).",
                        "Calculate the factorial, i.e. multiply the numbers up to and including $n (1 * 2 * ... * $n).",
                        "Berechne die Fakultät, also das Produkt der Zahlen bis einschließlich $n (1 * 2 * ... * $n)."
                    )
                )
            )
        }

        repeat(4) { i ->
            val k = rng.nextInt(2, 6)
            val s = rng.nextInt(2, 5)
            questions += TypeAnswer(
                60110 + i,
                t(
                    "W szafie masz $k różnych koszul i $s różnych par spodni. Na ile sposobów możesz skompletować ubiór?",
                    "Your wardrobe has $k different shirts and $s different pairs of trousers. In how many ways can you put together an outfit?",
                    "Im Schrank hast du $k verschiedene Hemden und $s verschiedene Hosen. Auf wie viele Arten kannst du ein Outfit zusammenstellen?"
                ),
                k * s,
                hint = Hint(
                    t(
                        "Użyj reguły mnożenia. Wystarczy pomnożyć $k przez $s.",
                        "Use the multiplication rule. Just multiply $k by $s.",
                        "Verwende die Produktregel. Multipliziere einfach $k mit $s."
                    )
                )
            )
        }

        return questions.shuffled(rng)
    }

    private fun bernoulli(seed: Long): List<Question> {
        val rng = Random(seed)

        return listOf(
            FractionAnswer(
                60200,
                t(
                    "Rzucamy monetą 2 razy. Jakie jest prawdopodobieństwo dokładnie jednego orła?",
                    "A coin is tossed 2 times. What is the probability of exactly one head?",
                    "Eine Münze wird 2-mal geworfen. Wie groß ist die Wahrscheinlichkeit für genau einmal Kopf?"
                ),
                1,
                2,
                hint = Hint(
                    t(
                        "Są dwa sprzyjające wyniki: OR, RO z czterech możliwych.",
                        "There are two favourable outcomes: HT, TH out of four possible.",
                        "Es gibt zwei günstige Ergebnisse: KZ, ZK von vier möglichen."
                    )
                )
            ),
            FractionAnswer(
                60201,
                t(
                    "Rzucamy monetą 3 razy. Prawdopodobieństwo dokładnie trzech orłów wynosi:",
                    "A coin is tossed 3 times. The probability of exactly three heads is:",
                    "Eine Münze wird 3-mal geworfen. Die Wahrscheinlichkeit für genau dreimal Kopf ist:"
                ),
                1,
                8,
                hint = Hint(
                    t(
                        "Jedna sekwencja OOO z ośmiu możliwych.",
                        "One sequence HHH out of eight possible.",
                        "Eine Folge KKK von acht möglichen."
                    )
                )
            ),
            FractionAnswer(
                60202,
                t(
                    "Rzucamy kostką 2 razy. Prawdopodobieństwo dwóch szóstek wynosi:",
                    "A die is rolled 2 times. The probability of two sixes is:",
                    "Ein Würfel wird 2-mal geworfen. Die Wahrscheinlichkeit für zwei Sechsen ist:"
                ),
                1,
                36,
                hint = Hint(
                    t(
                        "Każdy rzut ma szansę 1/6, więc 1/6 * 1/6.",
                        "Each roll has a 1/6 chance, so 1/6 * 1/6.",
                        "Jeder Wurf hat die Wahrscheinlichkeit 1/6, also 1/6 * 1/6."
                    )
                )
            ),
            FractionAnswer(
                60203,
                t(
                    "Rzucamy monetą 3 razy. Prawdopodobieństwo dokładnie jednego orła wynosi:",
                    "A coin is tossed 3 times. The probability of exactly one head is:",
                    "Eine Münze wird 3-mal geworfen. Die Wahrscheinlichkeit für genau einmal Kopf ist:"
                ),
                3,
                8,
                hint = Hint(
                    t(
                        "Wybieramy miejsce dla jednego orła: C(3,1)=3.",
                        "Choose the position of the single head: C(3,1)=3.",
                        "Wähle die Position für das eine Mal Kopf: C(3,1)=3."
                    )
                )
            )
        ).shuffled(rng)
    }

    private fun conditionalProbability(seed: Long): List<Question> {
        val rng = Random(seed)

        return listOf(
            FractionAnswer(
                60300,
                t(
                    "W klasie jest 12 dziewczyn, 8 chłopaków. 6 dziewczyn lubi fizykę. Losujemy dziewczynę. Prawdopodobieństwo, że lubi fizykę:",
                    "A class has 12 girls and 8 boys. 6 girls like physics. A girl is chosen at random. The probability that she likes physics:",
                    "In einer Klasse sind 12 Mädchen und 8 Jungen. 6 Mädchen mögen Physik. Ein Mädchen wird zufällig ausgewählt. Die Wahrscheinlichkeit, dass es Physik mag:"
                ),
                1,
                2,
                hint = Hint(
                    t(
                        "Warunek zawęża zbiór do 12 dziewczyn. 6/12 = 1/2.",
                        "The condition narrows the set to the 12 girls. 6/12 = 1/2.",
                        "Die Bedingung schränkt die Menge auf die 12 Mädchen ein. 6/12 = 1/2."
                    )
                )
            ),
            FractionAnswer(
                60301,
                t(
                    "W pudełku są 3 kule czerwone i 2 niebieskie. Losujemy jedną kulę. Prawdopodobieństwo wylosowania czerwonej wynosi:",
                    "A box contains 3 red and 2 blue balls. One ball is drawn. The probability of drawing a red one is:",
                    "In einer Schachtel sind 3 rote und 2 blaue Kugeln. Eine Kugel wird gezogen. Die Wahrscheinlichkeit, eine rote zu ziehen, ist:"
                ),
                3,
                5,
                hint = Hint(
                    t(
                        "Przed losowaniem 3 z 5 kul są czerwone.",
                        "Before the draw, 3 of the 5 balls are red.",
                        "Vor dem Ziehen sind 3 der 5 Kugeln rot."
                    )
                )
            ),
            FractionAnswer(
                60302,
                t(
                    "W grupie 10 osób 4 uczą się rozszerzonej matematyki. Losujemy osobę z tej grupy. Prawdopodobieństwo:",
                    "In a group of 10 people, 4 study advanced mathematics. A person is chosen from the group. The probability that they study it:",
                    "In einer Gruppe von 10 Personen lernen 4 Mathematik auf erhöhtem Niveau. Eine Person wird ausgewählt. Die Wahrscheinlichkeit, dass sie dazugehört:"
                ),
                2,
                5,
                hint = Hint(
                    t(
                        "4/10 skracamy do 2/5.",
                        "4/10 simplifies to 2/5.",
                        "4/10 wird zu 2/5 gekürzt."
                    )
                )
            ),
            FractionAnswer(
                60303,
                t(
                    "Z talii liczb 1-10 wylosowano liczbę parzystą. Jakie jest prawdopodobieństwo, że jest większa od 5?",
                    "An even number was drawn from the numbers 1-10. What is the probability that it is greater than 5?",
                    "Aus den Zahlen 1-10 wurde eine gerade Zahl gezogen. Wie groß ist die Wahrscheinlichkeit, dass sie größer als 5 ist?"
                ),
                3,
                5,
                hint = Hint(
                    t(
                        "Parzyste: 2,4,6,8,10. Większe od 5: 6,8,10, czyli 3/5.",
                        "Even: 2,4,6,8,10. Greater than 5: 6,8,10, so 3/5.",
                        "Gerade: 2,4,6,8,10. Größer als 5: 6,8,10, also 3/5."
                    ),
                    steps = listOf(
                        t(
                            "Tu warunkiem są liczby parzyste.",
                            "Here the condition is the even numbers.",
                            "Die Bedingung sind hier die geraden Zahlen."
                        ),
                        t(
                            "Sprzyjające: 6, 8, 10.",
                            "Favourable: 6, 8, 10.",
                            "Günstig: 6, 8, 10."
                        ),
                        t(
                            "Wynik: 3/5.",
                            "Result: 3/5.",
                            "Ergebnis: 3/5."
                        )
                    )
                )
            )
        ).shuffled(rng)
    }

    private fun derivativeRate(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()

        repeat(8) { i ->
            val power = rng.nextInt(2, 4)
            val a = rng.nextInt(1, 4).let { if (rng.nextBoolean()) it else -it }
            val x = rng.nextInt(-2, 3)

            val value = if (power == 2) {
                (2 * a) * x
            } else {
                (3 * a) * (x * x)
            }

            questions += TypeAnswer(
                70100 + i,
                t(
                    "Dla f(x) = ${a}x^$power oblicz wartość pochodnej f'($x).",
                    "For f(x) = ${a}x^$power, calculate the value of the derivative f'($x).",
                    "Für f(x) = ${a}x^$power berechne den Wert der Ableitung f'($x)."
                ),
                value,
                hint = Hint(
                    t(
                        "Najpierw wyznacz wzór pochodnej z x^$power, a potem podstaw za x liczbę $x.",
                        "First find the derivative of x^$power, then substitute $x for x.",
                        "Bestimme zuerst die Ableitung von x^$power und setze dann $x für x ein."
                    ),
                    steps = listOf(
                        t(
                            "Wzór f'(x) wynosi: ${power * a}x^${power - 1}",
                            "The formula for f'(x) is: ${power * a}x^${power - 1}",
                            "Die Formel für f'(x) lautet: ${power * a}x^${power - 1}"
                        ),
                        t(
                            "Teraz podstawiamy $x: ${power * a} * ($x)^${power - 1} = $value",
                            "Now substitute $x: ${power * a} * ($x)^${power - 1} = $value",
                            "Jetzt $x einsetzen: ${power * a} * ($x)^${power - 1} = $value"
                        )
                    )
                )
            )
        }

        return questions.shuffled(rng)
    }

    private fun extrema(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()

        repeat(8) { i ->
            val p = rng.nextInt(-4, 5)
            val q = rng.nextInt(-6, 7)
            questions += TypeAnswer(
                70200 + i,
                t(
                    "Funkcja f(x) = (x - ($p))^2 + ($q). Podaj argument minimum.",
                    "The function f(x) = (x - ($p))^2 + ($q). Give the x-value of the minimum.",
                    "Die Funktion f(x) = (x - ($p))^2 + ($q). Gib die Stelle des Minimums an."
                ),
                p,
                hint = Hint(
                    t(
                        "Wierzchołek paraboli ma współrzędne ($p, $q), więc minimum jest dla x=$p.",
                        "The vertex of the parabola is at ($p, $q), so the minimum is at x=$p.",
                        "Der Scheitelpunkt der Parabel liegt bei ($p, $q), das Minimum also bei x=$p."
                    )
                )
            )
        }

        return questions.shuffled(rng)
    }

    private fun tangent(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()

        repeat(8) { i ->
            val x = rng.nextInt(-4, 5).let { if (it == 0) 2 else it }
            val slope = 2 * x
            val shapes = listOf(
                MathShape.FunctionPlot({ t -> t * t }, Color(0xFFE53935)),
                MathShape.PointMark(
                    Pt(x.toDouble(), (x * x).toDouble()),
                    label = "P",
                    color = Color(0xFFE53935)
                )
            )
            questions += GraphTypeAnswer(
                70300 + i,
                t(
                    "Dla f(x)=x^2 podaj współczynnik kierunkowy stycznej w punkcie x=$x.",
                    "For f(x)=x^2, give the slope of the tangent at x=$x.",
                    "Gib für f(x)=x^2 die Steigung der Tangente an der Stelle x=$x an."
                ),
                shapes,
                MathViewport(xMin = -6.0, xMax = 6.0, yMin = -2.0, yMax = 18.0),
                slope,
                hint = Hint(
                    t(
                        "Współczynnik stycznej to wartość pochodnej. f'(x)=2x.",
                        "The slope of the tangent is the value of the derivative. f'(x)=2x.",
                        "Die Steigung der Tangente ist der Wert der Ableitung. f'(x)=2x."
                    )
                )
            )
        }

        return questions.shuffled(rng)
    }

    private fun logProperties(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()
        repeat(4) { i ->
            val base = rng.nextInt(2, 4)
            val p1 = rng.nextInt(1, 4)
            val p2 = rng.nextInt(1, 3)
            val arg1 = base.toDouble().pow(p1.toDouble()).toInt()
            val arg2 = base.toDouble().pow(p2.toDouble()).toInt()

            questions += TypeAnswer(
                80100 + i,
                t(
                    "Oblicz wartość wyrażenia: log_$base($arg1) + log_$base($arg2)",
                    "Calculate the value of the expression: log_$base($arg1) + log_$base($arg2)",
                    "Berechne den Wert des Ausdrucks: log_$base($arg1) + log_$base($arg2)"
                ),
                p1 + p2,
                hint = Hint(
                    t(
                        "Wzór na dodawanie logarytmów: log_a(x) + log_a(y) = log_a(x * y). Skoro podstawa to $base, oblicz log_$base(${arg1 * arg2}).",
                        "Rule for adding logarithms: log_a(x) + log_a(y) = log_a(x * y). Since the base is $base, calculate log_$base(${arg1 * arg2}).",
                        "Regel für die Addition von Logarithmen: log_a(x) + log_a(y) = log_a(x * y). Da die Basis $base ist, berechne log_$base(${arg1 * arg2})."
                    )
                )
            )
        }
        return questions.shuffled(rng)
    }

    private fun logEquations(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()
        repeat(4) { i ->
            val base = rng.nextInt(2, 4)
            val wynik = rng.nextInt(1, 4)
            val wartosc = base.toDouble().pow(wynik.toDouble()).toInt()
            val a = rng.nextInt(1, 5)

            questions += TypeAnswer(
                80200 + i,
                t(
                    "Rozwiąż równanie: log_$base(x - $a) = $wynik. Podaj samo x.",
                    "Solve the equation: log_$base(x - $a) = $wynik. Give only x.",
                    "Löse die Gleichung: log_$base(x - $a) = $wynik. Gib nur x an."
                ),
                wartosc + a,
                hint = Hint(
                    t(
                        "Z definicji logarytmu: podstawa do potęgi wyniku daje środek. Więc x - $a = $base^$wynik.",
                        "By the definition of the logarithm: the base raised to the result gives the argument. So x - $a = $base^$wynik.",
                        "Nach der Definition des Logarithmus: Die Basis hoch das Ergebnis ergibt das Argument. Also x - $a = $base^$wynik."
                    )
                )
            )
        }
        return questions.shuffled(rng)
    }

    private fun logPowers(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()
        repeat(4) { i ->
            val base = rng.nextInt(2, 4)
            val potega = rng.nextInt(2, 6)
            questions += TypeAnswer(
                80300 + i,
                t(
                    "Oblicz: log_$base($base^$potega)",
                    "Calculate: log_$base($base^$potega)",
                    "Berechne: log_$base($base^$potega)"
                ),
                potega,
                hint = Hint(
                    t(
                        "Wyrzuć potęgę ($potega) przed logarytm. Zostanie $potega * log_$base($base). A logarytm o tej samej podstawie i argumencie to zawsze 1.",
                        "Move the exponent ($potega) in front of the logarithm. You get $potega * log_$base($base). A logarithm with the same base and argument is always 1.",
                        "Ziehe den Exponenten ($potega) vor den Logarithmus. Es bleibt $potega * log_$base($base). Ein Logarithmus mit gleicher Basis und gleichem Argument ist immer 1."
                    )
                )
            )
        }
        return questions.shuffled(rng)
    }

    private fun absEquations(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()
        repeat(4) { i ->
            val a = rng.nextInt(-3, 4)
            val b = rng.nextInt(1, 6)
            val znakA = if (a >= 0) "- $a" else "+ ${-a}"
            val prawidlowa = "x = ${a + b} ${or()} x = ${a - b}"
            val bledne = listOf(
                "x = ${a + b}",
                "x = ${-a - b} ${or()} x = ${-a + b}",
                t("brak rozwiązań", "no solutions", "keine Lösungen")
            )

            questions += select(
                90100 + i,
                t(
                    "Rozwiąż równanie: |x $znakA| = $b",
                    "Solve the equation: |x $znakA| = $b",
                    "Löse die Gleichung: |x $znakA| = $b"
                ),
                prawidlowa,
                bledne,
                rng,
                Hint(
                    t(
                        "Z modułu wyciągasz dwa przypadki: środek = $b ORAZ środek = -$b.",
                        "The absolute value gives two cases: inside = $b AND inside = -$b.",
                        "Der Betrag ergibt zwei Fälle: Inneres = $b UND Inneres = -$b."
                    )
                )
            )
        }
        return questions.shuffled(rng)
    }

    private fun absInequalities(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()
        repeat(4) { i ->
            val a = rng.nextInt(-2, 3)
            val b = rng.nextInt(2, 5)
            val znakA = if (a > 0) "- $a" else "+ ${-a}"
            val min = a - b
            val max = a + b
            val belongs = t("x należy do", "x is in", "x liegt in")
            val prawidlowa = "$belongs ($min, $max)"

            questions += select(
                90200 + i,
                t(
                    "Rozwiąż nierówność: |x $znakA| < $b",
                    "Solve the inequality: |x $znakA| < $b",
                    "Löse die Ungleichung: |x $znakA| < $b"
                ),
                prawidlowa,
                listOf(
                    "$belongs (-$b, $b)",
                    "x < $min ${or()} x > $max",
                    "$belongs [$min, $max]"
                ),
                rng,
                Hint(
                    t(
                        "Odległość liczby x od punktu $a jest mniejsza niż $b. Szukasz wszystkiego pomiędzy $min a $max.",
                        "The distance of x from the point $a is less than $b. You are looking for everything between $min and $max.",
                        "Der Abstand von x zum Punkt $a ist kleiner als $b. Gesucht ist alles zwischen $min und $max."
                    )
                )
            )
        }
        return questions.shuffled(rng)
    }

    private fun absDistance(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()
        repeat(4) { i ->
            val x1 = rng.nextInt(-5, 0)
            val x2 = rng.nextInt(1, 6)
            val dist = abs(x1 - x2)

            questions += TypeAnswer(
                90300 + i,
                t(
                    "Jaka jest odległość między punktami $x1 oraz $x2 na osi liczbowej?",
                    "What is the distance between the points $x1 and $x2 on the number line?",
                    "Wie groß ist der Abstand zwischen den Punkten $x1 und $x2 auf dem Zahlenstrahl?"
                ),
                dist,
                hint = Hint(
                    t(
                        "Oblicz różnicę tych liczb i weź wartość bezwzględną: |$x1 - $x2| = $dist.",
                        "Calculate the difference of the numbers and take the absolute value: |$x1 - $x2| = $dist.",
                        "Berechne die Differenz der Zahlen und nimm den Betrag: |$x1 - $x2| = $dist."
                    )
                )
            )
        }
        return questions.shuffled(rng)
    }


    private fun cubeDiagonals(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()
        repeat(4) { i ->
            val a = rng.nextInt(2, 6)
            val zapytajO_Szerokosc = rng.nextBoolean()
            val pytanie = if (zapytajO_Szerokosc) {
                t("przekątnej ściany bocznej", "the diagonal of a face", "die Flächendiagonale")
            } else {
                t("przekątnej sześcianu", "the space diagonal of the cube", "die Raumdiagonale des Würfels")
            }
            val odpowiedz = if (zapytajO_Szerokosc) "$a√2" else "$a√3"
            val zla1 = if (zapytajO_Szerokosc) "$a√3" else "$a√2"

            questions += select(
                100100 + i,
                t(
                    "Krawędź sześcianu wynosi $a. Jaka jest długość $pytanie?",
                    "The edge of a cube is $a. What is the length of $pytanie?",
                    "Die Kante eines Würfels ist $a. Wie lang ist $pytanie?"
                ),
                odpowiedz,
                listOf(zla1, "${a*2}√2", "${a*a}"),
                rng,
                Hint(
                    t(
                        "Przekątna ściany (kwadratu) to a√2, a przekątna całego sześcianu to a√3.",
                        "The diagonal of a face (a square) is a√2, and the space diagonal of the cube is a√3.",
                        "Die Flächendiagonale (eines Quadrats) ist a√2, die Raumdiagonale des Würfels ist a√3."
                    )
                )
            )
        }
        return questions.shuffled(rng)
    }

    private fun prismVolume(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()
        repeat(4) { i ->
            val a = rng.nextInt(2, 5)
            val h = rng.nextInt(3, 7)
            val objetosc = (a * a) * h

            questions += TypeAnswer(
                100200 + i,
                t(
                    "Oblicz objętość graniastosłupa o podstawie kwadratu. Bok podstawy a=$a, wysokość bryły H=$h.",
                    "Calculate the volume of a prism with a square base. Base side a=$a, height H=$h.",
                    "Berechne das Volumen eines Prismas mit quadratischer Grundfläche. Grundseite a=$a, Höhe H=$h."
                ),
                objetosc,
                hint = Hint(
                    t(
                        "Wzór na objętość to Pole podstawy razy Wysokość (V = Pp * H). Podstawa to kwadrat, więc Pp = $a * $a.",
                        "The volume is base area times height (V = B * H). The base is a square, so B = $a * $a.",
                        "Das Volumen ist Grundfläche mal Höhe (V = G * H). Die Grundfläche ist ein Quadrat, also G = $a * $a."
                    )
                )
            )
        }
        return questions.shuffled(rng)
    }

    private fun pyramidVolume(seed: Long): List<Question> {
        val rng = Random(seed)
        val questions = mutableListOf<Question>()
        repeat(4) { i ->
            val a = rng.nextInt(2, 5)
            val h = rng.nextInt(1, 4) * 3
            val objetosc = (a * a * h) / 3

            questions += TypeAnswer(
                100300 + i,
                t(
                    "Oblicz objętość ostrosłupa prawidłowego czworokątnego (podstawa to kwadrat). Krawędź podstawy a=$a, wysokość bryły H=$h.",
                    "Calculate the volume of a regular square pyramid (the base is a square). Base edge a=$a, height H=$h.",
                    "Berechne das Volumen einer regelmäßigen quadratischen Pyramide (die Grundfläche ist ein Quadrat). Grundkante a=$a, Höhe H=$h."
                ),
                objetosc,
                hint = Hint(
                    t(
                        "Wzór dla ostrosłupa to 1/3 * Pp * H. Podstawa to kwadrat, czyli Pp = $a*$a=${a*a}. Wylicz: 1/3 * ${a*a} * $h.",
                        "The formula for a pyramid is 1/3 * B * H. The base is a square, so B = $a*$a=${a*a}. Calculate: 1/3 * ${a*a} * $h.",
                        "Die Formel für eine Pyramide ist 1/3 * G * H. Die Grundfläche ist ein Quadrat, also G = $a*$a=${a*a}. Berechne: 1/3 * ${a*a} * $h."
                    )
                )
            )
        }
        return questions.shuffled(rng)
    }
}
