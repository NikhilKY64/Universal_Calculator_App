package com.universalcalculator.ui.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateCalculationScreen(
    openDrawer: () -> Unit,
    navigateToSettings: () -> Unit
) {
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now().plusDays(7)) }
    
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val startDateVal = if (startDate.isBefore(endDate)) startDate else endDate
    val endDateVal = if (startDate.isBefore(endDate)) endDate else startDate
    val period = Period.between(startDateVal, endDateVal)
    val daysBetween = Math.abs(ChronoUnit.DAYS.between(startDateVal, endDateVal))
    
    fun formatUnit(value: Int, unit: String): String? {
        if (value == 0) return null
        return "$value $unit${if (value > 1) "s" else ""}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Date Calculation") },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = navigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Difference between dates", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedButton(
                        onClick = { showStartPicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("From: ${startDate.format(formatter)}")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedButton(
                        onClick = { showEndPicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("To: ${endDate.format(formatter)}")
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Difference",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val periodParts = listOfNotNull(
                        formatUnit(period.years, "year"),
                        formatUnit(period.months, "month"),
                        formatUnit(period.days, "day")
                    )
                    val periodString = if (periodParts.isEmpty()) "0 days" else periodParts.joinToString(", ")
                    
                    Text(
                        text = periodString,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$daysBetween days",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (showStartPicker) {
                val state = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
                DatePickerDialog(
                    onDismissRequest = { showStartPicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            state.selectedDateMillis?.let {
                                startDate = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                            }
                            showStartPicker = false
                        }) { Text("OK") }
                    },
                    modifier = Modifier.wrapContentSize().scale(0.9f)
                ) {
                    DatePicker(
                        state = state,
                        modifier = Modifier.padding(bottom = 16.dp),
                        title = null,
                        headline = null,
                        showModeToggle = false
                    )
                }
            }

            if (showEndPicker) {
                val state = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L)
                DatePickerDialog(
                    onDismissRequest = { showEndPicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            state.selectedDateMillis?.let {
                                endDate = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                            }
                            showEndPicker = false
                        }) { Text("OK") }
                    },
                    modifier = Modifier.wrapContentSize().scale(0.9f)
                ) {
                    DatePicker(
                        state = state,
                        modifier = Modifier.padding(bottom = 16.dp),
                        title = null,
                        headline = null,
                        showModeToggle = false
                    )
                }
            }
        }
    }
}
