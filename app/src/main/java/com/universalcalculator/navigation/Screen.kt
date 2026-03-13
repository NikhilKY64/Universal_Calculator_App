package com.universalcalculator.navigation

sealed class Screen(val route: String, val title: String) {
    data object StandardCalculator : Screen("standard_calculator", "Standard Calculator")
    data object ScientificCalculator : Screen("scientific_calculator", "Scientific Calculator")
    data object GraphingCalculator : Screen("graphing_calculator", "Graphing")
    data object DateCalculation : Screen("date_calculation", "Date Calculation")
    
    // Converter Screens
    data object ConverterCurrency : Screen("converter_currency", "Currency")
    data object ConverterLength : Screen("converter_length", "Length")
    data object ConverterWeight : Screen("converter_weight", "Weight")
    data object ConverterTemperature : Screen("converter_temperature", "Temperature")
    data object ConverterArea : Screen("converter_area", "Area")
    data object ConverterSpeed : Screen("converter_speed", "Speed")
    data object ConverterTime : Screen("converter_time", "Time")
    data object ConverterVolume : Screen("converter_volume", "Volume")
    data object ConverterEnergy : Screen("converter_energy", "Energy")
    data object ConverterPower : Screen("converter_power", "Power")
    data object ConverterData : Screen("converter_data", "Data")
    data object ConverterPressure : Screen("converter_pressure", "Pressure")
    data object ConverterAngle : Screen("converter_angle", "Angle")
    
    data object Settings : Screen("settings", "Settings")
}
