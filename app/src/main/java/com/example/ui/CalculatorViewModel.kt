package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CalculationEntity
import com.example.math.*
import com.example.units.ConversionResult
import com.example.units.ConversionUnit
import com.example.units.UnitCategory
import com.example.units.UnitConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.AccentPalette
import com.example.ui.theme.ThemeMode

enum class AppNavDestination {
    CALCULATOR,
    ADVANCED_MATH,
    UNIT_CONVERTER,
    HISTORY,
    FORMULAS,
    SETTINGS
}

enum class AdvancedMathTab {
    QUADRATIC,
    LINEAR_SYSTEM,
    SUMMATION,
    NUMBER_THEORY,
    SEQUENCES,
    EXPONENTIAL,
    CALCULUS,
    STATISTICS
}

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.calculationDao()

    val historyList: StateFlow<List<CalculationEntity>> = dao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val prefs: SharedPreferences = application.getSharedPreferences("calcmaster_settings", Context.MODE_PRIVATE)

    // Settings & Customization
    private val _themeMode = MutableStateFlow(
        try {
            ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name)
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _accentPalette = MutableStateFlow(
        try {
            AccentPalette.valueOf(prefs.getString("accent_palette", AccentPalette.DEFAULT_BLUE.name) ?: AccentPalette.DEFAULT_BLUE.name)
        } catch (_: Exception) {
            AccentPalette.DEFAULT_BLUE
        }
    )
    val accentPalette: StateFlow<AccentPalette> = _accentPalette.asStateFlow()

    private val _decimalPrecision = MutableStateFlow(prefs.getInt("decimal_precision", 8))
    val decimalPrecision: StateFlow<Int> = _decimalPrecision.asStateFlow()

    private val _vibrationFeedback = MutableStateFlow(prefs.getBoolean("vibration_feedback", true))
    val vibrationFeedback: StateFlow<Boolean> = _vibrationFeedback.asStateFlow()

    private val _showLivePreview = MutableStateFlow(prefs.getBoolean("show_live_preview", true))
    val showLivePreview: StateFlow<Boolean> = _showLivePreview.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    fun setAccentPalette(palette: AccentPalette) {
        _accentPalette.value = palette
        prefs.edit().putString("accent_palette", palette.name).apply()
    }

    fun setDecimalPrecision(precision: Int) {
        _decimalPrecision.value = precision
        prefs.edit().putInt("decimal_precision", precision).apply()
    }

    fun toggleVibrationFeedback() {
        val next = !_vibrationFeedback.value
        _vibrationFeedback.value = next
        prefs.edit().putBoolean("vibration_feedback", next).apply()
    }

    fun toggleShowLivePreview() {
        val next = !_showLivePreview.value
        _showLivePreview.value = next
        prefs.edit().putBoolean("show_live_preview", next).apply()
    }

    // Navigation State
    private val _currentDestination = MutableStateFlow(AppNavDestination.CALCULATOR)
    val currentDestination: StateFlow<AppNavDestination> = _currentDestination.asStateFlow()

    fun setDestination(dest: AppNavDestination) {
        _currentDestination.value = dest
    }

    // --- Calculator State ---
    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _livePreview = MutableStateFlow<String?>(null)
    val livePreview: StateFlow<String?> = _livePreview.asStateFlow()

    private val _angleMode = MutableStateFlow(AngleMode.DEG)
    val angleMode: StateFlow<AngleMode> = _angleMode.asStateFlow()

    private val _isInverse = MutableStateFlow(false)
    val isInverse: StateFlow<Boolean> = _isInverse.asStateFlow()

    private val _isScientificExpanded = MutableStateFlow(true)
    val isScientificExpanded: StateFlow<Boolean> = _isScientificExpanded.asStateFlow()

    private val _calculationResult = MutableStateFlow<CalculationResult?>(null)
    val calculationResult: StateFlow<CalculationResult?> = _calculationResult.asStateFlow()

    private val _showSolutionDialog = MutableStateFlow(false)
    val showSolutionDialog: StateFlow<Boolean> = _showSolutionDialog.asStateFlow()

    private val _memoryValue = MutableStateFlow(0.0)
    val memoryValue: StateFlow<Double> = _memoryValue.asStateFlow()

    private val _hasMemory = MutableStateFlow(false)
    val hasMemory: StateFlow<Boolean> = _hasMemory.asStateFlow()

    // Variable X and Y states
    private val _xVariable = MutableStateFlow(2.0)
    val xVariable: StateFlow<Double> = _xVariable.asStateFlow()

    private val _yVariable = MutableStateFlow(3.0)
    val yVariable: StateFlow<Double> = _yVariable.asStateFlow()

    fun setXVariable(v: Double) {
        _xVariable.value = v
        computeLivePreview()
        if (_calculationResult.value != null) onEvaluate()
    }

    fun setYVariable(v: Double) {
        _yVariable.value = v
        computeLivePreview()
        if (_calculationResult.value != null) onEvaluate()
    }

    // --- Advanced Math Solvers State ---
    private val _advancedTab = MutableStateFlow(AdvancedMathTab.QUADRATIC)
    val advancedTab: StateFlow<AdvancedMathTab> = _advancedTab.asStateFlow()

    fun setAdvancedTab(tab: AdvancedMathTab) {
        _advancedTab.value = tab
    }

    // Quadratic
    val quadA = MutableStateFlow("1")
    val quadB = MutableStateFlow("-5")
    val quadC = MutableStateFlow("6")
    private val _quadSolution = MutableStateFlow<AdvancedSolvers.QuadraticSolution?>(null)
    val quadSolution: StateFlow<AdvancedSolvers.QuadraticSolution?> = _quadSolution.asStateFlow()

    fun solveQuadratic() {
        val a = quadA.value.toDoubleOrNull() ?: 1.0
        val b = quadB.value.toDoubleOrNull() ?: 0.0
        val c = quadC.value.toDoubleOrNull() ?: 0.0
        val sol = AdvancedSolvers.solveQuadratic(a, b, c)
        _quadSolution.value = sol

        // Save to history
        viewModelScope.launch {
            dao.insertCalculation(
                CalculationEntity(
                    expression = "${AdvancedSolvers.format(a)}x² + (${AdvancedSolvers.format(b)})x + (${AdvancedSolvers.format(c)}) = 0",
                    result = "x₁=${sol.root1Str}, x₂=${sol.root2Str}",
                    angleMode = "QUAD"
                )
            )
        }
    }

    // Linear System 2x2
    val sysA1 = MutableStateFlow("2")
    val sysB1 = MutableStateFlow("1")
    val sysC1 = MutableStateFlow("8")
    val sysA2 = MutableStateFlow("1")
    val sysB2 = MutableStateFlow("-1")
    val sysC2 = MutableStateFlow("1")
    private val _systemSolution = MutableStateFlow<AdvancedSolvers.LinearSystemSolution?>(null)
    val systemSolution: StateFlow<AdvancedSolvers.LinearSystemSolution?> = _systemSolution.asStateFlow()

    fun solveLinearSystem() {
        val a1 = sysA1.value.toDoubleOrNull() ?: 1.0
        val b1 = sysB1.value.toDoubleOrNull() ?: 1.0
        val c1 = sysC1.value.toDoubleOrNull() ?: 0.0
        val a2 = sysA2.value.toDoubleOrNull() ?: 1.0
        val b2 = sysB2.value.toDoubleOrNull() ?: -1.0
        val c2 = sysC2.value.toDoubleOrNull() ?: 0.0
        val sol = AdvancedSolvers.solveLinearSystem(a1, b1, c1, a2, b2, c2)
        _systemSolution.value = sol

        viewModelScope.launch {
            dao.insertCalculation(
                CalculationEntity(
                    expression = "{ ${sysA1.value}x+${sysB1.value}y=${sysC1.value}; ${sysA2.value}x+${sysB2.value}y=${sysC2.value} }",
                    result = "x=${sol.xResult}, y=${sol.yResult}",
                    angleMode = "SYS"
                )
            )
        }
    }

    // Summation
    val sumFormula = MutableStateFlow("x²")
    val sumStart = MutableStateFlow("1")
    val sumEnd = MutableStateFlow("5")
    private val _summationResult = MutableStateFlow<AdvancedSolvers.SummationResult?>(null)
    val summationResult: StateFlow<AdvancedSolvers.SummationResult?> = _summationResult.asStateFlow()

    fun solveSummation() {
        val f = sumFormula.value.ifBlank { "x" }
        val s = sumStart.value.ifBlank { "1" }
        val e = sumEnd.value.ifBlank { "5" }
        val sol = AdvancedSolvers.evaluateSummation(f, s, e, _angleMode.value)
        _summationResult.value = sol

        viewModelScope.launch {
            dao.insertCalculation(
                CalculationEntity(
                    expression = "Σ (x=$s to $e) of ($f)",
                    result = sol.formattedSum,
                    angleMode = _angleMode.value.name
                )
            )
        }
    }

    // Number Theory (GCF, LCM, Primes)
    val ntNumA = MutableStateFlow("24")
    val ntNumB = MutableStateFlow("36")
    private val _numberTheoryResult = MutableStateFlow<AdvancedSolvers.NumberTheorySolution?>(null)
    val numberTheoryResult: StateFlow<AdvancedSolvers.NumberTheorySolution?> = _numberTheoryResult.asStateFlow()

    fun solveNumberTheory() {
        val a = ntNumA.value.toLongOrNull() ?: 24L
        val b = ntNumB.value.toLongOrNull() ?: 36L
        val sol = AdvancedSolvers.solveNumberTheory(a, b)
        _numberTheoryResult.value = sol

        viewModelScope.launch {
            dao.insertCalculation(
                CalculationEntity(
                    expression = "GCF($a, $b) & LCM($a, $b)",
                    result = "GCF=${sol.gcf}, LCM=${sol.lcm}",
                    angleMode = "NT"
                )
            )
        }
    }

    // Sequences (Arithmetic & Geometric)
    val seqFirstTerm = MutableStateFlow("3")
    val seqDiffOrRatio = MutableStateFlow("5")
    val seqTargetN = MutableStateFlow("10")
    val isSeqGeometric = MutableStateFlow(false)
    private val _sequenceResult = MutableStateFlow<AdvancedSolvers.SequenceSolution?>(null)
    val sequenceResult: StateFlow<AdvancedSolvers.SequenceSolution?> = _sequenceResult.asStateFlow()

    fun solveSequence() {
        val a1 = seqFirstTerm.value.toDoubleOrNull() ?: 1.0
        val factor = seqDiffOrRatio.value.toDoubleOrNull() ?: 2.0
        val n = seqTargetN.value.toIntOrNull() ?: 10
        val sol = if (isSeqGeometric.value) {
            AdvancedSolvers.solveGeometricSequence(a1, factor, n)
        } else {
            AdvancedSolvers.solveArithmeticSequence(a1, factor, n)
        }
        _sequenceResult.value = sol

        viewModelScope.launch {
            dao.insertCalculation(
                CalculationEntity(
                    expression = "${sol.sequenceType} (a1=$a1, factor=$factor, n=$n)",
                    result = "a_$n=${AdvancedSolvers.format(sol.nthTerm)}, S_$n=${AdvancedSolvers.format(sol.sumNTerms)}",
                    angleMode = "SEQ"
                )
            )
        }
    }

    // Calculus
    val calcFormula = MutableStateFlow("x³ - 2x + 1")
    val calcPoint = MutableStateFlow("2")
    val calcLower = MutableStateFlow("0")
    val calcUpper = MutableStateFlow("3")
    private val _calculusResult = MutableStateFlow<AdvancedSolvers.CalculusResult?>(null)
    val calculusResult: StateFlow<AdvancedSolvers.CalculusResult?> = _calculusResult.asStateFlow()

    fun solveDerivative() {
        val f = calcFormula.value.ifBlank { "x" }
        val pt = calcPoint.value.toDoubleOrNull() ?: 0.0
        val sol = AdvancedSolvers.numericalDerivative(f, pt, _angleMode.value)
        _calculusResult.value = sol

        viewModelScope.launch {
            dao.insertCalculation(
                CalculationEntity(
                    expression = "d/dx [$f] at x=$pt",
                    result = sol.formattedResult,
                    angleMode = _angleMode.value.name
                )
            )
        }
    }

    fun solveIntegral() {
        val f = calcFormula.value.ifBlank { "x" }
        val a = calcLower.value.toDoubleOrNull() ?: 0.0
        val b = calcUpper.value.toDoubleOrNull() ?: 1.0
        val sol = AdvancedSolvers.numericalIntegral(f, a, b, _angleMode.value)
        _calculusResult.value = sol

        viewModelScope.launch {
            dao.insertCalculation(
                CalculationEntity(
                    expression = "∫_{$a}^{$b} ($f) dx",
                    result = sol.formattedResult,
                    angleMode = _angleMode.value.name
                )
            )
        }
    }

    // Statistics
    val statsInput = MutableStateFlow("10, 14, 18, 22, 26, 30, 35")
    private val _statsResult = MutableStateFlow<AdvancedSolvers.StatisticsSummary?>(null)
    val statsResult: StateFlow<AdvancedSolvers.StatisticsSummary?> = _statsResult.asStateFlow()

    fun solveStatistics() {
        val nums = statsInput.value.split(",")
            .mapNotNull { it.trim().toDoubleOrNull() }
        val sol = AdvancedSolvers.calculateStatistics(nums)
        _statsResult.value = sol
    }

    // Exponential Equation: a · bˣ = c
    val expA = MutableStateFlow("1")
    val expBase = MutableStateFlow("2")
    val expC = MutableStateFlow("16")
    private val _exponentialSolution = MutableStateFlow<MathKnowledge.ExponentialSolution?>(null)
    val exponentialSolution: StateFlow<MathKnowledge.ExponentialSolution?> = _exponentialSolution.asStateFlow()

    fun solveExponential() {
        val a = expA.value.toDoubleOrNull() ?: 1.0
        val base = expBase.value.toDoubleOrNull() ?: 2.0
        val c = expC.value.toDoubleOrNull() ?: 16.0
        val sol = MathKnowledge.solveExponentialEquation(a, base, c)
        _exponentialSolution.value = sol

        viewModelScope.launch {
            dao.insertCalculation(
                CalculationEntity(
                    expression = "${expA.value} · (${expBase.value})ˣ = ${expC.value}",
                    result = "x = ${sol.formattedResult}",
                    angleMode = "EXP"
                )
            )
        }
    }

    // --- Unit Converter State ---
    private val _selectedCategory = MutableStateFlow(UnitCategory.LENGTH)
    val selectedCategory: StateFlow<UnitCategory> = _selectedCategory.asStateFlow()

    private val _converterInput = MutableStateFlow("1")
    val converterInput: StateFlow<String> = _converterInput.asStateFlow()

    private val _fromUnit = MutableStateFlow(UnitConverter.units[UnitCategory.LENGTH]!![0])
    val fromUnit: StateFlow<ConversionUnit> = _fromUnit.asStateFlow()

    private val _toUnit = MutableStateFlow(UnitConverter.units[UnitCategory.LENGTH]!![1])
    val toUnit: StateFlow<ConversionUnit> = _toUnit.asStateFlow()

    private val _conversionResult = MutableStateFlow<ConversionResult?>(null)
    val conversionResult: StateFlow<ConversionResult?> = _conversionResult.asStateFlow()

    init {
        updateConversion()
        solveQuadratic()
        solveLinearSystem()
        solveSummation()
        solveExponential()
        solveStatistics()
    }

    // Exponent/Power Mode (when active, typed numbers become superscripts: ⁰¹²³⁴⁵⁶⁷⁸⁹)
    private val _isPowerMode = MutableStateFlow(false)
    val isPowerMode: StateFlow<Boolean> = _isPowerMode.asStateFlow()

    fun togglePowerMode() {
        _isPowerMode.value = !_isPowerMode.value
    }

    // --- Calculator Actions ---

    fun onAppend(text: String) {
        val curr = _expression.value

        val digitToSup = mapOf(
            "0" to "⁰", "1" to "¹", "2" to "²", "3" to "³", "4" to "⁴",
            "5" to "⁵", "6" to "⁶", "7" to "⁷", "8" to "⁸", "9" to "⁹",
            "-" to "⁻", "+" to "⁺", "x" to "ˣ", "y" to "ʸ", "n" to "ⁿ"
        )

        var toAppend = text
        if (_isPowerMode.value) {
            if (digitToSup.containsKey(text)) {
                toAppend = digitToSup[text] ?: text
            } else if (text in listOf("×", "÷", "−", "+", "=", "%", "(", ")")) {
                // Exit power mode upon typing a standard operator
                _isPowerMode.value = false
            }
        }

        // If we just evaluated an expression and user taps a number, start fresh
        if (_calculationResult.value != null && isDigitOrConst(text)) {
            _expression.value = toAppend
            _calculationResult.value = null
        } else if (_calculationResult.value != null && isOperator(text)) {
            val res = _calculationResult.value?.formattedResult ?: ""
            if (!res.startsWith("Error")) {
                _expression.value = res + toAppend
            } else {
                _expression.value = toAppend
            }
            _calculationResult.value = null
        } else {
            _expression.value = curr + toAppend
        }
        computeLivePreview()
    }

    private fun isDigitOrConst(text: String): Boolean {
        return text in listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "π", "e", "x", "y")
    }

    private fun isOperator(text: String): Boolean {
        return text in listOf("+", "−", "×", "÷", "%", "^")
    }

    fun onClear() {
        _expression.value = ""
        _livePreview.value = null
        _calculationResult.value = null
        _isPowerMode.value = false
    }

    fun onDelete() {
        val curr = _expression.value
        if (curr.isNotEmpty()) {
            if (curr.endsWith("    ")) {
                _expression.value = curr.dropLast(4)
            } else if (curr.endsWith("  ")) {
                _expression.value = curr.dropLast(2)
            } else if (curr.endsWith("\n")) {
                _expression.value = curr.dropLast(1)
            } else {
                val fns = listOf(
                    " = ", "= ", "=", "asin(", "acos(", "atan(", "sinh(", "cosh(", "tanh(",
                    "sin(", "cos(", "tan(", "sqrt(", "cbrt(", "log2(", "log(", "ln(", "abs(",
                    "nCr(", "nPr(", "gcd(", "lcm(", "√(", "∛("
                )
                var matchedFn = false
                for (fn in fns) {
                    if (curr.endsWith(fn)) {
                        _expression.value = curr.dropLast(fn.length)
                        matchedFn = true
                        break
                    }
                }
                if (!matchedFn) {
                    _expression.value = curr.dropLast(1)
                }
            }
        }
        computeLivePreview()
    }

    /**
     * Inserts a newline (Enter / ↵) to start a new line in a multi-line expression.
     */
    fun onEnter() {
        val curr = _expression.value
        if (curr.isNotEmpty() && !curr.endsWith("\n")) {
            _expression.value = "$curr\n"
            computeLivePreview()
        }
    }

    /**
     * Inserts a 2-space indentation (Indent / ⇥) for multi-line expressions.
     */
    fun onIndent() {
        val curr = _expression.value
        _expression.value = "$curr  "
        computeLivePreview()
    }

    fun toggleAngleMode() {
        val newMode = if (_angleMode.value == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
        _angleMode.value = newMode
        computeLivePreview()
        if (_expression.value.isNotBlank() && _calculationResult.value != null) {
            onEvaluate()
        }
    }

    fun toggleInverse() {
        _isInverse.value = !_isInverse.value
    }

    fun toggleScientificKeyboard() {
        _isScientificExpanded.value = !_isScientificExpanded.value
    }

    fun openSolutionSheet() {
        _showSolutionDialog.value = true
    }

    fun closeSolutionSheet() {
        _showSolutionDialog.value = false
    }

    /**
     * Handles '=' press: allows typing '=' as an expression to write equations directly,
     * evaluates when expression ends with '=' or is a completed equation.
     */
    fun onEqualsPress() {
        val expr = _expression.value.trim()
        if (expr.isEmpty()) return

        if (!expr.contains("=")) {
            // Write '=' into expression directly so users can type equations (e.g., 2x + 4 = 10)
            val spacePrefix = if (_expression.value.endsWith(" ")) "" else " "
            onAppend("$spacePrefix= ")
        } else {
            // Expression already has '='
            if (expr.endsWith("=")) {
                // User entered trailing '=' (e.g., "5 + 3 = ") and pressed '=' again -> commit answer!
                val lhs = expr.dropLast(1).trim()
                if (lhs.isNotEmpty()) {
                    onEvaluate()
                    val res = _calculationResult.value?.formattedResult
                    if (res != null && !res.startsWith("Error")) {
                        _expression.value = res.removePrefix("=").trim()
                        _calculationResult.value = null
                        computeLivePreview()
                    }
                }
            } else {
                // Full equation like 2x + 4 = 10 -> solve and store to history
                onEvaluate()
            }
        }
    }

    /**
     * Commits current live preview or evaluated result as the new base expression
     */
    fun commitResult() {
        val res = _calculationResult.value?.formattedResult ?: _livePreview.value
        if (res != null && !res.startsWith("Error")) {
            val clean = res.removePrefix("=").trim()
            if (clean.isNotEmpty()) {
                onEvaluate()
                _expression.value = clean
                _calculationResult.value = null
                computeLivePreview()
            }
        }
    }

    fun onEvaluate() {
        val expr = _expression.value.trim()
        if (expr.isBlank()) return

        val result = MathEngine.evaluateWithExplanation(
            rawExpression = expr,
            angleMode = _angleMode.value,
            xVal = _xVariable.value,
            yVal = _yVariable.value
        )
        _calculationResult.value = result
        _livePreview.value = null

        if (result.isSuccess && !result.formattedResult.startsWith("Error")) {
            viewModelScope.launch {
                dao.insertCalculation(
                    CalculationEntity(
                        expression = expr,
                        result = result.formattedResult,
                        angleMode = _angleMode.value.name
                    )
                )
            }
        }
    }

    private fun computeLivePreview() {
        val expr = _expression.value.trim()
        if (expr.isNotEmpty() && !expr.endsWith("+") && !expr.endsWith("−") && !expr.endsWith("×") && !expr.endsWith("÷")) {
            try {
                if (expr.endsWith("=")) {
                    val sub = expr.dropLast(1).trim()
                    if (sub.isNotEmpty()) {
                        val eval = MathEngine.evaluateSimpleExpression(
                            expression = MathEngine.normalizeExpression(sub),
                            angleMode = _angleMode.value,
                            xVal = _xVariable.value,
                            yVal = _yVariable.value
                        )
                        if (eval.isFinite()) {
                            _livePreview.value = "= " + MathEngine.formatNumber(eval)
                            return
                        }
                    }
                } else if (expr.contains("=")) {
                    val preview = MathEngine.solveEquationQuickPreview(
                        rawEquation = expr,
                        angleMode = _angleMode.value,
                        defaultX = _xVariable.value,
                        defaultY = _yVariable.value
                    )
                    if (preview != null) {
                        _livePreview.value = preview
                        return
                    }
                } else {
                    val eval = MathEngine.evaluateSimpleExpression(
                        expression = MathEngine.normalizeExpression(expr),
                        angleMode = _angleMode.value,
                        xVal = _xVariable.value,
                        yVal = _yVariable.value
                    )
                    if (eval.isFinite()) {
                        _livePreview.value = "= " + MathEngine.formatNumber(eval)
                        return
                    }
                }
            } catch (_: Exception) {}
        }
        _livePreview.value = null
    }

    // Memory operations
    fun memoryAdd() {
        val currentVal = getCurrentNumericValue() ?: return
        _memoryValue.value += currentVal
        _hasMemory.value = true
    }

    fun memorySubtract() {
        val currentVal = getCurrentNumericValue() ?: return
        _memoryValue.value -= currentVal
        _hasMemory.value = true
    }

    fun memoryRecall() {
        if (_hasMemory.value) {
            onAppend(MathEngine.formatNumber(_memoryValue.value))
        }
    }

    fun memoryClear() {
        _memoryValue.value = 0.0
        _hasMemory.value = false
    }

    fun memoryStore() {
        val currentVal = getCurrentNumericValue() ?: return
        _memoryValue.value = currentVal
        _hasMemory.value = true
    }

    private fun getCurrentNumericValue(): Double? {
        val res = _calculationResult.value?.resultNumber
        if (res != null && res.isFinite()) return res
        val expr = _expression.value
        return expr.toDoubleOrNull()
    }

    // History actions
    fun loadFromHistory(item: CalculationEntity) {
        _expression.value = item.expression
        _angleMode.value = if (item.angleMode == "RAD") AngleMode.RAD else AngleMode.DEG
        onEvaluate()
        _currentDestination.value = AppNavDestination.CALCULATOR
    }

    fun clearHistory() {
        viewModelScope.launch {
            dao.clearHistory()
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            dao.deleteById(id)
        }
    }

    // --- Unit Converter Actions ---

    fun selectCategory(cat: UnitCategory) {
        _selectedCategory.value = cat
        val unitList = UnitConverter.units[cat] ?: emptyList()
        if (unitList.isNotEmpty()) {
            _fromUnit.value = unitList[0]
            _toUnit.value = if (unitList.size > 1) unitList[1] else unitList[0]
        }
        updateConversion()
    }

    fun setFromUnit(unit: ConversionUnit) {
        _fromUnit.value = unit
        updateConversion()
    }

    fun setToUnit(unit: ConversionUnit) {
        _toUnit.value = unit
        updateConversion()
    }

    fun swapUnits() {
        val temp = _fromUnit.value
        _fromUnit.value = _toUnit.value
        _toUnit.value = temp
        updateConversion()
    }

    fun setConverterInput(input: String) {
        if (input.isEmpty() || input == "-" || input == "." || input == "-.") {
            _converterInput.value = input
            return
        }
        if (input.toDoubleOrNull() != null) {
            _converterInput.value = input
            updateConversion()
        }
    }

    fun onConverterNumpad(key: String) {
        val current = _converterInput.value
        when (key) {
            "C" -> {
                _converterInput.value = "0"
                updateConversion()
            }
            "DEL" -> {
                if (current.length <= 1 || (current.length == 2 && current.startsWith("-"))) {
                    _converterInput.value = "0"
                } else {
                    _converterInput.value = current.dropLast(1)
                }
                updateConversion()
            }
            "±" -> {
                _converterInput.value = if (current.startsWith("-")) current.substring(1) else "-$current"
                updateConversion()
            }
            "." -> {
                if (!current.contains(".")) {
                    _converterInput.value = "$current."
                    updateConversion()
                }
            }
            else -> {
                if (current == "0") {
                    _converterInput.value = key
                } else {
                    _converterInput.value = current + key
                }
                updateConversion()
            }
        }
    }

    private fun updateConversion() {
        val num = _converterInput.value.toDoubleOrNull() ?: 0.0
        _conversionResult.value = UnitConverter.convert(num, _fromUnit.value, _toUnit.value)
    }
}
