package com.example.math

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

object MathEngine {

    private val decimalSymbols = DecimalFormatSymbols(Locale.US)
    private val standardFormat = DecimalFormat("#,##0.##########", decimalSymbols)
    private val scientificFormat = DecimalFormat("0.######E0", decimalSymbols)

    fun formatNumber(value: Double): String {
        if (value.isNaN()) return "Undefined (NaN)"
        if (value.isInfinite()) return if (value > 0) "Infinity (∞)" else "-Infinity (-∞)"
        if (abs(value) < 1.0E-12 && value != 0.0) return "0"
        
        // If integer value
        if (value % 1.0 == 0.0 && abs(value) < 1.0E15) {
            return String.format(Locale.US, "%.0f", value)
        }
        
        // Very large or very small numbers
        if ((abs(value) >= 1.0E12 || abs(value) <= 1.0E-6) && value != 0.0) {
            return scientificFormat.format(value)
        }
        return standardFormat.format(value)
    }

    /**
     * Cleans and normalizes user input expression into standard evaluation syntax.
     * Supports mathematical superscripts (², ³, ⁴, etc.) and radicals (√, ∛).
     */
    fun normalizeExpression(input: String): String {
        var s = input
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "pi")

        // Support Cube root ∛
        s = s.replace(Regex("""∛\("""), "cbrt(")
        s = s.replace(Regex("""∛(\d+(?:\.\d+)?)"""), "cbrt($1)")

        // Support Square root √
        s = s.replace(Regex("""√\("""), "sqrt(")
        s = s.replace(Regex("""√(\d+(?:\.\d+)?)"""), "sqrt($1)")
        s = s.replace("√", "sqrt")

        // Convert Unicode superscripts (⁰ ¹ ² ³ ⁴ ⁵ ⁶ ⁷ ⁸ ⁹ ⁺ ⁻ ˣ ʸ ⁿ ᵃ ᵇ) into standard ^(n)
        val supMap = mapOf(
            '⁰' to '0', '¹' to '1', '²' to '2', '³' to '3', '⁴' to '4',
            '⁵' to '5', '⁶' to '6', '⁷' to '7', '⁸' to '8', '⁹' to '9',
            '⁺' to '+', '⁻' to '-',
            'ˣ' to 'x', 'ʸ' to 'y', 'ⁿ' to 'n', 'ᵃ' to 'a', 'ᵇ' to 'b'
        )
        val supRegex = Regex("""([⁰¹²³⁴⁵⁶⁷⁸⁹⁺⁻ˣʸⁿᵃᵇ]+)""")
        s = supRegex.replace(s) { match ->
            val converted = match.value.map { supMap[it] ?: it }.joinToString("")
            "^($converted)"
        }

        return s.trim()
    }

    /**
     * Solves expression and produces detailed steps and notes
     */
    fun evaluateWithExplanation(
        rawExpression: String,
        angleMode: AngleMode,
        xVal: Double = 1.0,
        yVal: Double = 2.0
    ): CalculationResult {
        val expr = normalizeExpression(rawExpression)
        if (expr.isBlank()) {
            return CalculationResult(
                expression = rawExpression,
                resultNumber = 0.0,
                formattedResult = "0",
                angleMode = angleMode,
                steps = emptyList(),
                notes = emptyList(),
                alternatives = AlternativeRepresentations(scientific = "0"),
                isSuccess = true
            )
        }

        // Check if expression is a summation: sum(x, 1, inf, 3x+2) or Σ(x, 1, 5, 3x+2)
        val sumParsed = tryParseSummation(rawExpression) ?: tryParseSummation(expr)
        if (sumParsed != null) {
            val (formula, startStr, endStr) = sumParsed
            val sumResult = MathKnowledge.analyzeAndSolveSummation(formula, startStr, endStr, angleMode)
            val numVal = if (sumResult.totalSum.isInfinite() || sumResult.totalSum.isNaN()) 0.0 else sumResult.totalSum
            return CalculationResult(
                expression = rawExpression,
                resultNumber = numVal,
                formattedResult = sumResult.formattedSum,
                angleMode = angleMode,
                steps = sumResult.steps,
                notes = sumResult.notes,
                alternatives = computeAlternatives(numVal, angleMode),
                isSuccess = true
            )
        }

        val steps = mutableListOf<EvaluationStep>()
        val notes = mutableListOf<MathNote>()
        var stepCounter = 1

        try {
            // Check for concepts used in expression to build notes
            addConceptNotes(expr, angleMode, notes, xVal, yVal)

            // Step-by-step resolution simulation
            var currentExpr = insertImplicitMultiplications(expr)

            // Step 0: Check if expression contains x or y variables
            val hasX = currentExpr.contains(Regex("""\b[xX]\b""")) || expr.contains("x", ignoreCase = true)
            val hasY = currentExpr.contains(Regex("""\b[yY]\b""")) || expr.contains("y", ignoreCase = true)
            if (hasX || hasY) {
                val varSubDesc = buildList {
                    if (hasX) add("x = ${formatNumber(xVal)}")
                    if (hasY) add("y = ${formatNumber(yVal)}")
                }.joinToString(", ")

                var substituted = currentExpr
                if (hasX) substituted = substituted.replace(Regex("""\b[xX]\b"""), "(${formatNumber(xVal)})")
                if (hasY) substituted = substituted.replace(Regex("""\b[yY]\b"""), "(${formatNumber(yVal)})")

                steps.add(
                    EvaluationStep(
                        stepNumber = stepCounter++,
                        title = "Substitute Variable Values ($varSubDesc)",
                        subExpression = currentExpr,
                        ruleApplied = "Algebraic Variable Substitution",
                        intermediateResult = substituted,
                        expressionAfter = substituted,
                        note = "Replace unknown variables with their assigned values ($varSubDesc) to evaluate the function numerically."
                    )
                )
                currentExpr = substituted
            }

            // Step 1: Process factorials (postfix !)
            val factorialRegex = Regex("""(\d+(?:\.\d+)?)\s*!""")
            while (factorialRegex.containsMatchIn(currentExpr)) {
                val match = factorialRegex.find(currentExpr)!!
                val numStr = match.groupValues[1]
                val num = numStr.toDouble()
                val factVal = factorial(num.toInt())
                val formattedFact = formatNumber(factVal)
                currentExpr = currentExpr.replaceFirst(match.value, formattedFact)

                steps.add(
                    EvaluationStep(
                        stepNumber = stepCounter++,
                        title = "Evaluate Factorial: $numStr!",
                        subExpression = match.value,
                        ruleApplied = "Factorial rule: n! = n × (n - 1) × ... × 1",
                        intermediateResult = formattedFact,
                        expressionAfter = currentExpr,
                        note = "The factorial of $numStr is calculated as $numStr! = $formattedFact"
                    )
                )
            }

            // Step 2: Resolve Parentheses & Function calls from inside out
            val parenRegex = Regex("""\(([^()]+)\)""")
            while (parenRegex.containsMatchIn(currentExpr)) {
                val match = parenRegex.find(currentExpr)!!
                val inner = match.groupValues[1].trim()
                
                // If it's a function call prefix like sin(inner) or sqrt(inner)
                val beforeMatchIndex = match.range.first
                val prefix = getPrefixFunction(currentExpr, beforeMatchIndex)
                
                if (prefix != null) {
                    val fullFnCall = "$prefix($inner)"
                    // Check if it's a 2-arg function like nCr(n, r) or gcd(a, b)
                    if (inner.contains(",")) {
                        val parts = inner.split(",").map { it.trim() }
                        if (parts.size == 2) {
                            val a = evaluateSimpleExpression(parts[0], angleMode, xVal, yVal)
                            val b = evaluateSimpleExpression(parts[1], angleMode, xVal, yVal)
                            val twoArgResult = evaluateTwoArgFunction(prefix, a, b)
                            val resStr = formatNumber(twoArgResult)
                            currentExpr = currentExpr.replaceFirst(fullFnCall, resStr)

                            steps.add(
                                EvaluationStep(
                                    stepNumber = stepCounter++,
                                    title = "Evaluate $prefix(${parts[0]}, ${parts[1]})",
                                    subExpression = fullFnCall,
                                    ruleApplied = "Combinatorics / Number Theory Formula for $prefix",
                                    intermediateResult = resStr,
                                    expressionAfter = currentExpr,
                                    note = "$prefix(${formatNumber(a)}, ${formatNumber(b)}) evaluates to $resStr."
                                )
                            )
                            continue
                        }
                    }

                    // Single-argument function
                    val innerVal = evaluateSimpleExpression(inner, angleMode, xVal, yVal)
                    val fnResult = evaluateFunction(prefix, innerVal, angleMode)
                    val fnResultStr = formatNumber(fnResult)
                    currentExpr = currentExpr.replaceFirst(fullFnCall, fnResultStr)

                    val angleNote = if (prefix in listOf("sin", "cos", "tan", "asin", "acos", "atan")) {
                        " (evaluated in ${angleMode.name} mode)"
                    } else ""

                    steps.add(
                        EvaluationStep(
                            stepNumber = stepCounter++,
                            title = "Evaluate Function: $prefix($inner)",
                            subExpression = fullFnCall,
                            ruleApplied = "Function definition for $prefix$angleNote",
                            intermediateResult = fnResultStr,
                            expressionAfter = currentExpr,
                            note = "$prefix($inner) yields $fnResultStr$angleNote."
                        )
                    )
                } else {
                    // Just standard parenthesis (a + b)
                    val innerVal = evaluateSimpleExpression(inner, angleMode, xVal, yVal)
                    val innerStr = formatNumber(innerVal)
                    currentExpr = currentExpr.replaceFirst("($inner)", innerStr)

                    steps.add(
                        EvaluationStep(
                            stepNumber = stepCounter++,
                            title = "Solve Sub-expression in Parentheses: ($inner)",
                            subExpression = "($inner)",
                            ruleApplied = "PEMDAS / BODMAS Order of Operations: Parentheses first",
                            intermediateResult = innerStr,
                            expressionAfter = currentExpr,
                            note = "Evaluating the contents inside parentheses gives $innerStr."
                        )
                    )
                }
            }

            // Step 3: Exponents (a ^ b)
            val powerRegex = Regex("""(-?\d+(?:\.\d+)?)\s*\^\s*(-?\d+(?:\.\d+)?)""")
            while (powerRegex.containsMatchIn(currentExpr)) {
                val match = powerRegex.find(currentExpr)!!
                val base = match.groupValues[1].toDouble()
                val exp = match.groupValues[2].toDouble()
                val powVal = base.pow(exp)
                val powStr = formatNumber(powVal)
                currentExpr = currentExpr.replaceFirst(match.value, powStr)

                val (ruleName, ruleNote) = when {
                    exp == 0.0 -> Pair(
                        "Zero Exponent Law: a⁰ = 1",
                        "Any non-zero base raised to the power of 0 equals 1 ($base⁰ = 1)."
                    )
                    exp == 1.0 -> Pair(
                        "Identity Power Law: a¹ = a",
                        "Any base raised to the power of 1 remains unchanged ($base¹ = $base)."
                    )
                    exp == 2.0 -> Pair(
                        "Square Power: a² = a × a",
                        "Squaring base $base: $base × $base = $powStr."
                    )
                    exp == 3.0 -> Pair(
                        "Cube Power: a³ = a × a × a",
                        "Cubing base $base: $base × $base × $base = $powStr."
                    )
                    exp == 0.5 -> Pair(
                        "Fractional Exponent Law: a^(1/2) = √a",
                        "Raising to power 1/2 is the principal square root: √$base = $powStr."
                    )
                    exp < 0.0 -> Pair(
                        "Negative Exponent Law: a⁻ⁿ = 1 / aⁿ",
                        "$base^($exp) = 1 / ($base^${formatNumber(-exp)}) = $powStr."
                    )
                    abs(base - Math.E) < 1.0E-4 -> Pair(
                        "Natural Exponential Function: eˣ",
                        "Base e (Euler's number ≈ 2.71828) raised to power $exp gives $powStr."
                    )
                    else -> Pair(
                        "Exponent Law: aᵇ = exp(b · ln(a))",
                        "Base = $base, Exponent = $exp. Exponentiation yields $powStr."
                    )
                }

                steps.add(
                    EvaluationStep(
                        stepNumber = stepCounter++,
                        title = "Evaluate Power: ${match.groupValues[1]} ^ ${match.groupValues[2]}",
                        subExpression = match.value,
                        ruleApplied = ruleName,
                        intermediateResult = powStr,
                        expressionAfter = currentExpr,
                        note = ruleNote
                    )
                )
            }

            // Step 4: Multiplications, Divisions, Modulo (% / * / /)
            val mulDivRegex = Regex("""(-?\d+(?:\.\d+)?)\s*([*/%])\s*(-?\d+(?:\.\d+)?)""")
            while (mulDivRegex.containsMatchIn(currentExpr)) {
                val match = mulDivRegex.find(currentExpr)!!
                val left = match.groupValues[1].toDouble()
                val op = match.groupValues[2]
                val right = match.groupValues[3].toDouble()

                val subResult = when (op) {
                    "*" -> left * right
                    "/" -> {
                        if (right == 0.0) throw ArithmeticException("Division by zero")
                        left / right
                    }
                    "%" -> left % right
                    else -> 0.0
                }
                val subStr = formatNumber(subResult)
                currentExpr = currentExpr.replaceFirst(match.value, subStr)

                val opName = when (op) {
                    "*" -> "Multiplication"
                    "/" -> "Division"
                    "%" -> "Modulo"
                    else -> "Operation"
                }

                steps.add(
                    EvaluationStep(
                        stepNumber = stepCounter++,
                        title = "Perform $opName: ${match.groupValues[1]} $op ${match.groupValues[3]}",
                        subExpression = match.value,
                        ruleApplied = "PEMDAS: Multiplication & Division evaluated left to right",
                        intermediateResult = subStr,
                        expressionAfter = currentExpr,
                        note = "${match.groupValues[1]} $op ${match.groupValues[3]} = $subStr."
                    )
                )
            }

            // Step 5: Additions & Subtractions
            val addSubRegex = Regex("""(-?\d+(?:\.\d+)?)\s*([+\-])\s*(\d+(?:\.\d+)?)""")
            while (addSubRegex.containsMatchIn(currentExpr) && !isSingleNumber(currentExpr)) {
                val match = addSubRegex.find(currentExpr)!!
                val left = match.groupValues[1].toDouble()
                val op = match.groupValues[2]
                val right = match.groupValues[3].toDouble()

                val subResult = if (op == "+") left + right else left - right
                val subStr = formatNumber(subResult)
                currentExpr = currentExpr.replaceFirst(match.value, subStr)

                val opName = if (op == "+") "Addition" else "Subtraction"
                steps.add(
                    EvaluationStep(
                        stepNumber = stepCounter++,
                        title = "Perform $opName: ${match.groupValues[1]} $op ${match.groupValues[3]}",
                        subExpression = match.value,
                        ruleApplied = "PEMDAS: Addition & Subtraction evaluated left to right",
                        intermediateResult = subStr,
                        expressionAfter = currentExpr,
                        note = "${match.groupValues[1]} $op ${match.groupValues[3]} = $subStr."
                    )
                )
            }

            // Final parse of the remaining value
            val finalVal = currentExpr.toDoubleOrNull() ?: evaluateSimpleExpression(expr, angleMode, xVal, yVal)
            val formattedFinal = formatNumber(finalVal)

            // If no steps were generated, add a summary step
            if (steps.isEmpty()) {
                steps.add(
                    EvaluationStep(
                        stepNumber = 1,
                        title = "Direct Evaluation",
                        subExpression = rawExpression,
                        ruleApplied = "Evaluation",
                        intermediateResult = formattedFinal,
                        expressionAfter = formattedFinal,
                        note = "The expression $rawExpression directly evaluates to $formattedFinal."
                    )
                )
            }

            val alternatives = computeAlternatives(finalVal, angleMode)

            return CalculationResult(
                expression = rawExpression,
                resultNumber = finalVal,
                formattedResult = formattedFinal,
                angleMode = angleMode,
                steps = steps,
                notes = notes,
                alternatives = alternatives,
                isSuccess = true
            )
        } catch (e: Exception) {
            val errorMsg = when {
                e.message?.contains("Division by zero", ignoreCase = true) == true -> "Error: Division by zero"
                e is ArithmeticException -> "Math Error: ${e.message}"
                else -> "Syntax Error: Please check equation syntax"
            }
            return CalculationResult(
                expression = rawExpression,
                resultNumber = Double.NaN,
                formattedResult = errorMsg,
                angleMode = angleMode,
                steps = steps,
                notes = listOf(
                    MathNote(
                        title = "Evaluation Error",
                        content = "The expression could not be fully resolved. Check for unmatched parentheses, missing operands, or invalid domain inputs (like dividing by zero or logarithm of a negative number)."
                    )
                ),
                alternatives = AlternativeRepresentations(scientific = "NaN"),
                isSuccess = false,
                errorMessage = errorMsg
            )
        }
    }

    /**
     * Parses summation syntax like:
     * - sum(x, 1, inf, 3x + 2) or sum(x, 1, 5, 3x+2)
     * - Σ(x, 1, inf, 3x + 2) or Σ(x, 1, 5, 3x+2)
     * - sum(1, 10, x²) or sum(x², 1, 10)
     */
    fun tryParseSummation(raw: String): Triple<String, String, String>? {
        val s = raw.trim()
        val sumPrefixes = listOf("sum(", "Σ(", "sum (", "Σ (")
        val matchPrefix = sumPrefixes.find { s.startsWith(it, ignoreCase = true) } ?: return null
        if (!s.endsWith(")")) return null
        val inner = s.substring(matchPrefix.length, s.length - 1).trim()

        val parts = mutableListOf<String>()
        var depth = 0
        val cur = StringBuilder()
        for (char in inner) {
            when (char) {
                '(', '[', '{' -> { depth++; cur.append(char) }
                ')', ']', '}' -> { depth--; cur.append(char) }
                ',' -> {
                    if (depth == 0) {
                        parts.add(cur.toString().trim())
                        cur.clear()
                    } else {
                        cur.append(char)
                    }
                }
                else -> cur.append(char)
            }
        }
        if (cur.isNotEmpty()) parts.add(cur.toString().trim())

        if (parts.size == 4) {
            // sum(x, 1, inf, 3x + 2) -> (formula = parts[3], start = parts[1], end = parts[2])
            return Triple(parts[3], parts[1], parts[2])
        }
        if (parts.size == 3) {
            val isFirstNum = parts[0].toIntOrNull() != null
            return if (isFirstNum) {
                // sum(1, 5, 3x+2)
                Triple(parts[2], parts[0], parts[1])
            } else {
                // sum(3x+2, 1, 5)
                Triple(parts[0], parts[1], parts[2])
            }
        }
        return null
    }

    private fun isSingleNumber(str: String): Boolean {
        val s = str.trim()
        return s.toDoubleOrNull() != null
    }

    private fun getPrefixFunction(expr: String, parenOpenIdx: Int): String? {
        val fns = listOf(
            "sinh", "cosh", "tanh",
            "asin", "acos", "atan",
            "sin", "cos", "tan",
            "sqrt", "cbrt",
            "log2", "log10", "log", "ln",
            "abs", "ncr", "npr", "gcd", "lcm"
        )
        for (fn in fns) {
            val start = parenOpenIdx - fn.length
            if (start >= 0 && expr.substring(start, parenOpenIdx).equals(fn, ignoreCase = true)) {
                return fn
            }
        }
        return null
    }

    private fun evaluateTwoArgFunction(fn: String, a: Double, b: Double): Double {
        return when (fn.lowercase()) {
            "ncr" -> AdvancedSolvers.combinations(a.toLong(), b.toLong())
            "npr" -> AdvancedSolvers.permutations(a.toLong(), b.toLong())
            "gcd" -> AdvancedSolvers.gcd(a.toLong(), b.toLong()).first.toDouble()
            "lcm" -> AdvancedSolvers.lcm(a.toLong(), b.toLong()).toDouble()
            else -> 0.0
        }
    }

    private fun evaluateFunction(fn: String, arg: Double, angleMode: AngleMode): Double {
        return when (fn.lowercase()) {
            "sin" -> {
                val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
                sin(rad)
            }
            "cos" -> {
                val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
                cos(rad)
            }
            "tan" -> {
                val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
                if (abs(cos(rad)) < 1.0E-14) throw ArithmeticException("Tangent undefined at 90° / π/2")
                tan(rad)
            }
            "asin" -> {
                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error: asin domain is [-1, 1]")
                val resRad = asin(arg)
                if (angleMode == AngleMode.DEG) Math.toDegrees(resRad) else resRad
            }
            "acos" -> {
                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Domain error: acos domain is [-1, 1]")
                val resRad = acos(arg)
                if (angleMode == AngleMode.DEG) Math.toDegrees(resRad) else resRad
            }
            "atan" -> {
                val resRad = atan(arg)
                if (angleMode == AngleMode.DEG) Math.toDegrees(resRad) else resRad
            }
            "sinh" -> sinh(arg)
            "cosh" -> cosh(arg)
            "tanh" -> tanh(arg)
            "ln" -> {
                if (arg <= 0) throw ArithmeticException("Domain error: ln(x) requires x > 0")
                ln(arg)
            }
            "log", "log10" -> {
                if (arg <= 0) throw ArithmeticException("Domain error: log(x) requires x > 0")
                log10(arg)
            }
            "log2" -> {
                if (arg <= 0) throw ArithmeticException("Domain error: log2(x) requires x > 0")
                ln(arg) / ln(2.0)
            }
            "sqrt" -> {
                if (arg < 0) throw ArithmeticException("Domain error: sqrt(x) requires x ≥ 0 in real numbers")
                sqrt(arg)
            }
            "cbrt" -> cbrt(arg)
            "abs" -> abs(arg)
            else -> arg
        }
    }

    private fun factorial(n: Int): Double {
        if (n < 0) throw ArithmeticException("Factorial is undefined for negative integers")
        if (n > 170) throw ArithmeticException("Factorial overflow (n > 170)")
        var res = 1.0
        for (i in 2..n) {
            res *= i
        }
        return res
    }

    private fun insertImplicitMultiplications(expr: String): String {
        var s = expr
        // Constants replacements
        s = s.replace("pi", "${Math.PI}")
        s = s.replace(Regex("""\be\b"""), "${Math.E}")

        // 2(3) -> 2*(3)
        s = s.replace(Regex("""(\d)(\()"""), "$1*$2")
        // )2 -> )*2
        s = s.replace(Regex("""(\))(\d)"""), "$1*$2")
        // )( -> )*(
        s = s.replace(Regex("""(\))(\()"""), "$1*$2")
        // 2x -> 2*x, 2y -> 2*y
        s = s.replace(Regex("""(\d)([xyXY])"""), "$1*$2")
        // xy -> x*y
        s = s.replace(Regex("""([xX])([yY])"""), "$1*$2")
        // x( -> x*(
        s = s.replace(Regex("""([xyXY])(\()"""), "$1*$2")
        // )x -> )*x
        s = s.replace(Regex("""(\))([xyXY])"""), "$1*$2")

        // 2sin( -> 2*sin(
        val fns = "sin|cos|tan|asin|acos|atan|sinh|cosh|tanh|ln|log|sqrt|cbrt|abs|ncr|npr|gcd|lcm"
        s = s.replace(Regex("""(\d)($fns)"""), "$1*$2")
        return s
    }

    /**
     * Evaluates a simple expression containing numbers, variables and basic operators (+, -, *, /, ^)
     */
    fun evaluateSimpleExpression(
        expression: String,
        angleMode: AngleMode,
        xVal: Double = 1.0,
        yVal: Double = 2.0
    ): Double {
        val sanitized = insertImplicitMultiplications(expression)
        val parser = ExpressionParser(sanitized, angleMode, xVal, yVal)
        return parser.parse()
    }

    private fun addConceptNotes(
        expr: String,
        angleMode: AngleMode,
        notes: MutableList<MathNote>,
        xVal: Double,
        yVal: Double
    ) {
        notes.add(
            MathNote(
                title = "Order of Operations (PEMDAS / BODMAS)",
                content = "Mathematical operations are evaluated in strict priority: Parentheses / Brackets first, followed by Exponents and Functions, then Multiplication and Division (left-to-right), and finally Addition and Subtraction (left-to-right).",
                formula = "P → E → MD → AS"
            )
        )

        if (expr.contains("x", ignoreCase = true) || expr.contains("y", ignoreCase = true)) {
            notes.add(
                MathNote(
                    title = "Algebraic Variables (x, y)",
                    content = "Variables represent independent numerical inputs in algebra. The current calculation evaluates the expression at x = ${formatNumber(xVal)} and y = ${formatNumber(yVal)}. You can adjust x and y on the calculator panel to see the function evaluated at different points.",
                    formula = "f(x, y)"
                )
            )
        }

        if (expr.contains("sin", ignoreCase = true) ||
            expr.contains("cos", ignoreCase = true) ||
            expr.contains("tan", ignoreCase = true)
        ) {
            val modeDesc = if (angleMode == AngleMode.DEG) {
                "The calculator is currently in DEG (Degree) mode. Full circle = 360°, right angle = 90°. (e.g., sin(30°) = 0.5)."
            } else {
                "The calculator is currently in RAD (Radian) mode. Full circle = 2π rad, right angle = π/2 rad. (e.g., sin(π/6) = 0.5)."
            }
            notes.add(
                MathNote(
                    title = "Trigonometric Angle Mode: ${angleMode.name}",
                    content = "$modeDesc To switch between degrees and radians, tap the DEG / RAD indicator on the calculator display.",
                    formula = if (angleMode == AngleMode.DEG) "1° = π / 180 rad" else "1 rad = 180° / π"
                )
            )
        }

        if (expr.contains("^")) {
            notes.add(
                MathNote(
                    title = "Exponentiation Laws",
                    content = "The expression a^b raises base a to power b. Common laws: a^m × a^n = a^(m+n), (a^m)^n = a^(m×n), and a^(-n) = 1/(a^n). Any non-zero base to power 0 equals 1 (a^0 = 1).",
                    formula = "a^b = a × a × ... (b times)"
                )
            )
        }

        if (expr.contains("ln", ignoreCase = true) || expr.contains("log", ignoreCase = true)) {
            notes.add(
                MathNote(
                    title = "Logarithmic Properties",
                    content = "Natural log ln(x) has base e (Euler's number ≈ 2.71828). Standard log(x) has base 10. Key identity: log(a × b) = log(a) + log(b), and log(a^b) = b × log(a). Logarithms are only defined for positive real inputs x > 0.",
                    formula = "ln(e^x) = x, log10(10^x) = x"
                )
            )
        }

        if (expr.contains("ncr", ignoreCase = true) || expr.contains("npr", ignoreCase = true)) {
            notes.add(
                MathNote(
                    title = "Combinatorics (Permutations & Combinations)",
                    content = "Permutations nPr counts ordered selections: n! / (n - r)!. Combinations nCr counts unordered selections (subsets): n! / [r!(n - r)!].",
                    formula = "nCr = n! / (r!(n-r)!), nPr = n! / (n-r)!"
                )
            )
        }

        if (expr.contains("gcd", ignoreCase = true) || expr.contains("lcm", ignoreCase = true)) {
            notes.add(
                MathNote(
                    title = "Number Theory (GCD & LCM)",
                    content = "Greatest Common Divisor (GCD) is the largest integer dividing both numbers. Least Common Multiple (LCM) is the smallest positive multiple shared by both. Relationship: a × b = gcd(a, b) × lcm(a, b).",
                    formula = "gcd(a, b) × lcm(a, b) = |a × b|"
                )
            )
        }
    }

    private fun computeAlternatives(value: Double, angleMode: AngleMode): AlternativeRepresentations {
        val scientific = if (value.isNaN() || value.isInfinite()) "N/A" else scientificFormat.format(value)
        val fraction = FractionHelper.toFraction(value)?.toString()
        val percentage = if (value.isFinite()) "${standardFormat.format(value * 100)}%" else null

        // Integer conversions
        var binary: String? = null
        var hex: String? = null
        var octal: String? = null

        if (value.isFinite() && value % 1.0 == 0.0 && abs(value) <= Long.MAX_VALUE.toDouble()) {
            val lVal = value.toLong()
            binary = "0b" + java.lang.Long.toBinaryString(lVal)
            hex = "0x" + java.lang.Long.toHexString(lVal).uppercase(Locale.US)
            octal = "0o" + java.lang.Long.toOctalString(lVal)
        }

        // Angle conversions
        var degToRad: String? = null
        var radToDeg: String? = null
        if (value.isFinite() && abs(value) <= 3600.0) {
            val toRad = Math.toRadians(value)
            degToRad = "${standardFormat.format(toRad)} rad (${standardFormat.format(value / 180.0)}π)"
            val toDeg = Math.toDegrees(value)
            radToDeg = "${standardFormat.format(toDeg)}°"
        }

        return AlternativeRepresentations(
            fraction = fraction,
            scientific = scientific,
            percentage = percentage,
            binary = binary,
            hex = hex,
            octal = octal,
            degreesToRadians = degToRad,
            radiansToDegrees = radToDeg
        )
    }

    /**
     * Recursive descent expression parser for math evaluation
     */
    private class ExpressionParser(
        private val str: String,
        private val angleMode: AngleMode,
        private val xVal: Double,
        private val yVal: Double
    ) {
        private var pos = -1
        private var ch = 0

        private fun nextChar() {
            ch = if (++pos < str.length) str[pos].code else -1
        }

        private fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < str.length) throw RuntimeException("Unexpected character: " + ch.toChar())
            return x
        }

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.code) -> x += parseTerm()
                    eat('-'.code) -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        private fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw ArithmeticException("Division by zero")
                        x /= divisor
                    }
                    eat('%'.code) -> x %= parseFactor()
                    else -> return x
                }
            }
        }

        private fun parseFactor(): Double {
            if (eat('+'.code)) return +parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = pos
            if (eat('('.code)) {
                x = parseExpression()
                eat(')'.code)
            } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                x = str.substring(startPos, pos).toDouble()
            } else if (ch in 'a'.code..'z'.code || ch in 'A'.code..'Z'.code) {
                while (ch in 'a'.code..'z'.code || ch in 'A'.code..'Z'.code || ch in '0'.code..'9'.code) nextChar()
                val func = str.substring(startPos, pos)
                if (eat('('.code)) {
                    val arg = parseExpression()
                    if (eat(','.code)) {
                        val arg2 = parseExpression()
                        eat(')'.code)
                        x = evaluateTwoArgFunction(func, arg, arg2)
                    } else {
                        eat(')'.code)
                        x = evaluateFunction(func, arg, angleMode)
                    }
                } else {
                    x = when (func.lowercase()) {
                        "pi" -> Math.PI
                        "e" -> Math.E
                        "x" -> xVal
                        "y" -> yVal
                        else -> throw RuntimeException("Unknown function, variable or constant: $func")
                    }
                }
            } else {
                throw RuntimeException("Unexpected character: " + if (ch != -1) ch.toChar() else "EOF")
            }

            // Postfix factorial
            if (eat('!'.code)) {
                x = factorial(x.toInt())
            }

            // Exponentiation
            if (eat('^'.code)) {
                x = x.pow(parseFactor())
            }

            return x
        }
    }
}
