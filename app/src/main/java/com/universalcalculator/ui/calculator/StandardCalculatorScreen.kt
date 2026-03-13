package com.universalcalculator.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universalcalculator.viewmodel.CalculatorAction
import com.universalcalculator.viewmodel.CalculatorViewModel
import com.universalcalculator.ui.ads.AdBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardCalculatorScreen(
    viewModel: CalculatorViewModel,
    openDrawer: () -> Unit,
    navigateToSettings: () -> Unit
) {
    val expression by viewModel.expression.collectAsState()
    val result by viewModel.result.collectAsState()
    val error by viewModel.error.collectAsState()
    val history by viewModel.history.collectAsState(initial = emptyList())
    
    val sheetState = rememberModalBottomSheetState()
    var showHistory by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Standard") },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { showHistory = true }) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
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
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 58.dp), // leave 58dp gap for banner
                verticalArrangement = Arrangement.Bottom
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = expression,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                        maxLines = 2
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
                    Spacer(modifier = Modifier.height(24.dp))
                }

                val buttons = listOf(
                    listOf("C", "(", ")", "÷"),
                    listOf("7", "8", "9", "×"),
                    listOf("4", "5", "6", "−"),
                    listOf("1", "2", "3", "+"),
                    listOf("0", ".", "⌫", "=")
                )

                buttons.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        row.forEach { symbol ->
                            CalculatorButton(
                                symbol = symbol,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(6.dp),
                                onClick = {
                                    resolveAction(symbol, viewModel)
                                }
                            )
                        }
                    }
                }
            }

            // Banner ad pinned at the very bottom of the screen
            AdBanner(
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    if (showHistory) {
        ModalBottomSheet(
            onDismissRequest = { showHistory = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxHeight(0.8f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("History", style = MaterialTheme.typography.headlineSmall)
                    TextButton(onClick = { viewModel.clearHistory() }) {
                        Text("Clear")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(history) { item ->
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(text = item.expression, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "= ${item.result}", style = MaterialTheme.typography.headlineSmall)
                            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

fun resolveAction(symbol: String, viewModel: CalculatorViewModel) {
    when (symbol) {
        "C" -> viewModel.onAction(CalculatorAction.Clear)
        "⌫" -> viewModel.onAction(CalculatorAction.Delete)
        "=" -> viewModel.onAction(CalculatorAction.Calculate)
        "÷", "×", "−", "+", "^" -> viewModel.onAction(CalculatorAction.Operation(symbol))
        "(" -> viewModel.onAction(CalculatorAction.ParenthesisOpen)
        ")" -> viewModel.onAction(CalculatorAction.ParenthesisClose)
        "." -> viewModel.onAction(CalculatorAction.Decimal)
        "sin", "cos", "tan", "log", "ln", "sqrt" -> viewModel.onAction(CalculatorAction.Function(symbol))
        "x²" -> viewModel.onAction(CalculatorAction.Operation("²"))
        "x³" -> viewModel.onAction(CalculatorAction.Operation("³"))
        else -> viewModel.onAction(CalculatorAction.Number(symbol.toInt()))
    }
}

@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isOperation = listOf("÷", "×", "−", "+", "=", "^").contains(symbol)
    val isAction = listOf("C", "⌫", "(", ")").contains(symbol)
    val isFunction = listOf("sin", "cos", "tan", "log", "ln", "sqrt", "x²", "x³").contains(symbol)
    
    val orangeAction = Color(0xFFFFB74D)
    
    val containerColor = when {
        isAction -> orangeAction
        symbol == "=" -> MaterialTheme.colorScheme.primary
        isOperation -> MaterialTheme.colorScheme.secondaryContainer
        isFunction -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = when {
        isAction -> Color.White
        symbol == "=" -> MaterialTheme.colorScheme.onPrimary
        isOperation -> MaterialTheme.colorScheme.onSecondaryContainer
        isFunction -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = symbol,
            fontSize = if (isFunction) 18.sp else 32.sp,
            color = contentColor
        )
    }
}
