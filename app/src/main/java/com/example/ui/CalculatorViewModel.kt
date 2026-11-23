package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CalculationEntity
import com.example.math.*
import com.example.science.ScienceSolvers
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

enum class CalculatorContextMode(val label: String) {
    MATH("Math"),
    PHYSICS("Physics"),
    CHEMISTRY("Chemistry")
}

enum class AdvancedMathTab(
    val title: String,
    val subtitle: String,
    val category: String
) {
    SUMMATION("Summation (Σ)", "Finite & infinite series", "Calculus & Series"),
    CIRCLES("Circles & Sectors", "Radius, area, arc & chord", "Geometry & Trig"),
    TRIGONOMETRY("Trigonometry & Triangles", "6 ratios, unit circle & SOH-CAH-TOA", "Geometry & Trig"),
    SOLIDS_3D("3D Solids Geometry", "Sphere, cylinder, cone & prism", "Geometry & Trig"),
    QUADRATIC("Quadratic Equation", "ax² + bx + c = 0 roots & vertex", "Algebra & Logs"),
    LINEAR_SYSTEM("2×2 Linear System", "Simultaneous equations (Cramer's)", "Algebra & Logs"),
    EXPONENTIAL("Exponential (a·bˣ = c)", "Logarithmic reduction solver", "Algebra & Logs"),
    LOGARITHM("Logarithm (log_b x)", "Arbitrary base & change of base", "Algebra & Logs"),
    CALCULUS("Calculus (∫ & d/dx)", "Definite integral & derivative", "Calculus & Series"),
    SEQUENCES("Sequences (A.P. & G.P.)", "nth term & partial sum", "Calculus & Series"),
    NUMBER_THEORY("GCF & LCM", "Euclidean steps & prime factors", "Discrete & Stats"),
    COMBINATORICS("Combinatorics (nPr & nCr)", "Permutations & combinations", "Discrete & Stats"),
    STATISTICS("Statistics", "Mean, median, mode & std dev", "Discrete & Stats"),
    PHYSICS_EXPRESSIONS("Physics Expressions", "Circular, force, momentum, gravity & circuits", "Science & Physics")
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

    // --- Circles & Sectors State ---
    val circleInputType = MutableStateFlow(GeometryTrigSolvers.CircleInputType.RADIUS)
    val circleValue = MutableStateFlow("5")
    val circleAngle = MutableStateFlow("60")
    val circleAngleMode = MutableStateFlow(AngleMode.DEG)
    private val _circleSolution = MutableStateFlow<GeometryTrigSolvers.CircleSolution?>(null)
    val circleSolution: StateFlow<GeometryTrigSolvers.CircleSolution?> = _circleSolution.asStateFlow()

    fun solveCircle(logHistory: Boolean = false) {
        val v = circleValue.value.toDoubleOrNull() ?: 0.0
        val ang = circleAngle.value.toDoubleOrNull() ?: 60.0
        val sol = GeometryTrigSolvers.solveCircle(
            inputType = circleInputType.value,
            inputValue = v,
            centralAngleInput = ang,
            angleMode = circleAngleMode.value
        )
        _circleSolution.value = sol
        if (logHistory && sol.isValid) {
            viewModelScope.launch {
                dao.insertCalculation(
                    CalculationEntity(
                        expression = "Circle (${circleInputType.value.symbol}=${circleValue.value}, θ=${circleAngle.value})",
                        result = "r=${AdvancedSolvers.format(sol.radius)}, A=${AdvancedSolvers.format(sol.area)}",
                        angleMode = circleAngleMode.value.name
                    )
                )
            }
        }
    }

    // --- Trigonometry & Triangles State ---
    // Sub-tab: 0 = Unit Circle & Ratios, 1 = Right Triangle, 2 = General Triangle
    val trigSubMode = MutableStateFlow(0)
    val trigAngleInput = MutableStateFlow("45")
    val trigAngleMode = MutableStateFlow(AngleMode.DEG)
    private val _trigAngleSolution = MutableStateFlow<GeometryTrigSolvers.TrigAngleSolution?>(null)
    val trigAngleSolution: StateFlow<GeometryTrigSolvers.TrigAngleSolution?> = _trigAngleSolution.asStateFlow()

    val rightTriMode = MutableStateFlow(GeometryTrigSolvers.RightTriMode.TWO_LEGS)
    val rightTriVal1 = MutableStateFlow("3")
    val rightTriVal2 = MutableStateFlow("4")
    private val _rightTriSolution = MutableStateFlow<GeometryTrigSolvers.RightTriangleSolution?>(null)
    val rightTriSolution: StateFlow<GeometryTrigSolvers.RightTriangleSolution?> = _rightTriSolution.asStateFlow()

    val generalTriMode = MutableStateFlow(GeometryTrigSolvers.GeneralTriMode.SSS)
    val generalTriVal1 = MutableStateFlow("5")
    val generalTriVal2 = MutableStateFlow("7")
    val generalTriVal3 = MutableStateFlow("8")
    private val _generalTriSolution = MutableStateFlow<GeometryTrigSolvers.GeneralTriangleSolution?>(null)
    val generalTriSolution: StateFlow<GeometryTrigSolvers.GeneralTriangleSolution?> = _generalTriSolution.asStateFlow()

    fun solveTrigAngle(logHistory: Boolean = false) {
        val ang = trigAngleInput.value.toDoubleOrNull() ?: 0.0
        val sol = GeometryTrigSolvers.solveTrigAngle(ang, trigAngleMode.value)
        _trigAngleSolution.value = sol
        if (logHistory) {
            viewModelScope.launch {
                dao.insertCalculation(
                    CalculationEntity(
                        expression = "Trig(${trigAngleInput.value} ${trigAngleMode.value.name})",
                        result = "sin=${AdvancedSolvers.format(sol.sinVal)}, cos=${AdvancedSolvers.format(sol.cosVal)}",
                        angleMode = trigAngleMode.value.name
                    )
                )
            }
        }
    }

    fun solveRightTriangle(logHistory: Boolean = false) {
        val v1 = rightTriVal1.value.toDoubleOrNull() ?: 0.0
        val v2 = rightTriVal2.value.toDoubleOrNull() ?: 0.0
        val sol = GeometryTrigSolvers.solveRightTriangle(rightTriMode.value, v1, v2)
        _rightTriSolution.value = sol
        if (logHistory && sol.isValid) {
            viewModelScope.launch {
                dao.insertCalculation(
                    CalculationEntity(
                        expression = "Right △ (${rightTriMode.value.label}: ${rightTriVal1.value}, ${rightTriVal2.value})",
                        result = "a=${AdvancedSolvers.format(sol.a)}, b=${AdvancedSolvers.format(sol.b)}, c=${AdvancedSolvers.format(sol.c)}",
                        angleMode = "DEG"
                    )
                )
            }
        }
    }

    fun solveGeneralTriangle(logHistory: Boolean = false) {
        val v1 = generalTriVal1.value.toDoubleOrNull() ?: 0.0
        val v2 = generalTriVal2.value.toDoubleOrNull() ?: 0.0
        val v3 = generalTriVal3.value.toDoubleOrNull() ?: 0.0
        val sol = GeometryTrigSolvers.solveGeneralTriangle(generalTriMode.value, v1, v2, v3)
        _generalTriSolution.value = sol
        if (logHistory && sol.isValid) {
            viewModelScope.launch {
                dao.insertCalculation(
                    CalculationEntity(
                        expression = "Triangle ${generalTriMode.value.name} (${generalTriVal1.value}, ${generalTriVal2.value}, ${generalTriVal3.value})",
                        result = "A=${AdvancedSolvers.format(sol.angleADeg)}°, Area=${AdvancedSolvers.format(sol.area)}",
                        angleMode = "DEG"
                    )
                )
            }
        }
    }

    // --- 3D Solids State ---
    val solidType = MutableStateFlow(GeometryTrigSolvers.SolidType.SPHERE)
    val solidDim1 = MutableStateFlow("4")
    val solidDim2 = MutableStateFlow("10")
    val solidDim3 = MutableStateFlow("6")
    private val _solidSolution = MutableStateFlow<GeometryTrigSolvers.Solid3DSolution?>(null)
    val solidSolution: StateFlow<GeometryTrigSolvers.Solid3DSolution?> = _solidSolution.asStateFlow()

    fun solveSolid3D(logHistory: Boolean = false) {
        val d1 = solidDim1.value.toDoubleOrNull() ?: 0.0
        val d2 = solidDim2.value.toDoubleOrNull() ?: 0.0
        val d3 = solidDim3.value.toDoubleOrNull() ?: 0.0
        val sol = GeometryTrigSolvers.solveSolid3D(solidType.value, d1, d2, d3)
        _solidSolution.value = sol
        if (logHistory) {
            viewModelScope.launch {
                dao.insertCalculation(
                    CalculationEntity(
                        expression = "${solidType.value.label} (${solidDim1.value})",
                        result = "V=${AdvancedSolvers.format(sol.volume)}, A=${AdvancedSolvers.format(sol.surfaceArea)}",
                        angleMode = "3D"
                    )
                )
            }
        }
    }

    // --- Logarithm State ---
    val logBase = MutableStateFlow("2")
    val logArg = MutableStateFlow("64")
    private val _logSolution = MutableStateFlow<GeometryTrigSolvers.LogarithmSolution?>(null)
    val logSolution: StateFlow<GeometryTrigSolvers.LogarithmSolution?> = _logSolution.asStateFlow()

    fun solveLogarithm(logHistory: Boolean = false) {
        val b = logBase.value.toDoubleOrNull() ?: 2.0
        val x = logArg.value.toDoubleOrNull() ?: 8.0
        val sol = GeometryTrigSolvers.solveLogarithm(b, x)
        _logSolution.value = sol
        if (logHistory && sol.isValid) {
            viewModelScope.launch {
                dao.insertCalculation(
                    CalculationEntity(
                        expression = "log_${logBase.value}(${logArg.value})",
                        result = sol.formattedResult,
                        angleMode = "LOG"
                    )
                )
            }
        }
    }

    // --- Combinatorics State ---
    val combN = MutableStateFlow("10")
    val combR = MutableStateFlow("3")
    private val _combSolution = MutableStateFlow<GeometryTrigSolvers.CombinatoricsSolution?>(null)
    val combSolution: StateFlow<GeometryTrigSolvers.CombinatoricsSolution?> = _combSolution.asStateFlow()

    fun solveCombinatorics(logHistory: Boolean = false) {
        val n = combN.value.toLongOrNull() ?: 0L
        val r = combR.value.toLongOrNull() ?: 0L
        val sol = GeometryTrigSolvers.solveCombinatorics(n, r)
        _combSolution.value = sol
        if (logHistory && sol.isValid) {
            viewModelScope.launch {
                dao.insertCalculation(
                    CalculationEntity(
                        expression = "Combinatorics(n=$n, r=$r)",
                        result = "nPr=${AdvancedSolvers.format(sol.nPr)}, nCr=${AdvancedSolvers.format(sol.nCr)}",
                        angleMode = "COMB"
                    )
                )
            }
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

    // --- Science Context Mode (Math / Physics / Chemistry) ---
    private val _contextMode = MutableStateFlow(CalculatorContextMode.MATH)
    val contextMode: StateFlow<CalculatorContextMode> = _contextMode.asStateFlow()

    fun setContextMode(mode: CalculatorContextMode) {
        _contextMode.value = mode
    }

    // Physics Mode State
    val physicsSubTab = MutableStateFlow(0) // 0 = Constants & Calc, 1 = Physics Expressions
    val physicsSearchQuery = MutableStateFlow("")
    val physicsFormulaType = MutableStateFlow(ScienceSolvers.PhysicsFormulaType.CIRCULAR_MOTION)
    val physVal1 = MutableStateFlow("1200")
    val physVal2 = MutableStateFlow("20")
    val physVal3 = MutableStateFlow("50")
    val physVal4 = MutableStateFlow("4")
    private val _physicsFormulaSolution = MutableStateFlow<ScienceSolvers.PhysicsFormulaSolution?>(null)
    val physicsFormulaSolution: StateFlow<ScienceSolvers.PhysicsFormulaSolution?> = _physicsFormulaSolution.asStateFlow()

    fun selectPhysicsFormula(type: ScienceSolvers.PhysicsFormulaType) {
        physicsFormulaType.value = type
        when (type) {
            ScienceSolvers.PhysicsFormulaType.KINEMATICS -> {
                physVal1.value = "0"; physVal2.value = "9.81"; physVal3.value = "5"
            }
            ScienceSolvers.PhysicsFormulaType.PROJECTILE -> {
                physVal1.value = "30"; physVal2.value = "45"; physVal3.value = "9.80665"
            }
            ScienceSolvers.PhysicsFormulaType.FORCE_WORK -> {
                physVal1.value = "10"; physVal2.value = "4.5"; physVal3.value = "12"; physVal4.value = "3"
            }
            ScienceSolvers.PhysicsFormulaType.CIRCULAR_MOTION -> {
                physVal1.value = "1200"; physVal2.value = "20"; physVal3.value = "50"
            }
            ScienceSolvers.PhysicsFormulaType.MOMENTUM_IMPULSE -> {
                physVal1.value = "1500"; physVal2.value = "25"; physVal3.value = "0"; physVal4.value = "0.25"
            }
            ScienceSolvers.PhysicsFormulaType.ENERGY -> {
                physVal1.value = "2.5"; physVal2.value = "12"; physVal3.value = "10"
            }
            ScienceSolvers.PhysicsFormulaType.GRAVITATION -> {
                physVal1.value = "5.972E24"; physVal2.value = "1000"; physVal3.value = "6.771E6"
            }
            ScienceSolvers.PhysicsFormulaType.OSCILLATIONS -> {
                physVal1.value = "1.0"; physVal2.value = "250"; physVal3.value = "2.5"
            }
            ScienceSolvers.PhysicsFormulaType.FLUIDS_PRESSURE -> {
                physVal1.value = "1000"; physVal2.value = "1.0"; physVal3.value = "0.5"; physVal4.value = "10"
            }
            ScienceSolvers.PhysicsFormulaType.OHMS_LAW -> {
                physVal1.value = "12"; physVal2.value = "470"
            }
            ScienceSolvers.PhysicsFormulaType.ELECTROSTATICS -> {
                physVal1.value = "2E-6"; physVal2.value = "-3E-6"; physVal3.value = "0.15"
            }
            ScienceSolvers.PhysicsFormulaType.PHOTON -> {
                physVal1.value = "5.5E14"
            }
        }
        solvePhysicsFormula()
    }

    fun solvePhysicsFormula() {
        val v1 = physVal1.value.toDoubleOrNull() ?: 0.0
        val v2 = physVal2.value.toDoubleOrNull() ?: 0.0
        val v3 = physVal3.value.toDoubleOrNull() ?: 0.0
        val v4 = physVal4.value.toDoubleOrNull() ?: 0.0
        _physicsFormulaSolution.value = when (physicsFormulaType.value) {
            ScienceSolvers.PhysicsFormulaType.KINEMATICS -> ScienceSolvers.solvePhysicsKinematics(v1, v2, v3)
            ScienceSolvers.PhysicsFormulaType.PROJECTILE -> ScienceSolvers.solvePhysicsProjectile(v1, v2, v3)
            ScienceSolvers.PhysicsFormulaType.FORCE_WORK -> ScienceSolvers.solvePhysicsForceWork(v1, v2, v3, v4)
            ScienceSolvers.PhysicsFormulaType.CIRCULAR_MOTION -> ScienceSolvers.solvePhysicsCircularMotion(v1, v2, v3)
            ScienceSolvers.PhysicsFormulaType.MOMENTUM_IMPULSE -> ScienceSolvers.solvePhysicsMomentumImpulse(v1, v2, v3, v4)
            ScienceSolvers.PhysicsFormulaType.ENERGY -> ScienceSolvers.solvePhysicsEnergy(v1, v2, v3)
            ScienceSolvers.PhysicsFormulaType.GRAVITATION -> ScienceSolvers.solvePhysicsGravitation(v1, v2, v3)
            ScienceSolvers.PhysicsFormulaType.OSCILLATIONS -> ScienceSolvers.solvePhysicsOscillations(v1, v2, v3)
            ScienceSolvers.PhysicsFormulaType.FLUIDS_PRESSURE -> ScienceSolvers.solvePhysicsFluidsPressure(v1, v2, v3, v4)
            ScienceSolvers.PhysicsFormulaType.OHMS_LAW -> ScienceSolvers.solvePhysicsOhmsLaw(v1, v2)
            ScienceSolvers.PhysicsFormulaType.ELECTROSTATICS -> ScienceSolvers.solvePhysicsElectrostatics(v1, v2, v3)
            ScienceSolvers.PhysicsFormulaType.PHOTON -> ScienceSolvers.solvePhysicsPhoton(v1)
        }
    }

    // Chemistry Mode State
    val chemistrySubTab = MutableStateFlow(0) // 0 = pH / pOH, 1 = Gas & Dilution, 2 = Constants & Calc
    val chemSearchQuery = MutableStateFlow("")
    val phInputType = MutableStateFlow(ScienceSolvers.PhInputType.PH)
    val phInputValue = MutableStateFlow("3.5")
    private val _phSolution = MutableStateFlow<ScienceSolvers.PhSolution?>(null)
    val phSolution: StateFlow<ScienceSolvers.PhSolution?> = _phSolution.asStateFlow()

    fun solvePh() {
        val v = phInputValue.value.toDoubleOrNull() ?: Double.NaN
        _phSolution.value = ScienceSolvers.solvePhSystem(phInputType.value, v)
    }

    val gasTarget = MutableStateFlow(ScienceSolvers.GasSolveTarget.PRESSURE)
    val gasP = MutableStateFlow("1.0")
    val gasV = MutableStateFlow("22.414")
    val gasN = MutableStateFlow("1.0")
    val gasT = MutableStateFlow("273.15")
    private val _gasSolution = MutableStateFlow<ScienceSolvers.IdealGasSolution?>(null)
    val gasSolution: StateFlow<ScienceSolvers.IdealGasSolution?> = _gasSolution.asStateFlow()

    fun solveIdealGas() {
        val p = gasP.value.toDoubleOrNull() ?: 0.0
        val v = gasV.value.toDoubleOrNull() ?: 0.0
        val n = gasN.value.toDoubleOrNull() ?: 0.0
        val t = gasT.value.toDoubleOrNull() ?: 0.0
        _gasSolution.value = ScienceSolvers.solveIdealGas(gasTarget.value, p, v, n, t)
    }

    val dilutionTarget = MutableStateFlow(ScienceSolvers.DilutionTarget.V1)
    val dilM1 = MutableStateFlow("6.0")
    val dilV1 = MutableStateFlow("25")
    val dilM2 = MutableStateFlow("0.5")
    val dilV2 = MutableStateFlow("300")
    private val _dilutionSolution = MutableStateFlow<ScienceSolvers.DilutionSolution?>(null)
    val dilutionSolution: StateFlow<ScienceSolvers.DilutionSolution?> = _dilutionSolution.asStateFlow()

    fun solveDilution() {
        val m1 = dilM1.value.toDoubleOrNull() ?: 0.0
        val v1 = dilV1.value.toDoubleOrNull() ?: 0.0
        val m2 = dilM2.value.toDoubleOrNull() ?: 0.0
        val v2 = dilV2.value.toDoubleOrNull() ?: 0.0
        _dilutionSolution.value = ScienceSolvers.solveDilution(dilutionTarget.value, m1, v1, m2, v2)
    }

    init {
        updateConversion()
        solveQuadratic()
        solveLinearSystem()
        solveSummation()
        solveExponential()
        solveStatistics()
        solveNumberTheory()
        solveSequence()
        solveCircle()
        solveTrigAngle()
        solveRightTriangle()
        solveGeneralTriangle()
        solveSolid3D()
        solveLogarithm()
        solveCombinatorics()
        solvePhysicsFormula()
        solvePh()
        solveIdealGas()
        solveDilution()
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

    fun onInsertConstant(valueStr: String) {
        val curr = _expression.value
        if (_calculationResult.value != null || curr.isEmpty()) {
            _expression.value = valueStr
            _calculationResult.value = null
        } else {
            val last = curr.last()
            val needsMul = last.isDigit() || last == ')' || last == 'π' || last == 'e' || last == 'x' || last == 'y'
            _expression.value = if (needsMul) "$curr×$valueStr" else "$curr$valueStr"
        }
        computeLivePreview()
    }

    fun useScienceValueInCalculator(valueStr: String) {
        _expression.value = valueStr
        _calculationResult.value = null
        _contextMode.value = CalculatorContextMode.MATH
        computeLivePreview()
    }

    private fun isDigitOrConst(text: String): Boolean {
        return text in listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "π", "e", "x", "y") ||
            text.toDoubleOrNull() != null
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
