package com.chinmay.expensetracker.data.roomExpenseRepository

import androidx.room.*
import com.chinmay.expensetracker.domain.dataModels.ExpenseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY timestamp DESC")
    suspend fun getExpensesByDate(date: String): List<ExpenseEntity>

    @Query("SELECT SUM(amount) FROM expenses WHERE date = :date")
    suspend fun getTotalForDate(date: String): Double?

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getExpensesInDateRange(startDate: String, endDate: String): List<ExpenseEntity>

    @Query("SELECT SUM(amount) FROM expenses WHERE category = :category AND date BETWEEN :startDate AND :endDate")
    suspend fun getCategoryTotal(category: String, startDate: String, endDate: String): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()

    // Check for duplicates
    @Query("""
        SELECT COUNT(*) FROM expenses 
        WHERE title = :title 
        AND amount = :amount 
        AND category = :category 
        AND date = :date 
        AND ABS(timestamp - :timestamp) < 60000
    """)
    suspend fun getDuplicateCount(
        title: String,
        amount: Double,
        category: String,
        date: String,
        timestamp: Long
    ): Int

    @Query("SELECT * FROM expenses WHERE id = :expenseId LIMIT 1")
    suspend fun getExpenseById(expenseId: String): ExpenseEntity?

    @Query("UPDATE expenses SET title = :title, amount = :amount, category = :category, notes = :notes, receiptImagePath = :receiptImagePath WHERE id = :id")
    suspend fun updateExpenseQuery(
        id: String,
        title: String,
        amount: Double,
        category: String,
        notes: String,
        receiptImagePath: String?
    )
}