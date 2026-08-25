package com.visa.checklist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.visa.checklist.data.ChecklistItem
import com.visa.checklist.data.VisaAppState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val Forest = Color(0xFF0B3D2E)
private val Leaf = Color(0xFF1F6F54)
private val Cream = Color(0xFFF3F7F5)
private val Ink = Color(0xFF12201A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisaChecklistScreen(
    state: VisaAppState,
    onToggle: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onDeadlineChange: (Long?) -> Unit,
    onReminderChange: (Boolean) -> Unit,
    onReset: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val doneCount = state.items.count { it.done }
    val progress = if (state.items.isEmpty()) 0f else doneCount.toFloat() / state.items.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Visa Checklist", fontWeight = FontWeight.Bold)
                        Text(
                            "Documents & deadlines — official booking only",
                            style = MaterialTheme.typography.labelMedium,
                            color = Cream.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onReset) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Reset checklist")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Forest,
                    titleContentColor = Cream,
                    actionIconContentColor = Cream
                )
            )
        },
        containerColor = Cream
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ProgressCard(doneCount = doneCount, total = state.items.size, progress = progress)
            }

            item {
                SectionHeader(icon = Icons.Outlined.CalendarMonth, title = "Your deadline")
                DeadlineCard(
                    epochDay = state.deadlineEpochDay,
                    reminderEnabled = state.reminderEnabled,
                    onPickDate = { showDatePicker = true },
                    onClear = { onDeadlineChange(null) },
                    onReminderChange = onReminderChange
                )
            }

            item {
                SectionHeader(icon = Icons.Outlined.Checklist, title = "Document checklist")
            }

            items(state.items, key = { it.id }) { item ->
                ChecklistRow(item = item, onToggle = { onToggle(item.id) })
            }

            item {
                SectionHeader(icon = Icons.Outlined.Notes, title = "Personal notes")
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = onNotesChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    placeholder = { Text("Appointment number, center, questions for BLS…") },
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.deadlineEpochDay?.let {
                LocalDate.ofEpochDay(it)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            val day = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .toEpochDay()
                            onDeadlineChange(day)
                        }
                        showDatePicker = false
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun ProgressCard(doneCount: Int, total: Int, progress: Float) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(Forest, Leaf)),
                    RoundedCornerShape(18.dp)
                )
                .padding(18.dp)
        ) {
            Column {
                Text(
                    "$doneCount of $total ready",
                    color = Cream,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Track your own documents. Book only on the official BLS website.",
                    color = Cream.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Cream,
                    trackColor = Cream.copy(alpha = 0.25f)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Forest)
        Text(title, style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DeadlineCard(
    epochDay: Long?,
    reminderEnabled: Boolean,
    onPickDate: () -> Unit,
    onClear: () -> Unit,
    onReminderChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val label = epochDay?.let {
                LocalDate.ofEpochDay(it).format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy"))
            } ?: "No date set"
            Text(label, style = MaterialTheme.typography.titleMedium, color = Ink)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onPickDate) { Text("Set date") }
                if (epochDay != null) {
                    TextButton(onClick = onClear) { Text("Clear") }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier = Modifier.weight(1f)) {
                    Text("Morning reminder (9:00)", fontWeight = FontWeight.Medium)
                    Text(
                        "Local notification on your phone only",
                        style = MaterialTheme.typography.bodySmall,
                        color = Ink.copy(alpha = 0.6f)
                    )
                }
                Switch(
                    checked = reminderEnabled && epochDay != null,
                    onCheckedChange = onReminderChange,
                    enabled = epochDay != null
                )
            }
        }
    }
}

@Composable
private fun ChecklistRow(item: ChecklistItem, onToggle: () -> Unit) {
    Card(
        onClick = onToggle,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.done) Leaf.copy(alpha = 0.12f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = item.done, onCheckedChange = { onToggle() })
            Text(
                item.title,
                modifier = Modifier.padding(end = 12.dp),
                color = if (item.done) Ink.copy(alpha = 0.55f) else Ink
            )
        }
    }
}
