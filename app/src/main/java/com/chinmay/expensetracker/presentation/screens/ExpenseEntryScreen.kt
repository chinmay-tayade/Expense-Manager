package com.chinmay.expensetracker.presentation.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.chinmay.expensetracker.domain.dataModels.ExpenseCategory
import com.chinmay.expensetracker.presentation.viewModels.RoomExpenseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ExpenseEntryScreen(
    viewModel: RoomExpenseViewModel,
    onNavigateBack: () -> Unit = {},
    expenseId: String? = null
) {
    val entryState by viewModel.entryState.collectAsState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Navigation state
    var hasSubmitted by remember { mutableStateOf(false) }

    // UI state
    val screenTitle = if (entryState.isEditing) "Edit Expense" else "Add Expense"
    val buttonText = if (entryState.isEditing) "Update Expense" else "Add Expense"

    // Image handling
    var capturedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            capturedImageBitmap = imageBitmap
            imageBitmap?.let {
                val imagePath = "captured_${System.currentTimeMillis()}.jpg"
                viewModel.updateReceiptImage(imagePath)
            }
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            viewModel.updateReceiptImage(it.toString())
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(takePictureIntent)
        }
    }

    // Load expense for editing
    LaunchedEffect(expenseId) {
        if (!expenseId.isNullOrEmpty()) {
            viewModel.loadExpenseForEdit(expenseId)
        } else {
            viewModel.resetEntryForm()
        }
    }

    // Handle toast messages - FIXED NAVIGATION LOGIC
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }

            // Only navigate on successful submission, not during editing
            if (hasSubmitted && (
                        message.contains("successfully", ignoreCase = true) ||
                                message.contains("added", ignoreCase = true) ||
                                message.contains("updated", ignoreCase = true) ||
                                message.contains("saved", ignoreCase = true)
                        )) {
                kotlinx.coroutines.delay(1000)
                onNavigateBack()
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData = snackbarData,
                        containerColor = if (snackbarData.visuals.message.contains("success", ignoreCase = true)) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        contentColor = if (snackbarData.visuals.message.contains("success", ignoreCase = true)) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onError
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()

                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
        ) {
            // Top Bar
            TopAppBar(
                title = {
                    Text(
                        screenTitle,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetEntryForm()
                        onNavigateBack()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Today's Total Card (only for new expenses)
                if (!entryState.isEditing) {
                    TotalSpentCard(totalSpent = uiState.totalSpentToday)
                }

                // Main Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            text = if (entryState.isEditing) "Edit Expense Details" else "Expense Details",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Title Input
                        EnhancedTextField(
                            value = entryState.title,
                            onValueChange = viewModel::updateTitle,
                            label = "What did you spend on?",
                            leadingIcon = Icons.Default.Edit,
                            error = entryState.titleError,
                            placeholder = "e.g., Coffee, Groceries, Taxi"
                        )

                        // Amount Input
                        EnhancedTextField(
                            value = entryState.amount,
                            onValueChange = viewModel::updateAmount,
                            label = "Amount",
                            leadingIcon = Icons.Default.CurrencyRupee,
                            error = entryState.amountError,
                            keyboardType = KeyboardType.Decimal,
                            prefix = "₹",
                            placeholder = "0.00"
                        )

                        // Category Selection
                        EnhancedCategorySelector(
                            selectedCategory = entryState.selectedCategory,
                            onCategorySelected = viewModel::updateCategory
                        )

                        // Notes Input
                        EnhancedTextField(
                            value = entryState.notes,
                            onValueChange = viewModel::updateNotes,
                            label = "Notes (Optional)",
                            leadingIcon = Icons.Default.Notes,
                            placeholder = "Add any additional details...",
                            maxLines = 3,
                            supportingText = "${entryState.notes.length}/100"
                        )
                    }
                }

                // Receipt Section
                ReceiptSection(
                    imagePath = entryState.receiptImagePath,
                    capturedBitmap = capturedImageBitmap,
                    selectedUri = selectedImageUri,
                    onCameraClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            cameraLauncher.launch(takePictureIntent)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    onGalleryClick = {
                        galleryLauncher.launch("image/*")
                    },
                    onRemoveClick = {
                        capturedImageBitmap = null
                        selectedImageUri = null
                        viewModel.updateReceiptImage(null)
                    }
                )

                // Submit Button - FIXED LOGIC
                AnimatedSubmitButton(
                    isSubmitting = entryState.isSubmitting,
                    buttonText = buttonText,
                    onClick = {
                        hasSubmitted = true  // Set flag before submission
                        viewModel.submitExpense()
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun TotalSpentCard(totalSpent: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.TrendingUp,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Today's Total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = "₹${String.format("%.2f", totalSpent)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    prefix: String = "",
    placeholder: String = "",
    maxLines: Int = 1,
    supportingText: String = ""
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint = if (error != null) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            },
            prefix = if (prefix.isNotEmpty()) { { Text(prefix) } } else null,
            placeholder = { Text(placeholder) },
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            maxLines = maxLines,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        } else if (supportingText.isNotEmpty()) {
            Text(
                text = supportingText,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun EnhancedCategorySelector(
    selectedCategory: ExpenseCategory,
    onCategorySelected: (ExpenseCategory) -> Unit
) {
    Column {
        Text(
            text = "Category",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(ExpenseCategory.entries.toTypedArray()) { category ->
                CategoryChip(
                    category = category,
                    isSelected = selectedCategory == category,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryChip(
    category: ExpenseCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(
                category.displayName,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        },
        selected = isSelected,
        leadingIcon = {
            Icon(
                getCategoryIcon(category),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
        modifier = Modifier.animateContentSize(),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = getCategoryColor(category).copy(alpha = 0.2f),
            selectedLabelColor = getCategoryColor(category),
            selectedLeadingIconColor = getCategoryColor(category)
        )
    )
}

@Composable
private fun ReceiptSection(
    imagePath: String?,
    capturedBitmap: Bitmap?,
    selectedUri: Uri?,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Receipt",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (capturedBitmap != null || selectedUri != null || imagePath != null) {
                // Show image preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    when {
                        capturedBitmap != null -> {
                            Image(
                                bitmap = capturedBitmap.asImageBitmap(),
                                contentDescription = "Receipt",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        selectedUri != null -> {
                            Image(
                                painter = rememberAsyncImagePainter(selectedUri),
                                contentDescription = "Receipt",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        imagePath != null -> {
                            Image(
                                painter = rememberAsyncImagePainter(imagePath),
                                contentDescription = "Receipt",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    // Remove button
                    IconButton(
                        onClick = onRemoveClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(
                                MaterialTheme.colorScheme.errorContainer,
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCameraClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retake")
                    }

                    OutlinedButton(
                        onClick = onGalleryClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Choose")
                    }
                }
            } else {
                // Show empty state with buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Add Receipt",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "Take a photo or choose from gallery",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ElevatedButton(
                            onClick = onCameraClick,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Camera")
                        }

                        ElevatedButton(
                            onClick = onGalleryClick,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gallery")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimatedSubmitButton(
    isSubmitting: Boolean,
    buttonText: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isSubmitting,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        )
    ) {
        AnimatedContent(
            targetState = isSubmitting,
            transitionSpec = {
                slideInVertically { it } + fadeIn() with
                        slideOutVertically { -it } + fadeOut()
            },
            label = "submit_button_animation"
        ) { submitting ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (submitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (buttonText.contains("Update")) "Updating..." else "Adding...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        if (buttonText.contains("Update")) Icons.Default.Update else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
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