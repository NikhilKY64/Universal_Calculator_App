package com.universalcalculator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.universalcalculator.navigation.Screen

@Composable
fun DrawerContent(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    closeDrawer: () -> Unit
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Universal Calculator",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        Text(
            text = "Calculator",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        DrawerItem(Screen.StandardCalculator, currentRoute, onNavigate, closeDrawer)
        DrawerItem(Screen.ScientificCalculator, currentRoute, onNavigate, closeDrawer)
        DrawerItem(Screen.GraphingCalculator, currentRoute, onNavigate, closeDrawer)
        DrawerItem(Screen.DateCalculation, currentRoute, onNavigate, closeDrawer)
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        Text(
            text = "Converter",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        DrawerItem(Screen.ConverterCurrency, currentRoute, onNavigate, closeDrawer)
        DrawerItem(Screen.ConverterLength, currentRoute, onNavigate, closeDrawer)
        DrawerItem(Screen.ConverterWeight, currentRoute, onNavigate, closeDrawer)
        DrawerItem(Screen.ConverterTemperature, currentRoute, onNavigate, closeDrawer)
        DrawerItem(Screen.ConverterArea, currentRoute, onNavigate, closeDrawer)
        DrawerItem(Screen.ConverterSpeed, currentRoute, onNavigate, closeDrawer)
        DrawerItem(Screen.ConverterTime, currentRoute, onNavigate, closeDrawer)
        DrawerItem(Screen.ConverterVolume, currentRoute, onNavigate, closeDrawer)
        DrawerItem(Screen.ConverterEnergy, currentRoute, onNavigate, closeDrawer)
        DrawerItem(Screen.ConverterPower, currentRoute, onNavigate, closeDrawer)
        DrawerItem(Screen.ConverterData, currentRoute, onNavigate, closeDrawer)
        DrawerItem(Screen.ConverterPressure, currentRoute, onNavigate, closeDrawer)
        DrawerItem(Screen.ConverterAngle, currentRoute, onNavigate, closeDrawer)
        Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DrawerItem(
    screen: Screen,
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    closeDrawer: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(screen.title) },
        selected = currentRoute == screen.route,
        onClick = {
            onNavigate(screen)
            closeDrawer()
        },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}
