
package com.chinmay.expensetracker

import android.app.Application
import com.chinmay.expensetracker.data.roomExpenseRepository.RoomExpenseRepository
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ExpenseTrackerApplication : Application() {

    // Repository instance - Lazy initialization
    val expenseRepository by lazy {
        RoomExpenseRepository(this)
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize any required components here
        // Setup crash reporting, analytics, etc.

        // Log app initialization
        android.util.Log.d("ExpenseTracker", "Application started")
    }

    override fun onTerminate() {
        super.onTerminate()
        // Cleanup resources if needed
        android.util.Log.d("ExpenseTracker", "Application terminated")
    }
}