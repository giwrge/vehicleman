package com.vehicleman.presentation.addeditrecord

data class AddEditRecordState(
    val date: Long = System.currentTimeMillis(),
    val odometer: String = "",
    val isExpense: Boolean = true,
    val title: String = "",
    val description: String = "",
    val amount: String = "",
    val reminderDate: Long? = null
)
