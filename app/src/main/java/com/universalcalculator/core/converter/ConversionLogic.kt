package com.universalcalculator.core.converter

enum class ConversionCategory {
    CURRENCY, LENGTH, WEIGHT, TEMPERATURE, AREA, SPEED, TIME,
    VOLUME, ENERGY, POWER, DATA, PRESSURE, ANGLE
}

data class UnitItem(val name: String, val factor: Double, val offset: Double = 0.0)

object ConversionLogic {
    private val lengthUnits = listOf(
        UnitItem("Meter", 1.0),
        UnitItem("Kilometer", 1000.0),
        UnitItem("Centimeter", 0.01),
        UnitItem("Millimeter", 0.001),
        UnitItem("Mile", 1609.34),
        UnitItem("Yard", 0.9144),
        UnitItem("Foot", 0.3048),
        UnitItem("Inch", 0.0254)
    )

    private val weightUnits = listOf(
        UnitItem("Kilogram", 1.0),
        UnitItem("Gram", 0.001),
        UnitItem("Milligram", 0.000001),
        UnitItem("Pound", 0.453592),
        UnitItem("Ounce", 0.0283495)
    )

    private val temperatureUnits = listOf(
        UnitItem("Celsius (°C)", 1.0, 0.0),
        UnitItem("Fahrenheit (°F)", 5.0/9.0, -32.0),
        UnitItem("Kelvin (K)", 1.0, -273.15),
        UnitItem("Rankine (°R)", 5.0/9.0, -491.67)
    )

    private val areaUnits = listOf(
        UnitItem("Square Meter", 1.0),
        UnitItem("Square Kilometer", 1000000.0),
        UnitItem("Square Mile", 2589988.11),
        UnitItem("Acre", 4046.86),
        UnitItem("Hectare", 10000.0)
    )

    private val speedUnits = listOf(
        // Base Unit
        UnitItem("Meter/sec (m/s)", 1.0),
        UnitItem("Meter/min (m/min)", 1.0 / 60.0),
        UnitItem("Meter/hour (m/h)", 1.0 / 3600.0),

        // Kilometers
        UnitItem("Km/sec (km/s)", 1000.0),
        UnitItem("Km/min (km/min)", 1000.0 / 60.0),
        UnitItem("Km/hour (km/h)", 1000.0 / 3600.0),

        // Centimeters
        UnitItem("Cm/sec (cm/s)", 0.01),
        UnitItem("Cm/min (cm/min)", 0.01 / 60.0),
        UnitItem("Cm/hour (cm/h)", 0.01 / 3600.0),

        // Miles (1 mile = 1609.34 meters)
        UnitItem("Mile/sec (miles/s)", 1609.34),
        UnitItem("Mile/min (miles/min)", 1609.34 / 60.0),
        UnitItem("Mile/hour (mph)", 1609.34 / 3600.0),

        // Yards (1 yard = 0.9144 meters)
        UnitItem("Yard/sec (yd/s)", 0.9144),
        UnitItem("Yard/min (yd/min)", 0.9144 / 60.0),
        UnitItem("Yard/hour (yd/h)", 0.9144 / 3600.0),

        // Feet (1 foot = 0.3048 meters)
        UnitItem("Foot/sec (ft/s)", 0.3048),
        UnitItem("Foot/min (ft/min)", 0.3048 / 60.0),
        UnitItem("Foot/hour (ft/h)", 0.3048 / 3600.0),

        // Inches (1 inch = 0.0254 meters)
        UnitItem("Inch/sec (in/s)", 0.0254),
        UnitItem("Inch/min (in/min)", 0.0254 / 60.0),
        UnitItem("Inch/hour (in/h)", 0.0254 / 3600.0),

        // Other common speed units
        UnitItem("Knot (kt)", 0.514444),
        // Mach (Speed of sound in dry air at 20°C is approx 343 m/s)
        UnitItem("Mach", 343.0)
    )

    private val timeUnits = listOf(
        UnitItem("Second", 1.0),
        UnitItem("Minute", 60.0),
        UnitItem("Hour", 3600.0),
        UnitItem("Day", 86400.0),
        UnitItem("Week", 604800.0)
    )

    // Static exchange rates for initial requirement
    private val currencyUnits = listOf(
        UnitItem("USD", 1.0),
        UnitItem("EUR", 1.08),
        UnitItem("GBP", 1.25),
        UnitItem("JPY", 0.0067),
        UnitItem("INR", 0.012)
    )

    private val volumeUnits = listOf(
        UnitItem("Liter (L)", 1.0),
        UnitItem("Milliliter (mL)", 0.001),
        UnitItem("Cubic meter (m³)", 1000.0),
        UnitItem("Cubic centimeter (cm³)", 0.001),
        UnitItem("Gallon", 3.78541),
        UnitItem("Pint", 0.473176),
        UnitItem("Cup", 0.24)
    )

    private val energyUnits = listOf(
        UnitItem("Joule (J)", 1.0),
        UnitItem("Kilojoule (kJ)", 1000.0),
        UnitItem("Calorie (cal)", 4.184),
        UnitItem("Kilocalorie (kcal)", 4184.0),
        UnitItem("Kilowatt-hour (kWh)", 3600000.0)
    )

    private val powerUnits = listOf(
        UnitItem("Watt (W)", 1.0),
        UnitItem("Kilowatt (kW)", 1000.0),
        UnitItem("Megawatt (MW)", 1000000.0),
        UnitItem("Horsepower (hp)", 745.7)
    )

    private val dataUnits = listOf(
        UnitItem("Bit (b)", 1.0),
        UnitItem("Byte (B)", 8.0),
        UnitItem("Kilobyte (KB)", 8192.0),
        UnitItem("Megabyte (MB)", 8388608.0),
        UnitItem("Gigabyte (GB)", 8589934592.0),
        UnitItem("Terabyte (TB)", 8796093022208.0)
    )

    private val pressureUnits = listOf(
        UnitItem("Pascal (Pa)", 1.0),
        UnitItem("Kilopascal (kPa)", 1000.0),
        UnitItem("Bar", 100000.0),
        UnitItem("PSI", 6894.76),
        UnitItem("Atmosphere (atm)", 101325.0),
        UnitItem("mmHg", 133.322)
    )

    private val angleUnits = listOf(
        UnitItem("Degree (°)", 1.0),
        UnitItem("Radian (rad)", 180.0 / Math.PI),
        UnitItem("Gradian (grad)", 0.9)
    )

    fun getUnitsForCategory(category: ConversionCategory): List<UnitItem> {
        return when (category) {
            ConversionCategory.LENGTH -> lengthUnits
            ConversionCategory.WEIGHT -> weightUnits
            ConversionCategory.TEMPERATURE -> temperatureUnits
            ConversionCategory.AREA -> areaUnits
            ConversionCategory.SPEED -> speedUnits
            ConversionCategory.TIME -> timeUnits
            ConversionCategory.CURRENCY -> currencyUnits
            ConversionCategory.VOLUME -> volumeUnits
            ConversionCategory.ENERGY -> energyUnits
            ConversionCategory.POWER -> powerUnits
            ConversionCategory.DATA -> dataUnits
            ConversionCategory.PRESSURE -> pressureUnits
            ConversionCategory.ANGLE -> angleUnits
        }
    }

    fun convert(value: Double, fromUnit: UnitItem, toUnit: UnitItem): Double {
        val baseValue = (value + fromUnit.offset) * fromUnit.factor
        return (baseValue / toUnit.factor) - toUnit.offset
    }
}
