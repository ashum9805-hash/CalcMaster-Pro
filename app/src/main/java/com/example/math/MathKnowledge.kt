package com.example.math

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

/**
 * Dedicated Mathematical Knowledge Engine.
 * Provides deep step-by-step reasoning, formula identification, and pedagogical explanations for:
 * - Arithmetic and Geometric Sequences & Series
 * - Infinite Series Convergence & Divergence Tests (n-th Term Divergence Test, Geometric Series Test, p-Series)
 * - Exponential Equations & Laws of Powers
 * - Quadratic & Polynomial Analysis
 */
object MathKnowledge {

    private val symbols = DecimalFormatSymbols(Locale.US)
    private val df = DecimalFormat("#,##0.######", symbols)

    fun format(v: Double): String {
        if (v.isNaN()) return "NaN"
        if (v.isInfinite()) return if (v > 0) "∞" else "-∞"
        if (abs(v) < 1.0E-12 && v != 0.0) return "0"
        if (v % 1.0 == 0.0 && abs(v) < 1.0E14) return String.format(Locale.US, "%.0f", v)
        return df.format(v)
    }

    // ------------------------------------------------------------------------
    // 1. SEQUENCE & SERIES KNOWLEDGE ENGINE (Summations: Σ)
    // ------------------------------------------------------------------------

    data class TermInfo(
        val index: Int,
        val value: Double,
        val substitutionExpr: String
    )

    private fun toSuperscript(num: Int): String {
        val digits = mapOf(
            '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
            '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹',
            '-' to '⁻'
        )
        return num.toString().map { digits[it] ?: it }.joinToString("")
    }

    fun substituteVariable(formula: String, value: Int): String {
        val normalized = MathEngine.normalizeExpression(formula)
        val varChar = listOf('x', 'X', 'n', 'N', 'k', 'K', 'i', 'I').firstOrNull { ch ->
            val pattern = Regex("""(?<![a-zA-Z])$ch(?![a-zA-Z])""")
            pattern.containsMatchIn(normalized)
        } ?: 'x'

        var s = normalized
        val coeffRegex = Regex("""(\d+(?:\.\d+)?)\s*$varChar(?![a-zA-Z])""")
        s = coeffRegex.replace(s) { m ->
            "${m.groupValues[1]}*($value)"
        }
        val standaloneRegex = Regex("""(?<![a-zA-Z])$varChar(?![a-zA-Z])""")
        s = standaloneRegex.replace(s, "($value)")
        return s
    }

    fun formatTermDisplay(formula: String, value: Int): String {
        var s = formula.trim()
        if (s.contains("ˣ")) {
            return s.replace("ˣ", toSuperscript(value))
        }
        if (s.contains("ⁿ")) {
            return s.replace("ⁿ", toSuperscript(value))
        }
        val varChar = listOf('x', 'X', 'n', 'N', 'k', 'K', 'i', 'I').firstOrNull { ch ->
            val pattern = Regex("""(?<![a-zA-Z])$ch(?![a-zA-Z])""")
            pattern.containsMatchIn(s)
        } ?: 'x'

        val coeffRegex = Regex("""(\d+(?:\.\d+)?)\s*$varChar(?![a-zA-Z])""")
        s = coeffRegex.replace(s) { m ->
            "${m.groupValues[1]}($value)"
        }
        val standaloneRegex = Regex("""(?<![a-zA-Z])$varChar(?![a-zA-Z])""")
        s = standaloneRegex.replace(s, "($value)")
        return s
    }

    /**
     * Deep mathematical analysis of any summation expression:
     * - Computes first terms and displays substitutions (e.g. 3(1) + 2 = 5)
     * - Identifies first term (a₁), second term (a₂), third term (a₃)
     * - Tests for Arithmetic Progression (common difference d = a₂ - a₁)
     * - Tests for Geometric Progression (common ratio r = a₂ / a₁)
     * - Tests for p-Series, harmonic, alternating, and polynomial sums
     * - Applies explicit formulas (S_n = (n/2)(a₁+a_n), S_∞ = a/(1-r), Divergence Test lim a_n ≠ 0)
     */
    fun analyzeAndSolveSummation(
        formula: String,
        startStr: String,
        endStr: String,
        angleMode: AngleMode = AngleMode.DEG
    ): AdvancedSolvers.SummationResult {
        val start = startStr.trim().toIntOrNull() ?: 1
        val isInfinity = endStr.trim() in listOf("∞", "inf", "infinity", "Infinity", "+∞")
        val cleanFormula = formula.trim().ifBlank { "x" }

        val steps = mutableListOf<EvaluationStep>()
        val notes = mutableListOf<MathNote>()
        var stepNum = 1

        // Evaluate first 6 terms to analyze the sequence pattern
        val evaluatedTerms = mutableListOf<TermInfo>()
        for (i in 0..5) {
            val x = start + i
            val sub = substituteVariable(cleanFormula, x)
            val tv = try {
                MathEngine.evaluateSimpleExpression(MathEngine.normalizeExpression(sub), angleMode, xVal = x.toDouble())
            } catch (_: Exception) {
                0.0
            }
            val subDisplay = formatTermDisplay(cleanFormula, x)
            evaluatedTerms.add(TermInfo(x, tv, "$subDisplay = ${format(tv)}"))
        }

        val a1 = evaluatedTerms[0].value
        val a2 = evaluatedTerms[1].value
        val a3 = evaluatedTerms[2].value
        val a4 = evaluatedTerms[3].value

        val a1Str = format(a1)
        val a2Str = format(a2)
        val a3Str = format(a3)

        // Step 1: Identify boundary conditions and display initial terms
        val boundSymbol = if (isInfinity) "∞" else endStr.trim()
        steps.add(
            EvaluationStep(
                stepNumber = stepNum++,
                title = "Identify Initial Sequence Terms",
                subExpression = "f(x) = $cleanFormula, x ∈ [$start, $boundSymbol]",
                ruleApplied = "Sequence Term Evaluation: a_n = f(n)",
                intermediateResult = "a₁ = $a1Str, a₂ = $a2Str, a₃ = $a3Str",
                expressionAfter = "Sequence started",
                note = "Substitute the initial index values into f(x):\n" +
                        "• x = $start: ${evaluatedTerms[0].substitutionExpr} (First term, a₁)\n" +
                        "• x = ${start + 1}: ${evaluatedTerms[1].substitutionExpr} (Second term, a₂)\n" +
                        "• x = ${start + 2}: ${evaluatedTerms[2].substitutionExpr} (Third term, a₃)\n" +
                        "• x = ${start + 3}: ${evaluatedTerms[3].substitutionExpr} (Fourth term, a₄)"
            )
        )

        // Sequence type detection:
        val diff1 = a2 - a1
        val diff2 = a3 - a2
        val diff3 = a4 - a3
        val isArithmetic = abs(diff1 - diff2) < 1.0E-6 && abs(diff2 - diff3) < 1.0E-6

        val isGeometric = if (abs(a1) > 1.0E-9 && abs(a2) > 1.0E-9 && abs(a3) > 1.0E-9) {
            val r1 = a2 / a1
            val r2 = a3 / a2
            val r3 = a4 / a3
            abs(r1 - r2) < 1.0E-6 && abs(r2 - r3) < 1.0E-6
        } else false

        // -------------------------------------------------------------
        // Case A: ARITHMETIC PROGRESSION (e.g. 3x + 2, 2x - 1, x, etc.)
        // -------------------------------------------------------------
        if (isArithmetic) {
            val d = diff1
            val dStr = format(d)

            steps.add(
                EvaluationStep(
                    stepNumber = stepNum++,
                    title = "Determine Common Difference (d)",
                    subExpression = "d = a₂ - a₁",
                    ruleApplied = "Arithmetic Progression: d = a_{k+1} - a_k",
                    intermediateResult = "d = $a2Str - ($a1Str) = $dStr",
                    expressionAfter = "d = $dStr",
                    note = "Checking consecutive differences:\n" +
                            "• a₂ - a₁ = $a2Str - ($a1Str) = $dStr\n" +
                            "• a₃ - a₂ = $a3Str - ($a2Str) = ${format(diff2)}\n" +
                            "Because the difference is constant (d = $dStr), the sequence is an Arithmetic Progression (AP)."
                )
            )

            steps.add(
                EvaluationStep(
                    stepNumber = stepNum++,
                    title = "Formulate General Term (a_n)",
                    subExpression = "a_n = a₁ + (n - 1)d",
                    ruleApplied = "n-th Term of an Arithmetic Sequence",
                    intermediateResult = "a_n = $a1Str + (n - 1)($dStr)",
                    expressionAfter = "Formula established",
                    note = "Any term at position n is given by a_n = a₁ + (n - 1)d."
                )
            )

            notes.add(
                MathNote(
                    title = "Arithmetic Sequence & Series",
                    content = "A sequence of numbers where each term after the first is found by adding a constant d (common difference). The n-th term is a_n = a₁ + (n - 1)d.",
                    formula = "a_n = a₁ + (n - 1)d"
                )
            )

            if (isInfinity) {
                // Infinite Arithmetic Series
                if (abs(d) < 1.0E-9 && abs(a1) < 1.0E-9) {
                    // Constant 0 series
                    steps.add(
                        EvaluationStep(
                            stepNumber = stepNum++,
                            title = "Sum of Zeros",
                            subExpression = "Σ 0 = 0",
                            ruleApplied = "Zero Identity",
                            intermediateResult = "0",
                            expressionAfter = "Sum = 0",
                            note = "Sum of an infinite series of zeros is 0."
                        )
                    )
                    return buildResult(cleanFormula, "$start", "∞", true, 0.0, "0", evaluatedTerms, steps, notes)
                }

                // Non-zero arithmetic series diverges by the Divergence Test!
                val limitSign = if (d > 0 || (d == 0.0 && a1 > 0)) "∞" else "-∞"
                steps.add(
                    EvaluationStep(
                        stepNumber = stepNum++,
                        title = "Apply Divergence Test (n-th Term Test)",
                        subExpression = "lim_{n→∞} a_n = lim_{n→∞} [$a1Str + (n - 1)($dStr)]",
                        ruleApplied = "Divergence Test: If lim_{n→∞} a_n ≠ 0, then Σ a_n diverges",
                        intermediateResult = "lim_{n→∞} a_n = $limitSign ≠ 0",
                        expressionAfter = "Series Diverges to $limitSign",
                        note = "By the n-th Term Test for Divergence (Divergence Test):\n" +
                                "Since the common difference d = $dStr ≠ 0, the terms do not approach 0 (lim_{n→∞} a_n = $limitSign ≠ 0).\n" +
                                "Therefore, the infinite arithmetic series CANNOT converge to a finite number; it diverges to $limitSign."
                    )
                )

                notes.add(
                    MathNote(
                        title = "Divergence Test (n-th Term Test)",
                        content = "If the limit of a_n as n approaches infinity does not equal 0 (or does not exist), the infinite series Σ a_n must diverge. An arithmetic series with d ≠ 0 always diverges.",
                        formula = "lim_{n→∞} a_n ≠ 0  ⟹  Σ a_n diverges"
                    )
                )

                return buildResult(
                    cleanFormula, "$start", "∞", true,
                    if (limitSign == "∞") Double.POSITIVE_INFINITY else Double.NEGATIVE_INFINITY,
                    "Diverges ($limitSign)",
                    evaluatedTerms, steps, notes
                )
            } else {
                // Finite Arithmetic Series
                val end = endStr.trim().toIntOrNull() ?: 5
                val n = max(0, end - start + 1)
                val an = a1 + (n - 1) * d
                val anStr = format(an)
                val sum = (n.toDouble() / 2.0) * (a1 + an)
                val sumStr = format(sum)

                steps.add(
                    EvaluationStep(
                        stepNumber = stepNum++,
                        title = "Apply Arithmetic Series Sum Formula",
                        subExpression = "S_n = (n / 2) · [2a₁ + (n - 1)d] = (n / 2) · [a₁ + a_n]",
                        ruleApplied = "Gauss Arithmetic Sum Formula",
                        intermediateResult = "S_$n = ($n / 2) · [$a1Str + $anStr] = $sumStr",
                        expressionAfter = "Sum = $sumStr",
                        note = "Substituting parameters into the sum formula:\n" +
                                "• Number of terms: n = $end - $start + 1 = $n\n" +
                                "• First term: a₁ = $a1Str\n" +
                                "• Last term: a_$n = $anStr\n" +
                                "• S_$n = ($n / 2) × ($a1Str + $anStr) = ${format(n.toDouble() / 2.0)} × ${format(a1 + an)} = $sumStr."
                    )
                )

                notes.add(
                    MathNote(
                        title = "Arithmetic Series Sum Formula",
                        content = "The sum of the first n terms of an arithmetic progression is given by S_n = (n/2)(a₁ + a_n) or S_n = (n/2)[2a₁ + (n-1)d].",
                        formula = "S_n = n/2 · (a₁ + a_n)"
                    )
                )

                return buildResult(cleanFormula, "$start", "$end", false, sum, sumStr, evaluatedTerms, steps, notes)
            }
        }

        // -------------------------------------------------------------
        // Case B: GEOMETRIC PROGRESSION (e.g. (1/2)ˣ, 3 · 2ˣ, etc.)
        // -------------------------------------------------------------
        if (isGeometric) {
            val r = a2 / a1
            val rStr = format(r)

            steps.add(
                EvaluationStep(
                    stepNumber = stepNum++,
                    title = "Determine Common Ratio (r)",
                    subExpression = "r = a₂ / a₁",
                    ruleApplied = "Geometric Progression: r = a_{k+1} / a_k",
                    intermediateResult = "r = $a2Str / ($a1Str) = $rStr",
                    expressionAfter = "r = $rStr",
                    note = "Checking consecutive term ratios:\n" +
                            "• a₂ / a₁ = $a2Str / ($a1Str) = $rStr\n" +
                            "• a₃ / a₂ = $a3Str / ($a2Str) = ${format(a3 / a2)}\n" +
                            "Because the ratio is constant (r = $rStr), the sequence is a Geometric Progression (GP)."
                )
            )

            steps.add(
                EvaluationStep(
                    stepNumber = stepNum++,
                    title = "Formulate General Term (a_n)",
                    subExpression = "a_n = a₁ · r^(n - 1)",
                    ruleApplied = "n-th Term of a Geometric Sequence",
                    intermediateResult = "a_n = ($a1Str) · ($rStr)^(n - 1)",
                    expressionAfter = "Formula established",
                    note = "Any term at position n is given by multiplying the first term by r^(n - 1)."
                )
            )

            notes.add(
                MathNote(
                    title = "Geometric Sequence & Series",
                    content = "A sequence of numbers where each term after the first is found by multiplying the previous one by a fixed number r (common ratio).",
                    formula = "a_n = a₁ · r^{n-1}"
                )
            )

            if (isInfinity) {
                // Infinite Geometric Series
                if (abs(r) < 1.0) {
                    val infSum = a1 / (1.0 - r)
                    val infSumStr = format(infSum)

                    steps.add(
                        EvaluationStep(
                            stepNumber = stepNum++,
                            title = "Geometric Series Convergence Criterion (|r| < 1)",
                            subExpression = "|r| = |$rStr| = ${format(abs(r))} < 1",
                            ruleApplied = "Infinite Geometric Series Convergence Test",
                            intermediateResult = "Convergent (|r| < 1)",
                            expressionAfter = "Converges to finite limit",
                            note = "Because the absolute value of the common ratio |r| = ${format(abs(r))} is strictly less than 1, the powers r^n approach 0 as n → ∞. Therefore, the infinite series converges."
                        )
                    )

                    steps.add(
                        EvaluationStep(
                            stepNumber = stepNum++,
                            title = "Calculate Infinite Sum (S_∞)",
                            subExpression = "S_∞ = a₁ / (1 - r)",
                            ruleApplied = "Sum of an Infinite Geometric Series",
                            intermediateResult = "S_∞ = ($a1Str) / (1 - ($rStr)) = $infSumStr",
                            expressionAfter = "S_∞ = $infSumStr",
                            note = "Substituting a₁ = $a1Str and r = $rStr into the infinite sum formula gives:\n" +
                                    "S_∞ = $a1Str / (${format(1.0 - r)}) = $infSumStr."
                        )
                    )

                    notes.add(
                        MathNote(
                            title = "Infinite Geometric Series Sum",
                            content = "An infinite geometric series converges if and only if |r| < 1. Its sum is S_∞ = a / (1 - r). If |r| ≥ 1, the series diverges.",
                            formula = "S_∞ = a / (1 - r)  (|r| < 1)"
                        )
                    )

                    return buildResult(cleanFormula, "$start", "∞", true, infSum, infSumStr, evaluatedTerms, steps, notes)
                } else {
                    // Divergent geometric series
                    steps.add(
                        EvaluationStep(
                            stepNumber = stepNum++,
                            title = "Geometric Series Divergence Test (|r| ≥ 1)",
                            subExpression = "|r| = |$rStr| = ${format(abs(r))} ≥ 1",
                            ruleApplied = "Geometric Series Divergence Criterion",
                            intermediateResult = "Divergent (|r| ≥ 1)",
                            expressionAfter = "Series Diverges",
                            note = "Because |r| = ${format(abs(r))} ≥ 1, the individual terms do not decay to zero. The infinite series diverges."
                        )
                    )

                    return buildResult(
                        cleanFormula, "$start", "∞", true,
                        Double.POSITIVE_INFINITY, "Diverges (∞)",
                        evaluatedTerms, steps, notes
                    )
                }
            } else {
                // Finite Geometric Series
                val end = endStr.trim().toIntOrNull() ?: 5
                val n = max(0, end - start + 1)
                val sum = if (abs(r - 1.0) < 1.0E-9) {
                    n * a1
                } else {
                    a1 * (1.0 - r.pow(n)) / (1.0 - r)
                }
                val sumStr = format(sum)

                steps.add(
                    EvaluationStep(
                        stepNumber = stepNum++,
                        title = "Apply Finite Geometric Sum Formula",
                        subExpression = "S_n = a₁ · (1 - r^n) / (1 - r)",
                        ruleApplied = "Finite Geometric Series Sum",
                        intermediateResult = "S_$n = ($a1Str) · [1 - ($rStr)^$n] / [1 - ($rStr)] = $sumStr",
                        expressionAfter = "Sum = $sumStr",
                        note = "Evaluating with n = $n terms:\n" +
                                "• r^$n = ${format(r.pow(n))}\n" +
                                "• 1 - r^$n = ${format(1.0 - r.pow(n))}\n" +
                                "• S_$n = ($a1Str × ${format(1.0 - r.pow(n))}) / ${format(1.0 - r)} = $sumStr."
                    )
                )

                notes.add(
                    MathNote(
                        title = "Finite Geometric Series Formula",
                        content = "The sum of the first n terms of a geometric progression is S_n = a(1 - r^n)/(1 - r) for r ≠ 1.",
                        formula = "S_n = a₁ · (1 - r^n) / (1 - r)"
                    )
                )

                return buildResult(cleanFormula, "$start", "$end", false, sum, sumStr, evaluatedTerms, steps, notes)
            }
        }

        // -------------------------------------------------------------
        // Case C: OTHER SPECIAL SERIES (p-Series, Basel, Taylor e, etc.)
        // -------------------------------------------------------------
        val normalized = cleanFormula.replace(" ", "").lowercase()

        // 1/x^2 (Basel problem)
        if (normalized in listOf("1/x^2", "1/(x^2)", "x^(-2)", "1/x²") && start == 1 && isInfinity) {
            val baselSum = (Math.PI * Math.PI) / 6.0
            val baselStr = format(baselSum)

            steps.add(
                EvaluationStep(
                    stepNumber = stepNum++,
                    title = "Identify Basel Problem (p-Series with p = 2)",
                    subExpression = "Σ_{x=1}^∞ 1/x²",
                    ruleApplied = "Euler's Solution to the Basel Problem",
                    intermediateResult = "π² / 6 ≈ $baselStr",
                    expressionAfter = "Sum = π²/6",
                    note = "This is the famous Basel problem solved by Leonhard Euler in 1734. The infinite sum of reciprocal squares converges to exactly π²/6 ≈ $baselStr."
                )
            )

            notes.add(
                MathNote(
                    title = "Basel Problem",
                    content = "First posed by Pietro Mengoli in 1644 and solved by Euler in 1734. It proves the connection between prime numbers, the Riemann zeta function ζ(2), and π.",
                    formula = "Σ_{n=1}^∞ 1/n² = π² / 6 ≈ 1.644934"
                )
            )

            return buildResult(cleanFormula, "1", "∞", true, baselSum, baselStr, evaluatedTerms, steps, notes)
        }

        // Harmonic series 1/x
        if (normalized in listOf("1/x", "1/(x)") && isInfinity) {
            steps.add(
                EvaluationStep(
                    stepNumber = stepNum++,
                    title = "Harmonic Series Divergence (p = 1)",
                    subExpression = "Σ_{x=$start}^∞ 1/x",
                    ruleApplied = "Integral Test for Divergence: ∫₁^∞ (1/x) dx = [ln(x)]₁^∞ = ∞",
                    intermediateResult = "Divergent (Sum → ∞)",
                    expressionAfter = "Diverges",
                    note = "The harmonic series 1 + 1/2 + 1/3 + 1/4 + ... diverges to infinity, even though its individual terms approach 0. Proved by the Integral Test."
                )
            )

            notes.add(
                MathNote(
                    title = "Harmonic Series",
                    content = "The harmonic series is the p-series with p = 1. Even though lim 1/n = 0, the sum diverges to infinity at a logarithmic rate.",
                    formula = "Σ_{n=1}^∞ 1/n = ∞"
                )
            )

            return buildResult(cleanFormula, "$start", "∞", true, Double.POSITIVE_INFINITY, "Diverges (∞)", evaluatedTerms, steps, notes)
        }

        // -------------------------------------------------------------
        // Case D: GENERAL / ARBITRARY SERIES
        // -------------------------------------------------------------
        if (isInfinity) {
            // General infinite series: test limit of n-th term
            val testTerm = evaluatedTerms.last().value
            var pSum = 0.0
            var lastVal = 0.0
            for (x in start..(start + 300)) {
                val sub = cleanFormula.replace("x", "($x)")
                val tv = try {
                    MathEngine.evaluateSimpleExpression(MathEngine.normalizeExpression(sub), angleMode)
                } catch (_: Exception) { 0.0 }
                pSum += tv
                lastVal = tv
            }

            val isDecaying = abs(lastVal) < 1.0E-4
            val resDesc = if (isDecaying) "≈ ${format(pSum)} (Converges)" else "Divergent (→ ∞)"

            steps.add(
                EvaluationStep(
                    stepNumber = stepNum++,
                    title = "Numerical Cauchy Convergence Analysis",
                    subExpression = "Partial sum S_{300}",
                    ruleApplied = "Cauchy Criterion & Partial Sums",
                    intermediateResult = resDesc,
                    expressionAfter = "Limit analysis",
                    note = "Evaluated 300 consecutive terms. Last computed term a_{300} = ${format(lastVal)}. State: $resDesc."
                )
            )

            return buildResult(
                cleanFormula, "$start", "∞", true,
                if (isDecaying) pSum else Double.POSITIVE_INFINITY,
                resDesc, evaluatedTerms, steps, notes
            )
        } else {
            // General finite series
            val end = endStr.trim().toIntOrNull() ?: 5
            val count = max(0, end - start + 1)
            var sum = 0.0
            val termList = mutableListOf<String>()

            for (x in start..end) {
                val sub = cleanFormula.replace("x", "($x)")
                val tv = try {
                    MathEngine.evaluateSimpleExpression(MathEngine.normalizeExpression(sub), angleMode)
                } catch (_: Exception) { 0.0 }
                sum += tv
                if (termList.size < 12) {
                    termList.add("x=$x: ${format(tv)}")
                }
            }
            if (count > 12) {
                termList.add("... (${count - 12} more terms)")
            }

            val sumStr = format(sum)
            steps.add(
                EvaluationStep(
                    stepNumber = stepNum++,
                    title = "Evaluate and Accumulate Terms",
                    subExpression = "Σ_{x=$start}^{$end} ($cleanFormula)",
                    ruleApplied = "Direct Term Summation",
                    intermediateResult = "Total = $sumStr",
                    expressionAfter = "Σ = $sumStr",
                    note = "Sum of all $count evaluated terms from x = $start to x = $end equals $sumStr."
                )
            )

            return buildResult(cleanFormula, "$start", "$end", false, sum, sumStr, evaluatedTerms, steps, notes)
        }
    }

    private fun buildResult(
        cleanFormula: String,
        startStr: String,
        endStr: String,
        isInfinite: Boolean,
        totalSum: Double,
        formattedSum: String,
        terms: List<TermInfo>,
        steps: List<EvaluationStep>,
        notes: List<MathNote>
    ): AdvancedSolvers.SummationResult {
        val termsList = terms.map { it.substitutionExpr }
        return AdvancedSolvers.SummationResult(
            expression = cleanFormula,
            startValStr = startStr,
            endValStr = endStr,
            isInfinite = isInfinite,
            totalSum = totalSum,
            formattedSum = formattedSum,
            termsList = termsList,
            steps = steps,
            notes = notes
        )
    }

    // ------------------------------------------------------------------------
    // 2. EXPONENTIAL & LOGARITHMIC EQUATION KNOWLEDGE ENGINE
    // ------------------------------------------------------------------------

    data class ExponentialSolution(
        val a: Double,
        val base: Double,
        val c: Double,
        val xResult: Double,
        val formattedResult: String,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    /**
     * Solves exponential equation: a · b^x = c  ⟹  b^x = c / a  ⟹  x = ln(c / a) / ln(b) = log_b(c / a)
     */
    fun solveExponentialEquation(a: Double, base: Double, c: Double): ExponentialSolution {
        val steps = mutableListOf<EvaluationStep>()
        val notes = mutableListOf<MathNote>()
        var stepNum = 1

        val aStr = format(a)
        val bStr = format(base)
        val cStr = format(c)

        steps.add(
            EvaluationStep(
                stepNumber = stepNum++,
                title = "Identify Exponential Equation Form",
                subExpression = "$aStr · ($bStr)^x = $cStr",
                ruleApplied = "Standard Exponential Form: a · b^x = c",
                intermediateResult = "a = $aStr, base b = $bStr, result c = $cStr",
                expressionAfter = "Coefficients identified",
                note = "Confirm the base is positive (b > 0, b ≠ 1) and the equation is structured to isolate the exponent."
            )
        )

        // Step 1: Isolate power: b^x = c / a
        if (a == 0.0) {
            val errDesc = "Undefined (a = 0)"
            return ExponentialSolution(a, base, c, Double.NaN, errDesc, steps, notes)
        }

        val isolatedRhs = c / a
        val isolatedRhsStr = format(isolatedRhs)

        steps.add(
            EvaluationStep(
                stepNumber = stepNum++,
                title = "Isolate Exponential Term: b^x",
                subExpression = "($bStr)^x = $cStr / $aStr",
                ruleApplied = "Division Property of Equality",
                intermediateResult = "($bStr)^x = $isolatedRhsStr",
                expressionAfter = "Power isolated",
                note = "Divide both sides by a = $aStr to isolate the power on the left side."
            )
        )

        // Check feasibility
        if (isolatedRhs <= 0.0) {
            steps.add(
                EvaluationStep(
                    stepNumber = stepNum++,
                    title = "Non-Positive RHS Check",
                    subExpression = "($bStr)^x = $isolatedRhsStr ≤ 0",
                    ruleApplied = "Range of Exponential Functions: b^x > 0 for all real x",
                    intermediateResult = "No Real Solution",
                    expressionAfter = "No real solution",
                    note = "For any real positive base b > 0, b^x is strictly positive (b^x > 0). Since $isolatedRhsStr ≤ 0, there is no real number x that satisfies this equation."
                )
            )

            notes.add(
                MathNote(
                    title = "Exponential Function Range",
                    content = "The range of f(x) = b^x for b > 0 is (0, ∞). An exponential cannot produce a zero or negative output in the real numbers.",
                    formula = "b^x > 0  ∀ x ∈ ℝ"
                )
            )

            return ExponentialSolution(a, base, c, Double.NaN, "No Real Solution (c/a ≤ 0)", steps, notes)
        }

        if (base <= 0.0 || base == 1.0) {
            val err = if (base == 1.0) "Base b = 1 is degenerate" else "Base b must be positive"
            steps.add(
                EvaluationStep(
                    stepNumber = stepNum++,
                    title = "Invalid Base Check",
                    subExpression = "Base b = $bStr",
                    ruleApplied = "Exponential Base Definition: b > 0 and b ≠ 1",
                    intermediateResult = err,
                    expressionAfter = "Invalid base",
                    note = err
                )
            )
            return ExponentialSolution(a, base, c, Double.NaN, err, steps, notes)
        }

        // Step 2: Take natural logarithm of both sides
        val lnRhs = ln(isolatedRhs)
        val lnBase = ln(base)
        val xVal = lnRhs / lnBase
        val xStr = format(xVal)

        steps.add(
            EvaluationStep(
                stepNumber = stepNum++,
                title = "Apply Natural Logarithm (ln) to Both Sides",
                subExpression = "ln(($bStr)^x) = ln($isolatedRhsStr)",
                ruleApplied = "Logarithm Power Rule: ln(b^x) = x · ln(b)",
                intermediateResult = "x · ln($bStr) = ln($isolatedRhsStr)",
                expressionAfter = "x · ln(b) = ln(RHS)",
                note = "Applying natural logarithm to both sides brings down the exponent x into a linear factor: ln(b^x) = x · ln(b)."
            )
        )

        steps.add(
            EvaluationStep(
                stepNumber = stepNum++,
                title = "Solve for x via Change of Base",
                subExpression = "x = ln($isolatedRhsStr) / ln($bStr) = log_{$bStr}($isolatedRhsStr)",
                ruleApplied = "Logarithmic Change of Base Formula: log_b(y) = ln(y) / ln(b)",
                intermediateResult = "x = ${format(lnRhs)} / ${format(lnBase)} = $xStr",
                expressionAfter = "x = $xStr",
                note = "Substitute the logarithmic values:\n" +
                        "• ln($isolatedRhsStr) ≈ ${format(lnRhs)}\n" +
                        "• ln($bStr) ≈ ${format(lnBase)}\n" +
                        "• x = ${format(lnRhs)} / ${format(lnBase)} = $xStr."
            )
        )

        // Verification Step
        val checkVal = a * base.pow(xVal)
        steps.add(
            EvaluationStep(
                stepNumber = stepNum++,
                title = "Verification Check",
                subExpression = "$aStr · ($bStr)^($xStr)",
                ruleApplied = "Equation Verification",
                intermediateResult = "≈ ${format(checkVal)} = $cStr ✓",
                expressionAfter = "Verified",
                note = "Plugging x = $xStr back into the original equation yields $aStr × ${format(base.pow(xVal))} = ${format(checkVal)}, matching $cStr."
            )
        )

        notes.add(
            MathNote(
                title = "Laws of Exponents & Logarithms",
                content = "To solve b^x = y, apply logarithms to both sides: x = log_b(y) = ln(y) / ln(b). Key exponent rules include b^m · b^n = b^{m+n}, (b^m)^n = b^{mn}, and b^{-n} = 1/b^n.",
                formula = "b^x = y  ⟺  x = log_b(y) = ln(y) / ln(b)"
            )
        )

        return ExponentialSolution(a, base, c, xVal, xStr, steps, notes)
    }
}
