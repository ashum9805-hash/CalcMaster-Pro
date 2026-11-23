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

    @Test
    fun testScientificNotationEvaluation() {
        val result = MathEngine.evaluateWithExplanation("6.022E23 * 2", AngleMode.DEG)
        assertTrue(result.isSuccess)
        assertEquals(1.2044e24, result.resultNumber, 1e20)

        val small = MathEngine.evaluateWithExplanation("1.6E-19 * 10", AngleMode.DEG)
        assertTrue(small.isSuccess)
        assertEquals(1.6e-18, small.resultNumber, 1e-22)
    }

    @Test
    fun testChemistryPhSolver() {
        val sol = com.example.science.ScienceSolvers.solvePhSystem(
            com.example.science.ScienceSolvers.PhInputType.H_PLUS,
            1e-4
        )
        assertTrue(sol.isValid)
        assertEquals(4.0, sol.pH, 1e-6)
        assertEquals(10.0, sol.pOH, 1e-6)
        assertEquals(1e-10, sol.ohMinus, 1e-15)
    }

    @Test
    fun testCircleAndTrigSolvers() {
        val circle = com.example.math.GeometryTrigSolvers.solveCircle(
            com.example.math.GeometryTrigSolvers.CircleInputType.RADIUS,
            5.0,
            60.0,
            AngleMode.DEG
        )
        assertTrue(circle.isValid)
        assertEquals(10.0, circle.diameter, 1e-6)
        assertEquals(Math.PI * 25.0, circle.area, 1e-6)

        val trig = com.example.math.GeometryTrigSolvers.solveTrigAngle(30.0, AngleMode.DEG)
        assertEquals(0.5, trig.sinVal, 1e-6)

        val rightTri = com.example.math.GeometryTrigSolvers.solveRightTriangle(
            com.example.math.GeometryTrigSolvers.RightTriMode.TWO_LEGS,
            3.0,
            4.0
        )
        assertTrue(rightTri.isValid)
        assertEquals(5.0, rightTri.c, 1e-6)
    }

    @Test
    fun testExpandedPhysicsSolvers() {
        // Circular Motion: m=1200 kg, v=20 m/s, r=50 m -> a_c = 8 m/s^2, F_c = 9600 N
        val circ = com.example.science.ScienceSolvers.solvePhysicsCircularMotion(1200.0, 20.0, 50.0)
        assertTrue(circ.isValid)
        assertEquals(9600.0, circ.primaryRawNumber, 1e-6)
        assertTrue(circ.secondaryValue.contains("8"))

        // Force, Weight & Work: m=10 kg, a=4.5 m/s^2, d=12 m, t=3 s -> F=45 N, W=540 J
        val force = com.example.science.ScienceSolvers.solvePhysicsForceWork(10.0, 4.5, 12.0, 3.0)
        assertTrue(force.isValid)
        assertEquals(45.0, force.primaryRawNumber, 1e-6)
        assertTrue(force.secondaryValue.contains("540"))

        // Momentum & Impulse: m=10 kg, u=2, v=8, dt=2 -> J = 60 N*s, F_avg = 30 N
        val mom = com.example.science.ScienceSolvers.solvePhysicsMomentumImpulse(10.0, 2.0, 8.0, 2.0)
        assertTrue(mom.isValid)
        assertEquals(60.0, mom.primaryRawNumber, 1e-6)
        assertTrue(mom.secondaryValue.contains("30"))

        // Projectile: v0=20, angle=30 deg, g=10 -> vy=10, T=2s, H=5m, R=34.641m
        val proj = com.example.science.ScienceSolvers.solvePhysicsProjectile(20.0, 30.0, 10.0)
        assertTrue(proj.isValid)
        assertEquals(20.0 * kotlin.math.cos(Math.toRadians(30.0)) * 2.0, proj.primaryRawNumber, 1e-4)

        // Gravitation: M1=5.972E24, M2=1, r=6.371E6 -> F ~ 9.82 N
        val grav = com.example.science.ScienceSolvers.solvePhysicsGravitation(5.972e24, 1.0, 6.371e6)
        assertTrue(grav.isValid)
        assertEquals(9.82, grav.primaryRawNumber, 0.05)

        // Fluids: m=1000, V=1 -> rho = 1000 kg/m^3
        val fluid = com.example.science.ScienceSolvers.solvePhysicsFluidsPressure(1000.0, 1.0, 1.0, 10.0)
        assertTrue(fluid.isValid)
        assertEquals(1000.0, fluid.primaryRawNumber, 1e-6)

        // Electrostatics: q1=1E-6, q2=1E-6, r=1 -> F ~ 8.98755E-3 N
        val elec = com.example.science.ScienceSolvers.solvePhysicsElectrostatics(1e-6, 1e-6, 1.0)
        assertTrue(elec.isValid)
        assertEquals(8.98755e-3, elec.primaryRawNumber, 1e-6)
    }
}
