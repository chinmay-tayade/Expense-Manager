package com.chinmay.expensetracker.data.expenseRepository


// Expense Repository - In-memory implementation
import com.chinmay.expensetracker.domain.dataModels.Expense
import com.chinmay.expensetracker.domain.dataModels.ExpenseCategory
import com.chinmay.expensetracker.domain.dataModels.ExpenseReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class ExpenseRepository {
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        // Mock data for demonstration
        generateMockData()
    }

    suspend fun addExpense(expense: Expense): Result<Unit> {
        return try {
            // Simulate network delay
            delay(500)

            val currentList = _expenses.value.toMutableList()
            currentList.add(expense)
            _expenses.value = currentList

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExpensesByDate(date: String): List<Expense> {
        delay(200) // Simulate loading
        return _expenses.value.filter { it.date == date }
    }

    suspend fun getTotalSpentToday(): Double {
        val today = dateFormatter.format(Date())
        return _expenses.value
            .filter { it.date == today }
            .sumOf { it.amount }
    }

    suspend fun getExpensesForDateRange(startDate: String, endDate: String): List<Expense> {
        delay(300)
        return _expenses.value.filter { expense ->
            expense.date >= startDate && expense.date <= endDate
        }
    }

    suspend fun generateExpenseReport(days: Int = 7): ExpenseReport {
        delay(400)
        val calendar = Calendar.getInstance()
        val endDate = dateFormatter.format(calendar.time)

        calendar.add(Calendar.DAY_OF_MONTH, -days + 1)
        val startDate = dateFormatter.format(calendar.time)

        val reportExpenses = getExpensesForDateRange(startDate, endDate)

        val dailyTotals = reportExpenses.groupBy { it.date }
            .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }

        val categoryTotals = reportExpenses.groupBy { it.category }
            .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }

        return ExpenseReport(
            dailyTotals = dailyTotals,
            categoryTotals = categoryTotals,
            totalExpenses = reportExpenses.size,
            totalAmount = reportExpenses.sumOf { it.amount },
            dateRange = "$startDate to $endDate"
        )
    }

    fun isDuplicateExpense(newExpense: Expense): Boolean {
        return _expenses.value.any { existing ->
            existing.title.equals(newExpense.title, ignoreCase = true) &&
                    existing.amount == newExpense.amount &&
                    existing.category == newExpense.category &&
                    existing.date == newExpense.date &&
                    Math.abs(existing.timestamp - newExpense.timestamp) < 60000 // Within 1 minute
        }
    }

    private fun generateMockData() {
        val calendar = Calendar.getInstance()
        val mockExpenses = mutableListOf<Expense>()

        // Generate expenses for the last 7 days
        repeat(7) { dayOffset ->
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_MONTH, -dayOffset)
            val date = dateFormatter.format(calendar.time)

            // Add 2-4 random expenses per day
            val expensesPerDay = (2..4).random()
            repeat(expensesPerDay) {
                val categories = ExpenseCategory.values()
                val category = categories.random()

                val expense = Expense(
                    title = generateMockTitle(category),
                    amount = (50..2000).random().toDouble(),
                    category = category,
                    notes = if ((0..1).random() == 1) generateMockNotes() else "",
                    date = date,
                    timestamp = calendar.timeInMillis + (0..86400000).random() // Random time within the day
                )
                mockExpenses.add(expense)
            }
        }

        _expenses.value = mockExpenses
    }

    private fun generateMockTitle(category: ExpenseCategory): String {
        return when (category) {
            ExpenseCategory.STAFF -> listOf("Salary Payment", "Bonus", "Overtime", "Staff Training").random()
            ExpenseCategory.TRAVEL -> listOf("Taxi Fare", "Flight Booking", "Hotel Stay", "Fuel Cost").random()
            ExpenseCategory.FOOD -> listOf("Team Lunch", "Office Snacks", "Client Dinner", "Coffee Meeting").random()
            ExpenseCategory.UTILITY -> listOf("Internet Bill", "Electricity", "Phone Bill", "Office Rent").random()
        }
    }

    private fun generateMockNotes(): String {
        return listOf(
            "Business meeting",
            "Team building activity",
            "Client presentation",
            "Emergency expense",
            "Monthly recurring"
        ).random()
    }
}