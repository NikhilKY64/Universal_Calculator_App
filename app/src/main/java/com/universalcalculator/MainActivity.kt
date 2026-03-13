package com.universalcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.universalcalculator.data.datastore.PreferencesManager
import com.universalcalculator.data.datastore.ThemeMode
import com.universalcalculator.data.history.HistoryRepository
import com.universalcalculator.navigation.MainNavGraphWithViewModels
import com.universalcalculator.navigation.Screen
import com.universalcalculator.ui.components.DrawerContent
import com.universalcalculator.ui.theme.UniversalCalculatorTheme
import com.universalcalculator.viewmodel.SettingsViewModel
import com.universalcalculator.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val preferencesManager = remember {
                PreferencesManager(applicationContext)
            }

            val historyRepository = remember {
                HistoryRepository(applicationContext)
            }

            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(preferencesManager)
            )

            val themeMode by settingsViewModel.themeMode.collectAsState(
                initial = ThemeMode.SYSTEM
            )

            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            val initialRoute = runBlocking { preferencesManager.lastRoute.first() } ?: Screen.StandardCalculator.route

            UniversalCalculatorTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(historyRepository, settingsViewModel, initialRoute)
                }
            }
        }
    }
}

@Composable
fun MainApp(
    historyRepository: HistoryRepository,
    settingsViewModel: SettingsViewModel,
    initialRoute: String
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        if (currentRoute != null) {
            settingsViewModel.setLastRoute(currentRoute)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                currentRoute = currentRoute,
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                closeDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        MainNavGraphWithViewModels(
            navController = navController,
            openDrawer = { scope.launch { drawerState.open() } },
            historyRepository = historyRepository,
            settingsViewModel = settingsViewModel,
            startDestination = initialRoute
        )
    }
}
