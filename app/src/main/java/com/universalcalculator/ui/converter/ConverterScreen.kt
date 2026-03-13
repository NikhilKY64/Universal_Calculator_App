package com.universalcalculator.ui.converter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.universalcalculator.core.converter.ConversionCategory
import com.universalcalculator.core.converter.UnitItem
import com.universalcalculator.viewmodel.ConverterViewModel
import com.universalcalculator.ui.ads.AdBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    category: ConversionCategory,
    openDrawer: () -> Unit,
    navigateToSettings: () -> Unit,
    viewModel: ConverterViewModel = viewModel()
) {
    LaunchedEffect(category) {
        viewModel.setCategory(category)
    }

    val units by viewModel.units.collectAsState()
    val fromUnit by viewModel.fromUnit.collectAsState()
    val toUnit by viewModel.toUnit.collectAsState()
    val inputValue by viewModel.inputValue.collectAsState()
    val resultValue by viewModel.resultValue.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${category.name.lowercase().replaceFirstChar { it.uppercase() }} Converter") },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Open Navigation")
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 58.dp) // leave space for banner
            ) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(24.dp))

                    UnitSelector(
                        label = "From",
                        units = units,
                        selectedUnit = fromUnit,
                        onUnitSelected = { viewModel.setFromUnit(it) },
                        value = inputValue,
                        onValueChange = { viewModel.setInputValue(it) },
                        isReadOnly = false
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FilledIconButton(onClick = { viewModel.swapUnits() }) {
                            Icon(Icons.Default.SwapVert, contentDescription = "Swap Units")
                        }
                    }

                    UnitSelector(
                        label = "To",
                        units = units,
                        selectedUnit = toUnit,
                        onUnitSelected = { viewModel.setToUnit(it) },
                        value = resultValue,
                        onValueChange = {},
                        isReadOnly = true
                    )
                }
            }

            // Banner ad pinned at the very bottom of the screen
            AdBanner(
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitSelector(
    label: String,
    units: List<UnitItem>,
    selectedUnit: UnitItem,
    onUnitSelected: (UnitItem) -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    isReadOnly: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedUnit.name,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    units.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(unit.name) },
                            onClick = {
                                onUnitSelected(unit)
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                readOnly = isReadOnly,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium,
                singleLine = true
            )
        }
    }
}
