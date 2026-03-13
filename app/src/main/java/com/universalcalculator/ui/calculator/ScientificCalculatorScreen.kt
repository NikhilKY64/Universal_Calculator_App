package com.universalcalculator.ui.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universalcalculator.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScientificCalculatorScreen(
    viewModel: CalculatorViewModel,
    openDrawer: () -> Unit,
    navigateToSettings: () -> Unit
) {
    val expression by viewModel.expression.collectAsState()
    val result by viewModel.result.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scientific") },
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
                .padding(8.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = expression,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error ?: result,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Light,
                        fontSize = if ((error ?: result).length > 10) 40.sp else 57.sp
                    ),
                    color = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            val buttons = listOf(
                listOf("sin", "cos", "tan", "sqrt", "^"),
                listOf("ln", "log", "x²", "x³", "⌫"),
                listOf("C", "(", ")", "÷", "×"),
                listOf("7", "8", "9", "−", "+"),
                listOf("4", "5", "6", ".", "="),
                listOf("1", "2", "3", "0", "")
            )

            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEach { symbol ->
                        if (symbol.isNotEmpty()) {
                            CalculatorButton(
                                symbol = symbol,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .aspectRatio(if (row.size == 5) 1.2f else 1f),
                                onClick = { resolveAction(symbol, viewModel) }
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f).padding(4.dp))
                        }
                    }
                }
            }
        }
    }
}
