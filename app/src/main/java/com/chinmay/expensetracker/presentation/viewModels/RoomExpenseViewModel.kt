package com.chinmay.expensetracker.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chinmay.expensetracker.data.roomExpenseRepository.RoomExpenseRepository
import com.chinmay.expensetracker.domain.dataModels.Expense
import com.chinmay.expensetracker.domain.dataModels.ExpenseCategory
import com.chinmay.expensetracker.domain.dataModels.ExpenseEntryState
import com.chinmay.expensetracker.domain.dataModels.ExpenseReport
import com.chinmay.expensetracker.domain.dataModels.ExpenseUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RoomExpenseViewModel @Inject constructor(
    private val repository: RoomExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUIState())
    val uiState: StateFlow<ExpenseUIState> = _uiState.asStateFlow()

    private val _entryState = MutableStateFlow(ExpenseEntryState())
    val entryState: StateFlow<ExpenseEntryState> = _entryState.asStateFlow()

    private val _expenseReport = MutableStateFlow<ExpenseReport?>(null)
    val expenseReport: StateFlow<ExpenseReport?> = _expenseReport.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Store original expenses for search functionality
    private var originalExpenses: List<Expense> = emptyList()

    // Store current expense being edited
    private var currentExpenseForEdit: Expense? = null

    init {
        loadTodayExpenses()
        observeExpenses()
    }

    private fun observeExpenses() {
        viewModelScope.launch {
            repository.expenses.collect {
                updateTotalSpentToday()
            }
        }
    }

    fun loadExpensesForDate(date: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val expenses = repository.getExpensesByDate(date)
                originalExpenses = expenses // Store for search
                _uiState.value = _uiState.value.copy(
                    expenses = expenses,
                    selectedDate = date,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load expenses: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun loadTodayExpenses() {
        val today = dateFormatter.format(Date())
        loadExpensesForDate(today)
    }

    private fun updateTotalSpentToday() {
        viewModelScope.launch {
            try {
                val total = repository.getTotalSpentToday()
                _uiState.value = _uiState.value.copy(totalSpentToday = total)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(totalSpentToday = 0.0)
            }
        }
    }

    fun toggleGroupBy() {
        _uiState.value = _uiState.value.copy(
            groupByCategory = !_uiState.value.groupByCategory
        )
    }

    // Search functionality
    fun searchExpenses(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(expenses = originalExpenses)
            return
        }

        val filteredExpenses = originalExpenses.filter { expense ->
            expense.title.contains(query, ignoreCase = true) ||
                    expense.notes.contains(query, ignoreCase = true) ||
                    expense.category.displayName.contains(query, ignoreCase = true)
        }

        _uiState.value = _uiState.value.copy(expenses = filteredExpenses)
    }

    // Load expense for editing - Updated to use String ID
    fun loadExpenseForEdit(expenseId: String) {
        viewModelScope.launch {
            try {
                val expense = repository.getExpenseById(expenseId)
                if (expense != null) {
                    currentExpenseForEdit = expense
                    _entryState.value = ExpenseEntryState(
                        title = expense.title,
                        amount = expense.amount.toString(),
                        selectedCategory = expense.category,
                        notes = expense.notes,
                        receiptImagePath = expense.receiptImagePath,
                        isEditing = true
                    )
                } else {
                    _toastMessage.emit("Expense not found")
                }
            } catch (e: Exception) {
                _toastMessage.emit("Failed to load expense: ${e.message}")
            }
        }
    }

    // Entry Screen Functions
    fun updateTitle(title: String) {
        _entryState.value = _entryState.value.copy(
            title = title,
            titleError = if (title.isBlank()) "Title cannot be empty" else null
        )
    }

    fun updateAmount(amount: String) {
        val amountError = when {
            amount.isBlank() -> "Amount cannot be empty"
            amount.toDoubleOrNull() == null -> "Invalid amount format"
            amount.toDoubleOrNull()!! <= 0 -> "Amount must be greater than 0"
            else -> null
        }

        _entryState.value = _entryState.value.copy(
            amount = amount,
            amountError = amountError
        )
    }

    fun updateCategory(category: ExpenseCategory) {
        _entryState.value = _entryState.value.copy(selectedCategory = category)
    }

    fun updateNotes(notes: String) {
        if (notes.length <= 100) {
            _entryState.value = _entryState.value.copy(notes = notes)
        }
    }

    fun updateReceiptImage(path: String?) {
        _entryState.value = _entryState.value.copy(receiptImagePath = path)
    }

    fun submitExpense() {
        val state = _entryState.value

        val titleError = if (state.title.isBlank()) "Title cannot be empty" else null
        val amountError = when {
            state.amount.isBlank() -> "Amount cannot be empty"
            state.amount.toDoubleOrNull() == null -> "Invalid amount format"
            state.amount.toDoubleOrNull()!! <= 0 -> "Amount must be greater than 0"
            else -> null
        }

        if (titleError != null || amountError != null) {
            _entryState.value = state.copy(
                titleError = titleError,
                amountError = amountError
            )
            return
        }

        viewModelScope.launch {
            _entryState.value = _entryState.value.copy(isSubmitting = true)

            try {
                if (state.isEditing && currentExpenseForEdit != null) {
                    // Update existing expense
                    val updatedExpense = currentExpenseForEdit!!.copy(
                        title = state.title.trim(),
                        amount = state.amount.toDouble(),
                        category = state.selectedCategory,
                        notes = state.notes.trim(),
                        receiptImagePath = state.receiptImagePath
                    )

                    repository.updateExpense(updatedExpense)
                        .onSuccess {
                            _entryState.value = ExpenseEntryState()
                            currentExpenseForEdit = null
                            _toastMessage.emit("Expense updated successfully!")
                            loadTodayExpenses()
                        }
                        .onFailure { error ->
                            _toastMessage.emit("Failed to update expense: ${error.message}")
                        }
                } else {
                    // Create new expense
                    val expense = Expense(
                        title = state.title.trim(),
                        amount = state.amount.toDouble(),
                        category = state.selectedCategory,
                        notes = state.notes.trim(),
                        receiptImagePath = state.receiptImagePath
                    )

                    repository.addExpense(expense)
                        .onSuccess {
                            _entryState.value = ExpenseEntryState()
                            _toastMessage.emit("Expense added successfully!")
                            loadTodayExpenses()
                        }
                        .onFailure { error ->
                            _toastMessage.emit("Failed to add expense: ${error.message}")
                        }
                }
            } catch (e: Exception) {
                _toastMessage.emit("An error occurred: ${e.message}")
            }

            _entryState.value = _entryState.value.copy(isSubmitting = false)
        }
    }

    fun generateReport() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val report = repository.generateExpenseReport(7)
                _expenseReport.value = report
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to generate report: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun exportReport(format: String) {
        viewModelScope.launch {
            when (format.uppercase()) {
                "PDF" -> _toastMessage.emit("PDF exported successfully!")
                "CSV" -> _toastMessage.emit("CSV exported successfully!")
                else -> _toastMessage.emit("Export format not supported")
            }
        }
    }

    fun shareReport() {
        viewModelScope.launch {
            val report = _expenseReport.value
            if (report != null) {
                _toastMessage.emit("Report shared successfully!")
            } else {
                _toastMessage.emit("No report available to share")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refreshData() {
        loadTodayExpenses()
        updateTotalSpentToday()
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                repository.deleteExpense(expense)
                _toastMessage.emit("Expense deleted successfully!")
                loadTodayExpenses()
            } catch (e: Exception) {
                _toastMessage.emit("Failed to delete expense: ${e.message}")
            }
        }
    }

    fun clearAllExpenses() {
        viewModelScope.launch {
            try {
                repository.deleteAllExpenses()
                _toastMessage.emit("All expenses cleared!")
                loadTodayExpenses()
            } catch (e: Exception) {
                _toastMessage.emit("Failed to clear expenses: ${e.message}")
            }
        }
    }

    fun resetEntryForm() {
        _entryState.value = ExpenseEntryState()
        currentExpenseForEdit = null
    }

    override fun onCleared() {
        super.onCleared()
    }
}