package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.math.AdvancedSolvers
import com.example.math.AngleMode
import com.example.math.MathEngine
import com.example.units.UnitCategory
import com.example.units.UnitConverter
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SciCalc", appName)
    }

    @Test
    fun `test math engine basic arithmetic and order of operations`() {
        val res = MathEngine.evaluateWithExplanation("3 + 5 * 2", AngleMode.DEG)
        assertTrue(res.isSuccess)
        assertEquals("13", res.formattedResult)
        assertTrue(res.steps.isNotEmpty())
    }

    @Test
    fun `test variables x and y substitution`() {
        // With x = 2.0 and y = 3.0: 3x + 2y = 3(2) + 2(3) = 12
        val res = MathEngine.evaluateWithExplanation("3x + 2y", AngleMode.DEG, xVal = 2.0, yVal = 3.0)
        assertTrue(res.isSuccess)
        assertEquals("12", res.formattedResult)
        assertTrue(res.steps.any { it.title.contains("Substitute Variable") })
    }

    @Test
    fun `test quadratic equation solver`() {
        // x^2 - 5x + 6 = 0 -> roots 3 and 2
        val sol = AdvancedSolvers.solveQuadratic(1.0, -5.0, 6.0)
        assertEquals("3", sol.root1Str)
        assertEquals("2", sol.root2Str)
        assertEquals(1.0, sol.discriminant, 0.001)
        assertTrue(sol.steps.isNotEmpty())
    }

    @Test
    fun `test 2x2 linear system solver`() {
        // 2x + y = 8 and x - y = 1 -> x = 3, y = 2
        val sol = AdvancedSolvers.solveLinearSystem(2.0, 1.0, 8.0, 1.0, -1.0, 1.0)
        assertEquals("3", sol.xResult)
        assertEquals("2", sol.yResult)
        assertEquals("Unique Solution", sol.status)
        assertTrue(sol.steps.isNotEmpty())
    }

    @Test
    fun `test summation engine finite and infinite series`() {
        // Sum_{x=1}^5 x^2 = 1 + 4 + 9 + 16 + 25 = 55
        val sol = AdvancedSolvers.evaluateSummation("x^2", "1", "5")
        assertEquals("55", sol.formattedSum)
        assertEquals(55.0, sol.totalSum, 0.001)

        // Infinite geometric series: Sum_{x=1}^∞ (1/2)^x = 1.0
        val infSol = AdvancedSolvers.evaluateSummation("(1/2)^x", "1", "∞")
        assertTrue(infSol.isInfinite)
        assertEquals(1.0, infSol.totalSum, 0.001)
        assertEquals("1", infSol.formattedSum)
    }

    @Test
    fun `test calculus numerical derivative`() {
        // d/dx [x^3] at x = 2 -> 3*(2^2) = 12
        val sol = AdvancedSolvers.numericalDerivative("x^3", 2.0)
        assertEquals(12.0, sol.resultValue, 0.05)
    }

    @Test
    fun `test combinatorics combinations and permutations`() {
        val c = AdvancedSolvers.combinations(5, 2)
        assertEquals(10.0, c, 0.001)

        val p = AdvancedSolvers.permutations(5, 2)
        assertEquals(20.0, p, 0.001)
    }

    @Test
    fun `test unit converter length and temperature`() {
        val mUnit = UnitConverter.units[UnitCategory.LENGTH]!!.first { it.id == "m" }
        val kmUnit = UnitConverter.units[UnitCategory.LENGTH]!!.first { it.id == "km" }
        val lenResult = UnitConverter.convert(2500.0, mUnit, kmUnit)
        assertEquals(2.5, lenResult.outputValue, 0.0001)

        val cUnit = UnitConverter.units[UnitCategory.TEMPERATURE]!!.first { it.id == "c" }
        val fUnit = UnitConverter.units[UnitCategory.TEMPERATURE]!!.first { it.id == "f" }
        val tempResult = UnitConverter.convert(100.0, cUnit, fUnit)
        assertEquals(212.0, tempResult.outputValue, 0.0001)
    }

    @Test
    fun `test mathematical superscripts and radicals`() {
        // 5² = 25
        val resSquare = MathEngine.evaluateWithExplanation("5²", AngleMode.DEG)
        assertTrue(resSquare.isSuccess)
        assertEquals("25", resSquare.formattedResult)

        // 2³ = 8
        val resCube = MathEngine.evaluateWithExplanation("2³", AngleMode.DEG)
        assertEquals("8", resCube.formattedResult)

        // 2¹⁰ = 1024
        val resMultiSup = MathEngine.evaluateWithExplanation("2¹⁰", AngleMode.DEG)
        assertEquals("1024", resMultiSup.formattedResult)

        // Radical √(144) + ∛(27) = 12 + 3 = 15
        val resRadicals = MathEngine.evaluateWithExplanation("√(144) + ∛(27)", AngleMode.DEG)
        assertEquals("15", resRadicals.formattedResult)

        // Variables with superscripts: x² + y³ at x=3, y=2 -> 9 + 8 = 17
        val resVarSup = MathEngine.evaluateWithExplanation("x² + y³", AngleMode.DEG, xVal = 3.0, yVal = 2.0)
        assertEquals("17", resVarSup.formattedResult)
    }

    @Test
    fun `test summation mathematical knowledge and step-by-step reasoning for arithmetic series`() {
        // sum(x, 1, inf, 3x + 2): a1 = 5, a2 = 8, d = 3. Divergence test -> Diverges (∞)
        val res = com.example.math.MathKnowledge.analyzeAndSolveSummation("3x + 2", "1", "∞")
        assertTrue(res.isInfinite)
        assertEquals("Diverges (∞)", res.formattedSum)

        // Verify that steps identify a1, a2, and common difference d
        assertTrue("Steps should identify initial terms", res.steps.any { it.title.contains("Initial Sequence Terms") })
        assertTrue("Steps should calculate common difference d", res.steps.any { it.title.contains("Common Difference") })
        assertTrue("Steps should apply Divergence Test", res.steps.any { it.ruleApplied.contains("Divergence Test") })

        // Finite arithmetic series: sum(x, 1, 5, 3x + 2) = 5 + 8 + 11 + 14 + 17 = 55
        val finiteRes = com.example.math.MathKnowledge.analyzeAndSolveSummation("3x + 2", "1", "5")
        assertFalse(finiteRes.isInfinite)
        assertEquals("55", finiteRes.formattedSum)
        assertTrue(finiteRes.steps.any { it.ruleApplied.contains("Arithmetic") })
    }

    @Test
    fun `test geometric series analysis with common ratio identification and convergence`() {
        // (1/2)^x from x=1 to inf: a1 = 0.5, r = 0.5 < 1 -> S_inf = 0.5 / (1 - 0.5) = 1.0
        val geoRes = com.example.math.MathKnowledge.analyzeAndSolveSummation("(1/2)ˣ", "1", "∞")
        assertTrue(geoRes.isInfinite)
        assertEquals("1", geoRes.formattedSum)
        assertTrue("Steps should calculate common ratio r", geoRes.steps.any { it.title.contains("Common Ratio") })
        assertTrue("Steps should apply infinite geometric series formula", geoRes.steps.any { it.ruleApplied.contains("Geometric") })
    }

    @Test
    fun `test exponential equation solver and logarithmic steps`() {
        // 1 * 2^x = 16 -> x = 4
        val expSol = com.example.math.MathKnowledge.solveExponentialEquation(1.0, 2.0, 16.0)
        assertEquals("4", expSol.formattedResult)
        assertEquals(4.0, expSol.xResult, 0.001)
        assertTrue("Steps should include natural logarithm", expSol.steps.any { it.title.contains("Logarithm") })
        assertTrue("Steps should include verification check", expSol.steps.any { it.title.contains("Verification") })
    }

    @Test
    fun `test calculator expression evaluation of sum syntax with detailed steps`() {
        // User inputs "sum(x, 1, inf, 3x + 2)" directly in main calculator
        val calcRes = MathEngine.evaluateWithExplanation("sum(x, 1, inf, 3x + 2)", AngleMode.DEG)
        assertTrue(calcRes.isSuccess)
        assertEquals("Diverges (∞)", calcRes.formattedResult)
        assertTrue("Steps should be populated from MathKnowledge", calcRes.steps.isNotEmpty())
        assertTrue("Notes should explain divergence", calcRes.notes.isNotEmpty())

        // User inputs finite sum "sum(x, 1, 5, 3x + 2)"
        val finiteCalcRes = MathEngine.evaluateWithExplanation("sum(x, 1, 5, 3x + 2)", AngleMode.DEG)
        assertTrue(finiteCalcRes.isSuccess)
        assertEquals("55", finiteCalcRes.formattedResult)
    }
}
