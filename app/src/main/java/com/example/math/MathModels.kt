package com.example.math

enum class AngleMode {
    DEG,
    RAD
}

data class EvaluationStep(
    val stepNumber: Int,
    val title: String,
    val subExpression: String,
    val ruleApplied: String,
    val intermediateResult: String,
    val expressionAfter: String,
    val note: String
)

data class MathNote(
    val title: String,
    val content: String,
    val formula: String? = null
)

data class AlternativeRepresentations(
    val fraction: String? = null,
    val scientific: String,
    val percentage: String? = null,
    val binary: String? = null,
    val hex: String? = null,
    val octal: String? = null,
    val degreesToRadians: String? = null,
    val radiansToDegrees: String? = null
)

data class CalculationResult(
    val expression: String,
    val resultNumber: Double,
    val formattedResult: String,
    val angleMode: AngleMode,
    val steps: List<EvaluationStep>,
    val notes: List<MathNote>,
    val alternatives: AlternativeRepresentations,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
)
