package com.chinmay.expensetracker.presentation.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chinmay.expensetracker.domain.dataModels.ExpenseCategory
import com.chinmay.expensetracker.domain.dataModels.ExpenseReport
import com.chinmay.expensetracker.presentation.viewModels.RoomExpenseViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun ExpenseReportScreen(
    viewModel: RoomExpenseViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val report by viewModel.expenseReport.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.generateReport()
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {  paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            // Fixed Top Bar
            ReportTopBar(
                onNavigateBack = onNavigateBack,
                onExportPDF = {
                    report?.let { reportData ->
                        scope.launch {
                            try {
                                generateAndSharePDF(context, reportData)
                                snackbarHostState.showSnackbar("PDF exported successfully!")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Failed to export PDF: ${e.message}")
                            }
                        }
                    }
                },
                onExportCSV = {
                    report?.let { reportData ->
                        scope.launch {
                            try {
                                generateAndShareCSV(context, reportData)
                                snackbarHostState.showSnackbar("CSV exported successfully!")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Failed to export CSV: ${e.message}")
                            }
                        }
                    }
                },
                onShare = {
                    report?.let { reportData ->
                        shareReport(context, reportData)
                    }
                }
            )

            when {
                uiState.isLoading -> LoadingState()
                report != null -> ReportContent(
                    report = report!!,
                    onExportPDF = {
                        scope.launch {
                            try {
                                generateAndSharePDF(context, report!!)
                                snackbarHostState.showSnackbar("PDF exported successfully!")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Failed to export PDF: ${e.message}")
                            }
                        }
                    },
                    onExportCSV = {
                        scope.launch {
                            try {
                                generateAndShareCSV(context, report!!)
                                snackbarHostState.showSnackbar("CSV exported successfully!")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Failed to export CSV: ${e.message}")
                            }
                        }
                    }
                )
                else -> ReportErrorState(
                    onRetry = { viewModel.generateReport() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportTopBar(
    onNavigateBack: () -> Unit,
    onExportPDF: () -> Unit,
    onExportCSV: () -> Unit,
    onShare: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                "Expense Reports",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            IconButton(onClick = onShare) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Share",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = { showMenu = true }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Export PDF") },
                    onClick = {
                        showMenu = false
                        onExportPDF()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Export CSV") },
                    onClick = {
                        showMenu = false
                        onExportCSV()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.TableChart, contentDescription = null)
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    strokeWidth = 3.dp
                )
            }

            Text(
                text = "Generating your report...",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Analyzing your expense data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ReportErrorState(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
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
                text = "Failed to generate report",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

@Composable
private fun ReportContent(
    report: ExpenseReport,
    onExportPDF: () -> Unit,
    onExportCSV: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SummaryCard(report = report)
        }

        item {
            EnhancedDailyChart(
                dailyTotals = report.dailyTotals,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            ModernCategoryBreakdown(
                categoryTotals = report.categoryTotals,
                totalAmount = report.totalAmount
            )
        }

        item {
            InsightsCard(report = report)
        }

        item {
            DetailedDataTables(report = report)
        }

        item {
            ExportCard(
                onExportPDF = onExportPDF,
                onExportCSV = onExportCSV
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SummaryCard(report: ExpenseReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "7-Day Summary",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = report.dateRange,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryMetric(
                    title = "Total Expenses",
                    value = report.totalExpenses.toString(),
                    icon = Icons.Default.Receipt,
                    color = MaterialTheme.colorScheme.primary
                )

                SummaryMetric(
                    title = "Amount Spent",
                    value = "₹${String.format("%.0f", report.totalAmount)}",
                    icon = Icons.Default.CurrencyRupee,
                    color = MaterialTheme.colorScheme.secondary
                )

                SummaryMetric(
                    title = "Daily Average",
                    value = "₹${String.format("%.0f", report.totalAmount / 7)}",
                    icon = Icons.Default.Analytics,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
private fun SummaryMetric(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = color
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EnhancedDailyChart(
    dailyTotals: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Daily Spending Trend",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your spending pattern over the week",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (dailyTotals.isNotEmpty()) {
                AdvancedBarChart(
                    data = dailyTotals,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No data available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdvancedBarChart(
    data: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface

    // Filter out empty days and sort by date
    val filteredData = remember(data) {
        data.filter { it.value > 0 }
            .toList()
            .sortedBy { it.first }
    }

    if (filteredData.isEmpty()) {
        // Show empty state
        Box(
            modifier = modifier.fillMaxWidth().height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.BarChart,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
                Text(
                    text = "No expense data available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        return
    }

    // Horizontally scrollable chart
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(filteredData) { (date, amount) ->
            SingleBarItem(
                date = date,
                amount = amount,
                maxAmount = filteredData.maxOf { it.second },
                primaryColor = primaryColor,
                onSurface = onSurface
            )
        }
    }
}

@Composable
private fun SingleBarItem(
    date: String,
    amount: Double,
    maxAmount: Double,
    primaryColor: Color,
    onSurface: Color
) {
    val chartHeight = 200.dp
    val barWidth = 60.dp

    // Calculate bar height percentage
    val heightPercentage = (amount / maxAmount).coerceIn(0.1, 1.0)
    val barHeight = chartHeight * heightPercentage.toFloat()

    // Format date
    val (dayName, dayNumber) = remember(date) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
            val numberFormat = SimpleDateFormat("dd", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            Pair(
                dayFormat.format(parsedDate!!),
                numberFormat.format(parsedDate)
            )
        } catch (e: Exception) {
            Pair("", date.takeLast(2))
        }
    }

    // Check if it's today
    val isToday = remember(date) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        date == today
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Amount label
        Text(
            text = "₹${amount.toInt()}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )

        // Bar
        Box(
            modifier = Modifier
                .width(barWidth)
                .height(chartHeight)
        ) {
            // Background bar (light)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        primaryColor.copy(alpha = 0.1f),
                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    )
            )

            // Actual bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barHeight)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryColor,
                                primaryColor.copy(alpha = 0.8f)
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    )
            )

            // Today indicator
            if (isToday) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Color.White,
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }

        // Date labels
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isToday) primaryColor else onSurface
            )
            Text(
                text = dayNumber,
                style = MaterialTheme.typography.labelSmall,
                color = onSurface.copy(alpha = 0.7f)
            )

            if (isToday) {
                Text(
                    text = "TODAY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }
        }
    }
}

// Alternative: Canvas-based scrollable version for more customization
@Composable
private fun CanvasScrollableBarChart(
    data: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface

    // Filter out empty days
    val filteredData = remember(data) {
        data.filter { it.value > 0 }
            .toList()
            .sortedBy { it.first }
    }

    if (filteredData.isEmpty()) return

    val barWidth = 80.dp
    val barSpacing = 20.dp
    val totalWidth = (barWidth + barSpacing) * filteredData.size

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            Canvas(
                modifier = Modifier
                    .width(totalWidth)
                    .height(280.dp)
            ) {
                val maxValue = filteredData.maxOf { it.second }
                val chartHeight = size.height - 80.dp.toPx()
                val barWidthPx = barWidth.toPx()
                val barSpacingPx = barSpacing.toPx()

                filteredData.forEachIndexed { index, (date, amount) ->
                    val startX = index * (barWidthPx + barSpacingPx)
                    val barHeight = (amount / maxValue * chartHeight).toFloat()
                    val barTop = size.height - 60.dp.toPx() - barHeight

                    // Draw bar
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryColor,
                                primaryColor.copy(alpha = 0.7f)
                            ),
                            startY = barTop,
                            endY = barTop + barHeight
                        ),
                        topLeft = Offset(startX, barTop),
                        size = Size(barWidthPx, barHeight),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )

                    // Amount label
                    drawContext.canvas.nativeCanvas.drawText(
                        "₹${amount.toInt()}",
                        startX + barWidthPx / 2,
                        barTop - 10.dp.toPx(),
                        Paint().apply {
                            color = primaryColor.toArgb()
                            textAlign = Paint.Align.CENTER
                            textSize = 28f
                            isFakeBoldText = true
                            isAntiAlias = true
                        }
                    )

                    // Date labels
                    val (dayName, dayNumber) = try {
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
                        val numberFormat = SimpleDateFormat("dd", Locale.getDefault())
                        val parsedDate = inputFormat.parse(date)
                        Pair(dayFormat.format(parsedDate!!), numberFormat.format(parsedDate))
                    } catch (e: Exception) {
                        Pair("", date.takeLast(2))
                    }

                    drawContext.canvas.nativeCanvas.drawText(
                        dayName,
                        startX + barWidthPx / 2,
                        size.height - 35.dp.toPx(),
                        Paint().apply {
                            color = onSurface.toArgb()
                            textAlign = Paint.Align.CENTER
                            textSize = 24f
                            isFakeBoldText = true
                            isAntiAlias = true
                        }
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        dayNumber,
                        startX + barWidthPx / 2,
                        size.height - 10.dp.toPx(),
                        Paint().apply {
                            color = onSurface.copy(alpha = 0.7f).toArgb()
                            textAlign = Paint.Align.CENTER
                            textSize = 20f
                            isAntiAlias = true
                        }
                    )
                }
            }
        }
    }
}

// Alternative Line Chart Version (Stock Market Style)
@Composable
private fun StockMarketLineChart(
    data: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface

    // Generate last 7 days data including empty days
    val last7Days = remember {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val days = mutableListOf<Pair<String, Double>>()

        for (i in 6 downTo 0) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_MONTH, -i)
            val dateStr = dateFormat.format(calendar.time)
            val amount = data[dateStr] ?: 0.0
            days.add(dateStr to amount)
        }
        days
    }

    Canvas(modifier = modifier) {
        if (last7Days.isEmpty()) return@Canvas

        val maxValue = last7Days.maxOfOrNull { it.second } ?: 1.0
        val chartMaxValue = if (maxValue <= 0) 1.0 else maxValue * 1.2

        val chartWidth = size.width - 100.dp.toPx()
        val chartHeight = size.height - 120.dp.toPx()
        val pointSpacing = chartWidth / (last7Days.size - 1)

        // Draw grid
        val gridPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#E8E8E8")
            strokeWidth = 1f
            pathEffect = android.graphics.DashPathEffect(floatArrayOf(5f, 5f), 0f)
            isAntiAlias = true
        }

        for (i in 0..4) {
            val y = size.height - 100.dp.toPx() - (i * chartHeight / 4)
            drawContext.canvas.nativeCanvas.drawLine(
                50.dp.toPx(), y, size.width - 50.dp.toPx(), y, gridPaint
            )
        }

        // Create path for line
        val linePath = androidx.compose.ui.graphics.Path()
        val points = mutableListOf<Offset>()

        last7Days.forEachIndexed { index, (date, amount) ->
            val x = 50.dp.toPx() + index * pointSpacing
            val y = size.height - 100.dp.toPx() - (amount / chartMaxValue * chartHeight).toFloat()

            points.add(Offset(x, y))

            if (index == 0) {
                linePath.moveTo(x, y)
            } else {
                linePath.lineTo(x, y)
            }
        }

        // Draw area under curve with gradient
        val areaPath = androidx.compose.ui.graphics.Path().apply {
            addPath(linePath)
            lineTo(points.last().x, size.height - 100.dp.toPx())
            lineTo(points.first().x, size.height - 100.dp.toPx())
            close()
        }

        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.3f),
                    primaryColor.copy(alpha = 0.05f)
                )
            )
        )

        // Draw main line
        drawPath(
            path = linePath,
            color = primaryColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 4.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )

        // Draw points and labels
        points.forEachIndexed { index, point ->
            val (date, amount) = last7Days[index]

            // Draw point
            drawCircle(
                color = Color.White,
                radius = 8.dp.toPx(),
                center = point
            )
            drawCircle(
                color = primaryColor,
                radius = 6.dp.toPx(),
                center = point
            )

            // Draw value label
            if (amount > 0) {
                drawContext.canvas.nativeCanvas.drawText(
                    "₹${amount.toInt()}",
                    point.x,
                    point.y - 15.dp.toPx(),
                    Paint().apply {
                        color = onSurface.toArgb()
                        textAlign = Paint.Align.CENTER
                        textSize = 24f
                        isFakeBoldText = true
                        isAntiAlias = true
                    }
                )
            }

            // Draw date label
            val dayName = try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("EEE\ndd", Locale.getDefault())
                val parsedDate = inputFormat.parse(date)
                outputFormat.format(parsedDate!!)
            } catch (e: Exception) {
                date.substring(5)
            }

            drawContext.canvas.nativeCanvas.drawText(
                dayName,
                point.x,
                size.height - 60.dp.toPx(),
                Paint().apply {
                    color = onSurface.copy(alpha = 0.7f).toArgb()
                    textAlign = Paint.Align.CENTER
                    textSize = 20f
                    isAntiAlias = true
                }
            )
        }
    }
}

@Composable
private fun ModernCategoryBreakdown(
    categoryTotals: Map<ExpenseCategory, Double>,
    totalAmount: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Category Breakdown",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "How you spent across different categories",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (categoryTotals.isNotEmpty()) {
                categoryTotals.entries
                    .sortedByDescending { it.value }
                    .forEach { (category, amount) ->
                        val percentage = ((amount / totalAmount) * 100).toInt()

                        ModernCategoryItem(
                            category = category,
                            amount = amount,
                            percentage = percentage,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No expenses found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernCategoryItem(
    category: ExpenseCategory,
    amount: Double,
    percentage: Int,
    modifier: Modifier = Modifier
) {
    val categoryColor = getCategoryColor(category)

    Column(modifier = modifier) {
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
                        .size(36.dp)
                        .background(categoryColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(category),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = categoryColor
                    )
                }

                Column {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$percentage% of total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Text(
                text = "₹${String.format("%.0f", amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = categoryColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = categoryColor,
            trackColor = categoryColor.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun InsightsCard(report: ExpenseReport) {
    val highestCategory = report.categoryTotals.maxByOrNull { it.value }
    val avgDaily = report.totalAmount / 7

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Smart Insights",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (highestCategory != null) {
                InsightItem(
                    icon = Icons.Default.TrendingUp,
                    title = "Highest Spending",
                    description = "${highestCategory.key.displayName} accounts for ${((highestCategory.value / report.totalAmount) * 100).toInt()}% of your expenses"
                )
            }

            InsightItem(
                icon = Icons.Default.CalendarToday,
                title = "Daily Average",
                description = "You spend an average of ₹${String.format("%.0f", avgDaily)} per day"
            )

            if (report.totalExpenses > 0) {
                InsightItem(
                    icon = Icons.Default.Receipt,
                    title = "Expense Frequency",
                    description = "You recorded ${report.totalExpenses} expenses over 7 days"
                )
            }
        }
    }
}

@Composable
private fun InsightItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.tertiary
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun DetailedDataTables(report: ExpenseReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Detailed Breakdown",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Daily Totals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            report.dailyTotals.entries
                .sortedByDescending { it.key }
                .forEach { (date, amount) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatDateForTable(date),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "₹${String.format("%.2f", amount)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (date != report.dailyTotals.keys.minOrNull()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 2.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    }
                }
        }
    }
}

@Composable
private fun ExportCard(
    onExportPDF: () -> Unit,
    onExportCSV: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.FileDownload,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Export Options",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Text(
                text = "Save your report in different formats",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onExportPDF,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PDF")
                }

                Button(
                    onClick = onExportCSV,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(
                        Icons.Default.TableChart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CSV")
                }
            }
        }
    }
}

// Fixed Helper Functions for Export and Sharing
private suspend fun generateAndSharePDF(context: Context, report: ExpenseReport) {
    try {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply {
            textSize = 16f
            isAntiAlias = true
        }

        // Title
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("Expense Report", 50f, 50f, paint)

        // Date range
        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Period: ${report.dateRange}", 50f, 80f, paint)

        // Summary
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("Summary", 50f, 120f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Total Expenses: ${report.totalExpenses}", 70f, 150f, paint)
        canvas.drawText("Total Amount: ₹${String.format("%.2f", report.totalAmount)}", 70f, 170f, paint)
        canvas.drawText("Daily Average: ₹${String.format("%.2f", report.totalAmount / 7)}", 70f, 190f, paint)

        // Category breakdown
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("Category Breakdown", 50f, 230f, paint)

        var yPos = 260f
        paint.textSize = 14f
        paint.isFakeBoldText = false

        report.categoryTotals.entries.sortedByDescending { it.value }.forEach { (category, amount) ->
            val percentage = ((amount / report.totalAmount) * 100).toInt()
            canvas.drawText("${category.displayName}: ₹${String.format("%.2f", amount)} ($percentage%)", 70f, yPos, paint)
            yPos += 20f
        }

        // Daily breakdown
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("Daily Breakdown", 50f, yPos + 20f, paint)

        yPos += 60f
        paint.textSize = 14f
        paint.isFakeBoldText = false

        report.dailyTotals.entries.sortedBy { it.key }.forEach { (date, amount) ->
            canvas.drawText("$date: ₹${String.format("%.2f", amount)}", 70f, yPos, paint)
            yPos += 20f
        }

        document.finishPage(page)

        // Save to file
        val downloadsDir = context.getExternalFilesDir(null)
        val file = File(downloadsDir, "expense_report_${System.currentTimeMillis()}.pdf")
        document.writeTo(FileOutputStream(file))
        document.close()

        // Share the file
        shareFile(context, file, "application/pdf")

    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    }
}

private suspend fun generateAndShareCSV(context: Context, report: ExpenseReport) {
    try {
        val csvContent = buildString {
            appendLine("Smart Expense Tracker - Report")
            appendLine("Period,${report.dateRange}")
            appendLine("")
            appendLine("Summary")
            appendLine("Total Expenses,${report.totalExpenses}")
            appendLine("Total Amount,₹${String.format("%.2f", report.totalAmount)}")
            appendLine("Daily Average,₹${String.format("%.2f", report.totalAmount / 7)}")
            appendLine("")
            appendLine("Daily Breakdown")
            appendLine("Date,Amount")
            report.dailyTotals.entries.sortedBy { it.key }.forEach { (date, amount) ->
                appendLine("$date,₹${String.format("%.2f", amount)}")
            }
            appendLine("")
            appendLine("Category Breakdown")
            appendLine("Category,Amount,Percentage")
            report.categoryTotals.entries.sortedByDescending { it.value }.forEach { (category, amount) ->
                val percentage = ((amount / report.totalAmount) * 100).toInt()
                appendLine("${category.displayName},₹${String.format("%.2f", amount)},$percentage%")
            }
        }

        val downloadsDir = context.getExternalFilesDir(null)
        val file = File(downloadsDir, "expense_report_${System.currentTimeMillis()}.csv")
        file.writeText(csvContent)

        shareFile(context, file, "text/csv")

    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    }
}

private fun shareReport(context: Context, report: ExpenseReport) {
    val shareText = buildString {
        appendLine("💰 My Expense Report")
        appendLine("Period: ${report.dateRange}")
        appendLine("")
        appendLine("📊 Summary:")
        appendLine("• Total Expenses: ${report.totalExpenses}")
        appendLine("• Amount Spent: ₹${String.format("%.2f", report.totalAmount)}")
        appendLine("• Daily Average: ₹${String.format("%.2f", report.totalAmount / 7)}")
        appendLine("")
        appendLine("📈 Top Categories:")
        report.categoryTotals.entries.sortedByDescending { it.value }.take(3).forEach { (category, amount) ->
            val percentage = ((amount / report.totalAmount) * 100).toInt()
            appendLine("• ${category.displayName}: ₹${String.format("%.0f", amount)} ($percentage%)")
        }
        appendLine("")
        appendLine("Generated by Smart Expense Tracker")
    }

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
        putExtra(Intent.EXTRA_SUBJECT, "My Expense Report - ${report.dateRange}")
    }

    context.startActivity(Intent.createChooser(shareIntent, "Share Report"))
}

private fun shareFile(context: Context, file: File, mimeType: String) {
    val uri = androidx.core.content.FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(shareIntent, "Share ${file.name}"))
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

private fun formatDateForTable(dateString: String): String {
    return try {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateString
    }
}