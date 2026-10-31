package com.example

import com.example.math.AngleMode
import com.example.math.MathEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testLinearEquationSolving() {
        val result = MathEngine.evaluateWithExplanation("2x + 4 = 10", AngleMode.DEG)
        assertTrue(result.isSuccess)
        assertEquals("x = 3", result.formattedResult)
    }

    @Test
    fun testQuadraticEquationSolving() {
        val result = MathEngine.evaluateWithExplanation("x² - 4 = 0", AngleMode.DEG)
        assertTrue(result.isSuccess)
        assertTrue(result.formattedResult.contains("2") && result.formattedResult.contains("-2"))
    }

    @Test
    fun testTrailingEqualsExpression() {
        val result = MathEngine.evaluateWithExplanation("5 + 3 =", AngleMode.DEG)
        assertTrue(result.isSuccess)
        assertEquals("8", result.formattedResult)
    }

    @Test
    fun testEqualityVerification() {
        val result = MathEngine.evaluateWithExplanation("10 + 5 = 15", AngleMode.DEG)
        assertTrue(result.isSuccess)
        assertTrue(result.formattedResult.startsWith("True"))
    }

    @Test
    fun testEquationQuickPreview() {
        val preview = MathEngine.solveEquationQuickPreview("3x = 12", AngleMode.DEG)
        assertEquals("x = 4", preview)

        val previewTrailing = MathEngine.solveEquationQuickPreview("25 * 4 =", AngleMode.DEG)
        assertEquals("= 100", previewTrailing)
    }

    @Test
    fun testMultiLineContinuousExpression() {
        val result = MathEngine.evaluateWithExplanation("100 + 200 +\n 300", AngleMode.DEG)
        assertTrue(result.isSuccess)
        assertEquals("600", result.formattedResult)
    }

    @Test
    fun testMultiLineLinearSystem() {
        val result = MathEngine.evaluateWithExplanation("2x + y = 8\nx - y = 1", AngleMode.DEG)
        assertTrue(result.isSuccess)
        assertEquals("x = 3, y = 2", result.formattedResult)
    }

    @Test
    fun testMultiLineSequentialAssignment() {
        val result = MathEngine.evaluateWithExplanation("x = 5\n2x + 4", AngleMode.DEG)
        assertTrue(result.isSuccess)
        assertEquals("14", result.formattedResult)
    }
}


