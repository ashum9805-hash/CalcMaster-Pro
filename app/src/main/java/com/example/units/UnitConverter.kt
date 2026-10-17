package com.example.units

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

object UnitConverter {

    private val symbols = DecimalFormatSymbols(Locale.US)
    private val decimalFormat = DecimalFormat("#,##0.######", symbols)
    private val scientificFormat = DecimalFormat("0.######E0", symbols)

    fun formatNumber(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "∞" else "-∞"
        if (abs(value) < 1.0E-12 && value != 0.0) return "0"
        if ((abs(value) >= 1.0E9 || abs(value) <= 1.0E-5) && value != 0.0) {
            return scientificFormat.format(value)
        }
        return decimalFormat.format(value)
    }

    val units: Map<UnitCategory, List<ConversionUnit>> = mapOf(
        UnitCategory.LENGTH to listOf(
            ConversionUnit("m", "Meter", "m", UnitCategory.LENGTH, 1.0),
            ConversionUnit("km", "Kilometer", "km", UnitCategory.LENGTH, 1000.0),
            ConversionUnit("cm", "Centimeter", "cm", UnitCategory.LENGTH, 0.01),
            ConversionUnit("mm", "Millimeter", "mm", UnitCategory.LENGTH, 0.001),
            ConversionUnit("um", "Micrometer", "µm", UnitCategory.LENGTH, 1.0E-6),
            ConversionUnit("nm", "Nanometer", "nm", UnitCategory.LENGTH, 1.0E-9),
            ConversionUnit("mi", "Mile", "mi", UnitCategory.LENGTH, 1609.344),
            ConversionUnit("yd", "Yard", "yd", UnitCategory.LENGTH, 0.9144),
            ConversionUnit("ft", "Foot", "ft", UnitCategory.LENGTH, 0.3048),
            ConversionUnit("in", "Inch", "in", UnitCategory.LENGTH, 0.0254),
            ConversionUnit("nmi", "Nautical Mile", "NM", UnitCategory.LENGTH, 1852.0)
        ),
        UnitCategory.MASS to listOf(
            ConversionUnit("kg", "Kilogram", "kg", UnitCategory.MASS, 1.0),
            ConversionUnit("g", "Gram", "g", UnitCategory.MASS, 0.001),
            ConversionUnit("mg", "Milligram", "mg", UnitCategory.MASS, 1.0E-6),
            ConversionUnit("t", "Metric Ton", "t", UnitCategory.MASS, 1000.0),
            ConversionUnit("lb", "Pound", "lb", UnitCategory.MASS, 0.45359237),
            ConversionUnit("oz", "Ounce", "oz", UnitCategory.MASS, 0.028349523125),
            ConversionUnit("st", "Stone", "st", UnitCategory.MASS, 6.35029318),
            ConversionUnit("ct", "Carat", "ct", UnitCategory.MASS, 0.0002)
        ),
        UnitCategory.TEMPERATURE to listOf(
            ConversionUnit("c", "Celsius", "°C", UnitCategory.TEMPERATURE, 1.0, isTemperature = true),
            ConversionUnit("f", "Fahrenheit", "°F", UnitCategory.TEMPERATURE, 1.0, isTemperature = true),
            ConversionUnit("k", "Kelvin", "K", UnitCategory.TEMPERATURE, 1.0, isTemperature = true),
            ConversionUnit("r", "Rankine", "°R", UnitCategory.TEMPERATURE, 1.0, isTemperature = true)
        ),
        UnitCategory.AREA to listOf(
            ConversionUnit("sq_m", "Square Meter", "m²", UnitCategory.AREA, 1.0),
            ConversionUnit("sq_km", "Square Kilometer", "km²", UnitCategory.AREA, 1.0E6),
            ConversionUnit("sq_cm", "Square Centimeter", "cm²", UnitCategory.AREA, 0.0001),
            ConversionUnit("ha", "Hectare", "ha", UnitCategory.AREA, 10000.0),
            ConversionUnit("ac", "Acre", "ac", UnitCategory.AREA, 4046.8564224),
            ConversionUnit("sq_mi", "Square Mile", "mi²", UnitCategory.AREA, 2589988.110336),
            ConversionUnit("sq_yd", "Square Yard", "yd²", UnitCategory.AREA, 0.83612736),
            ConversionUnit("sq_ft", "Square Foot", "ft²", UnitCategory.AREA, 0.09290304),
            ConversionUnit("sq_in", "Square Inch", "in²", UnitCategory.AREA, 0.00064516)
        ),
        UnitCategory.VOLUME to listOf(
            ConversionUnit("l", "Liter", "L", UnitCategory.VOLUME, 1.0),
            ConversionUnit("ml", "Milliliter", "mL", UnitCategory.VOLUME, 0.001),
            ConversionUnit("cu_m", "Cubic Meter", "m³", UnitCategory.VOLUME, 1000.0),
            ConversionUnit("gal", "Gallon (US)", "gal", UnitCategory.VOLUME, 3.785411784),
            ConversionUnit("qt", "Quart (US)", "qt", UnitCategory.VOLUME, 0.946352946),
            ConversionUnit("pt", "Pint (US)", "pt", UnitCategory.VOLUME, 0.473176473),
            ConversionUnit("cup", "Cup (US)", "cup", UnitCategory.VOLUME, 0.2365882365),
            ConversionUnit("fl_oz", "Fluid Ounce (US)", "fl oz", UnitCategory.VOLUME, 0.0295735295625),
            ConversionUnit("tbsp", "Tablespoon (US)", "tbsp", UnitCategory.VOLUME, 0.01478676478125),
            ConversionUnit("tsp", "Teaspoon (US)", "tsp", UnitCategory.VOLUME, 0.00492892159375)
        ),
        UnitCategory.SPEED to listOf(
            ConversionUnit("m_s", "Meter per second", "m/s", UnitCategory.SPEED, 1.0),
            ConversionUnit("km_h", "Kilometer per hour", "km/h", UnitCategory.SPEED, 0.277777778),
            ConversionUnit("mph", "Mile per hour", "mph", UnitCategory.SPEED, 0.44704),
            ConversionUnit("knot", "Knot", "kn", UnitCategory.SPEED, 0.514444444),
            ConversionUnit("ft_s", "Foot per second", "ft/s", UnitCategory.SPEED, 0.3048)
        ),
        UnitCategory.TIME to listOf(
            ConversionUnit("s", "Second", "s", UnitCategory.TIME, 1.0),
            ConversionUnit("ms", "Millisecond", "ms", UnitCategory.TIME, 0.001),
            ConversionUnit("us", "Microsecond", "µs", UnitCategory.TIME, 1.0E-6),
            ConversionUnit("min", "Minute", "min", UnitCategory.TIME, 60.0),
            ConversionUnit("h", "Hour", "hr", UnitCategory.TIME, 3600.0),
            ConversionUnit("d", "Day", "d", UnitCategory.TIME, 86400.0),
            ConversionUnit("wk", "Week", "wk", UnitCategory.TIME, 604800.0),
            ConversionUnit("mo", "Month (Avg)", "mo", UnitCategory.TIME, 2629800.0),
            ConversionUnit("yr", "Year (365d)", "yr", UnitCategory.TIME, 31536000.0)
        ),
        UnitCategory.DIGITAL to listOf(
            ConversionUnit("byte", "Byte", "B", UnitCategory.DIGITAL, 1.0),
            ConversionUnit("bit", "Bit", "bit", UnitCategory.DIGITAL, 0.125),
            ConversionUnit("kb", "Kilobyte (decimal)", "KB", UnitCategory.DIGITAL, 1000.0),
            ConversionUnit("kib", "Kibibyte (binary)", "KiB", UnitCategory.DIGITAL, 1024.0),
            ConversionUnit("mb", "Megabyte (decimal)", "MB", UnitCategory.DIGITAL, 1.0E6),
            ConversionUnit("mib", "Mebibyte (binary)", "MiB", UnitCategory.DIGITAL, 1048576.0),
            ConversionUnit("gb", "Gigabyte (decimal)", "GB", UnitCategory.DIGITAL, 1.0E9),
            ConversionUnit("gib", "Gibibyte (binary)", "GiB", UnitCategory.DIGITAL, 1073741824.0),
            ConversionUnit("tb", "Terabyte (decimal)", "TB", UnitCategory.DIGITAL, 1.0E12),
            ConversionUnit("pb", "Petabyte (decimal)", "PB", UnitCategory.DIGITAL, 1.0E15)
        ),
        UnitCategory.PRESSURE to listOf(
            ConversionUnit("pa", "Pascal", "Pa", UnitCategory.PRESSURE, 1.0),
            ConversionUnit("kpa", "Kilopascal", "kPa", UnitCategory.PRESSURE, 1000.0),
            ConversionUnit("bar", "Bar", "bar", UnitCategory.PRESSURE, 100000.0),
            ConversionUnit("psi", "Pounds per sq inch", "psi", UnitCategory.PRESSURE, 6894.757293),
            ConversionUnit("atm", "Standard Atmosphere", "atm", UnitCategory.PRESSURE, 101325.0),
            ConversionUnit("torr", "Torr (mmHg)", "Torr", UnitCategory.PRESSURE, 133.3223684)
        ),
        UnitCategory.ENERGY to listOf(
            ConversionUnit("j", "Joule", "J", UnitCategory.ENERGY, 1.0),
            ConversionUnit("kj", "Kilojoule", "kJ", UnitCategory.ENERGY, 1000.0),
            ConversionUnit("cal", "Calorie", "cal", UnitCategory.ENERGY, 4.184),
            ConversionUnit("kcal", "Kilocalorie (food)", "kcal", UnitCategory.ENERGY, 4184.0),
            ConversionUnit("wh", "Watt-hour", "Wh", UnitCategory.ENERGY, 3600.0),
            ConversionUnit("kwh", "Kilowatt-hour", "kWh", UnitCategory.ENERGY, 3600000.0),
            ConversionUnit("ev", "Electronvolt", "eV", UnitCategory.ENERGY, 1.602176634E-19),
            ConversionUnit("btu", "BTU", "BTU", UnitCategory.ENERGY, 1055.056)
        )
    )

    fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): ConversionResult {
        val steps = mutableListOf<String>()
        val output: Double
        val formulaDesc: String

        if (from.isTemperature) {
            // Temperature conversion logic
            val celsius = when (from.id) {
                "c" -> value
                "f" -> (value - 32.0) * (5.0 / 9.0)
                "k" -> value - 273.15
                "r" -> (value - 491.67) * (5.0 / 9.0)
                else -> value
            }

            output = when (to.id) {
                "c" -> celsius
                "f" -> (celsius * (9.0 / 5.0)) + 32.0
                "k" -> celsius + 273.15
                "r" -> (celsius + 273.15) * (9.0 / 5.0)
                else -> celsius
            }

            formulaDesc = getTemperatureFormula(from.id, to.id)
            steps.add("Step 1: Identify temperature conversion formula: $formulaDesc")
            if (from.id != "c") {
                steps.add("Step 2: Convert source temperature $value ${from.symbol} to Celsius (°C) = ${formatNumber(celsius)} °C")
            }
            steps.add("Step 3: Convert to target unit ${to.name} (${to.symbol}) using target equation = ${formatNumber(output)} ${to.symbol}")
        } else {
            // Standard linear conversion through category base unit
            val baseValue = value * from.toBaseFactor
            output = baseValue / to.toBaseFactor

            val ratio = from.toBaseFactor / to.toBaseFactor
            formulaDesc = "1 ${from.symbol} = ${formatNumber(ratio)} ${to.symbol}"

            steps.add("Step 1: Convert $value ${from.symbol} to base unit: $value × ${formatNumber(from.toBaseFactor)} = ${formatNumber(baseValue)}")
            steps.add("Step 2: Convert base value to target unit ${to.symbol}: ${formatNumber(baseValue)} ÷ ${formatNumber(to.toBaseFactor)} = ${formatNumber(output)} ${to.symbol}")
            steps.add("Step 3: Direct conversion multiplier: multiply by ${formatNumber(ratio)}")
        }

        // Build comparison table for all units in category
        val categoryUnits = units[from.category] ?: emptyList()
        val comparisons = categoryUnits.map { targetUnit ->
            val compVal = if (from.isTemperature) {
                convertTemp(value, from.id, targetUnit.id)
            } else {
                (value * from.toBaseFactor) / targetUnit.toBaseFactor
            }
            UnitComparisonItem(
                unit = targetUnit,
                convertedValue = compVal,
                formattedValue = formatNumber(compVal)
            )
        }

        return ConversionResult(
            inputValue = value,
            fromUnit = from,
            toUnit = to,
            outputValue = output,
            formulaDescription = formulaDesc,
            steps = steps,
            comparisonList = comparisons
        )
    }

    private fun convertTemp(value: Double, fromId: String, toId: String): Double {
        val c = when (fromId) {
            "c" -> value
            "f" -> (value - 32.0) * (5.0 / 9.0)
            "k" -> value - 273.15
            "r" -> (value - 491.67) * (5.0 / 9.0)
            else -> value
        }
        return when (toId) {
            "c" -> c
            "f" -> (c * (9.0 / 5.0)) + 32.0
            "k" -> c + 273.15
            "r" -> (c + 273.15) * (9.0 / 5.0)
            else -> c
        }
    }

    private fun getTemperatureFormula(fromId: String, toId: String): String {
        return when ("${fromId}_to_${toId}") {
            "c_to_f" -> "°F = (°C × 9/5) + 32"
            "f_to_c" -> "°C = (°F - 32) × 5/9"
            "c_to_k" -> "K = °C + 273.15"
            "k_to_c" -> "°C = K - 273.15"
            "f_to_k" -> "K = (°F - 32) × 5/9 + 273.15"
            "k_to_f" -> "°F = (K - 273.15) × 9/5 + 32"
            "c_to_r" -> "°R = (°C + 273.15) × 9/5"
            "r_to_c" -> "°C = (°R - 491.67) × 5/9"
            else -> "Direct identity: 1:1"
        }
    }
}
