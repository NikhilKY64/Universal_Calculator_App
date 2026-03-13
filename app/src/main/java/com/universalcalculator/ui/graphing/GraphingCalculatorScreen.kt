package com.universalcalculator.ui.graphing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.universalcalculator.viewmodel.CalculatorAction
import com.universalcalculator.viewmodel.GraphingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphingCalculatorScreen(
    openDrawer: () -> Unit,
    navigateToSettings: () -> Unit,
    viewModel: GraphingViewModel = viewModel()
) {
    val equations by viewModel.equations.collectAsState()
    val selectedId by viewModel.selectedEquationId.collectAsState()
    var isKeypadVisible by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Graphing Calculator") },
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
        ) {
            // Graph View (Top Half)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                GraphView(equations = equations)
                
                // Add Equation Button Overlaid
                FloatingActionButton(
                    onClick = { viewModel.addEquation() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Equation")
                }
            }

            // Equations List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = if (isKeypadVisible) 110.dp else 220.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                items(equations) { eq ->
                    val isSelected = eq.id == selectedId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectEquation(eq.id) }
                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.toggleVisibility(eq.id) }) {
                            Icon(
                                if (eq.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Visibility",
                                tint = eq.color
                            )
                        }

                        OutlinedTextField(
                            value = eq.expression,
                            onValueChange = { viewModel.updateExpression(eq.id, it) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        viewModel.selectEquation(eq.id)
                                    }
                                },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = eq.color,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        if (equations.size > 1) {
                            IconButton(onClick = { viewModel.removeEquation(eq.id) }) {
                                Icon(Icons.Default.Close, "Remove")
                            }
                        }
                    }
                    HorizontalDivider()
                }
            }

            // Keypad Toggle Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isKeypadVisible = !isKeypadVisible }
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isKeypadVisible) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = "Toggle Keypad"
                )
            }

            AnimatedVisibility(
                visible = isKeypadVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    GraphingHeader(onInsertText = { viewModel.insertText(it) })
                    GraphingKeypad(
                        onAction = { viewModel.onAction(it) },
                        onInsertText = { viewModel.insertText(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun GraphingHeader(onInsertText: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        HeaderDropdown(
            title = "Trigonometry",
            options = listOf("sin(", "cos(", "tan(", "asin(", "acos(", "atan("),
            onOptionSelected = onInsertText
        )
        HeaderDropdown(
            title = "Inequalities",
            options = listOf("<", ">", "<=", ">="),
            onOptionSelected = onInsertText
        )
        HeaderDropdown(
            title = "Function",
            options = listOf("f(x)=", "sqrt(", "log(", "ln(", "abs("),
            onOptionSelected = onInsertText
        )
    }
}

@Composable
fun HeaderDropdown(title: String, options: List<String>, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { expanded = true }.padding(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Expand $title", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun GraphingKeypad(
    onAction: (CalculatorAction) -> Unit,
    onInsertText: (String) -> Unit
) {
    val buttons = listOf(
        listOf("x²", "1/x", "|x|", "x", "y"),
        listOf("sqrt", "(", ")", "C", "⌫"),
        listOf("x^", "7", "8", "9", "÷"),
        listOf("10^", "4", "5", "6", "×"),
        listOf("log", "1", "2", "3", "−"),
        listOf("ln", "π", "e", ".", "+"),
        listOf("sin", "cos", "tan", "0", "=")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { symbol ->
                    GraphingButton(
                        symbol = symbol,
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .aspectRatio(1.5f),
                        onClick = {
                            when (symbol) {
                                "C" -> onAction(CalculatorAction.Clear)
                                "⌫" -> onAction(CalculatorAction.Delete)
                                "÷", "×", "−", "+" -> onAction(CalculatorAction.Operation(symbol))
                                "x^" -> onAction(CalculatorAction.Operation("^"))
                                "10^" -> onInsertText("10^") 
                                "x²" -> onAction(CalculatorAction.Operation("^2"))
                                "1/x" -> onInsertText("1/")
                                "|x|" -> onInsertText("abs(")
                                "sqrt" -> onInsertText("sqrt(")
                                "(" -> onAction(CalculatorAction.ParenthesisOpen)
                                ")" -> onAction(CalculatorAction.ParenthesisClose)
                                "." -> onAction(CalculatorAction.Decimal)
                                "log", "ln", "sin", "cos", "tan" -> onAction(CalculatorAction.Function(symbol))
                                "π" -> onInsertText("π")
                                "e" -> onInsertText("e")
                                "x", "y", "=" -> onInsertText(symbol)
                                else -> {
                                    if (symbol.toIntOrNull() != null) {
                                        onAction(CalculatorAction.Number(symbol.toInt()))
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GraphingButton(
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isOperation = listOf("÷", "×", "−", "+", "=", "x^", "10^").contains(symbol)
    val isAction = listOf("C", "⌫", "(", ")").contains(symbol)
    val isSpecial = listOf("x", "y", "π", "e", "|x|", "1/x", "x²", "sqrt", "log", "ln", "sin", "cos", "tan").contains(symbol)
    
    val containerColor = when {
        isAction -> Color(0xFFFFB74D)
        symbol == "=" -> MaterialTheme.colorScheme.primary
        isOperation -> MaterialTheme.colorScheme.secondaryContainer
        isSpecial -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    
    val contentColor = when {
        isAction -> Color.White
        symbol == "=" -> MaterialTheme.colorScheme.onPrimary
        isOperation -> MaterialTheme.colorScheme.onSecondaryContainer
        isSpecial -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = symbol,
            fontSize = 18.sp,
            color = contentColor,
            fontWeight = FontWeight.Medium
        )
    }
}
