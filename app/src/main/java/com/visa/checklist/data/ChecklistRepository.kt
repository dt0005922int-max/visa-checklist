package com.visa.checklist.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore("visa_checklist")

class ChecklistRepository(private val context: Context) {

    private val itemsKey = stringPreferencesKey("items_json")
    private val notesKey = stringPreferencesKey("notes")
    private val deadlineKey = longPreferencesKey("deadline_epoch_day")
    private val reminderKey = booleanPreferencesKey("reminder_enabled")

    val state: Flow<VisaAppState> = context.dataStore.data.map { prefs ->
        val items = prefs[itemsKey]?.let { decodeItems(it) } ?: defaultItems()
        VisaAppState(
            items = items,
            notes = prefs[notesKey].orEmpty(),
            deadlineEpochDay = prefs[deadlineKey],
            reminderEnabled = prefs[reminderKey] ?: false
        )
    }

    suspend fun toggleItem(id: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[itemsKey]?.let { decodeItems(it) } ?: defaultItems()
            val updated = current.map {
                if (it.id == id) it.copy(done = !it.done) else it
            }
            prefs[itemsKey] = encodeItems(updated)
        }
    }

    suspend fun setNotes(notes: String) {
        context.dataStore.edit { prefs ->
            prefs[notesKey] = notes
        }
    }

    suspend fun setDeadline(epochDay: Long?) {
        context.dataStore.edit { prefs ->
            if (epochDay == null) {
                prefs.remove(deadlineKey)
            } else {
                prefs[deadlineKey] = epochDay
            }
        }
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[reminderKey] = enabled
        }
    }

    suspend fun resetChecklist() {
        context.dataStore.edit { prefs ->
            prefs[itemsKey] = encodeItems(defaultItems())
        }
    }

    private fun encodeItems(items: List<ChecklistItem>): String {
        val array = JSONArray()
        items.forEach { item ->
            array.put(
                JSONObject()
                    .put("id", item.id)
                    .put("title", item.title)
                    .put("done", item.done)
            )
        }
        return array.toString()
    }

    private fun decodeItems(raw: String): List<ChecklistItem> {
        val array = JSONArray(raw)
        return buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                add(
                    ChecklistItem(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        done = obj.getBoolean("done")
                    )
                )
            }
        }
    }
}
