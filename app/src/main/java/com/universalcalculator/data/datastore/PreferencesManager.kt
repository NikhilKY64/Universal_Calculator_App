package com.universalcalculator.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

enum class ThemeMode { SYSTEM, LIGHT, DARK }

class PreferencesManager(private val context: Context) {
    private val THEME_KEY = stringPreferencesKey("theme_mode")
    private val LAST_ROUTE_KEY = stringPreferencesKey("last_route")
    
    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        when (preferences[THEME_KEY]) {
            ThemeMode.LIGHT.name -> ThemeMode.LIGHT
            ThemeMode.DARK.name -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    val lastRoute: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LAST_ROUTE_KEY]
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = mode.name
        }
    }

    suspend fun setLastRoute(route: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_ROUTE_KEY] = route
        }
    }
}
