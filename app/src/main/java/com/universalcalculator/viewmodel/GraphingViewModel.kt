package com.universalcalculator.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange

data class GraphEquation(
    val id: Int,
    val expression: TextFieldValue = TextFieldValue(""),
    val color: Color,
    val isVisible: Boolean = true
)

class GraphingViewModel : ViewModel() {
    private val defaultColors = listOf(
        Color(0xFFE53935), // Red
        Color(0xFF1E88E5), // Blue
        Color(0xFF43A047), // Green
        Color(0xFFFDD835), // Yellow
        Color(0xFF8E24AA), // Purple
        Color(0xFFF4511E)  // Orange
    )

    private val _equations = MutableStateFlow<List<GraphEquation>>(
        listOf(GraphEquation(id = 1, color = defaultColors[0]))
    )
    val equations: StateFlow<List<GraphEquation>> = _equations.asStateFlow()

    private val _selectedEquationId = MutableStateFlow<Int>(1)
    val selectedEquationId: StateFlow<Int> = _selectedEquationId.asStateFlow()

    fun selectEquation(id: Int) {
        _selectedEquationId.value = id
    }

    fun addEquation() {
        _equations.update { list ->
            val nextId = (list.maxOfOrNull { it.id } ?: 0) + 1
            val color = defaultColors[(nextId - 1) % defaultColors.size]
            list + GraphEquation(id = nextId, color = color)
        }
        val nextId = _equations.value.last().id
        _selectedEquationId.value = nextId
    }

    fun removeEquation(id: Int) {
        _equations.update { list ->
            if (list.size > 1) list.filter { it.id != id } else list
        }
        if (_selectedEquationId.value == id) {
            _selectedEquationId.value = _equations.value.first().id
        }
    }

    fun toggleVisibility(id: Int) {
        _equations.update { list ->
            list.map { if (it.id == id) it.copy(isVisible = !it.isVisible) else it }
        }
    }

    fun updateExpression(id: Int, newExpression: TextFieldValue) {
        _equations.update { list ->
            list.map { if (it.id == id) it.copy(expression = newExpression) else it }
        }
    }

    fun onAction(action: CalculatorAction) {
        _equations.update { list ->
            list.map { eq ->
                if (eq.id == _selectedEquationId.value) {
                    val newExp = processAction(eq.expression, action)
                    eq.copy(expression = newExp)
                } else {
                    eq
                }
            }
        }
    }

    private fun processAction(current: TextFieldValue, action: CalculatorAction): TextFieldValue {
        val text = current.text
        val cursor = minOf(current.selection.start, current.selection.end)
        val end = maxOf(current.selection.start, current.selection.end)
        
        fun insert(str: String): TextFieldValue {
            val newText = text.substring(0, cursor) + str + text.substring(end)
            return TextFieldValue(newText, TextRange(cursor + str.length))
        }

        return when (action) {
            is CalculatorAction.Number -> insert(action.number.toString())
            is CalculatorAction.Operation -> insert(action.operation)
            is CalculatorAction.Function -> insert("${action.function}(")
            CalculatorAction.Clear -> TextFieldValue("", TextRange(0))
            CalculatorAction.Delete -> {
                if (cursor == end && cursor > 0) {
                    val newText = text.substring(0, cursor - 1) + text.substring(end)
                    TextFieldValue(newText, TextRange(cursor - 1))
                } else if (cursor != end) {
                    val newText = text.substring(0, cursor) + text.substring(end)
                    TextFieldValue(newText, TextRange(cursor))
                } else current
            }
            CalculatorAction.Decimal -> insert(".")
            CalculatorAction.ParenthesisOpen -> insert("(")
            CalculatorAction.ParenthesisClose -> insert(")")
            CalculatorAction.Calculate -> current
        }
    }

    fun insertText(textString: String) {
        _equations.update { list ->
            list.map { eq ->
                if (eq.id == _selectedEquationId.value) {
                    val cur = eq.expression
                    val cursor = minOf(cur.selection.start, cur.selection.end)
                    val end = maxOf(cur.selection.start, cur.selection.end)
                    val newText = cur.text.substring(0, cursor) + textString + cur.text.substring(end)
                    eq.copy(expression = TextFieldValue(newText, TextRange(cursor + textString.length)))
                } else {
                    eq
                }
            }
        }
    }
}
