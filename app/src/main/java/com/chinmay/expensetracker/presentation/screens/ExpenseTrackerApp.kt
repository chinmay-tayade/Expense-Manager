package com.chinmay.expensetracker.presentation.screens


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.chinmay.expensetracker.com.chinmay.expensetracker.presentation.navigation.AppWithBottomNavigation
import com.chinmay.expensetracker.presentation.navigation.NavigationHost
import com.chinmay.expensetracker.presentation.viewModels.RoomExpenseViewModel



@Composable
fun ExpenseTrackerApp() {
    val navController = rememberNavController()
    val expenseViewModel: RoomExpenseViewModel = hiltViewModel()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AppWithBottomNavigation(
            navController = navController,
            expenseViewModel = expenseViewModel
        )
    }
}

