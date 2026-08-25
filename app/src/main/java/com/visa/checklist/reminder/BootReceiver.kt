package com.visa.checklist.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.visa.checklist.data.ChecklistRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        runBlocking {
            val state = ChecklistRepository(context).state.first()
            if (state.reminderEnabled && state.deadlineEpochDay != null) {
                ReminderScheduler.schedule(context, state.deadlineEpochDay)
            }
        }
    }
}
