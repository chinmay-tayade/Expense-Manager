package com.chinmay.expensetracker.com.chinmay.expensetracker.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.chinmay.expensetracker.presentation.navigation.NavigationHost
import com.chinmay.expensetracker.presentation.viewModels.RoomExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppWithBottomNavigation(
    navController: NavHostController,
    expenseViewModel: RoomExpenseViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(currentDestination)) {
                BottomNavigation(
                    navController = navController,
                    currentRoute = currentDestination
                )
            }
        }
    ) { paddingValues ->
        NavigationHost(
            navController = navController,
            expenseViewModel = expenseViewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun BottomNavigation(
    navController: NavHostController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.List,
                    contentDescription = "Expenses tab",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Expenses") },
            selected = currentRoute == ExpenseRoutes.EXPENSE_LIST,
            onClick = {
                navController.navigateToExpenseList()
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add expense tab",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Add") },
            selected = currentRoute == ExpenseRoutes.EXPENSE_ENTRY,
            onClick = {
                navController.navigateToExpenseEntry()
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Assessment,
                    contentDescription = "Reports tab",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Reports") },
            selected = currentRoute == ExpenseRoutes.EXPENSE_REPORTS,
            onClick = {
                navController.navigateToExpenseReports()
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

// Function to determine if bottom bar should be shown
private fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return when (currentRoute) {
        ExpenseRoutes.EXPENSE_LIST,
        ExpenseRoutes.EXPENSE_ENTRY,
        ExpenseRoutes.EXPENSE_REPORTS -> true
        else -> false
    }
}

// Navigation extensions for better type safety
fun NavHostController.navigateToExpenseEntry() {
    navigate(ExpenseRoutes.EXPENSE_ENTRY)
}

fun NavHostController.navigateToExpenseReports() {
    navigate(ExpenseRoutes.EXPENSE_REPORTS)
}

fun NavController.navigateToExpenseDetails(expenseId: String) {
    navigate("${ExpenseRoutes.EXPENSE_DETAILS}/$expenseId")
}



fun NavHostController.navigateToExpenseList() {
    navigate(ExpenseRoutes.EXPENSE_LIST) {
        // Clear back stack when going to main screen
        popUpTo(ExpenseRoutes.EXPENSE_LIST) {
            inclusive = false
        }
        launchSingleTop = true
    }
}

// Add this to your ExpenseRoutes object
object ExpenseRoutes {
    const val EXPENSE_LIST = "expense_list"
    const val EXPENSE_ENTRY = "expense_entry"
    const val EXPENSE_REPORTS = "expense_reports"
    const val EXPENSE_DETAILS = "expense_details" // Add this line
}

