package com.universalcalculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.universalcalculator.data.datastore.PreferencesManager
import com.universalcalculator.data.datastore.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {
    val themeMode: StateFlow<ThemeMode> = preferencesManager.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeMode.SYSTEM
    )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
        }
    }

    val lastRoute: StateFlow<String?> = preferencesManager.lastRoute.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun setLastRoute(route: String) {
        viewModelScope.launch {
            preferencesManager.setLastRoute(route)
        }
    }
}

class SettingsViewModelFactory(private val prefManager: PreferencesManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(prefManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
