package com.chinmay.expensetracker.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.chinmay.expensetracker.com.chinmay.expensetracker.presentation.navigation.ExpenseRoutes
import com.chinmay.expensetracker.com.chinmay.expensetracker.presentation.navigation.navigateToExpenseDetails
import com.chinmay.expensetracker.com.chinmay.expensetracker.presentation.navigation.navigateToExpenseEntry
import com.chinmay.expensetracker.com.chinmay.expensetracker.presentation.navigation.navigateToExpenseReports
import com.chinmay.expensetracker.presentation.screens.ExpenseDetailsScreen
import com.chinmay.expensetracker.presentation.viewModels.RoomExpenseViewModel
import com.chinmay.expensetracker.presentation.screens.ExpenseEntryScreen
import com.chinmay.expensetracker.presentation.screens.ExpenseListScreen
import com.chinmay.expensetracker.presentation.screens.ExpenseReportScreen

@Composable
fun NavigationHost(
    navController: NavHostController,
    expenseViewModel: RoomExpenseViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ExpenseRoutes.EXPENSE_LIST,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth }
            ) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth }
            ) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth }
            ) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth }
            ) + fadeOut()
        }
    ) {
        composable(ExpenseRoutes.EXPENSE_LIST) {
            ExpenseListScreen(
                viewModel = expenseViewModel,
                onNavigateToEntry = {
                    navController.navigateToExpenseEntry()
                },
                onNavigateToReports = {
                    navController.navigateToExpenseReports()
                },
                onNavigateToDetails = { expense ->
                    navController.navigateToExpenseDetails(expenseId = expense.id)
                }
            )
        }

        composable(ExpenseRoutes.EXPENSE_ENTRY) {
            ExpenseEntryScreen(
                viewModel = expenseViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(ExpenseRoutes.EXPENSE_REPORTS) {
            ExpenseReportScreen(
                viewModel = expenseViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "${ExpenseRoutes.EXPENSE_DETAILS}/{expenseId}",
            arguments = listOf(
                navArgument("expenseId") {
                    type = NavType.StringType // Changed to StringType to match your ID format
                }
            )
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId") ?: return@composable
            val uiState by expenseViewModel.uiState.collectAsState()

            // Find the expense from the current state
            val expense = uiState.expenses.find { it.id == expenseId }

            if (expense != null) {
                ExpenseDetailsScreen(
                    expense = expense,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onEditExpense = {
                        // Navigate to edit screen
                        navController.navigate("${ExpenseRoutes.EXPENSE_ENTRY}/${expense.id}")
                    },
                    onDeleteExpense = {
                        expenseViewModel.deleteExpense(expense)
                        navController.popBackStack()
                    }
                )
            } else {
                // Handle case where expense is not found
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }

        // Optional: Add edit route if you want to handle editing
        composable(
            route = "${ExpenseRoutes.EXPENSE_ENTRY}/{expenseId}",
            arguments = listOf(
                navArgument("expenseId") {
                    type = NavType.StringType // Changed to StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId") ?: ""

            // Load expense for editing if ID is provided
            LaunchedEffect(expenseId) {
                if (expenseId.isNotEmpty()) {
                    expenseViewModel.loadExpenseForEdit(expenseId)
                }
            }

            ExpenseEntryScreen(
                viewModel = expenseViewModel,
                onNavigateBack = {
                    expenseViewModel.resetEntryForm() // Clear form when going back
                    navController.popBackStack()
                }
            )
        }
    }
}