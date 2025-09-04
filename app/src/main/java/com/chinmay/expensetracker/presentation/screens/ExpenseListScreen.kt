package com.chinmay.expensetracker.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chinmay.expensetracker.domain.dataModels.Expense
import com.chinmay.expensetracker.domain.dataModels.ExpenseCategory
import com.chinmay.expensetracker.domain.dataModels.ExpenseUIState
import com.chinmay.expensetracker.presentation.viewModels.RoomExpenseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable

fun ExpenseListScreen(
    viewModel: RoomExpenseViewModel,
    onNavigateToEntry: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToDetails: (Expense) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadTodayExpenses()
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToEntry
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()

                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            // Enhanced Top App Bar
            TopAppBar(
                showSearch = showSearch,
                searchQuery = searchQuery,
                onSearchQueryChange = {
                    searchQuery = it
                    viewModel.searchExpenses(it)
                },
                onSearchToggle = { showSearch = !showSearch },
                onDatePickerClick = { showDatePicker = true },
                onReportsClick = onNavigateToReports
            )

            when {
                uiState.isLoading -> LoadingState()
                uiState.error != null -> ErrorState(
                    error = uiState.error!!,
                    onRetry = { viewModel.loadTodayExpenses() },
                    onDismiss = { viewModel.clearError() }
                )
                else -> {
                    ExpenseListContent(
                        uiState = uiState,
                        onToggleGroupBy = viewModel::toggleGroupBy,
                        onDeleteExpense = { expense ->
                            scope.launch {
                                try {
                                    viewModel.deleteExpense(expense)
                                    snackbarHostState.showSnackbar("Expense deleted successfully")
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Failed to delete expense")
                                }
                            }
                        },
                        onExpenseClick = onNavigateToDetails
                    )
                }
            }
        }

        // Enhanced Date Picker
        if (showDatePicker) {
            EnhancedDatePickerDialog(
                onDateSelected = { date ->
                    viewModel.loadExpensesForDate(date)
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false },
                onTodayClick = {
                    viewModel.loadTodayExpenses()
                    showDatePicker = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
private fun TopAppBar(
    showSearch: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onDatePickerClick: () -> Unit,
    onReportsClick: () -> Unit
) {
    AnimatedContent(
        targetState = showSearch,
        transitionSpec = {
            slideInVertically { -it } + fadeIn() with slideOutVertically { -it } + fadeOut()
        },
        label = "topbar_animation"
    ) { isSearching ->
        if (isSearching) {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Search expenses...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onSearchToggle) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Close search")
                    }
                },
                actions = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        } else {
            TopAppBar(
                title = {
                    Text(
                        "My Expenses",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    IconButton(onClick = onSearchToggle) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDatePickerClick) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Select Date",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onReportsClick) {
                        Icon(
                            Icons.Default.Assessment,
                            contentDescription = "View Reports",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 3.dp
            )
            Text(
                text = "Loading expenses...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Oops! Something went wrong",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Dismiss")
                    }
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseListContent(
    uiState: ExpenseUIState,
    onToggleGroupBy: () -> Unit,
    onDeleteExpense: (Expense) -> Unit,
    onExpenseClick: (Expense) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            // Enhanced Summary Card
            EnhancedSummaryCard(
                date = uiState.selectedDate,
                totalCount = uiState.expenses.size,
                totalAmount = uiState.expenses.sumOf { it.amount },
                groupByCategory = uiState.groupByCategory,
                onToggleGroupBy = onToggleGroupBy
            )
        }

        if (uiState.expenses.isEmpty()) {
            item {
                EmptyState()
            }
        } else {
            if (uiState.groupByCategory) {
                val groupedExpenses = uiState.expenses.groupBy { it.category }
                    .toList()
                    .sortedByDescending { (_, expenses) -> expenses.sumOf { it.amount } }

                items(
                    items = groupedExpenses,
                    key = { it.first }
                ) { (category, categoryExpenses) ->
                    CategoryGroupCard(
                        category = category,
                        expenses = categoryExpenses,
                        onDeleteExpense = onDeleteExpense,
                        onExpenseClick = onExpenseClick
                    )
                }
            } else {
                items(
                    items = uiState.expenses.sortedByDescending { it.timestamp },
                    key = { it.id }
                ) { expense ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300)),
                        label = "expense_item_animation"
                    ) {
                        ExpenseItem(
                            expense = expense,
                            onDelete = { onDeleteExpense(expense) },
                            onClick = { onExpenseClick(expense) }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
        }
    }
}

@Composable
private fun EnhancedSummaryCard(
    date: String,
    totalCount: Int,
    totalAmount: Double,
    groupByCategory: Boolean,
    onToggleGroupBy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatDate(date),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$totalCount expenses",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "₹",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = String.format("%.2f", totalAmount),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    FilterChip(
                        onClick = onToggleGroupBy,
                        label = {
                            Text(
                                text = if (groupByCategory) "Categories" else "Timeline",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        selected = groupByCategory,
                        leadingIcon = {
                            Icon(
                                if (groupByCategory) Icons.Default.Category else Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Receipt,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
        }

        Text(
            text = "No expenses yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Start tracking your expenses by adding your first entry",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun CategoryGroupCard(
    category: ExpenseCategory,
    expenses: List<Expense>,
    onDeleteExpense: (Expense) -> Unit,
    onExpenseClick: (Expense) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = getCategoryColor(category).copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                getCategoryColor(category).copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            getCategoryIcon(category),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = getCategoryColor(category)
                        )
                    }

                    Column {
                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${expenses.size} expenses",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Text(
                    text = "₹${String.format("%.2f", expenses.sumOf { it.amount })}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = getCategoryColor(category)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            expenses.sortedByDescending { it.timestamp }.forEach { expense ->
                ExpenseItem(
                    expense = expense,
                    showCategory = false,
                    onDelete = { onDeleteExpense(expense) },
                    onClick = { onExpenseClick(expense) },
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                if (expense != expenses.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpenseItem(
    expense: Expense,
    showCategory: Boolean = true,
    onDelete: () -> Unit,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showCategory) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            getCategoryColor(expense.category).copy(alpha = 0.15f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(expense.category),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = getCategoryColor(expense.category)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (showCategory) {
                    Text(
                        text = expense.category.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = getCategoryColor(expense.category),
                        fontWeight = FontWeight.Medium
                    )
                }

                if (expense.notes.isNotBlank()) {
                    Text(
                        text = expense.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = formatTime(expense.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    if (expense.receiptImagePath != null && expense.receiptImagePath.isNotBlank()) {
                        Icon(
                            Icons.Default.Receipt,
                            contentDescription = "Has receipt",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "₹${String.format("%.2f", expense.amount)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Expense") },
            text = { Text("Are you sure you want to delete \"${expense.title}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun FloatingActionButton(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 12.dp,
            pressedElevation = 16.dp
        )
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Add Expense",
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun EnhancedDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    onTodayClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Select Date",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onTodayClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(Icons.Default.Today, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Today")
                }

                HorizontalDivider()

                Text(
                    "Recent dates:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val calendar = Calendar.getInstance()

                repeat(7) { dayOffset ->
                    calendar.time = Date()
                    calendar.add(Calendar.DAY_OF_MONTH, -dayOffset)
                    val date = dateFormatter.format(calendar.time)

                    TextButton(
                        onClick = { onDateSelected(date) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(formatDate(date))
                            if (dayOffset == 0) {
                                Text(
                                    "Today",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            } else if (dayOffset == 1) {
                                Text(
                                    "Yesterday",
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Helper functions
private fun getCategoryIcon(category: ExpenseCategory) = when (category) {
    ExpenseCategory.STAFF -> Icons.Default.People
    ExpenseCategory.TRAVEL -> Icons.Default.DirectionsCar
    ExpenseCategory.FOOD -> Icons.Default.Restaurant
    ExpenseCategory.UTILITY -> Icons.Default.Build
}

@Composable
private fun getCategoryColor(category: ExpenseCategory) = when (category) {
    ExpenseCategory.STAFF -> Color(0xFF2E7D32)
    ExpenseCategory.TRAVEL -> Color(0xFF1565C0)
    ExpenseCategory.FOOD -> Color(0xFFE65100)
    ExpenseCategory.UTILITY -> Color(0xFF6A1B9A)
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)

        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }
        val dateCalendar = Calendar.getInstance().apply { time = date!! }

        when {
            isSameDay(dateCalendar, today) -> "Today"
            isSameDay(dateCalendar, yesterday) -> "Yesterday"
            else -> outputFormat.format(date!!)
        }
    } catch (e: Exception) {
        dateString
    }
}

private fun formatTime(timestamp: Long): String {
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return format.format(Date(timestamp))
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}