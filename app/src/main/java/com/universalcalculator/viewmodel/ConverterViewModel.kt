package com.universalcalculator.viewmodel

import androidx.lifecycle.ViewModel
import com.universalcalculator.core.converter.ConversionCategory
import com.universalcalculator.core.converter.ConversionLogic
import com.universalcalculator.core.converter.UnitItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.DecimalFormat

class ConverterViewModel : ViewModel() {
    private val _category = MutableStateFlow(ConversionCategory.LENGTH)
    val category: StateFlow<ConversionCategory> = _category.asStateFlow()

    private val _units = MutableStateFlow(ConversionLogic.getUnitsForCategory(ConversionCategory.LENGTH))
    val units: StateFlow<List<UnitItem>> = _units.asStateFlow()

    private val _fromUnit = MutableStateFlow(_units.value.first())
    val fromUnit: StateFlow<UnitItem> = _fromUnit.asStateFlow()

    private val _toUnit = MutableStateFlow(_units.value.getOrNull(1) ?: _units.value.first())
    val toUnit: StateFlow<UnitItem> = _toUnit.asStateFlow()

    private val _inputValue = MutableStateFlow("")
    val inputValue: StateFlow<String> = _inputValue.asStateFlow()

    private val _resultValue = MutableStateFlow("")
    val resultValue: StateFlow<String> = _resultValue.asStateFlow()

    fun setCategory(category: ConversionCategory) {
        _category.value = category
        val newUnits = ConversionLogic.getUnitsForCategory(category)
        _units.value = newUnits
        
        // Find corresponding items from new list to keep previous object state consistent
        _fromUnit.value = newUnits.first()
        _toUnit.value = newUnits.getOrNull(1) ?: newUnits.first()
        calculateResult()
    }

    fun setFromUnit(unit: UnitItem) {
        _fromUnit.value = unit
        calculateResult()
    }

    fun setToUnit(unit: UnitItem) {
        _toUnit.value = unit
        calculateResult()
    }

    fun setInputValue(value: String) {
        if (value.isBlank() || value.count { it == '.' } <= 1) {
            _inputValue.value = value
            calculateResult()
        }
    }

    private fun calculateResult() {
        val input = _inputValue.value.toDoubleOrNull()
        if (input == null) {
            _resultValue.value = ""
            return
        }
        val result = ConversionLogic.convert(input, _fromUnit.value, _toUnit.value)
        val df = DecimalFormat("#.######")
        _resultValue.value = df.format(result)
    }

    fun swapUnits() {
        val temp = _fromUnit.value
        _fromUnit.value = _toUnit.value
        _toUnit.value = temp
        
        val tempVal = _inputValue.value
        _inputValue.value = _resultValue.value
        // Don't directly set result value back to temp to avoid precision loss feedback loop
        // Recalculating will set appropriate string formatting based on new inputs
        calculateResult()
    }
}
