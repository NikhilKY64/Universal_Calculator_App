package com.universalcalculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.universalcalculator.core.math.ExpressionEvaluator
import com.universalcalculator.data.history.HistoryItem
import com.universalcalculator.data.history.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalculatorViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val evaluator = ExpressionEvaluator()

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val history = historyRepository.history

    fun onAction(action: CalculatorAction) {
        if (_error.value != null && action !is CalculatorAction.Clear) {
            _expression.value = ""
            _error.value = null
        }
        
        when (action) {
            is CalculatorAction.Number -> append(action.number.toString())
            is CalculatorAction.Operation -> append(action.operation)
            CalculatorAction.Clear -> {
                _expression.value = ""
                _result.value = ""
                _error.value = null
            }
            CalculatorAction.Delete -> {
                if (_expression.value.isNotEmpty()) {
                    _expression.value = _expression.value.dropLast(1)
                }
            }
            CalculatorAction.Calculate -> calculateResult()
            is CalculatorAction.Function -> append("${action.function}(")
            CalculatorAction.ParenthesisOpen -> append("(")
            CalculatorAction.ParenthesisClose -> append(")")
            CalculatorAction.Decimal -> {
                if (!_expression.value.endsWith(".")) {
                    append(".")
                }
            }
        }
    }

    private fun append(str: String) {
        // If result is present and user starts typing a new number, clear expression
        if (_result.value.isNotEmpty() && _expression.value == _result.value) {
            val isOperation = listOf("+", "-", "×", "÷", "^").contains(str)
            if (!isOperation) {
                _expression.value = ""
            }
        }
        _result.value = ""
        _expression.value += str
    }

    private fun calculateResult() {
        if (_expression.value.isBlank()) return
        try {
            val res = evaluator.evaluate(_expression.value)
            val formattedResult = if (res % 1 == 0.0) res.toLong().toString() else res.toString()
            
            viewModelScope.launch {
                historyRepository.addHistory(HistoryItem(_expression.value, formattedResult))
            }
            _result.value = formattedResult
            _expression.value = formattedResult
        } catch (e: ArithmeticException) {
            _error.value = e.message ?: "Divide by zero"
        } catch (e: Exception) {
            _error.value = "Error"
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clearHistory()
        }
    }
}

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    data class Operation(val operation: String) : CalculatorAction()
    data class Function(val function: String) : CalculatorAction()
    data object Clear : CalculatorAction()
    data object Delete : CalculatorAction()
    data object Calculate : CalculatorAction()
    data object Decimal : CalculatorAction()
    data object ParenthesisOpen : CalculatorAction()
    data object ParenthesisClose : CalculatorAction()
}

class CalculatorViewModelFactory(private val repository: HistoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculatorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
