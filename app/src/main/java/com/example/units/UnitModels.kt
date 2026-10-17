package com.example.units

enum class UnitCategory(val displayName: String, val iconName: String) {
    LENGTH("Length", "Straighten"),
    MASS("Weight & Mass", "FitnessCenter"),
    TEMPERATURE("Temperature", "Thermostat"),
    AREA("Area", "SquareFoot"),
    VOLUME("Volume", "LocalDrink"),
    SPEED("Speed", "Speed"),
    TIME("Time", "Schedule"),
    DIGITAL("Digital Data", "Memory"),
    PRESSURE("Pressure", "Compress"),
    ENERGY("Energy", "Bolt")
}

data class ConversionUnit(
    val id: String,
    val name: String,
    val symbol: String,
    val category: UnitCategory,
    // Factor relative to category base unit: value_in_base = value * toBaseFactor
    val toBaseFactor: Double = 1.0,
    val isTemperature: Boolean = false
)

data class ConversionResult(
    val inputValue: Double,
    val fromUnit: ConversionUnit,
    val toUnit: ConversionUnit,
    val outputValue: Double,
    val formulaDescription: String,
    val steps: List<String>,
    val comparisonList: List<UnitComparisonItem>
)

data class UnitComparisonItem(
    val unit: ConversionUnit,
    val convertedValue: Double,
    val formattedValue: String
)
