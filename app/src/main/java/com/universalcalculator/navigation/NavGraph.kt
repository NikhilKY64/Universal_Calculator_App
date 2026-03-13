package com.universalcalculator.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.universalcalculator.core.converter.ConversionCategory
import com.universalcalculator.data.history.HistoryRepository
import com.universalcalculator.ui.calculator.StandardCalculatorScreen
import com.universalcalculator.ui.calculator.ScientificCalculatorScreen
import com.universalcalculator.ui.calculator.DateCalculationScreen
import com.universalcalculator.ui.converter.ConverterScreen
import com.universalcalculator.ui.graphing.GraphingCalculatorScreen
import com.universalcalculator.ui.settings.SettingsScreen
import com.universalcalculator.viewmodel.CalculatorViewModel
import com.universalcalculator.viewmodel.CalculatorViewModelFactory
import com.universalcalculator.viewmodel.SettingsViewModel

@Composable
fun MainNavGraphWithViewModels(
    navController: NavHostController,
    openDrawer: () -> Unit,
    historyRepository: HistoryRepository,
    settingsViewModel: SettingsViewModel,
    startDestination: String = Screen.StandardCalculator.route,
    navigateToSettings: () -> Unit = { navController.navigate(Screen.Settings.route) }
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.StandardCalculator.route) {
            val calculatorViewModel: CalculatorViewModel = viewModel(
                factory = CalculatorViewModelFactory(historyRepository)
            )
            StandardCalculatorScreen(calculatorViewModel, openDrawer, navigateToSettings)
        }
        composable(Screen.ScientificCalculator.route) {
            val calculatorViewModel: CalculatorViewModel = viewModel(
                factory = CalculatorViewModelFactory(historyRepository)
            )
            ScientificCalculatorScreen(calculatorViewModel, openDrawer, navigateToSettings)
        }
        composable(Screen.GraphingCalculator.route) {
            val graphingViewModel: com.universalcalculator.viewmodel.GraphingViewModel = viewModel()
            GraphingCalculatorScreen(openDrawer, navigateToSettings, graphingViewModel)
        }
        composable(Screen.DateCalculation.route) {
            DateCalculationScreen(openDrawer, navigateToSettings)
        }
        composable(Screen.ConverterCurrency.route) { ConverterScreen(category = ConversionCategory.CURRENCY, openDrawer = openDrawer, navigateToSettings = navigateToSettings) }
        composable(Screen.ConverterLength.route) { ConverterScreen(category = ConversionCategory.LENGTH, openDrawer = openDrawer, navigateToSettings = navigateToSettings) }
        composable(Screen.ConverterWeight.route) { ConverterScreen(category = ConversionCategory.WEIGHT, openDrawer = openDrawer, navigateToSettings = navigateToSettings) }
        composable(Screen.ConverterTemperature.route) { ConverterScreen(category = ConversionCategory.TEMPERATURE, openDrawer = openDrawer, navigateToSettings = navigateToSettings) }
        composable(Screen.ConverterArea.route) { ConverterScreen(category = ConversionCategory.AREA, openDrawer = openDrawer, navigateToSettings = navigateToSettings) }
        composable(Screen.ConverterSpeed.route) { ConverterScreen(category = ConversionCategory.SPEED, openDrawer = openDrawer, navigateToSettings = navigateToSettings) }
        composable(Screen.ConverterTime.route) { ConverterScreen(category = ConversionCategory.TIME, openDrawer = openDrawer, navigateToSettings = navigateToSettings) }
        composable(Screen.ConverterVolume.route) { ConverterScreen(category = ConversionCategory.VOLUME, openDrawer = openDrawer, navigateToSettings = navigateToSettings) }
        composable(Screen.ConverterEnergy.route) { ConverterScreen(category = ConversionCategory.ENERGY, openDrawer = openDrawer, navigateToSettings = navigateToSettings) }
        composable(Screen.ConverterPower.route) { ConverterScreen(category = ConversionCategory.POWER, openDrawer = openDrawer, navigateToSettings = navigateToSettings) }
        composable(Screen.ConverterData.route) { ConverterScreen(category = ConversionCategory.DATA, openDrawer = openDrawer, navigateToSettings = navigateToSettings) }
        composable(Screen.ConverterPressure.route) { ConverterScreen(category = ConversionCategory.PRESSURE, openDrawer = openDrawer, navigateToSettings = navigateToSettings) }
        composable(Screen.ConverterAngle.route) { ConverterScreen(category = ConversionCategory.ANGLE, openDrawer = openDrawer, navigateToSettings = navigateToSettings) }
        composable(Screen.Settings.route) {
            SettingsScreen(settingsViewModel, openDrawer)
        }
    }
}
