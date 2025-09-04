package com.chinmay.expensetracker.data.roomExpenseRepository

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Context
import com.chinmay.expensetracker.domain.dataModels.Expense
import com.chinmay.expensetracker.domain.dataModels.ExpenseReport
import java.text.SimpleDateFormat
import java.util.*

class RoomExpenseRepository(context: Context) {
    private val database = ExpenseDatabase.getDatabase(context)
    private val expenseDao = database.expenseDao()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val expenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
        .map { entities -> entities.map { it.toExpense() } }

    suspend fun addExpense(expense: Expense): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                // Check for duplicates
                val duplicateCount = expenseDao.getDuplicateCount(
                    title = expense.title,
                    amount = expense.amount,
                    category = expense.category.name,
                    date = expense.date,
                    timestamp = expense.timestamp
                )

                if (duplicateCount > 0) {
                    return@withContext Result.failure(Exception("Duplicate expense detected"))
                }

                expenseDao.insertExpense(expense.toEntity())
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExpensesByDate(date: String): List<Expense> {
        return withContext(Dispatchers.IO) {
            expenseDao.getExpensesByDate(date).map { it.toExpense() }
        }
    }

    suspend fun getTotalSpentToday(): Double {
        val today = dateFormatter.format(Date())
        return withContext(Dispatchers.IO) {
            expenseDao.getTotalForDate(today) ?: 0.0
        }
    }

    suspend fun getExpensesForDateRange(startDate: String, endDate: String): List<Expense> {
        return withContext(Dispatchers.IO) {
            expenseDao.getExpensesInDateRange(startDate, endDate).map { it.toExpense() }
        }
    }

    suspend fun generateExpenseReport(days: Int = 7): ExpenseReport {
        return withContext(Dispatchers.IO) {
            val calendar = Calendar.getInstance()
            val endDate = dateFormatter.format(calendar.time)

            calendar.add(Calendar.DAY_OF_MONTH, -days + 1)
            val startDate = dateFormatter.format(calendar.time)

            val reportExpenses = getExpensesForDateRange(startDate, endDate)

            val dailyTotals = reportExpenses.groupBy { it.date }
                .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }

            val categoryTotals = reportExpenses.groupBy { it.category }
                .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }

            ExpenseReport(
                dailyTotals = dailyTotals,
                categoryTotals = categoryTotals,
                totalExpenses = reportExpenses.size,
                totalAmount = reportExpenses.sumOf { it.amount },
                dateRange = "$startDate to $endDate"
            )
        }
    }

    suspend fun deleteExpense(expense: Expense) {
        withContext(Dispatchers.IO) {
            expenseDao.deleteExpense(expense.toEntity())
        }
    }

    suspend fun deleteAllExpenses() {
        withContext(Dispatchers.IO) {
            expenseDao.deleteAllExpenses()
        }
    }

    suspend fun getExpenseById(expenseId: String): Expense? {
        return try {
            withContext(Dispatchers.IO) {
                expenseDao.getExpenseById(expenseId)?.toExpense()
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateExpense(expense: Expense): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                expenseDao.updateExpense(expense.toEntity())
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}