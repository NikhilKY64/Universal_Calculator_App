package com.universalcalculator.data.history

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.historyDataStore by preferencesDataStore(name = "history")

data class HistoryItem(val expression: String, val result: String)

class HistoryRepository(private val context: Context) {
    private val HISTORY_KEY = stringPreferencesKey("calculator_history")

    val history: Flow<List<HistoryItem>> = context.historyDataStore.data.map { preferences ->
        val raw = preferences[HISTORY_KEY] ?: ""
        if (raw.isBlank()) emptyList()
        else raw.split("||").mapNotNull {
            val parts = it.split("==")
            if (parts.size == 2) HistoryItem(parts[0], parts[1]) else null
        }.reversed() // Newest first
    }

    suspend fun addHistory(item: HistoryItem) {
        context.historyDataStore.edit { preferences ->
            val current = preferences[HISTORY_KEY] ?: ""
            val newItemStr = "${item.expression}==${item.result}"
            val list = if (current.isBlank()) listOf(newItemStr) else current.split("||") + newItemStr
            val trimmed = list.takeLast(50) // Keep last 50 items
            preferences[HISTORY_KEY] = trimmed.joinToString("||")
        }
    }

    suspend fun clearHistory() {
        context.historyDataStore.edit { preferences ->
            preferences[HISTORY_KEY] = ""
        }
    }
}
