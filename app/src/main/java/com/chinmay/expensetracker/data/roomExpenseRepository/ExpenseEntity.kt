package com.chinmay.expensetracker.data.roomExpenseRepository


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val amount: Double,
    val category: String,
    val notes: String,
    val receiptImagePath: String?,
    val timestamp: Long,
    val date: String,

)
