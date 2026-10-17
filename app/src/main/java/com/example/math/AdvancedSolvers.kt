package com.example.math

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

object AdvancedSolvers {

    private val symbols = DecimalFormatSymbols(Locale.US)
    private val df = DecimalFormat("#,##0.######", symbols)

    fun format(v: Double): String {
        if (v.isNaN()) return "NaN"
        if (v.isInfinite()) return if (v > 0) "∞" else "-∞"
        if (abs(v) < 1.0E-12 && v != 0.0) return "0"
        if (v % 1.0 == 0.0 && abs(v) < 1.0E14) return String.format(Locale.US, "%.0f", v)
        return df.format(v)
    }

    // --- 1. Quadratic Equation Solver: ax² + bx + c = 0 ---
    data class QuadraticSolution(
        val a: Double,
        val b: Double,
        val c: Double,
        val discriminant: Double,
        val discriminantDesc: String,
        val vertexX: Double,
        val vertexY: Double,
        val root1Str: String,
        val root2Str: String,
        val isComplex: Boolean,
        val factoredForm: String,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    fun solveQuadratic(a: Double, b: Double, c: Double): QuadraticSolution {
        if (a == 0.0) {
            // Linear equation: bx + c = 0 => x = -c / b
            val x = if (b != 0.0) -c / b else Double.NaN
            val xStr = format(x)
            val steps = listOf(
                EvaluationStep(
                    stepNumber = 1,
                    title = "Linear Degeneracy (a = 0)",
                    subExpression = "${format(b)}x + ${format(c)} = 0",
                    ruleApplied = "Linear Equation Solving",
                    intermediateResult = "x = $xStr",
                    expressionAfter = "x = $xStr",
                    note = "Since the quadratic coefficient a = 0, the equation reduces to linear: bx + c = 0. Solving yields x = -c/b = -(${format(c)}) / ${format(b)} = $xStr."
                )
            )
            return QuadraticSolution(
                a = a, b = b, c = c,
                discriminant = 0.0,
                discriminantDesc = "Linear equation (a = 0)",
                vertexX = x, vertexY = 0.0,
                root1Str = xStr, root2Str = "N/A",
                isComplex = false,
                factoredForm = "${format(b)}(x - ($xStr))",
                steps = steps,
                notes = listOf(
                    MathNote(
                        title = "Linear Equation",
                        content = "An equation of degree 1 with a single solution x = -c/b.",
                        formula = "bx + c = 0"
                    )
                )
            )
        }

        val delta = b * b - 4 * a * c
        val deltaStr = format(delta)
        val vx = -b / (2 * a)
        val vy = c - (b * b) / (4 * a)

        val steps = mutableListOf<EvaluationStep>()
        var stepNum = 1

        steps.add(
            EvaluationStep(
                stepNumber = stepNum++,
                title = "Identify Coefficients",
                subExpression = "${format(a)}x² + (${format(b)})x + (${format(c)}) = 0",
                ruleApplied = "Standard Form: ax² + bx + c = 0",
                intermediateResult = "a = ${format(a)}, b = ${format(b)}, c = ${format(c)}",
                expressionAfter = "Standard coefficients extracted",
                note = "Confirm the quadratic equation is arranged in standard form ax² + bx + c = 0."
            )
        )

        steps.add(
            EvaluationStep(
                stepNumber = stepNum++,
                title = "Calculate Discriminant (Δ)",
                subExpression = "Δ = b² - 4ac",
                ruleApplied = "Discriminant Formula",
                intermediateResult = "Δ = (${format(b)})² - 4(${format(a)})(${format(c)}) = $deltaStr",
                expressionAfter = "Δ = $deltaStr",
                note = when {
                    delta > 0 -> "Since Δ = $deltaStr > 0, there are 2 distinct real roots."
                    delta == 0.0 -> "Since Δ = 0, there is exactly 1 repeated real root (vertex touches the x-axis)."
                    else -> "Since Δ = $deltaStr < 0, there are 2 complex conjugate roots with non-zero imaginary parts."
                }
            )
        )

        val root1Str: String
        val root2Str: String
        val isComplex = delta < 0
        val factored: String

        if (delta >= 0) {
            val sqrtDelta = sqrt(delta)
            val r1 = (-b + sqrtDelta) / (2 * a)
            val r2 = (-b - sqrtDelta) / (2 * a)
            root1Str = format(r1)
            root2Str = format(r2)

            steps.add(
                EvaluationStep(
                    stepNumber = stepNum++,
                    title = "Apply Quadratic Formula for Roots",
                    subExpression = "x = (-b ± √Δ) / (2a)",
                    ruleApplied = "Quadratic Formula",
                    intermediateResult = "x₁ = (${format(-b)} + ${format(sqrtDelta)}) / ${format(2 * a)} = $root1Str, x₂ = (${format(-b)} - ${format(sqrtDelta)}) / ${format(2 * a)} = $root2Str",
                    expressionAfter = "x₁ = $root1Str, x₂ = $root2Str",
                    note = "Substitute -b = ${format(-b)}, √Δ = ${format(sqrtDelta)}, and 2a = ${format(2 * a)} into the quadratic formula."
                )
            )

            factored = if (r1 == r2) {
                "${format(a)}(x - $root1Str)²"
            } else {
                "${format(a)}(x - ($root1Str))(x - ($root2Str))"
            }
        } else {
            val realPart = -b / (2 * a)
            val imagPart = sqrt(-delta) / (2 * abs(a))
            val realStr = format(realPart)
            val imagStr = format(imagPart)
            root1Str = "$realStr + ${imagStr}i"
            root2Str = "$realStr - ${imagStr}i"

            steps.add(
                EvaluationStep(
                    stepNumber = stepNum++,
                    title = "Solve for Complex Roots",
                    subExpression = "x = (-b ± i√|Δ|) / (2a)",
                    ruleApplied = "Complex Roots Definition: i = √(-1)",
                    intermediateResult = "x₁ = $root1Str, x₂ = $root2Str",
                    expressionAfter = "x = $realStr ± ${imagStr}i",
                    note = "Because Δ < 0, √Δ = i√(${format(-delta)}) = ${imagStr}i. The roots form a complex conjugate pair."
                )
            )

            factored = "${format(a)}(x - ($root1Str))(x - ($root2Str))"
        }

        steps.add(
            EvaluationStep(
                stepNumber = stepNum++,
                title = "Parabola Vertex Coordinates",
                subExpression = "V(h, k) = (-b / 2a, -Δ / 4a)",
                ruleApplied = "Vertex Form of Parabola",
                intermediateResult = "V(${format(vx)}, ${format(vy)})",
                expressionAfter = "Vertex = (${format(vx)}, ${format(vy)})",
                note = "The parabola's turning point (minimum if a > 0, maximum if a < 0) is at (${format(vx)}, ${format(vy)})."
            )
        )

        val notes = listOf(
            MathNote(
                title = "Quadratic Formula",
                content = "For any second-degree polynomial ax² + bx + c = 0 with a ≠ 0, roots are given by x = (-b ± √(b² - 4ac)) / (2a).",
                formula = "x = (-b ± √Δ) / 2a"
            ),
            MathNote(
                title = "Nature of the Discriminant",
                content = "Δ > 0 implies two distinct real roots (two x-intercepts). Δ = 0 implies a single repeated root (tangent to x-axis). Δ < 0 implies complex conjugate roots (no real x-intercepts).",
                formula = "Δ = b² - 4ac"
            ),
            MathNote(
                title = "Vieta's Formulas",
                content = "The sum of the roots is x₁ + x₂ = -b/a, and the product of the roots is x₁ × x₂ = c/a.",
                formula = "Sum = -b/a, Product = c/a"
            )
        )

        return QuadraticSolution(
            a = a, b = b, c = c,
            discriminant = delta,
            discriminantDesc = if (delta > 0) "Two distinct real roots" else if (delta == 0.0) "One repeated real root" else "Two complex conjugate roots",
            vertexX = vx, vertexY = vy,
            root1Str = root1Str, root2Str = root2Str,
            isComplex = isComplex,
            factoredForm = factored,
            steps = steps,
            notes = notes
        )
    }

    // --- 2. System of 2 Linear Equations: a1*x + b1*y = c1 and a2*x + b2*y = c2 ---
    data class LinearSystemSolution(
        val a1: Double, val b1: Double, val c1: Double,
        val a2: Double, val b2: Double, val c2: Double,
        val detD: Double,
        val detDx: Double,
        val detDy: Double,
        val xResult: String,
        val yResult: String,
        val status: String,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    fun solveLinearSystem(
        a1: Double, b1: Double, c1: Double,
        a2: Double, b2: Double, c2: Double
    ): LinearSystemSolution {
        val D = a1 * b2 - a2 * b1
        val Dx = c1 * b2 - c2 * b1
        val Dy = a1 * c2 - a2 * c1

        val steps = mutableListOf<EvaluationStep>()
        var stepNum = 1

        steps.add(
            EvaluationStep(
                stepNumber = stepNum++,
                title = "System Setup in Matrix Form",
                subExpression = "[${format(a1)} ${format(b1)} | ${format(c1)}], [${format(a2)} ${format(b2)} | ${format(c2)}]",
                ruleApplied = "Cramer's Rule Setup",
                intermediateResult = "D = a₁b₂ - a₂b₁",
                expressionAfter = "System ready",
                note = "Set up coefficient matrix and column vectors for Cramer's rule."
            )
        )

        steps.add(
            EvaluationStep(
                stepNumber = stepNum++,
                title = "Calculate Main Determinant (D)",
                subExpression = "D = |${format(a1)} ${format(b1)}| / |${format(a2)} ${format(b2)}|",
                ruleApplied = "2x2 Determinant Formula",
                intermediateResult = "D = (${format(a1)})(${format(b2)}) - (${format(a2)})(${format(b1)}) = ${format(D)}",
                expressionAfter = "D = ${format(D)}",
                note = "If D ≠ 0, the system has a unique solution. If D = 0, the lines are either parallel (no solution) or coincident (infinitely many solutions)."
            )
        )

        steps.add(
            EvaluationStep(
                stepNumber = stepNum++,
                title = "Calculate Dx and Dy Determinants",
                subExpression = "Dx = |c₁ b₁| / |c₂ b₂|, Dy = |a₁ c₁| / |a₂ c₂|",
                ruleApplied = "Cramer's Sub-determinants",
                intermediateResult = "Dx = (${format(c1)})(${format(b2)}) - (${format(c2)})(${format(b1)}) = ${format(Dx)}, Dy = (${format(a1)})(${format(c2)}) - (${format(a2)})(${format(c1)}) = ${format(Dy)}",
                expressionAfter = "Dx = ${format(Dx)}, Dy = ${format(Dy)}",
                note = "Replace the respective columns with the constants column (c₁, c₂)."
            )
        )

        val status: String
        val xRes: String
        val yRes: String

        if (abs(D) > 1.0E-12) {
            val x = Dx / D
            val y = Dy / D
            xRes = format(x)
            yRes = format(y)
            status = "Unique Solution"

            steps.add(
                EvaluationStep(
                    stepNumber = stepNum++,
                    title = "Solve for x and y",
                    subExpression = "x = Dx / D, y = Dy / D",
                    ruleApplied = "Cramer's Rule Quotient",
                    intermediateResult = "x = ${format(Dx)} / ${format(D)} = $xRes, y = ${format(Dy)} / ${format(D)} = $yRes",
                    expressionAfter = "x = $xRes, y = $yRes",
                    note = "The point of intersection between the two lines is ($xRes, $yRes)."
                )
            )
        } else {
            if (abs(Dx) < 1.0E-12 && abs(Dy) < 1.0E-12) {
                status = "Infinitely Many Solutions (Coincident Lines)"
                xRes = "Dependent"
                yRes = "Dependent"
                steps.add(
                    EvaluationStep(
                        stepNumber = stepNum++,
                        title = "Infinite Solutions",
                        subExpression = "D = 0, Dx = 0, Dy = 0",
                        ruleApplied = "Linear Dependency",
                        intermediateResult = "Infinite Solutions",
                        expressionAfter = "Coincident lines",
                        note = "Both equations represent the exact same line in 2D space."
                    )
                )
            } else {
                status = "No Solution (Parallel Lines)"
                xRes = "Undefined"
                yRes = "Undefined"
                steps.add(
                    EvaluationStep(
                        stepNumber = stepNum++,
                        title = "Inconsistent System",
                        subExpression = "D = 0, but Dx ≠ 0 or Dy ≠ 0",
                        ruleApplied = "Parallel Non-intersecting Lines",
                        intermediateResult = "No Solution",
                        expressionAfter = "Parallel lines",
                        note = "The two lines have identical slopes but different y-intercepts, so they never intersect."
                    )
                )
            }
        }

        val notes = listOf(
            MathNote(
                title = "Cramer's Rule for 2x2 Systems",
                content = "An efficient method for solving linear systems using determinants: x = det(Dx)/det(D) and y = det(Dy)/det(D).",
                formula = "x = Dx / D, y = Dy / D"
            ),
            MathNote(
                title = "Geometric Interpretation",
                content = "Two linear equations in two variables represent two straight lines on the Cartesian plane. Their solution is their point of intersection.",
                formula = "y = mx + b"
            )
        )

        return LinearSystemSolution(
            a1 = a1, b1 = b1, c1 = c1,
            a2 = a2, b2 = b2, c2 = c2,
            detD = D, detDx = Dx, detDy = Dy,
            xResult = xRes, yResult = yRes,
            status = status,
            steps = steps,
            notes = notes
        )
    }

    // --- 3. Summation Engine: Σ_{x=a}^b f(x) ---
    data class SummationResult(
        val expression: String,
        val startValStr: String,
        val endValStr: String,
        val isInfinite: Boolean,
        val totalSum: Double,
        val formattedSum: String,
        val termsList: List<String>,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    fun evaluateSummation(
        formula: String,
        startStr: String,
        endStr: String,
        angleMode: AngleMode = AngleMode.DEG
    ): SummationResult {
        return MathKnowledge.analyzeAndSolveSummation(formula, startStr, endStr, angleMode)
    }

    // --- 4. Calculus: Numerical Derivative & Definite Integral ---
    data class CalculusResult(
        val function: String,
        val operation: String,
        val resultValue: Double,
        val formattedResult: String,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    fun numericalDerivative(
        formula: String,
        x0: Double,
        angleMode: AngleMode = AngleMode.DEG
    ): CalculusResult {
        val h = 1.0E-5
        val xPlus = x0 + h
        val xMinus = x0 - h

        val fPlus = evaluateFormulaAt(formula, xPlus, angleMode)
        val fMinus = evaluateFormulaAt(formula, xMinus, angleMode)
        val deriv = (fPlus - fMinus) / (2 * h)
        val resStr = format(deriv)

        val steps = listOf(
            EvaluationStep(
                stepNumber = 1,
                title = "Select Point of Differentiation",
                subExpression = "d/dx [$formula] at x = ${format(x0)}",
                ruleApplied = "Derivative Definition: f'(x) = lim_{h→0} [f(x+h) - f(x-h)] / (2h)",
                intermediateResult = "x₀ = ${format(x0)}, step h = $h",
                expressionAfter = "Central difference quotient setup",
                note = "We apply the highly accurate central difference numerical derivative with step size h = 10⁻⁵."
            ),
            EvaluationStep(
                stepNumber = 2,
                title = "Evaluate Function at Perturbed Points",
                subExpression = "f(x₀ + h) and f(x₀ - h)",
                ruleApplied = "Function Evaluation",
                intermediateResult = "f(${format(xPlus)}) = ${format(fPlus)}, f(${format(xMinus)}) = ${format(fMinus)}",
                expressionAfter = "f(x+h) - f(x-h) = ${format(fPlus - fMinus)}",
                note = "Calculate function values just above and below x₀."
            ),
            EvaluationStep(
                stepNumber = 3,
                title = "Compute Central Difference Derivative",
                subExpression = "f'(x₀) ≈ (${format(fPlus)} - ${format(fMinus)}) / ${format(2 * h)}",
                ruleApplied = "Derivative Value",
                intermediateResult = "f'(${format(x0)}) = $resStr",
                expressionAfter = "Slope = $resStr",
                note = "The instantaneous rate of change (tangent line slope) at x = ${format(x0)} is approximately $resStr."
            )
        )

        val notes = listOf(
            MathNote(
                title = "Definition of Derivative",
                content = "The derivative represents the slope of the tangent line to the function at a given point, or the instantaneous rate of change.",
                formula = "f'(x) = lim_{Δx→0} Δy / Δx"
            ),
            MathNote(
                title = "Key Differentiation Rules",
                content = "Power Rule: d/dx[xⁿ] = n·xⁿ⁻¹. Exponential: d/dx[eˣ] = eˣ. Trigonometric: d/dx[sin(x)] = cos(x), d/dx[cos(x)] = -sin(x).",
                formula = "d/dx[xⁿ] = n·xⁿ⁻¹"
            )
        )

        return CalculusResult(
            function = formula,
            operation = "Derivative d/dx at x = ${format(x0)}",
            resultValue = deriv,
            formattedResult = resStr,
            steps = steps,
            notes = notes
        )
    }

    fun numericalIntegral(
        formula: String,
        a: Double,
        b: Double,
        angleMode: AngleMode = AngleMode.DEG
    ): CalculusResult {
        // Simpson's 1/3 Rule with n = 200 intervals
        val n = 200
        val h = (b - a) / n
        var sum = evaluateFormulaAt(formula, a, angleMode) + evaluateFormulaAt(formula, b, angleMode)

        for (i in 1 until n) {
            val x = a + i * h
            val fx = evaluateFormulaAt(formula, x, angleMode)
            sum += if (i % 2 == 0) 2 * fx else 4 * fx
        }
        val integralVal = (h / 3.0) * sum
        val resStr = format(integralVal)

        val steps = listOf(
            EvaluationStep(
                stepNumber = 1,
                title = "Definite Integral Limits",
                subExpression = "∫_{${format(a)}}^{${format(b)}} ($formula) dx",
                ruleApplied = "Fundamental Theorem of Calculus: ∫_a^b f(x)dx = F(b) - F(a)",
                intermediateResult = "Lower bound a = ${format(a)}, Upper bound b = ${format(b)}",
                expressionAfter = "Interval [${format(a)}, ${format(b)}]",
                note = "Computes the signed area under the curve y = $formula from x = ${format(a)} to x = ${format(b)}."
            ),
            EvaluationStep(
                stepNumber = 2,
                title = "Numerical Integration via Simpson's 1/3 Rule",
                subExpression = "∫ f(x)dx ≈ (h/3) [f(x₀) + 4f(x₁) + 2f(x₂) + ... + f(xₙ)]",
                ruleApplied = "Simpson's Parabolic Approximation (n = 200)",
                intermediateResult = "Step width h = (b - a)/n = ${format(h)}",
                expressionAfter = "Summing parabolic segments",
                note = "Quadratic parabolic interpolation yields high precision for smooth functions."
            ),
            EvaluationStep(
                stepNumber = 3,
                title = "Compute Total Definite Integral Value",
                subExpression = "Result",
                ruleApplied = "Definite Integral Value",
                intermediateResult = "Area = $resStr",
                expressionAfter = "∫ = $resStr",
                note = "The total net accumulated area under the curve is $resStr."
            )
        )

        val notes = listOf(
            MathNote(
                title = "Definite Integral & Area",
                content = "The definite integral gives the net signed area bounded between the function's curve and the x-axis from x = a to x = b.",
                formula = "Area = ∫_a^b f(x) dx"
            ),
            MathNote(
                title = "Fundamental Theorem of Calculus",
                content = "If F'(x) = f(x), then ∫_a^b f(x) dx = F(b) - F(a). Integration and differentiation are inverse operations.",
                formula = "∫_a^b f(x) dx = F(b) - F(a)"
            )
        )

        return CalculusResult(
            function = formula,
            operation = "Definite Integral from ${format(a)} to ${format(b)}",
            resultValue = integralVal,
            formattedResult = resStr,
            steps = steps,
            notes = notes
        )
    }

    private fun evaluateFormulaAt(formula: String, xVal: Double, angleMode: AngleMode): Double {
        val substituted = formula.replace("x", "($xVal)")
        return try {
            MathEngine.evaluateSimpleExpression(MathEngine.normalizeExpression(substituted), angleMode)
        } catch (_: Exception) {
            0.0
        }
    }

    // --- 5. Combinatorics: nPr and nCr ---
    fun combinations(n: Long, r: Long): Double {
        if (r < 0 || r > n) return 0.0
        if (r == 0L || r == n) return 1.0
        val k = min(r, n - r)
        var c = 1.0
        for (i in 1..k) {
            c = c * (n - (k - i)) / i
        }
        return c
    }

    fun permutations(n: Long, r: Long): Double {
        if (r < 0 || r > n) return 0.0
        var p = 1.0
        for (i in 0 until r) {
            p *= (n - i)
        }
        return p
    }

    // --- 6. Number Theory: GCD, LCM, Prime Factors ---
    fun gcd(a: Long, b: Long): Pair<Long, List<String>> {
        var x = abs(a)
        var y = abs(b)
        val steps = mutableListOf<String>()
        steps.add("Start Euclidean Algorithm for gcd($a, $b)")
        while (y != 0L) {
            val r = x % y
            steps.add("$x = $y × (${x / y}) + $r")
            x = y
            y = r
        }
        steps.add("Last non-zero remainder = $x. Thus gcd($a, $b) = $x.")
        return Pair(x, steps)
    }

    fun lcm(a: Long, b: Long): Long {
        if (a == 0L || b == 0L) return 0L
        val g = gcd(a, b).first
        return abs(a * b) / g
    }

    fun primeFactors(number: Long): List<Long> {
        val factors = mutableListOf<Long>()
        var n = abs(number)
        var d = 2L
        while (d * d <= n) {
            while (n % d == 0L) {
                factors.add(d)
                n /= d
            }
            d++
        }
        if (n > 1) factors.add(n)
        return factors
    }

    // --- 7. Statistics Engine ---
    data class StatisticsSummary(
        val count: Int,
        val sum: Double,
        val mean: Double,
        val median: Double,
        val mode: String,
        val minVal: Double,
        val maxVal: Double,
        val range: Double,
        val variance: Double,
        val standardDeviation: Double,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    fun calculateStatistics(numbers: List<Double>): StatisticsSummary? {
        if (numbers.isEmpty()) return null
        val sorted = numbers.sorted()
        val n = numbers.size
        val sum = numbers.sum()
        val mean = sum / n

        val median = if (n % 2 == 1) {
            sorted[n / 2]
        } else {
            (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0
        }

        val frequencies = numbers.groupingBy { it }.eachCount()
        val maxFreq = frequencies.values.maxOrNull() ?: 1
        val modes = frequencies.filter { it.value == maxFreq && maxFreq > 1 }.keys
        val modeStr = if (modes.isEmpty()) "No Mode (all unique)" else modes.joinToString(", ") { format(it) }

        val minVal = sorted.first()
        val maxVal = sorted.last()
        val range = maxVal - minVal

        val variance = if (n > 1) {
            numbers.sumOf { (it - mean) * (it - mean) } / (n - 1)
        } else 0.0
        val stdDev = sqrt(variance)

        val steps = listOf(
            EvaluationStep(
                stepNumber = 1,
                title = "Order Data Set & Compute Sum",
                subExpression = "n = $n values",
                ruleApplied = "Descriptive Statistics",
                intermediateResult = "Sum = ${format(sum)}",
                expressionAfter = "Sorted: ${sorted.take(6).joinToString(", ") { format(it) }}${if (n > 6) "..." else ""}",
                note = "Values arranged in ascending order. Total sum of all $n$ data points is ${format(sum)}."
            ),
            EvaluationStep(
                stepNumber = 2,
                title = "Calculate Arithmetic Mean (Average)",
                subExpression = "μ = (Σ x) / n",
                ruleApplied = "Mean Formula",
                intermediateResult = "Mean = ${format(sum)} / $n = ${format(mean)}",
                expressionAfter = "Mean = ${format(mean)}",
                note = "Average value representing central tendency."
            ),
            EvaluationStep(
                stepNumber = 3,
                title = "Calculate Median & Range",
                subExpression = "Range = Max - Min = ${format(maxVal)} - ${format(minVal)}",
                ruleApplied = "Spread & Middle Value",
                intermediateResult = "Median = ${format(median)}, Range = ${format(range)}",
                expressionAfter = "Median = ${format(median)}",
                note = "The middle value separating the higher half from the lower half is ${format(median)}."
            ),
            EvaluationStep(
                stepNumber = 4,
                title = "Sample Variance (s²) & Standard Deviation (s)",
                subExpression = "s² = Σ(x - μ)² / (n - 1)",
                ruleApplied = "Bessel's Correction Sample Variance",
                intermediateResult = "s² = ${format(variance)}, s = ${format(stdDev)}",
                expressionAfter = "Standard Deviation = ${format(stdDev)}",
                note = "Standard deviation measures the dispersion of values around the mean."
            )
        )

        val notes = listOf(
            MathNote(
                title = "Measures of Central Tendency",
                content = "Mean is the numerical average, Median is the middle observation, and Mode is the most frequent value.",
                formula = "μ = Σx / n"
            ),
            MathNote(
                title = "Standard Deviation (Spread)",
                content = "Standard deviation measures how concentrated data points are around the mean. Low standard deviation indicates data points tend to be close to the mean.",
                formula = "s = √[Σ(x - μ)² / (n - 1)]"
            )
        )

        return StatisticsSummary(
            count = n,
            sum = sum,
            mean = mean,
            median = median,
            mode = modeStr,
            minVal = minVal,
            maxVal = maxVal,
            range = range,
            variance = variance,
            standardDeviation = stdDev,
            steps = steps,
            notes = notes
        )
    }
}
