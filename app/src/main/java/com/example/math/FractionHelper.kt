package com.example.math

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToLong

object FractionHelper {

    data class Fraction(val numerator: Long, val denominator: Long) {
        override fun toString(): String {
            return if (denominator == 1L) {
                "$numerator"
            } else {
                "$numerator/$denominator"
            }
        }

        fun toMixedString(): String {
            if (denominator == 1L) return "$numerator"
            val whole = numerator / denominator
            val rem = abs(numerator % denominator)
            return if (whole != 0L && rem != 0L) {
                "$whole ${rem}/$denominator"
            } else {
                toString()
            }
        }
    }

    /**
     * Converts a double to a rational fraction using continued fractions.
     * Tolerance is set to 1e-6 to capture common fractions like 1/3, 5/8, etc.
     */
    fun toFraction(value: Double, tolerance: Double = 1.0E-6, maxDenominator: Long = 10000L): Fraction? {
        if (value.isNaN() || value.isInfinite()) return null
        if (abs(value) > 1.0E9) return null

        val sign = if (value < 0) -1L else 1L
        val positiveVal = abs(value)

        var h1 = 1L
        var h0 = 0L
        var k1 = 0L
        var k0 = 1L

        var b = positiveVal
        do {
            val a = floor(b).toLong()
            var aux = h1
            h1 = a * h1 + h0
            h0 = aux
            aux = k1
            k1 = a * k1 + k0
            k0 = aux

            if (k1 > maxDenominator) break
            val approx = h1.toDouble() / k1.toDouble()
            if (abs(positiveVal - approx) < tolerance) {
                return Fraction(sign * h1, k1)
            }
            b = 1.0 / (b - a)
        } while (abs(positiveVal - h1.toDouble() / k1.toDouble()) > tolerance && b.isFinite() && b < 1.0E12)

        val directFraction = Fraction(sign * h1, k1)
        if (abs(positiveVal - h1.toDouble() / k1.toDouble()) < 1.0E-4) {
            return directFraction
        }
        return null
    }
}
