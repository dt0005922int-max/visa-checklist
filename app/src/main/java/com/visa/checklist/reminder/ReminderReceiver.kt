package com.visa.checklist.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        ReminderScheduler.showNow(
            context,
            "Your visa deadline is today. Open the app and finish your checklist."
        )
    }
}
