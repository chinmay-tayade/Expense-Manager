package com.chinmay.expensetracker.data.roomExpenseRepository

import com.chinmay.expensetracker.domain.dataModels.Expense
import com.chinmay.expensetracker.domain.dataModels.ExpenseCategory


fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = this.id,
        title = this.title,
        amount = this.amount,
        category = this.category.name,
        notes = this.notes,
        receiptImagePath = this.receiptImagePath,
        timestamp = this.timestamp,
        date = this.date
    )
}

fun ExpenseEntity.toExpense(): Expense {
    return Expense(
        id = this.id,
        title = this.title,
        amount = this.amount,
        category = ExpenseCategory.valueOf(this.category),
        notes = this.notes,
        receiptImagePath = this.receiptImagePath,
        timestamp = this.timestamp,
        date = this.date
    )
}
