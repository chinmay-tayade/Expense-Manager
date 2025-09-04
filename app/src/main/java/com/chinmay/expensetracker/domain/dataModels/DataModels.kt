package com.chinmay.expensetracker.domain.dataModels


// Data Models for Smart Daily Expense Tracker

data class Expense(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val notes: String = "",
    val receiptImagePath: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val date: String = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        .format(java.util.Date())
)

enum class ExpenseCategory(val displayName: String) {
    STAFF("Staff"),
    TRAVEL("Travel"),
    FOOD("Food"),
    UTILITY("Utility")
}

data class ExpenseUIState(
    val expenses: List<Expense> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalSpentToday: Double = 0.0,
    val selectedDate: String = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        .format(java.util.Date()),
    val groupByCategory: Boolean = false
)

data class ExpenseEntryState(
    val title: String = "",
    val amount: String = "",
    val selectedCategory: ExpenseCategory = ExpenseCategory.FOOD,
    val notes: String = "",
    val receiptImagePath: String? = null,
    val titleError: String? = null,
    val amountError: String? = null,
    val isSubmitting: Boolean = false,
    val isEditing: Boolean = false // Add this line
)

data class ExpenseReport(
    val dailyTotals: Map<String, Double>,
    val categoryTotals: Map<ExpenseCategory, Double>,
    val totalExpenses: Int,
    val totalAmount: Double,
    val dateRange: String
)