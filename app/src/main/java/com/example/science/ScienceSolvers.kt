package com.example.science

import com.example.math.AdvancedSolvers
import com.example.math.EvaluationStep
import com.example.math.MathEngine
import com.example.math.MathNote
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

data class ScientificConstant(
    val symbol: String,
    val name: String,
    val valueStr: String,
    val displayValue: String,
    val unit: String,
    val domain: String,
    val description: String
)

object ScienceSolvers {

    private val sciFormat = DecimalFormat("0.####E0", DecimalFormatSymbols(Locale.US))

    fun fmt(v: Double): String = MathEngine.formatNumber(v)

    fun fmtSci(v: Double): String {
        if (!v.isFinite()) return AdvancedSolvers.format(v)
        if (v == 0.0) return "0"
        return if (abs(v) < 1e-3 || abs(v) >= 1e5) sciFormat.format(v) else AdvancedSolvers.format(v)
    }

    val physicsConstants = listOf(
        ScientificConstant("c", "Speed of Light (Vacuum)", "2.99792458E8", "2.998 × 10⁸", "m/s", "Physics", "Exact speed of light in vacuum"),
        ScientificConstant("g", "Standard Gravity", "9.80665", "9.80665", "m/s²", "Physics", "Standard gravitational acceleration on Earth"),
        ScientificConstant("G", "Gravitational Constant", "6.6743E-11", "6.6743 × 10⁻¹¹", "m³/(kg·s²)", "Physics", "Newtonian constant of gravitation"),
        ScientificConstant("h", "Planck's Constant", "6.62607015E-34", "6.626 × 10⁻³⁴", "J·s", "Physics", "Quantum of electromagnetic action"),
        ScientificConstant("ħ", "Reduced Planck Constant", "1.0545718E-34", "1.055 × 10⁻³⁴", "J·s", "Physics", "h / 2π, Dirac constant"),
        ScientificConstant("e", "Elementary Charge", "1.602176634E-19", "1.602 × 10⁻¹⁹", "C", "Physics", "Electric charge of a single proton"),
        ScientificConstant("mₑ", "Electron Mass", "9.1093837E-31", "9.109 × 10⁻³¹", "kg", "Physics", "Rest mass of an electron"),
        ScientificConstant("mₚ", "Proton Mass", "1.6726219E-27", "1.673 × 10⁻²⁷", "kg", "Physics", "Rest mass of a proton"),
        ScientificConstant("k_B", "Boltzmann Constant", "1.380649E-23", "1.381 × 10⁻²³", "J/K", "Physics", "Relates average kinetic energy to temperature"),
        ScientificConstant("ε₀", "Vacuum Permittivity", "8.8541878E-12", "8.854 × 10⁻¹²", "F/m", "Physics", "Electric constant of free space"),
        ScientificConstant("μ₀", "Vacuum Permeability", "1.256637E-6", "1.257 × 10⁻⁶", "N/A²", "Physics", "Magnetic constant of free space"),
        ScientificConstant("σ", "Stefan-Boltzmann", "5.670374E-8", "5.670 × 10⁻⁸", "W/(m²·K⁴)", "Physics", "Blackbody radiation constant"),
        ScientificConstant("atm", "Standard Atmosphere", "101325", "101,325", "Pa", "Physics", "Standard atmospheric pressure at sea level"),
        ScientificConstant("eV", "Electronvolt in Joules", "1.602176634E-19", "1.602 × 10⁻¹⁹", "J", "Physics", "Energy gained by 1 electron across 1 Volt")
    )

    val chemistryConstants = listOf(
        ScientificConstant("N_A", "Avogadro's Number", "6.02214076E23", "6.022 × 10²³", "mol⁻¹", "Chemistry", "Number of particles per mole"),
        ScientificConstant("R_SI", "Gas Constant (SI)", "8.3144626", "8.3145", "J/(mol·K)", "Chemistry", "Universal gas constant in Joules"),
        ScientificConstant("R_atm", "Gas Constant (atm)", "0.08205736", "0.08206", "L·atm/(mol·K)", "Chemistry", "Ideal gas constant for atm & Liters"),
        ScientificConstant("F", "Faraday Constant", "96485.332", "96,485.3", "C/mol", "Chemistry", "Electric charge of one mole of electrons"),
        ScientificConstant("K_w", "Water Ion Product (25°C)", "1.0E-14", "1.0 × 10⁻¹⁴", "M²", "Chemistry", "Autoionization constant [H⁺][OH⁻] at 25°C"),
        ScientificConstant("V_m", "Molar Volume (STP)", "22.414", "22.414", "L/mol", "Chemistry", "Volume of 1 mol ideal gas at 0°C, 1 atm"),
        ScientificConstant("K₀", "Kelvin Offset (0°C)", "273.15", "273.15", "K", "Chemistry", "Add to °C to convert to Kelvin"),
        ScientificConstant("u", "Atomic Mass Unit (Da)", "1.660539E-27", "1.661 × 10⁻²⁷", "kg", "Chemistry", "1/12 the mass of a Carbon-12 atom"),
        ScientificConstant("H₂O", "Molar Mass: Water", "18.015", "18.015", "g/mol", "Molar Mass", "Molecular weight of H₂O"),
        ScientificConstant("CO₂", "Molar Mass: Carbon Dioxide", "44.01", "44.01", "g/mol", "Molar Mass", "Molecular weight of CO₂"),
        ScientificConstant("NaCl", "Molar Mass: Sodium Chloride", "58.44", "58.44", "g/mol", "Molar Mass", "Formula weight of table salt"),
        ScientificConstant("HCl", "Molar Mass: Hydrochloric Acid", "36.46", "36.46", "g/mol", "Molar Mass", "Molecular weight of HCl"),
        ScientificConstant("NaOH", "Molar Mass: Sodium Hydroxide", "40.00", "40.00", "g/mol", "Molar Mass", "Formula weight of NaOH"),
        ScientificConstant("H₂SO₄", "Molar Mass: Sulfuric Acid", "98.079", "98.079", "g/mol", "Molar Mass", "Molecular weight of H₂SO₄"),
        ScientificConstant("C₆H₁₂O₆", "Molar Mass: Glucose", "180.156", "180.156", "g/mol", "Molar Mass", "Molecular weight of Glucose")
    )

    // -------------------------------------------------------------
    // 1. CHEMISTRY: pH / pOH / [H+] / [OH-] 4-WAY SOLVER
    // -------------------------------------------------------------
    enum class PhInputType(val label: String, val unitHint: String, val placeholder: String) {
        PH("pH", "0 – 14 scale", "3.5"),
        POH("pOH", "0 – 14 scale", "10.5"),
        H_PLUS("[H⁺] Concentration", "mol/L (M)", "0.001"),
        OH_MINUS("[OH⁻] Concentration", "mol/L (M)", "1E-10")
    }

    data class PhSolution(
        val pH: Double,
        val pOH: Double,
        val hPlus: Double,
        val ohMinus: Double,
        val classification: String,
        val isValid: Boolean = true,
        val errorMessage: String? = null,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    fun solvePhSystem(inputType: PhInputType, value: Double): PhSolution {
        fun invalid(msg: String) = PhSolution(
            0.0, 0.0, 0.0, 0.0, "Invalid",
            isValid = false, errorMessage = msg, steps = emptyList(), notes = emptyList()
        )
        if (!value.isFinite()) return invalid("Please enter a valid number.")

        val pH: Double
        val pOH: Double
        val hPlus: Double
        val ohMinus: Double
        val steps = mutableListOf<EvaluationStep>()

        when (inputType) {
            PhInputType.PH -> {
                pH = value
                pOH = 14.0 - pH
                hPlus = 10.0.pow(-pH)
                ohMinus = 10.0.pow(-pOH)
                steps.add(EvaluationStep(1, "Derive pOH from pH", "pOH = 14 - pH = 14 - ${fmt(pH)}", "Water Ion Product at 25°C (pH + pOH = 14)", fmt(pOH), "pOH = ${fmt(pOH)}", "At 25°C, pKw = 14."))
                steps.add(EvaluationStep(2, "Compute [H⁺] & [OH⁻]", "[H⁺] = 10^(-pH),  [OH⁻] = 10^(-pOH)", "Inverse Base-10 Logarithm", "[H⁺] = ${fmtSci(hPlus)} M, [OH⁻] = ${fmtSci(ohMinus)} M", "[H⁺] = ${fmtSci(hPlus)} M", "Convert logarithmic p-scale back to molarity (mol/L)."))
            }
            PhInputType.POH -> {
                pOH = value
                pH = 14.0 - pOH
                ohMinus = 10.0.pow(-pOH)
                hPlus = 10.0.pow(-pH)
                steps.add(EvaluationStep(1, "Derive pH from pOH", "pH = 14 - pOH = 14 - ${fmt(pOH)}", "pH + pOH = 14 (25°C)", fmt(pH), "pH = ${fmt(pH)}", "Subtract pOH from 14 to obtain pH."))
                steps.add(EvaluationStep(2, "Compute [H⁺] & [OH⁻]", "[H⁺] = 10^(-pH),  [OH⁻] = 10^(-pOH)", "Exponential Concentration", "[H⁺] = ${fmtSci(hPlus)} M, [OH⁻] = ${fmtSci(ohMinus)} M", "[OH⁻] = ${fmtSci(ohMinus)} M", "Raise 10 to negative pH and negative pOH."))
            }
            PhInputType.H_PLUS -> {
                if (value <= 0.0) return invalid("Molarity [H⁺] must be strictly positive (> 0).")
                hPlus = value
                pH = -log10(hPlus)
                pOH = 14.0 - pH
                ohMinus = 1.0e-14 / hPlus
                steps.add(EvaluationStep(1, "Calculate pH from [H⁺]", "pH = -log₁₀(${fmtSci(hPlus)})", "Sørenson pH Definition", fmt(pH), "pH = ${fmt(pH)}", "Take the negative base-10 logarithm of hydronium molarity."))
                steps.add(EvaluationStep(2, "Calculate pOH & [OH⁻]", "pOH = 14 - ${fmt(pH)} = ${fmt(pOH)},  [OH⁻] = K_w / [H⁺]", "Water Autoionization K_w = 10⁻¹⁴", "[OH⁻] = ${fmtSci(ohMinus)} M", "pOH = ${fmt(pOH)}", "Since [H⁺][OH⁻] = 1.0×10⁻¹⁴, divide Kw by [H⁺]."))
            }
            PhInputType.OH_MINUS -> {
                if (value <= 0.0) return invalid("Molarity [OH⁻] must be strictly positive (> 0).")
                ohMinus = value
                pOH = -log10(ohMinus)
                pH = 14.0 - pOH
                hPlus = 1.0e-14 / ohMinus
                steps.add(EvaluationStep(1, "Calculate pOH from [OH⁻]", "pOH = -log₁₀(${fmtSci(ohMinus)})", "pOH Definition", fmt(pOH), "pOH = ${fmt(pOH)}", "Take the negative base-10 logarithm of hydroxide concentration."))
                steps.add(EvaluationStep(2, "Calculate pH & [H⁺]", "pH = 14 - ${fmt(pOH)} = ${fmt(pH)},  [H⁺] = K_w / [OH⁻]", "pH + pOH = 14 (25°C)", "[H⁺] = ${fmtSci(hPlus)} M", "pH = ${fmt(pH)}", "Relate hydroxide to hydronium via Kw."))
            }
        }

        val classification = when {
            pH < 3.0 -> "Strongly Acidic"
            pH < 6.8 -> "Weakly Acidic"
            pH <= 7.2 -> "Neutral"
            pH <= 11.0 -> "Weakly Basic (Alkaline)"
            else -> "Strongly Basic (Alkaline)"
        }

        val notes = listOf(
            MathNote("Acid-Base Equilibrium (25°C)", "Pure water autoionizes such that [H⁺][OH⁻] = 1.0 × 10⁻¹⁴ M². Each 1 unit change in pH corresponds to a 10× change in [H⁺] concentration.", "pH = -log₁₀[H⁺],  pH + pOH = 14")
        )

        return PhSolution(pH, pOH, hPlus, ohMinus, classification, true, null, steps, notes)
    }

    // -------------------------------------------------------------
    // 2. CHEMISTRY: IDEAL GAS LAW (PV = nRT)
    // -------------------------------------------------------------
    enum class GasSolveTarget(val label: String, val unit: String) {
        PRESSURE("Pressure (P)", "atm"),
        VOLUME("Volume (V)", "L"),
        MOLES("Moles (n)", "mol"),
        TEMP("Temperature (T)", "K")
    }

    data class IdealGasSolution(
        val target: GasSolveTarget,
        val resultValue: Double,
        val pressureAtm: Double,
        val volumeL: Double,
        val moles: Double,
        val tempK: Double,
        val isValid: Boolean = true,
        val errorMessage: String? = null,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    fun solveIdealGas(target: GasSolveTarget, p: Double, v: Double, n: Double, t: Double): IdealGasSolution {
        val R = 0.08205736
        fun invalid(msg: String) = IdealGasSolution(target, 0.0, p, v, n, t, false, msg, emptyList(), emptyList())

        return when (target) {
            GasSolveTarget.PRESSURE -> {
                if (v <= 0 || n <= 0 || t <= 0) return invalid("Volume, moles, and Kelvin temperature must be > 0.")
                val res = (n * R * t) / v
                IdealGasSolution(
                    target, res, res, v, n, t, true, null,
                    steps = listOf(EvaluationStep(1, "Solve PV = nRT for Pressure", "P = (n·R·T) / V = (${fmt(n)} × 0.08206 × ${fmt(t)}) / ${fmt(v)}", "Ideal Gas Law", "${fmt(res)} atm", "P = ${fmt(res)} atm", "Uses R = 0.082057 L·atm/(mol·K).")),
                    notes = listOf(MathNote("Ideal Gas Law", "Relates pressure, volume, moles, and absolute temperature (Kelvin = °C + 273.15).", "PV = nRT"))
                )
            }
            GasSolveTarget.VOLUME -> {
                if (p <= 0 || n <= 0 || t <= 0) return invalid("Pressure, moles, and Kelvin temperature must be > 0.")
                val res = (n * R * t) / p
                IdealGasSolution(
                    target, res, p, res, n, t, true, null,
                    steps = listOf(EvaluationStep(1, "Solve PV = nRT for Volume", "V = (n·R·T) / P = (${fmt(n)} × 0.08206 × ${fmt(t)}) / ${fmt(p)}", "Ideal Gas Law", "${fmt(res)} L", "V = ${fmt(res)} L", "Compute volume in Liters.")),
                    notes = listOf(MathNote("Ideal Gas Law", "At STP (1 atm, 273.15 K), 1 mole occupies ~22.414 L.", "V = nRT / P"))
                )
            }
            GasSolveTarget.MOLES -> {
                if (p <= 0 || v <= 0 || t <= 0) return invalid("Pressure, volume, and Kelvin temperature must be > 0.")
                val res = (p * v) / (R * t)
                IdealGasSolution(
                    target, res, p, v, res, t, true, null,
                    steps = listOf(EvaluationStep(1, "Solve PV = nRT for Moles (n)", "n = (P·V) / (R·T) = (${fmt(p)} × ${fmt(v)}) / (0.08206 × ${fmt(t)})", "Ideal Gas Law", "${fmt(res)} mol", "n = ${fmt(res)} mol", "Calculate chemical amount in moles.")),
                    notes = listOf(MathNote("Ideal Gas Law", "Moles n = PV / RT.", "PV = nRT"))
                )
            }
            GasSolveTarget.TEMP -> {
                if (p <= 0 || v <= 0 || n <= 0) return invalid("Pressure, volume, and moles must be > 0.")
                val res = (p * v) / (n * R)
                IdealGasSolution(
                    target, res, p, v, n, res, true, null,
                    steps = listOf(EvaluationStep(1, "Solve PV = nRT for Temperature (T)", "T = (P·V) / (n·R) = (${fmt(p)} × ${fmt(v)}) / (${fmt(n)} × 0.08206)", "Ideal Gas Law", "${fmt(res)} K (${fmt(res - 273.15)} °C)", "T = ${fmt(res)} K", "Temperature is always in absolute Kelvin.")),
                    notes = listOf(MathNote("Absolute Temperature", "Convert Kelvin to Celsius via °C = K - 273.15.", "T = PV / nR"))
                )
            }
        }
    }

    // -------------------------------------------------------------
    // 3. CHEMISTRY: DILUTION FORMULA (M1 * V1 = M2 * V2)
    // -------------------------------------------------------------
    enum class DilutionTarget(val label: String) {
        M1("Initial Molarity (M₁)"),
        V1("Initial Volume (V₁)"),
        M2("Final Molarity (M₂)"),
        V2("Final Volume (V₂)")
    }

    data class DilutionSolution(
        val target: DilutionTarget,
        val resultValue: Double,
        val isValid: Boolean = true,
        val errorMessage: String? = null,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    fun solveDilution(target: DilutionTarget, m1: Double, v1: Double, m2: Double, v2: Double): DilutionSolution {
        fun invalid(msg: String) = DilutionSolution(target, 0.0, false, msg, emptyList(), emptyList())
        return when (target) {
            DilutionTarget.M1 -> {
                if (v1 <= 0 || m2 <= 0 || v2 <= 0) return invalid("All known values must be > 0.")
                val res = (m2 * v2) / v1
                DilutionSolution(target, res, true, null, listOf(EvaluationStep(1, "Solve M₁ = (M₂·V₂)/V₁", "(${fmt(m2)} × ${fmt(v2)}) / ${fmt(v1)}", "Conservation of Solute Moles", "${fmt(res)} M", "M₁ = ${fmt(res)} M", "Total moles remain constant during dilution.")), listOf(MathNote("Dilution Equation", "M₁V₁ = M₂V₂ ensures moles before dilution equal moles after dilution.", "M₁V₁ = M₂V₂")))
            }
            DilutionTarget.V1 -> {
                if (m1 <= 0 || m2 <= 0 || v2 <= 0) return invalid("All known values must be > 0.")
                val res = (m2 * v2) / m1
                DilutionSolution(target, res, true, null, listOf(EvaluationStep(1, "Solve V₁ = (M₂·V₂)/M₁", "(${fmt(m2)} × ${fmt(v2)}) / ${fmt(m1)}", "Conservation of Solute Moles", "${fmt(res)} mL/L", "V₁ = ${fmt(res)}", "Volume of stock solution required.")), listOf(MathNote("Dilution Equation", "Use consistent volume units (both mL or both L) for V₁ and V₂.", "V₁ = M₂V₂ / M₁")))
            }
            DilutionTarget.M2 -> {
                if (m1 <= 0 || v1 <= 0 || v2 <= 0) return invalid("All known values must be > 0.")
                val res = (m1 * v1) / v2
                DilutionSolution(target, res, true, null, listOf(EvaluationStep(1, "Solve M₂ = (M₁·V₁)/V₂", "(${fmt(m1)} × ${fmt(v1)}) / ${fmt(v2)}", "Conservation of Solute Moles", "${fmt(res)} M", "M₂ = ${fmt(res)} M", "Resulting final concentration.")), listOf(MathNote("Dilution Equation", "Adding solvent lowers molarity proportionally.", "M₂ = M₁V₁ / V₂")))
            }
            DilutionTarget.V2 -> {
                if (m1 <= 0 || v1 <= 0 || m2 <= 0) return invalid("All known values must be > 0.")
                val res = (m1 * v1) / m2
                DilutionSolution(target, res, true, null, listOf(EvaluationStep(1, "Solve V₂ = (M₁·V₁)/M₂", "(${fmt(m1)} × ${fmt(v1)}) / ${fmt(m2)}", "Conservation of Solute Moles", "${fmt(res)} mL/L", "V₂ = ${fmt(res)}", "Final total solution volume.")), listOf(MathNote("Dilution Equation", "Solvent added = V₂ - V₁.", "V₂ = M₁V₁ / M₂")))
            }
        }
    }

    // -------------------------------------------------------------
    // 4. PHYSICS FORMULA & PHYSICAL QUANTITY SOLVERS
    // -------------------------------------------------------------
    enum class PhysicsFormulaType(
        val label: String,
        val formulaSummary: String,
        val category: String
    ) {
        KINEMATICS("Kinematics (SUVAT Motion)", "v = u + at,  s = ut + ½at²", "Mechanics & Motion"),
        PROJECTILE("Projectile Motion", "R = v₀²sin(2θ)/g,  H = (v₀sinθ)²/2g", "Mechanics & Motion"),
        FORCE_WORK("Force, Weight, Work & Power", "F = ma,  W = F·d,  P = W/t", "Mechanics & Motion"),
        CIRCULAR_MOTION("Circular Motion (Centripetal)", "a_c = v²/r,  F_c = mv²/r,  ω = v/r", "Circular, Momentum & Energy"),
        MOMENTUM_IMPULSE("Momentum & Impulse", "p = mv,  J = Δp = F_avg·Δt", "Circular, Momentum & Energy"),
        ENERGY("Mechanical Energy (KE & PE)", "E_k = ½mv²,  E_p = mgh,  p = mv", "Circular, Momentum & Energy"),
        GRAVITATION("Universal Gravitation & Orbits", "F = GM₁M₂/r²,  v_esc = √(2GM/r)", "Gravity, Waves & Fluids"),
        OSCILLATIONS("Oscillations (Pendulum & Spring)", "T = 2π√(L/g),  T_s = 2π√(m/k)", "Gravity, Waves & Fluids"),
        FLUIDS_PRESSURE("Density & Fluid Pressure", "ρ = m/V,  P = F/A,  P_h = ρgh", "Gravity, Waves & Fluids"),
        OHMS_LAW("Ohm's Law & DC Power", "I = V / R,  P = V·I", "Electricity & Quantum"),
        ELECTROSTATICS("Coulomb Force & Electric Field", "F_e = k_e·q₁q₂/r²,  E = k_e·q₁/r²", "Electricity & Quantum"),
        PHOTON("Photon & Wave Energy", "E = h·f,  λ = c / f,  p = h/λ", "Electricity & Quantum")
    }

    data class PhysicsFormulaSolution(
        val type: PhysicsFormulaType,
        val primaryLabel: String,
        val primaryValue: String,
        val primaryRawNumber: Double,
        val secondaryLabel: String,
        val secondaryValue: String,
        val extraMetrics: List<Pair<String, String>> = emptyList(),
        val isValid: Boolean = true,
        val errorMessage: String? = null,
        val steps: List<EvaluationStep>,
        val notes: List<MathNote>
    )

    private fun invalidPhysics(type: PhysicsFormulaType, msg: String) = PhysicsFormulaSolution(
        type = type,
        primaryLabel = "Error",
        primaryValue = msg,
        primaryRawNumber = Double.NaN,
        secondaryLabel = "",
        secondaryValue = "",
        isValid = false,
        errorMessage = msg,
        steps = emptyList(),
        notes = emptyList()
    )

    fun solvePhysicsKinematics(u: Double, a: Double, t: Double): PhysicsFormulaSolution {
        val v = u + a * t
        val s = u * t + 0.5 * a * t * t
        val vAvg = (u + v) / 2.0
        return PhysicsFormulaSolution(
            type = PhysicsFormulaType.KINEMATICS,
            primaryLabel = "Final Velocity (v)",
            primaryValue = "${fmtSci(v)} m/s",
            primaryRawNumber = v,
            secondaryLabel = "Displacement (s)",
            secondaryValue = "${fmtSci(s)} m",
            extraMetrics = listOf(
                "Avg Velocity (v_avg)" to "${fmtSci(vAvg)} m/s",
                "Velocity Change (Δv)" to "${fmtSci(v - u)} m/s"
            ),
            steps = listOf(
                EvaluationStep(1, "Final Velocity", "v = u + a·t = ${fmt(u)} + (${fmt(a)})(${fmt(t)})", "First Kinematic Equation", "${fmtSci(v)} m/s", "v = ${fmtSci(v)} m/s", "Assumes constant linear acceleration a."),
                EvaluationStep(2, "Displacement", "s = u·t + ½a·t² = ${fmt(u)}(${fmt(t)}) + 0.5(${fmt(a)})(${fmt(t)})²", "Second Kinematic Equation", "${fmtSci(s)} m", "s = ${fmtSci(s)} m", "Net displacement over time t.")
            ),
            notes = listOf(MathNote("SUVAT Kinematics", "Valid for uniform acceleration: v = u + at, s = ut + ½at², and v² = u² + 2as.", "v² = u² + 2as"))
        )
    }

    fun solvePhysicsProjectile(v0: Double, angleDeg: Double, gInput: Double): PhysicsFormulaSolution {
        if (v0 < 0 || gInput <= 0) return invalidPhysics(PhysicsFormulaType.PROJECTILE, "Speed v₀ must be ≥ 0 and gravity g > 0.")
        val rad = Math.toRadians(angleDeg)
        val vx = v0 * kotlin.math.cos(rad)
        val vy = v0 * kotlin.math.sin(rad)
        val tFlight = if (vy <= 0.0) 0.0 else (2.0 * vy) / gInput
        val maxHeight = if (vy <= 0.0) 0.0 else (vy * vy) / (2.0 * gInput)
        val range = vx * tFlight
        return PhysicsFormulaSolution(
            type = PhysicsFormulaType.PROJECTILE,
            primaryLabel = "Horizontal Range (R)",
            primaryValue = "${fmtSci(range)} m",
            primaryRawNumber = range,
            secondaryLabel = "Max Height (H_max)",
            secondaryValue = "${fmtSci(maxHeight)} m",
            extraMetrics = listOf(
                "Time of Flight (T)" to "${fmtSci(tFlight)} s",
                "Velocity (vₓ, v_y)" to "${fmtSci(vx)}, ${fmtSci(vy)} m/s"
            ),
            steps = listOf(
                EvaluationStep(1, "Velocity Components", "v₀ₓ = ${fmt(v0)}·cos(${fmt(angleDeg)}°) = ${fmtSci(vx)},  v₀_y = ${fmt(v0)}·sin(${fmt(angleDeg)}°) = ${fmtSci(vy)}", "Vector Decomposition", "vₓ=${fmtSci(vx)}, v_y=${fmtSci(vy)} m/s", "Components resolved", "Horizontal speed stays constant; vertical speed accelerates at -g."),
                EvaluationStep(2, "Time of Flight & Peak", "T = 2v₀_y / g = ${fmtSci(tFlight)} s,  H_max = v₀_y² / (2g) = ${fmtSci(maxHeight)} m", "Vertical Kinematics", "H = ${fmtSci(maxHeight)} m", "T = ${fmtSci(tFlight)} s", "Vertical velocity equals 0 at apex (t = T/2)."),
                EvaluationStep(3, "Horizontal Range", "R = v₀ₓ · T = ${fmtSci(vx)} × ${fmtSci(tFlight)}", "Horizontal Motion", "${fmtSci(range)} m", "R = ${fmtSci(range)} m", "Maximum range occurs at 45° on level ground.")
            ),
            notes = listOf(MathNote("Projectile Trajectory", "Neglecting air resistance, the trajectory is parabolic: y(x) = x·tanθ - (gx²)/(2v₀²cos²θ).", "R = v₀²sin(2θ) / g"))
        )
    }

    fun solvePhysicsForceWork(massKg: Double, accelMs2: Double, distanceM: Double, timeSec: Double): PhysicsFormulaSolution {
        if (massKg < 0) return invalidPhysics(PhysicsFormulaType.FORCE_WORK, "Mass must be ≥ 0.")
        val g = 9.80665
        val netForce = massKg * accelMs2
        val weightEarth = massKg * g
        val workJ = netForce * distanceM
        val powerW = if (abs(timeSec) < 1e-12) Double.NaN else workJ / timeSec
        return PhysicsFormulaSolution(
            type = PhysicsFormulaType.FORCE_WORK,
            primaryLabel = "Net Force (F = m·a)",
            primaryValue = "${fmtSci(netForce)} N",
            primaryRawNumber = netForce,
            secondaryLabel = "Mechanical Work (W = F·d)",
            secondaryValue = "${fmtSci(workJ)} J",
            extraMetrics = listOf(
                "Weight on Earth (mg)" to "${fmtSci(weightEarth)} N",
                "Power (P = W/t)" to if (powerW.isNaN()) "Undefined (t=0)" else "${fmtSci(powerW)} W"
            ),
            steps = listOf(
                EvaluationStep(1, "Newton's Second Law Force", "F = m · a = ${fmt(massKg)} × ${fmt(accelMs2)}", "F = m·a", "${fmtSci(netForce)} N", "F = ${fmtSci(netForce)} N", "1 Newton = 1 kg·m/s². Earth weight W = m·g = ${fmtSci(weightEarth)} N."),
                EvaluationStep(2, "Mechanical Work Done", "W = F · d = ${fmtSci(netForce)} × ${fmt(distanceM)}", "Work Formula (θ = 0°)", "${fmtSci(workJ)} J", "W = ${fmtSci(workJ)} J", "Work transfers energy over displacement d."),
                EvaluationStep(3, "Average Power", "P = W / t = ${fmtSci(workJ)} / ${fmt(timeSec)}", "Rate of Work", if (powerW.isNaN()) "N/A" else "${fmtSci(powerW)} W", "P = ${if (powerW.isNaN()) "N/A" else fmtSci(powerW)} W", "1 Watt = 1 Joule per second.")
            ),
            notes = listOf(MathNote("Newton's 2nd Law & Work", "Net force produces acceleration proportional to mass (F = ma). Work done by a parallel force is W = Fd.", "1 N = 1 kg·m/s²,  1 W = 1 J/s"))
        )
    }

    fun solvePhysicsCircularMotion(massKg: Double, speedMs: Double, radiusM: Double): PhysicsFormulaSolution {
        if (radiusM <= 0 || massKg < 0) return invalidPhysics(PhysicsFormulaType.CIRCULAR_MOTION, "Radius r must be > 0 and mass m ≥ 0.")
        val ac = (speedMs * speedMs) / radiusM
        val fc = massKg * ac
        val omega = speedMs / radiusM
        val period = if (abs(speedMs) < 1e-12) Double.POSITIVE_INFINITY else (2.0 * Math.PI * radiusM) / abs(speedMs)
        val freq = if (period.isInfinite() || period == 0.0) 0.0 else 1.0 / period
        return PhysicsFormulaSolution(
            type = PhysicsFormulaType.CIRCULAR_MOTION,
            primaryLabel = "Centripetal Force (F_c)",
            primaryValue = "${fmtSci(fc)} N",
            primaryRawNumber = fc,
            secondaryLabel = "Centripetal Accel (a_c)",
            secondaryValue = "${fmtSci(ac)} m/s²",
            extraMetrics = listOf(
                "Angular Vel (ω = v/r)" to "${fmtSci(omega)} rad/s",
                "Period (T) & Freq (f)" to "${fmtSci(period)} s  (${fmtSci(freq)} Hz)"
            ),
            steps = listOf(
                EvaluationStep(1, "Centripetal Acceleration", "a_c = v² / r = (${fmt(speedMs)})² / ${fmt(radiusM)}", "Uniform Circular Acceleration", "${fmtSci(ac)} m/s²", "a_c = ${fmtSci(ac)} m/s²", "Directed radially inward toward the center of the circle."),
                EvaluationStep(2, "Centripetal Force", "F_c = m · a_c = ${fmt(massKg)} × ${fmtSci(ac)}", "Newton's 2nd Law (Radial)", "${fmtSci(fc)} N", "F_c = ${fmtSci(fc)} N", "Required inward net force to keep mass m on circular path of radius r."),
                EvaluationStep(3, "Angular Speed, Period & Frequency", "ω = v/r = ${fmtSci(omega)} rad/s,  T = 2πr/v = ${fmtSci(period)} s,  f = 1/T = ${fmtSci(freq)} Hz", "Rotational Kinematics", "ω = ${fmtSci(omega)} rad/s", "T = ${fmtSci(period)} s", "One full revolution covers 2π radians (2πr meters).")
            ),
            notes = listOf(MathNote("Uniform Circular Motion", "Even at constant speed v, velocity direction continuously changes, creating inward centripetal acceleration a_c = v²/r = ω²r.", "F_c = mv² / r = mω²r"))
        )
    }

    fun solvePhysicsMomentumImpulse(massKg: Double, uMs: Double, vMs: Double, deltaT: Double): PhysicsFormulaSolution {
        if (massKg < 0) return invalidPhysics(PhysicsFormulaType.MOMENTUM_IMPULSE, "Mass must be ≥ 0.")
        val pi = massKg * uMs
        val pf = massKg * vMs
        val impulse = pf - pi
        val avgForce = if (abs(deltaT) < 1e-12) Double.NaN else impulse / deltaT
        val deltaKe = 0.5 * massKg * (vMs * vMs - uMs * uMs)
        return PhysicsFormulaSolution(
            type = PhysicsFormulaType.MOMENTUM_IMPULSE,
            primaryLabel = "Impulse (J = Δp)",
            primaryValue = "${fmtSci(impulse)} N·s",
            primaryRawNumber = impulse,
            secondaryLabel = "Avg Impact Force (F_avg)",
            secondaryValue = if (avgForce.isNaN()) "Undefined (Δt=0)" else "${fmtSci(avgForce)} N",
            extraMetrics = listOf(
                "Initial / Final (p_i → p_f)" to "${fmtSci(pi)} → ${fmtSci(pf)} kg·m/s",
                "Kinetic Energy ΔE_k" to "${fmtSci(deltaKe)} J"
            ),
            steps = listOf(
                EvaluationStep(1, "Initial & Final Momentum", "p_i = m·u = ${fmtSci(pi)},  p_f = m·v = ${fmtSci(pf)} kg·m/s", "Momentum Definition p = mv", "Δp = ${fmtSci(impulse)} kg·m/s", "J = ${fmtSci(impulse)} N·s", "Impulse equals change in momentum: J = m(v - u)."),
                EvaluationStep(2, "Average Force Over Δt", "F_avg = Δp / Δt = ${fmtSci(impulse)} / ${fmt(deltaT)}", "Impulse-Momentum Theorem", if (avgForce.isNaN()) "N/A" else "${fmtSci(avgForce)} N", "F_avg = ${if (avgForce.isNaN()) "N/A" else fmtSci(avgForce)} N", "Longer impact times Δt reduce peak collision force.")
            ),
            notes = listOf(MathNote("Impulse-Momentum Theorem", "Total momentum is conserved in closed systems. An external impulse J = F_avg·Δt changes momentum by Δp.", "J = Δp = F·Δt"))
        )
    }

    fun solvePhysicsEnergy(m: Double, v: Double, h: Double = 0.0): PhysicsFormulaSolution {
        if (m < 0) return invalidPhysics(PhysicsFormulaType.ENERGY, "Mass must be ≥ 0.")
        val g = 9.80665
        val ek = 0.5 * m * v * v
        val ep = m * g * h
        val etot = ek + ep
        val p = m * v
        return PhysicsFormulaSolution(
            type = PhysicsFormulaType.ENERGY,
            primaryLabel = "Kinetic Energy (E_k)",
            primaryValue = "${fmtSci(ek)} J",
            primaryRawNumber = ek,
            secondaryLabel = "Potential Energy (E_p = mgh)",
            secondaryValue = "${fmtSci(ep)} J",
            extraMetrics = listOf(
                "Total Mech Energy (E_tot)" to "${fmtSci(etot)} J",
                "Linear Momentum (p = mv)" to "${fmtSci(p)} kg·m/s"
            ),
            steps = listOf(
                EvaluationStep(1, "Kinetic Energy", "E_k = ½·m·v² = 0.5 × ${fmt(m)} × (${fmt(v)})²", "Classical Kinetic Energy", "${fmtSci(ek)} J", "E_k = ${fmtSci(ek)} J", "Kinetic energy scales with the square of speed."),
                EvaluationStep(2, "Gravitational Potential & Total", "E_p = m·g·h = ${fmt(m)} × 9.80665 × ${fmt(h)} = ${fmtSci(ep)} J,  E_tot = ${fmtSci(etot)} J", "Mechanical Energy Conservation", "${fmtSci(etot)} J", "p = ${fmtSci(p)} kg·m/s", "Also computes linear momentum p = mv.")
            ),
            notes = listOf(MathNote("Conservation of Mechanical Energy", "Without non-conservative forces like friction, E_k + E_p stays constant during motion.", "E_tot = ½mv² + mgh"))
        )
    }

    fun solvePhysicsGravitation(m1Kg: Double, m2Kg: Double, distanceM: Double): PhysicsFormulaSolution {
        if (distanceM <= 0 || m1Kg < 0 || m2Kg < 0) {
            return invalidPhysics(PhysicsFormulaType.GRAVITATION, "Distance r must be > 0 and masses ≥ 0.")
        }
        val G = 6.6743e-11
        val force = G * m1Kg * m2Kg / (distanceM * distanceM)
        val gField = G * m1Kg / (distanceM * distanceM)
        val vEsc = kotlin.math.sqrt((2.0 * G * m1Kg) / distanceM)
        val vOrb = kotlin.math.sqrt((G * m1Kg) / distanceM)
        return PhysicsFormulaSolution(
            type = PhysicsFormulaType.GRAVITATION,
            primaryLabel = "Gravitational Force (F_g)",
            primaryValue = "${fmtSci(force)} N",
            primaryRawNumber = force,
            secondaryLabel = "Field Strength g (from M₁)",
            secondaryValue = "${fmtSci(gField)} m/s²",
            extraMetrics = listOf(
                "Escape Speed (v_esc)" to "${fmtSci(vEsc)} m/s",
                "Circular Orbit (v_orb)" to "${fmtSci(vOrb)} m/s"
            ),
            steps = listOf(
                EvaluationStep(1, "Newton's Law of Gravitation", "F = G·M₁·M₂ / r² = 6.6743×10⁻¹¹ × ${fmtSci(m1Kg)} × ${fmtSci(m2Kg)} / (${fmtSci(distanceM)})²", "Inverse-Square Law", "${fmtSci(force)} N", "F = ${fmtSci(force)} N", "Attractive force between two masses separated by r."),
                EvaluationStep(2, "Field Strength & Cosmic Velocities", "g = GM₁/r² = ${fmtSci(gField)} m/s²,  v_orb = √(GM₁/r) = ${fmtSci(vOrb)} m/s,  v_esc = √(2GM₁/r) = ${fmtSci(vEsc)} m/s", "Orbital Mechanics", "v_esc = ${fmtSci(vEsc)} m/s", "g = ${fmtSci(gField)} m/s²", "Escape speed is √2 times circular orbital speed at distance r.")
            ),
            notes = listOf(MathNote("Universal Gravitation", "Uses G = 6.6743×10⁻¹¹ m³/(kg·s²). Earth mass ≈ 5.972×10²⁴ kg, radius ≈ 6.371×10⁶ m.", "F = GM₁M₂ / r²"))
        )
    }

    fun solvePhysicsOscillations(lengthM: Double, springK: Double, massKg: Double): PhysicsFormulaSolution {
        if (lengthM < 0 || springK <= 0 || massKg < 0) {
            return invalidPhysics(PhysicsFormulaType.OSCILLATIONS, "Spring constant k must be > 0; length and mass ≥ 0.")
        }
        val g = 9.80665
        val tPend = 2.0 * Math.PI * kotlin.math.sqrt(lengthM / g)
        val fPend = if (tPend > 0) 1.0 / tPend else 0.0
        val tSpring = 2.0 * Math.PI * kotlin.math.sqrt(massKg / springK)
        val omegaSpring = kotlin.math.sqrt(springK / if (massKg > 0) massKg else 1.0)
        return PhysicsFormulaSolution(
            type = PhysicsFormulaType.OSCILLATIONS,
            primaryLabel = "Pendulum Period (T_p)",
            primaryValue = "${fmtSci(tPend)} s (${fmtSci(fPend)} Hz)",
            primaryRawNumber = tPend,
            secondaryLabel = "Spring-Mass Period (T_s)",
            secondaryValue = "${fmtSci(tSpring)} s",
            extraMetrics = listOf(
                "Spring Ang Freq (ω)" to if (massKg > 0) "${fmtSci(omegaSpring)} rad/s" else "N/A",
                "Spring Freq (f_s)" to if (tSpring > 0) "${fmtSci(1.0 / tSpring)} Hz" else "0 Hz"
            ),
            steps = listOf(
                EvaluationStep(1, "Simple Pendulum Period", "T = 2π·√(L / g) = 2π·√(${fmt(lengthM)} / 9.80665)", "Small-Angle Pendulum Approximation", "${fmtSci(tPend)} s", "f_p = ${fmtSci(fPend)} Hz", "Independent of pendulum bob mass for small angles."),
                EvaluationStep(2, "Mass-Spring Oscillator", "T = 2π·√(m / k) = 2π·√(${fmt(massKg)} / ${fmt(springK)})", "Hooke's Law SHM", "${fmtSci(tSpring)} s", "ω = ${fmtSci(omegaSpring)} rad/s", "Period increases with mass and decreases with stiffer springs.")
            ),
            notes = listOf(MathNote("Simple Harmonic Motion", "Restoring force is proportional to displacement (F = -kx), yielding sinusoidal motion with constant frequency.", "ω = 2πf = 2π / T"))
        )
    }

    fun solvePhysicsFluidsPressure(massKg: Double, volumeM3: Double, areaM2: Double, depthM: Double): PhysicsFormulaSolution {
        if (volumeM3 <= 0 || areaM2 <= 0 || massKg < 0 || depthM < 0) {
            return invalidPhysics(PhysicsFormulaType.FLUIDS_PRESSURE, "Volume and area must be > 0; mass and depth ≥ 0.")
        }
        val g = 9.80665
        val density = massKg / volumeM3
        val weightN = massKg * g
        val surfacePressurePa = weightN / areaM2
        val hydrostaticPa = density * g * depthM
        val totalAbsPa = 101325.0 + hydrostaticPa
        return PhysicsFormulaSolution(
            type = PhysicsFormulaType.FLUIDS_PRESSURE,
            primaryLabel = "Density (ρ = m / V)",
            primaryValue = "${fmtSci(density)} kg/m³",
            primaryRawNumber = density,
            secondaryLabel = "Surface Pressure (P = F/A)",
            secondaryValue = "${fmtSci(surfacePressurePa)} Pa",
            extraMetrics = listOf(
                "Hydrostatic (P_h = ρgh)" to "${fmtSci(hydrostaticPa)} Pa",
                "Abs Pressure (P₀ + ρgh)" to "${fmtSci(totalAbsPa)} Pa (${fmt(totalAbsPa / 101325.0)} atm)"
            ),
            steps = listOf(
                EvaluationStep(1, "Mass Density", "ρ = m / V = ${fmt(massKg)} / ${fmt(volumeM3)}", "Density Definition", "${fmtSci(density)} kg/m³", "ρ = ${fmtSci(density)} kg/m³", "Water density is ~1000 kg/m³."),
                EvaluationStep(2, "Solid Weight Pressure", "P = (m·g) / A = ${fmtSci(weightN)} / ${fmt(areaM2)}", "Pressure P = F / A", "${fmtSci(surfacePressurePa)} Pa", "P = ${fmtSci(surfacePressurePa)} Pa", "1 Pascal = 1 N/m²."),
                EvaluationStep(3, "Hydrostatic Gauge Pressure", "P_h = ρ·g·h = ${fmtSci(density)} × 9.80665 × ${fmt(depthM)}", "Pascal's Law (Fluid Column)", "${fmtSci(hydrostaticPa)} Pa", "P_abs = ${fmtSci(totalAbsPa)} Pa", "Add 1 atm (101,325 Pa) for total absolute pressure.")
            ),
            notes = listOf(MathNote("Density & Fluid Statics", "Fluid pressure increases linearly with depth (P = P₀ + ρgh) regardless of container shape.", "1 atm = 101.325 kPa"))
        )
    }

    fun solvePhysicsOhmsLaw(voltage: Double, resistance: Double): PhysicsFormulaSolution {
        val safeR = if (abs(resistance) < 1e-12) 1e-12 else resistance
        val current = voltage / safeR
        val power = voltage * current
        val conductance = 1.0 / safeR
        return PhysicsFormulaSolution(
            type = PhysicsFormulaType.OHMS_LAW,
            primaryLabel = "Current (I = V/R)",
            primaryValue = "${fmtSci(current)} A",
            primaryRawNumber = current,
            secondaryLabel = "Power (P = V·I)",
            secondaryValue = "${fmtSci(power)} W",
            extraMetrics = listOf(
                "Conductance (G = 1/R)" to "${fmtSci(conductance)} S",
                "Heat in 1 min (E = P·60)" to "${fmtSci(power * 60.0)} J"
            ),
            steps = listOf(
                EvaluationStep(1, "Ohm's Law Current", "I = V / R = ${fmt(voltage)} / ${fmt(safeR)}", "Ohm's Law: V = I·R", "${fmtSci(current)} A", "I = ${fmtSci(current)} A", "Current measured in Amperes (C/s)."),
                EvaluationStep(2, "Electrical Power", "P = V·I = ${fmt(voltage)} × ${fmtSci(current)}", "Joule Heating / Circuit Power", "${fmtSci(power)} W", "P = ${fmtSci(power)} W", "Energy dissipated per second in Watts (J/s).")
            ),
            notes = listOf(MathNote("DC Circuit Laws", "Ohm's Law V = IR and Power P = IV = I²R = V²/R.", "V = IR, P = VI"))
        )
    }

    fun solvePhysicsElectrostatics(q1: Double, q2: Double, r: Double): PhysicsFormulaSolution {
        if (r <= 0) return invalidPhysics(PhysicsFormulaType.ELECTROSTATICS, "Distance r must be > 0.")
        val ke = 8.9875517923e9
        val force = (ke * q1 * q2) / (r * r)
        val eField = (ke * q1) / (r * r)
        val potentialEnergy = (ke * q1 * q2) / r
        return PhysicsFormulaSolution(
            type = PhysicsFormulaType.ELECTROSTATICS,
            primaryLabel = "Coulomb Force (F_e)",
            primaryValue = "${fmtSci(force)} N",
            primaryRawNumber = force,
            secondaryLabel = "Electric Field (E from q₁)",
            secondaryValue = "${fmtSci(eField)} N/C",
            extraMetrics = listOf(
                "Potential Energy (U_e)" to "${fmtSci(potentialEnergy)} J",
                "Interaction" to if (force > 0) "Repulsive (+)" else if (force < 0) "Attractive (-)" else "Zero"
            ),
            steps = listOf(
                EvaluationStep(1, "Coulomb Electrostatic Force", "F_e = k_e·q₁·q₂ / r² = 8.988×10⁹ × (${fmtSci(q1)})(${fmtSci(q2)}) / (${fmtSci(r)})²", "Coulomb's Law", "${fmtSci(force)} N", "F_e = ${fmtSci(force)} N", "Positive F indicates repulsion (like signs); negative indicates attraction."),
                EvaluationStep(2, "Electric Field & Potential Energy", "E = k_e·q₁ / r² = ${fmtSci(eField)} N/C,  U = k_e·q₁q₂ / r = ${fmtSci(potentialEnergy)} J", "Electrostatic Field & Energy", "U = ${fmtSci(potentialEnergy)} J", "E = ${fmtSci(eField)} N/C", "Coulomb constant k_e = 1/(4πε₀) ≈ 8.9876×10⁹ N·m²/C².")
            ),
            notes = listOf(MathNote("Coulomb's Law", "Electrostatic force obeys an inverse-square law analogous to Newton's gravitation, but can both attract and repel.", "F_e = k_e·q₁q₂ / r²"))
        )
    }

    fun solvePhysicsPhoton(freqHz: Double): PhysicsFormulaSolution {
        val h = 6.62607015e-34
        val c = 2.99792458e8
        val eCharge = 1.602176634e-19
        val safeF = if (freqHz <= 0.0) 1.0 else freqHz
        val energyJ = h * safeF
        val energyEv = energyJ / eCharge
        val wavelengthM = c / safeF
        val photonMomentum = h / wavelengthM
        return PhysicsFormulaSolution(
            type = PhysicsFormulaType.PHOTON,
            primaryLabel = "Photon Energy (E = hf)",
            primaryValue = "${fmtSci(energyJ)} J (${fmtSci(energyEv)} eV)",
            primaryRawNumber = energyJ,
            secondaryLabel = "Wavelength (λ = c/f)",
            secondaryValue = "${fmtSci(wavelengthM)} m (${fmtSci(wavelengthM * 1e9)} nm)",
            extraMetrics = listOf(
                "Photon Momentum (h/λ)" to "${fmtSci(photonMomentum)} kg·m/s",
                "Wavenumber (1/λ)" to "${fmtSci(1.0 / wavelengthM)} m⁻¹"
            ),
            steps = listOf(
                EvaluationStep(1, "Planck-Einstein Photon Energy", "E = h·f = 6.626×10⁻³⁴ × ${fmtSci(safeF)}", "Planck Relation", "${fmtSci(energyJ)} J = ${fmtSci(energyEv)} eV", "E = ${fmtSci(energyJ)} J", "Divide Joules by e (1.602×10⁻¹⁹) to get electronvolts (eV)."),
                EvaluationStep(2, "Electromagnetic Wavelength", "λ = c / f = 2.998×10⁸ / ${fmtSci(safeF)}", "Wave Speed Relation (c = λf)", "${fmtSci(wavelengthM)} m (${fmtSci(wavelengthM * 1e9)} nm)", "λ = ${fmtSci(wavelengthM)} m", "Visible light ranges roughly from 380 nm to 750 nm.")
            ),
            notes = listOf(MathNote("Quantum Wave-Particle Duality", "Higher frequency light carries more energy per photon and has a shorter wavelength.", "E = hf = hc / λ"))
        )
    }
}
