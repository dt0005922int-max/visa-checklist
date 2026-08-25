package com.visa.checklist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.visa.checklist.data.ChecklistRepository
import com.visa.checklist.data.VisaAppState
import com.visa.checklist.reminder.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChecklistViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ChecklistRepository(application)

    val state: StateFlow<VisaAppState> = repo.state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        VisaAppState()
    )

    fun toggleItem(id: String) {
        viewModelScope.launch { repo.toggleItem(id) }
    }

    fun setNotes(notes: String) {
        viewModelScope.launch { repo.setNotes(notes) }
    }

    fun setDeadline(epochDay: Long?) {
        viewModelScope.launch {
            repo.setDeadline(epochDay)
            val enabled = state.value.reminderEnabled
            val app = getApplication<Application>()
            if (enabled && epochDay != null) {
                ReminderScheduler.schedule(app, epochDay)
            } else {
                ReminderScheduler.cancel(app)
            }
        }
    }

    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repo.setReminderEnabled(enabled)
            val app = getApplication<Application>()
            val day = state.value.deadlineEpochDay
            if (enabled && day != null) {
                ReminderScheduler.schedule(app, day)
            } else {
                ReminderScheduler.cancel(app)
            }
        }
    }

    fun resetChecklist() {
        viewModelScope.launch { repo.resetChecklist() }
    }
}
