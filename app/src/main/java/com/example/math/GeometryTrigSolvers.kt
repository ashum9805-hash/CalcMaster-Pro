package com.example.math

import kotlin.math.*

object GeometryTrigSolvers {

    private fun fmt(v: Double): String = AdvancedSolvers.format(v)

    private fun fmtPi(coeff: Double): String {
        val rounded = round(coeff * 10000.0) / 10000.0
        return when {
            abs(rounded - 1.0) < 1e-6 -> "π"
            abs(rounded) < 1e-9 -> "0"
            else -> "${fmt(coeff)}π"
        }
    }

    // -------------------------------------------------------------
    // 1. CIRCLES, ARCS & SECTORS SOLVER
    // -------------------------------------------------------------
    enum class CircleInputType(val label: String, val symbol: String) {
        RADIUS("Radius (r)", "r"),
        DIAMETER("Diameter (d)", "d"),
        CIRCUMFERENCE("Circumference (C)", "C"),
        AREA("Area (A)", "A")
    }

    data class CircleSolution(
        val inputType: CircleInputType,
        val inputValue: Double,
        val radius: Double,
        val diameter: Double,
        val circumference: Double,
        val area: Double,
        val exactCircumferencePi: String,
        val exactAreaPi: String,
        val angleDeg: Double,
        val angleRad: Double,
        val arcLength: Double,
        val sectorArea: Double,
        val chordLength: Double,
        val segmentArea: Double,
        val standardEquation: String,
        val isValid: Boolean = true,
        val errorMessage: String? = null,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    fun solveCircle(
        inputType: CircleInputType,
        inputValue: Double,
        centralAngleInput: Double,
        angleMode: AngleMode
    ): CircleSolution {
        if (!inputValue.isFinite() || inputValue <= 0.0) {
            return CircleSolution(
                inputType = inputType,
                inputValue = inputValue,
                radius = 0.0, diameter = 0.0, circumference = 0.0, area = 0.0,
                exactCircumferencePi = "0", exactAreaPi = "0",
                angleDeg = 0.0, angleRad = 0.0,
                arcLength = 0.0, sectorArea = 0.0, chordLength = 0.0, segmentArea = 0.0,
                standardEquation = "x² + y² = r²",
                isValid = false,
                errorMessage = "Please enter a positive value greater than 0.",
                steps = emptyList(),
                notes = emptyList()
            )
        }

        val steps = mutableListOf<EvaluationStep>()
        var stepNum = 1

        val r = when (inputType) {
            CircleInputType.RADIUS -> {
                steps.add(
                    EvaluationStep(
                        stepNumber = stepNum++,
                        title = "Identify Radius (r)",
                        subExpression = "r = ${fmt(inputValue)}",
                        ruleApplied = "Direct Radius Input",
                        intermediateResult = "r = ${fmt(inputValue)}",
                        expressionAfter = "r = ${fmt(inputValue)}",
                        note = "All circle properties derive directly from radius r."
                    )
                )
                inputValue
            }
            CircleInputType.DIAMETER -> {
                val rad = inputValue / 2.0
                steps.add(
                    EvaluationStep(
                        stepNumber = stepNum++,
                        title = "Derive Radius from Diameter",
                        subExpression = "r = d / 2 = ${fmt(inputValue)} / 2",
                        ruleApplied = "Radius is Half the Diameter",
                        intermediateResult = "r = ${fmt(rad)}",
                        expressionAfter = "r = ${fmt(rad)}",
                        note = "Divide the diameter by 2 to obtain the radius."
                    )
                )
                rad
            }
            CircleInputType.CIRCUMFERENCE -> {
                val rad = inputValue / (2.0 * PI)
                steps.add(
                    EvaluationStep(
                        stepNumber = stepNum++,
                        title = "Derive Radius from Circumference",
                        subExpression = "r = C / (2π) = ${fmt(inputValue)} / (2π)",
                        ruleApplied = "Inverse of C = 2πr",
                        intermediateResult = "r = ${fmt(rad)}",
                        expressionAfter = "r = ${fmt(rad)}",
                        note = "Rearrange C = 2πr to solve for radius r."
                    )
                )
                rad
            }
            CircleInputType.AREA -> {
                val rad = sqrt(inputValue / PI)
                steps.add(
                    EvaluationStep(
                        stepNumber = stepNum++,
                        title = "Derive Radius from Area",
                        subExpression = "r = √(A / π) = √(${fmt(inputValue)} / π)",
                        ruleApplied = "Inverse of A = πr²",
                        intermediateResult = "r = ${fmt(rad)}",
                        expressionAfter = "r = ${fmt(rad)}",
                        note = "Divide area by π and take the principal square root."
                    )
                )
                rad
            }
        }

        val d = 2.0 * r
        val c = 2.0 * PI * r
        val a = PI * r * r

        steps.add(
            EvaluationStep(
                stepNumber = stepNum++,
                title = "Compute Diameter & Circumference",
                subExpression = "d = 2r,  C = 2πr = πd",
                ruleApplied = "Circle Perimeter Formulas",
                intermediateResult = "d = ${fmt(d)}, C = ${fmt(c)} (${fmtPi(2.0 * r)})",
                expressionAfter = "C = ${fmt(c)}",
                note = "Circumference is the total distance around the circle."
            )
        )

        steps.add(
            EvaluationStep(
                stepNumber = stepNum++,
                title = "Compute Circle Area",
                subExpression = "A = πr² = π(${fmt(r)})²",
                ruleApplied = "Circle Area Formula",
                intermediateResult = "A = ${fmt(a)} (${fmtPi(r * r)})",
                expressionAfter = "A = ${fmt(a)}",
                note = "Area enclosed inside the full 360° (2π rad) circle."
            )
        )

        val rawDeg = if (angleMode == AngleMode.DEG) centralAngleInput else centralAngleInput * 180.0 / PI
        val clampedDeg = rawDeg.coerceIn(0.0, 360.0)
        val angleRad = clampedDeg * PI / 180.0

        val arcLength = r * angleRad
        val sectorArea = 0.5 * r * r * angleRad
        val chordLength = 2.0 * r * sin(angleRad / 2.0)
        val segmentArea = 0.5 * r * r * (angleRad - sin(angleRad))

        steps.add(
            EvaluationStep(
                stepNumber = stepNum++,
                title = "Compute Sector, Arc & Chord (θ = ${fmt(clampedDeg)}°)",
                subExpression = "s = rθ_rad,  A_sec = ½r²θ_rad,  chord = 2r·sin(θ/2)",
                ruleApplied = "Circular Sector Geometry (θ_rad = ${fmt(angleRad)} rad)",
                intermediateResult = "Arc s = ${fmt(arcLength)}, Sector Area = ${fmt(sectorArea)}, Chord = ${fmt(chordLength)}",
                expressionAfter = "s = ${fmt(arcLength)}, A_sec = ${fmt(sectorArea)}",
                note = "Convert central angle θ to radians (θ_rad = θ° × π/180) before multiplying by radius r."
            )
        )

        val notes = listOf(
            MathNote(
                title = "Fundamental Circle Equations",
                content = "Diameter d = 2r, Circumference C = 2πr, Area A = πr². Centered at origin (0,0), the Cartesian equation is x² + y² = r².",
                formula = "C = 2πr,  A = πr²"
            ),
            MathNote(
                title = "Arc Length & Sector Area",
                content = "Using θ in radians, an arc spans fraction θ/(2π) of the circle: s = rθ and Sector Area = ½r²θ. Chord length between endpoints is 2r·sin(θ/2).",
                formula = "s = rθ,  A_sec = ½r²θ"
            )
        )

        return CircleSolution(
            inputType = inputType,
            inputValue = inputValue,
            radius = r,
            diameter = d,
            circumference = c,
            area = a,
            exactCircumferencePi = fmtPi(2.0 * r),
            exactAreaPi = fmtPi(r * r),
            angleDeg = clampedDeg,
            angleRad = angleRad,
            arcLength = arcLength,
            sectorArea = sectorArea,
            chordLength = chordLength,
            segmentArea = segmentArea,
            standardEquation = "x² + y² = ${fmt(r * r)}",
            steps = steps,
            notes = notes
        )
    }

    // -------------------------------------------------------------
    // 2. TRIGONOMETRY: ANGLE & UNIT CIRCLE (ALL 6 FUNCTIONS)
    // -------------------------------------------------------------
    data class TrigAngleSolution(
        val angleDeg: Double,
        val angleRad: Double,
        val piMultiple: String,
        val quadrant: String,
        val referenceAngleDeg: Double,
        val sinVal: Double,
        val cosVal: Double,
        val tanVal: Double?,
        val cscVal: Double?,
        val secVal: Double?,
        val cotVal: Double?,
        val exactSinHint: String?,
        val exactCosHint: String?,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    private fun exactUnitCircleHint(degNorm: Double): Pair<String?, String?> {
        val d = ((degNorm % 360.0) + 360.0) % 360.0
        val table = mapOf(
            0.0 to ("0" to "1"),
            30.0 to ("1/2" to "√3/2"),
            45.0 to ("√2/2" to "√2/2"),
            60.0 to ("√3/2" to "1/2"),
            90.0 to ("1" to "0"),
            120.0 to ("√3/2" to "-1/2"),
            135.0 to ("√2/2" to "-√2/2"),
            150.0 to ("1/2" to "-√3/2"),
            180.0 to ("0" to "-1"),
            210.0 to ("-1/2" to "-√3/2"),
            225.0 to ("-√2/2" to "-√2/2"),
            240.0 to ("-√3/2" to "-1/2"),
            270.0 to ("-1" to "0"),
            300.0 to ("-√3/2" to "1/2"),
            315.0 to ("-√2/2" to "√2/2"),
            330.0 to ("-1/2" to "√3/2")
        )
        for ((k, pair) in table) {
            if (abs(d - k) < 1e-4) return pair
        }
        return null to null
    }

    private fun gcdInt(a: Int, b: Int): Int {
        var x = abs(a)
        var y = abs(b)
        while (y != 0) {
            val t = y
            y = x % y
            x = t
        }
        return if (x == 0) 1 else x
    }

    fun solveTrigAngle(angleInput: Double, angleMode: AngleMode): TrigAngleSolution {
        val deg = if (angleMode == AngleMode.DEG) angleInput else angleInput * 180.0 / PI
        val rad = deg * PI / 180.0

        val normDeg = ((deg % 360.0) + 360.0) % 360.0
        val quadrant = when {
            abs(normDeg) < 1e-6 || abs(normDeg - 360.0) < 1e-6 -> "Positive X-Axis (0°)"
            abs(normDeg - 90.0) < 1e-6 -> "Positive Y-Axis (90°)"
            abs(normDeg - 180.0) < 1e-6 -> "Negative X-Axis (180°)"
            abs(normDeg - 270.0) < 1e-6 -> "Negative Y-Axis (270°)"
            normDeg < 90.0 -> "Quadrant I (All +)"
            normDeg < 180.0 -> "Quadrant II (sin +)"
            normDeg < 270.0 -> "Quadrant III (tan +)"
            else -> "Quadrant IV (cos +)"
        }

        val refDeg = when {
            normDeg <= 90.0 -> normDeg
            normDeg <= 180.0 -> 180.0 - normDeg
            normDeg <= 270.0 -> normDeg - 180.0
            else -> 360.0 - normDeg
        }

        val sinV = sin(rad).let { if (abs(it) < 1e-12) 0.0 else it }
        val cosV = cos(rad).let { if (abs(it) < 1e-12) 0.0 else it }
        val tanV = if (abs(cosV) < 1e-10) null else sinV / cosV
        val cscV = if (abs(sinV) < 1e-10) null else 1.0 / sinV
        val secV = if (abs(cosV) < 1e-10) null else 1.0 / cosV
        val cotV = if (abs(sinV) < 1e-10) null else cosV / sinV

        val piRatio = rad / PI
        val twelfth = round(piRatio * 12.0).toInt()
        val piMultipleStr = if (abs(piRatio * 12.0 - twelfth) < 1e-4) {
            if (twelfth == 0) "0"
            else {
                val g = gcdInt(twelfth, 12)
                val num = twelfth / g
                val den = 12 / g
                val sign = if (num < 0) "-" else ""
                val absNum = abs(num)
                val numPart = if (absNum == 1) "π" else "${absNum}π"
                if (den == 1) "$sign$numPart" else "$sign$numPart/$den"
            }
        } else {
            "${fmt(piRatio)}π"
        }

        val (exactSin, exactCos) = exactUnitCircleHint(normDeg)

        val steps = listOf(
            EvaluationStep(
                stepNumber = 1,
                title = "Convert Angle & Locate Quadrant",
                subExpression = "${fmt(deg)}° = ${fmt(rad)} rad ($piMultipleStr)",
                ruleApplied = "Radian Conversion: rad = deg × (π / 180°)",
                intermediateResult = "$quadrant, Reference Angle = ${fmt(refDeg)}°",
                expressionAfter = "θ = ${fmt(deg)}° (${piMultipleStr} rad)",
                note = "Coterminal angle in [0°, 360°) is ${fmt(normDeg)}°, placing the terminal ray in $quadrant."
            ),
            EvaluationStep(
                stepNumber = 2,
                title = "Evaluate Primary Functions (sin, cos, tan)",
                subExpression = "y = sin(θ), x = cos(θ), tan(θ) = sin(θ)/cos(θ)",
                ruleApplied = "Unit Circle Coordinates (cos θ, sin θ)",
                intermediateResult = "sin = ${fmt(sinV)}, cos = ${fmt(cosV)}, tan = ${tanV?.let { fmt(it) } ?: "Undefined"}",
                expressionAfter = "(x, y) = (${fmt(cosV)}, ${fmt(sinV)})",
                note = "On the unit circle (r = 1), cos(θ) gives the x-coordinate and sin(θ) gives the y-coordinate."
            ),
            EvaluationStep(
                stepNumber = 3,
                title = "Evaluate Reciprocal Functions (csc, sec, cot)",
                subExpression = "csc = 1/sin,  sec = 1/cos,  cot = cos/sin",
                ruleApplied = "Reciprocal Trigonometric Identities",
                intermediateResult = "csc = ${cscV?.let { fmt(it) } ?: "Undefined"}, sec = ${secV?.let { fmt(it) } ?: "Undefined"}, cot = ${cotV?.let { fmt(it) } ?: "Undefined"}",
                expressionAfter = "All 6 trig ratios solved",
                note = "Where sin(θ) = 0, csc and cot are undefined (division by zero). Where cos(θ) = 0, tan and sec are undefined."
            )
        )

        val notes = listOf(
            MathNote(
                title = "ASTC Sign Rule (Quadrants)",
                content = "Quadrant I: All positive. Quadrant II: Sine & Csc positive. Quadrant III: Tangent & Cot positive. Quadrant IV: Cosine & Sec positive.",
                formula = "sin²(θ) + cos²(θ) = 1"
            ),
            MathNote(
                title = "Reference Angle",
                content = "The acute angle formed between the terminal side of θ and the horizontal x-axis. Trig magnitudes depend on the reference angle (${fmt(refDeg)}°), while signs depend on the quadrant.",
                formula = "ref = ${fmt(refDeg)}°"
            )
        )

        return TrigAngleSolution(
            angleDeg = deg,
            angleRad = rad,
            piMultiple = piMultipleStr,
            quadrant = quadrant,
            referenceAngleDeg = refDeg,
            sinVal = sinV,
            cosVal = cosV,
            tanVal = tanV,
            cscVal = cscV,
            secVal = secV,
            cotVal = cotV,
            exactSinHint = exactSin,
            exactCosHint = exactCos,
            steps = steps,
            notes = notes
        )
    }

    // -------------------------------------------------------------
    // 3. RIGHT TRIANGLE SOLVER (SOH-CAH-TOA & PYTHAGORAS)
    // -------------------------------------------------------------
    enum class RightTriMode(val label: String, val field1Label: String, val field2Label: String) {
        TWO_LEGS("Two Legs (a, b)", "Opposite Leg (a)", "Adjacent Leg (b)"),
        LEG_AND_HYP("Leg & Hypotenuse (a, c)", "Leg (a)", "Hypotenuse (c)"),
        LEG_AND_ANGLE("Leg & Angle (a, ∠A)", "Opposite Leg (a)", "Angle A (°)"),
        HYP_AND_ANGLE("Hypotenuse & Angle (c, ∠A)", "Hypotenuse (c)", "Angle A (°)")
    }

    data class RightTriangleSolution(
        val a: Double,
        val b: Double,
        val c: Double,
        val angleADeg: Double,
        val angleBDeg: Double,
        val area: Double,
        val perimeter: Double,
        val isValid: Boolean = true,
        val errorMessage: String? = null,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    fun solveRightTriangle(mode: RightTriMode, v1: Double, v2: Double): RightTriangleSolution {
        fun invalid(msg: String) = RightTriangleSolution(
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            isValid = false, errorMessage = msg, steps = emptyList(), notes = emptyList()
        )

        if (!v1.isFinite() || !v2.isFinite() || v1 <= 0.0 || v2 <= 0.0) {
            return invalid("Both inputs must be positive numbers greater than 0.")
        }

        val a: Double
        val b: Double
        val c: Double
        val angleA: Double
        val angleB: Double
        val steps = mutableListOf<EvaluationStep>()

        when (mode) {
            RightTriMode.TWO_LEGS -> {
                a = v1
                b = v2
                c = hypot(a, b)
                angleA = atan2(a, b) * 180.0 / PI
                angleB = 90.0 - angleA
                steps.add(
                    EvaluationStep(
                        1, "Apply Pythagorean Theorem",
                        "c = √(a² + b²) = √(${fmt(a)}² + ${fmt(b)}²)",
                        "Pythagorean Theorem: a² + b² = c²",
                        "c = ${fmt(c)}", "c = ${fmt(c)}",
                        "The hypotenuse is the square root of the sum of squared legs."
                    )
                )
                steps.add(
                    EvaluationStep(
                        2, "Solve Acute Angles via Inverse Tangent",
                        "A = atan(a / b) = atan(${fmt(a)} / ${fmt(b)})",
                        "TOA: tan(A) = Opposite / Adjacent",
                        "A = ${fmt(angleA)}°, B = 90° - A = ${fmt(angleB)}°",
                        "A = ${fmt(angleA)}°, B = ${fmt(angleB)}°",
                        "The two acute angles in a right triangle sum to 90°."
                    )
                )
            }
            RightTriMode.LEG_AND_HYP -> {
                if (v1 >= v2) return invalid("Hypotenuse (c) must be strictly greater than leg (a).")
                a = v1
                c = v2
                b = sqrt(c * c - a * a)
                angleA = asin(a / c) * 180.0 / PI
                angleB = 90.0 - angleA
                steps.add(
                    EvaluationStep(
                        1, "Solve Missing Leg via Pythagorean Theorem",
                        "b = √(c² - a²) = √(${fmt(c)}² - ${fmt(a)}²)",
                        "Pythagorean Theorem",
                        "b = ${fmt(b)}", "b = ${fmt(b)}",
                        "Subtract the squared known leg from the squared hypotenuse."
                    )
                )
                steps.add(
                    EvaluationStep(
                        2, "Solve Acute Angles via Inverse Sine",
                        "A = asin(a / c) = asin(${fmt(a)} / ${fmt(c)})",
                        "SOH: sin(A) = Opposite / Hypotenuse",
                        "A = ${fmt(angleA)}°, B = ${fmt(angleB)}°",
                        "A = ${fmt(angleA)}°, B = ${fmt(angleB)}°",
                        "Complementary angle B = 90° - A."
                    )
                )
            }
            RightTriMode.LEG_AND_ANGLE -> {
                if (v2 >= 90.0) return invalid("Acute angle A must be strictly between 0° and 90°.")
                a = v1
                angleA = v2
                angleB = 90.0 - angleA
                val radA = angleA * PI / 180.0
                b = a / tan(radA)
                c = a / sin(radA)
                steps.add(
                    EvaluationStep(
                        1, "Find Complementary Angle B",
                        "B = 90° - ${fmt(angleA)}° = ${fmt(angleB)}°",
                        "Triangle Angle Sum (180° - 90° - A)",
                        "B = ${fmt(angleB)}°", "B = ${fmt(angleB)}°",
                        "Acute angles of a right triangle are complementary."
                    )
                )
                steps.add(
                    EvaluationStep(
                        2, "Solve Adjacent Leg & Hypotenuse",
                        "b = a / tan(A),  c = a / sin(A)",
                        "SOH-CAH-TOA Trigonometric Ratios",
                        "b = ${fmt(b)}, c = ${fmt(c)}", "b = ${fmt(b)}, c = ${fmt(c)}",
                        "Use tangent for the adjacent leg and sine for the hypotenuse."
                    )
                )
            }
            RightTriMode.HYP_AND_ANGLE -> {
                if (v2 >= 90.0) return invalid("Acute angle A must be strictly between 0° and 90°.")
                c = v1
                angleA = v2
                angleB = 90.0 - angleA
                val radA = angleA * PI / 180.0
                a = c * sin(radA)
                b = c * cos(radA)
                steps.add(
                    EvaluationStep(
                        1, "Resolve Components using Sine & Cosine",
                        "a = c·sin(A),  b = c·cos(A)",
                        "SOH & CAH Projections",
                        "a = ${fmt(a)}, b = ${fmt(b)}", "a = ${fmt(a)}, b = ${fmt(b)}",
                        "Multiply hypotenuse by sin(A) for opposite leg and cos(A) for adjacent leg."
                    )
                )
            }
        }

        val area = 0.5 * a * b
        val perimeter = a + b + c

        steps.add(
            EvaluationStep(
                steps.size + 1, "Compute Area & Perimeter",
                "Area = ½·a·b,  Perimeter = a + b + c",
                "Right Triangle Geometry",
                "Area = ${fmt(area)}, P = ${fmt(perimeter)}",
                "Area = ${fmt(area)}",
                "Since the legs meet at 90°, one leg is the base and the other is the exact height."
            )
        )

        val notes = listOf(
            MathNote(
                title = "SOH - CAH - TOA",
                content = "sin(A) = Opposite / Hypotenuse = a/c; cos(A) = Adjacent / Hypotenuse = b/c; tan(A) = Opposite / Adjacent = a/b.",
                formula = "a² + b² = c²"
            )
        )

        return RightTriangleSolution(
            a = a, b = b, c = c,
            angleADeg = angleA, angleBDeg = angleB,
            area = area, perimeter = perimeter,
            steps = steps, notes = notes
        )
    }

    // -------------------------------------------------------------
    // 4. GENERAL TRIANGLE SOLVER (LAW OF SINES & COSINES: SSS / SAS)
    // -------------------------------------------------------------
    enum class GeneralTriMode(val label: String) {
        SSS("3 Sides (SSS: a, b, c)"),
        SAS("2 Sides & Included Angle (SAS: a, b, ∠C)")
    }

    data class GeneralTriangleSolution(
        val a: Double,
        val b: Double,
        val c: Double,
        val angleADeg: Double,
        val angleBDeg: Double,
        val angleCDeg: Double,
        val area: Double,
        val perimeter: Double,
        val isValid: Boolean = true,
        val errorMessage: String? = null,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    fun solveGeneralTriangle(mode: GeneralTriMode, v1: Double, v2: Double, v3: Double): GeneralTriangleSolution {
        fun invalid(msg: String) = GeneralTriangleSolution(
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            isValid = false, errorMessage = msg, steps = emptyList(), notes = emptyList()
        )

        if (!v1.isFinite() || !v2.isFinite() || !v3.isFinite() || v1 <= 0.0 || v2 <= 0.0 || v3 <= 0.0) {
            return invalid("All side lengths and angles must be positive numbers.")
        }

        val a: Double
        val b: Double
        val c: Double
        val degA: Double
        val degB: Double
        val degC: Double
        val steps = mutableListOf<EvaluationStep>()

        if (mode == GeneralTriMode.SSS) {
            a = v1; b = v2; c = v3
            if (a + b <= c || a + c <= b || b + c <= a) {
                return invalid("Triangle inequality violated: sum of any two sides must exceed the third side.")
            }
            val cosA = ((b * b + c * c - a * a) / (2.0 * b * c)).coerceIn(-1.0, 1.0)
            val cosB = ((a * a + c * c - b * b) / (2.0 * a * c)).coerceIn(-1.0, 1.0)
            degA = acos(cosA) * 180.0 / PI
            degB = acos(cosB) * 180.0 / PI
            degC = 180.0 - degA - degB

            steps.add(
                EvaluationStep(
                    1, "Law of Cosines for Angle A & B",
                    "cos(A) = (b² + c² - a²) / (2bc)",
                    "Law of Cosines (SSS)",
                    "A = ${fmt(degA)}°, B = ${fmt(degB)}°",
                    "C = 180° - A - B = ${fmt(degC)}°",
                    "Solve two angles using the Law of Cosines, then subtract their sum from 180° for angle C."
                )
            )
        } else {
            a = v1; b = v2; degC = v3
            if (degC >= 180.0) return invalid("Included angle C must be strictly between 0° and 180°.")
            val radC = degC * PI / 180.0
            c = sqrt(a * a + b * b - 2.0 * a * b * cos(radC))
            val cosA = ((b * b + c * c - a * a) / (2.0 * b * c)).coerceIn(-1.0, 1.0)
            degA = acos(cosA) * 180.0 / PI
            degB = 180.0 - degA - degC

            steps.add(
                EvaluationStep(
                    1, "Find Third Side c via Law of Cosines",
                    "c = √(a² + b² - 2ab·cos(C))",
                    "Law of Cosines (SAS)",
                    "c = ${fmt(c)}", "c = ${fmt(c)}",
                    "Compute the side opposite to included angle C."
                )
            )
            steps.add(
                EvaluationStep(
                    2, "Solve Remaining Angles",
                    "cos(A) = (b² + c² - a²) / (2bc),  B = 180° - A - C",
                    "Law of Cosines & Angle Sum",
                    "A = ${fmt(degA)}°, B = ${fmt(degB)}°",
                    "A = ${fmt(degA)}°, B = ${fmt(degB)}°",
                    "Using Law of Cosines avoids the ambiguous sine case."
                )
            )
        }

        val s = (a + b + c) / 2.0
        val area = sqrt(maxOf(0.0, s * (s - a) * (s - b) * (s - c)))
        val perimeter = a + b + c

        steps.add(
            EvaluationStep(
                steps.size + 1, "Compute Area via Heron's Formula",
                "s = (a+b+c)/2 = ${fmt(s)},  Area = √[s(s-a)(s-b)(s-c)]",
                "Heron's Area Formula",
                "Area = ${fmt(area)}, Perimeter = ${fmt(perimeter)}",
                "Area = ${fmt(area)}",
                "Validates any general triangle using its semi-perimeter s."
            )
        )

        val notes = listOf(
            MathNote(
                title = "Law of Cosines & Law of Sines",
                content = "Law of Cosines: c² = a² + b² - 2ab·cos(C). Law of Sines: a/sin(A) = b/sin(B) = c/sin(C).",
                formula = "c² = a² + b² - 2ab·cos(C)"
            )
        )

        return GeneralTriangleSolution(
            a = a, b = b, c = c,
            angleADeg = degA, angleBDeg = degB, angleCDeg = degC,
            area = area, perimeter = perimeter,
            steps = steps, notes = notes
        )
    }

    // -------------------------------------------------------------
    // 5. 3D SOLIDS GEOMETRY SOLVER (SPHERE, CYLINDER, CONE, PRISM)
    // -------------------------------------------------------------
    enum class SolidType(val label: String, val dimsCount: Int, val label1: String, val label2: String, val label3: String) {
        SPHERE("Sphere", 1, "Radius (r)", "", ""),
        CYLINDER("Cylinder", 2, "Radius (r)", "Height (h)", ""),
        CONE("Right Cone", 2, "Radius (r)", "Height (h)", ""),
        RECT_PRISM("Rectangular Prism", 3, "Length (l)", "Width (w)", "Height (h)")
    }

    data class Solid3DSolution(
        val type: SolidType,
        val volume: Double,
        val surfaceArea: Double,
        val lateralOrExtraLabel: String,
        val lateralOrExtraValue: Double,
        val exactPiVolume: String?,
        val exactPiSurfaceArea: String?,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    fun solveSolid3D(type: SolidType, d1: Double, d2: Double, d3: Double): Solid3DSolution {
        val v1 = maxOf(0.0, d1)
        val v2 = maxOf(0.0, d2)
        val v3 = maxOf(0.0, d3)

        return when (type) {
            SolidType.SPHERE -> {
                val vol = (4.0 / 3.0) * PI * v1.pow(3)
                val sa = 4.0 * PI * v1.pow(2)
                val circ = 2.0 * PI * v1
                Solid3DSolution(
                    type = type,
                    volume = vol,
                    surfaceArea = sa,
                    lateralOrExtraLabel = "Great Circle Circumference",
                    lateralOrExtraValue = circ,
                    exactPiVolume = fmtPi((4.0 / 3.0) * v1.pow(3)),
                    exactPiSurfaceArea = fmtPi(4.0 * v1.pow(2)),
                    steps = listOf(
                        EvaluationStep(1, "Sphere Volume", "V = (4/3)πr³ = (4/3)π(${fmt(v1)})³", "Archimedes Sphere Volume", fmt(vol), "V = ${fmt(vol)}", "Cube the radius and multiply by 4π/3."),
                        EvaluationStep(2, "Sphere Surface Area", "A = 4πr² = 4π(${fmt(v1)})²", "Sphere Surface Formula", fmt(sa), "A = ${fmt(sa)}", "Surface area equals 4 times the area of a great circle.")
                    ),
                    notes = listOf(MathNote("Sphere Formulas", "A sphere of radius r has volume (4/3)πr³ and surface area 4πr².", "V = 4/3 πr³, A = 4πr²"))
                )
            }
            SolidType.CYLINDER -> {
                val vol = PI * v1 * v1 * v2
                val lat = 2.0 * PI * v1 * v2
                val sa = lat + 2.0 * PI * v1 * v1
                Solid3DSolution(
                    type = type,
                    volume = vol,
                    surfaceArea = sa,
                    lateralOrExtraLabel = "Lateral Surface Area",
                    lateralOrExtraValue = lat,
                    exactPiVolume = fmtPi(v1 * v1 * v2),
                    exactPiSurfaceArea = fmtPi(2.0 * v1 * v2 + 2.0 * v1 * v1),
                    steps = listOf(
                        EvaluationStep(1, "Cylinder Volume", "V = πr²h = π(${fmt(v1)})²(${fmt(v2)})", "Base Area × Height", fmt(vol), "V = ${fmt(vol)}", "Multiply circular base area πr² by height h."),
                        EvaluationStep(2, "Total Surface Area", "A = 2πrh + 2πr²", "Curved Side + 2 Bases", fmt(sa), "A = ${fmt(sa)}", "Unrolling the curved wall forms a rectangle of width 2πr and height h.")
                    ),
                    notes = listOf(MathNote("Right Circular Cylinder", "Volume is πr²h and total surface area combines lateral area 2πrh with top and bottom disks.", "V = πr²h, A = 2πr(r + h)"))
                )
            }
            SolidType.CONE -> {
                val slant = hypot(v1, v2)
                val vol = (1.0 / 3.0) * PI * v1 * v1 * v2
                val lat = PI * v1 * slant
                val sa = lat + PI * v1 * v1
                Solid3DSolution(
                    type = type,
                    volume = vol,
                    surfaceArea = sa,
                    lateralOrExtraLabel = "Slant Height (l = √(r²+h²))",
                    lateralOrExtraValue = slant,
                    exactPiVolume = fmtPi((1.0 / 3.0) * v1 * v1 * v2),
                    exactPiSurfaceArea = fmtPi(v1 * slant + v1 * v1),
                    steps = listOf(
                        EvaluationStep(1, "Compute Slant Height (l)", "l = √(r² + h²) = √(${fmt(v1)}² + ${fmt(v2)}²)", "Pythagorean Theorem", fmt(slant), "l = ${fmt(slant)}", "Slant height connects the apex to the base rim."),
                        EvaluationStep(2, "Cone Volume & Surface Area", "V = ⅓πr²h,  A = πr(r + l)", "Cone Formulas", "V = ${fmt(vol)}, A = ${fmt(sa)}", "V = ${fmt(vol)}", "A cone holds exactly 1/3 the volume of a cylinder with the same base and height.")
                    ),
                    notes = listOf(MathNote("Right Circular Cone", "Volume is ⅓πr²h. Lateral surface area is πrl where slant height l = √(r² + h²).", "V = ⅓πr²h, A = πr(r + l)"))
                )
            }
            SolidType.RECT_PRISM -> {
                val vol = v1 * v2 * v3
                val sa = 2.0 * (v1 * v2 + v2 * v3 + v1 * v3)
                val diag = sqrt(v1 * v1 + v2 * v2 + v3 * v3)
                Solid3DSolution(
                    type = type,
                    volume = vol,
                    surfaceArea = sa,
                    lateralOrExtraLabel = "Space Diagonal √(l²+w²+h²)",
                    lateralOrExtraValue = diag,
                    exactPiVolume = null,
                    exactPiSurfaceArea = null,
                    steps = listOf(
                        EvaluationStep(1, "Prism Volume", "V = l × w × h = ${fmt(v1)} × ${fmt(v2)} × ${fmt(v3)}", "Cuboid Volume", fmt(vol), "V = ${fmt(vol)}", "Multiply all three orthogonal dimensions."),
                        EvaluationStep(2, "Surface Area & Space Diagonal", "A = 2(lw + wh + lh),  d = √(l²+w²+h²)", "6 Rectangular Faces & 3D Pythagoras", "A = ${fmt(sa)}, d = ${fmt(diag)}", "A = ${fmt(sa)}", "Sum the areas of the three pairs of opposite faces.")
                    ),
                    notes = listOf(MathNote("Rectangular Prism (Cuboid)", "Volume = lwh, Total Surface Area = 2(lw + wh + lh), Space Diagonal = √(l² + w² + h²).", "V = l·w·h"))
                )
            }
        }
    }

    // -------------------------------------------------------------
    // 6. ARBITRARY BASE LOGARITHM SOLVER: log_b(x)
    // -------------------------------------------------------------
    data class LogarithmSolution(
        val base: Double,
        val argument: Double,
        val result: Double,
        val formattedResult: String,
        val lnArg: Double,
        val lnBase: Double,
        val isValid: Boolean = true,
        val errorMessage: String? = null,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    fun solveLogarithm(base: Double, arg: Double): LogarithmSolution {
        if (!base.isFinite() || !arg.isFinite() || base <= 0.0 || base == 1.0 || arg <= 0.0) {
            return LogarithmSolution(
                base, arg, Double.NaN, "Undefined", 0.0, 0.0,
                isValid = false,
                errorMessage = "Base b must be > 0 and ≠ 1, and argument x must be > 0.",
                steps = emptyList(), notes = emptyList()
            )
        }
        val lnX = ln(arg)
        val lnB = ln(base)
        val res = lnX / lnB
        val resStr = fmt(res)
        val steps = listOf(
            EvaluationStep(
                1, "Apply Change of Base Formula",
                "log_${fmt(base)}(${fmt(arg)}) = ln(${fmt(arg)}) / ln(${fmt(base)})",
                "Change of Base Identity",
                "${fmt(lnX)} / ${fmt(lnB)} = $resStr",
                "log_${fmt(base)}(${fmt(arg)}) = $resStr",
                "Any logarithm base b can be computed as the ratio of natural logarithms ln(x) / ln(b)."
            ),
            EvaluationStep(
                2, "Exponential Verification",
                "${fmt(base)}^($resStr) = ${fmt(arg)}",
                "Inverse Property: b^(log_b x) = x",
                "Verified: ${fmt(base.pow(res))}",
                "Result = $resStr",
                "Raising base ${fmt(base)} to the power $resStr recovers the argument ${fmt(arg)}."
            )
        )
        val notes = listOf(
            MathNote(
                "Definition of Logarithm",
                "y = log_b(x) is equivalent to b^y = x. Valid for real numbers when x > 0, b > 0, and b ≠ 1.",
                "log_b(x) = ln(x) / ln(b)"
            )
        )
        return LogarithmSolution(base, arg, res, resStr, lnX, lnB, true, null, steps, notes)
    }

    // -------------------------------------------------------------
    // 7. COMBINATORICS SOLVER: nPr, nCr & FACTORIALS
    // -------------------------------------------------------------
    data class CombinatoricsSolution(
        val n: Long,
        val r: Long,
        val nPr: Double,
        val nCr: Double,
        val singleOutcomeProb: String,
        val isValid: Boolean = true,
        val errorMessage: String? = null,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    fun solveCombinatorics(n: Long, r: Long): CombinatoricsSolution {
        if (n < 0 || r < 0 || r > n || n > 170) {
            return CombinatoricsSolution(
                n, r, 0.0, 0.0, "N/A",
                isValid = false,
                errorMessage = "Ensure 0 ≤ r ≤ n and n ≤ 170.",
                steps = emptyList(), notes = emptyList()
            )
        }
        val npr = AdvancedSolvers.permutations(n, r)
        val ncr = AdvancedSolvers.combinations(n, r)
        val probStr = if (ncr > 0.0 && ncr.isFinite()) {
            val pct = (1.0 / ncr) * 100.0
            "1 in ${fmt(ncr)} (${fmt(pct)}%)"
        } else "N/A"

        val steps = listOf(
            EvaluationStep(
                1, "Permutations (Order Matters): nPr",
                "P($n, $r) = $n! / ($n - $r)!",
                "Permutation Formula",
                "${fmt(npr)} ordered arrangements",
                "nPr = ${fmt(npr)}",
                "Counts ways to pick and arrange $r items out of $n distinct items where order matters."
            ),
            EvaluationStep(
                2, "Combinations (Order Does Not Matter): nCr",
                "C($n, $r) = $n! / [$r! × ($n - $r)!]",
                "Binomial Coefficient",
                "${fmt(ncr)} unique subsets",
                "nCr = ${fmt(ncr)}",
                "Divides nPr by r! (${fmt(AdvancedSolvers.permutations(r, r))}) to remove duplicate orderings."
            )
        )
        val notes = listOf(
            MathNote(
                "Permutations vs. Combinations",
                "Use Permutations (nPr) when order matters (e.g., race podium, PIN codes). Use Combinations (nCr) when order does not matter (e.g., lottery tickets, teams).",
                "nCr = nPr / r!"
            )
        )
        return CombinatoricsSolution(n, r, npr, ncr, probStr, true, null, steps, notes)
    }
}
