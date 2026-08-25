package com.visa.checklist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.visa.checklist.reminder.ReminderScheduler
import com.visa.checklist.ui.VisaChecklistScreen
import com.visa.checklist.ui.theme.VisaChecklistTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ChecklistViewModel by viewModels()

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* user choice respected */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ReminderScheduler.ensureChannel(this)
        requestNotificationPermissionIfNeeded()

        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            VisaChecklistTheme {
                VisaChecklistScreen(
                    state = state,
                    onToggle = viewModel::toggleItem,
                    onNotesChange = viewModel::setNotes,
                    onDeadlineChange = viewModel::setDeadline,
                    onReminderChange = viewModel::setReminderEnabled,
                    onReset = viewModel::resetChecklist
                )
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
