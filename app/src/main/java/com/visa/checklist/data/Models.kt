package com.visa.checklist.data

data class ChecklistItem(
    val id: String,
    val title: String,
    val done: Boolean = false
)

data class VisaAppState(
    val items: List<ChecklistItem> = defaultItems(),
    val notes: String = "",
    val deadlineEpochDay: Long? = null,
    val reminderEnabled: Boolean = false
)

fun defaultItems(): List<ChecklistItem> = listOf(
    ChecklistItem("passport", "Valid passport (6+ months)"),
    ChecklistItem("photos", "Passport-size photos"),
    ChecklistItem("form", "Visa application form filled"),
    ChecklistItem("invite", "Invitation / travel plan"),
    ChecklistItem("bank", "Bank statements (last 3–6 months)"),
    ChecklistItem("employment", "Employment / student letter"),
    ChecklistItem("insurance", "Travel insurance"),
    ChecklistItem("booking", "Flight / hotel bookings (if required)"),
    ChecklistItem("fee", "Visa fee payment ready"),
    ChecklistItem("appointment", "Appointment booked via official BLS site")
)
