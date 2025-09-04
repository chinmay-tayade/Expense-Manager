package com.chinmay.expensetracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.chinmay.expensetracker.R

// Custom Color Palette
private val PrimaryLight = Color(0xFF1565C0)
private val OnPrimaryLight = Color(0xFFFFFFFF)
private val PrimaryContainerLight = Color(0xFFE3F2FD)
private val OnPrimaryContainerLight = Color(0xFF0D47A1)

private val SecondaryLight = Color(0xFF43A047)
private val OnSecondaryLight = Color(0xFFFFFFFF)
private val SecondaryContainerLight = Color(0xFFE8F5E8)
private val OnSecondaryContainerLight = Color(0xFF1B5E20)

private val TertiaryLight = Color(0xFFE65100)
private val OnTertiaryLight = Color(0xFFFFFFFF)
private val TertiaryContainerLight = Color(0xFFFFE0B2)
private val OnTertiaryContainerLight = Color(0xFFBF360C)

private val ErrorLight = Color(0xFFD32F2F)
private val OnErrorLight = Color(0xFFFFFFFF)
private val ErrorContainerLight = Color(0xFFFFEBEE)
private val OnErrorContainerLight = Color(0xFFC62828)

private val BackgroundLight = Color(0xFFFCFCFC)
private val OnBackgroundLight = Color(0xFF1C1B1F)
private val SurfaceLight = Color(0xFFFFFFFF)
private val OnSurfaceLight = Color(0xFF1C1B1F)
private val SurfaceVariantLight = Color(0xFFF5F5F5)
private val OnSurfaceVariantLight = Color(0xFF49454F)

private val OutlineLight = Color(0xFF79747E)
private val OutlineVariantLight = Color(0xFFCAC4D0)

// Dark Theme Colors
private val PrimaryDark = Color(0xFF90CAF9)
private val OnPrimaryDark = Color(0xFF003D82)
private val PrimaryContainerDark = Color(0xFF0D47A1)
private val OnPrimaryContainerDark = Color(0xFFE3F2FD)

private val SecondaryDark = Color(0xFF81C784)
private val OnSecondaryDark = Color(0xFF1B5E20)
private val SecondaryContainerDark = Color(0xFF2E7D32)
private val OnSecondaryContainerDark = Color(0xFFE8F5E8)

private val TertiaryDark = Color(0xFFFFB74D)
private val OnTertiaryDark = Color(0xFFBF360C)
private val TertiaryContainerDark = Color(0xFFE65100)
private val OnTertiaryContainerDark = Color(0xFFFFE0B2)

private val ErrorDark = Color(0xFFEF5350)
private val OnErrorDark = Color(0xFF690005)
private val ErrorContainerDark = Color(0xFFC62828)
private val OnErrorContainerDark = Color(0xFFFFDAD6)

private val BackgroundDark = Color(0xFF1C1B1F)
private val OnBackgroundDark = Color(0xFFE6E1E5)
private val SurfaceDark = Color(0xFF1C1B1F)
private val OnSurfaceDark = Color(0xFFE6E1E5)
private val SurfaceVariantDark = Color(0xFF49454F)
private val OnSurfaceVariantDark = Color(0xFFCAC4D0)

private val OutlineDark = Color(0xFF938F99)
private val OutlineVariantDark = Color(0xFF49454F)

// Custom Font Family (you can add custom fonts)
val InterFontFamily = FontFamily(
    Font(R.font.bevietnampro_regular, FontWeight.Normal),
    Font(R.font.bevietnampro_medium, FontWeight.Medium),
    Font(R.font.bevietnampro_semibold, FontWeight.SemiBold),
    Font(R.font.bevietnampro_bold, FontWeight.Bold)
)

// Custom Typography
private val PremiumTypography = Typography(
    displayLarge = Typography().displayLarge.copy(fontFamily = InterFontFamily),
    displayMedium = Typography().displayMedium.copy(fontFamily = InterFontFamily),
    displaySmall = Typography().displaySmall.copy(fontFamily = InterFontFamily),

    headlineLarge = Typography().headlineLarge.copy(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold
    ),
    headlineMedium = Typography().headlineMedium.copy(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold
    ),
    headlineSmall = Typography().headlineSmall.copy(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold
    ),

    titleLarge = Typography().titleLarge.copy(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = Typography().titleMedium.copy(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium
    ),
    titleSmall = Typography().titleSmall.copy(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium
    ),

    bodyLarge = Typography().bodyLarge.copy(fontFamily = InterFontFamily),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = InterFontFamily),
    bodySmall = Typography().bodySmall.copy(fontFamily = InterFontFamily),

    labelLarge = Typography().labelLarge.copy(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium
    ),
    labelMedium = Typography().labelMedium.copy(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium
    ),
    labelSmall = Typography().labelSmall.copy(fontFamily = InterFontFamily)
)

// Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,

    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,

    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,

    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,

    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,

    outline = OutlineLight,
    outlineVariant = OutlineVariantLight
)

// Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,

    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,

    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,

    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,

    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,

    outline = OutlineDark,
    outlineVariant = OutlineVariantDark
)

// Theme Settings State
@Composable
fun rememberThemeSettings(): ThemeSettings {
    var isDarkTheme by remember { mutableStateOf(false) }
    var isDynamicColor by remember { mutableStateOf(true) }

    return ThemeSettings(
        isDarkTheme = isDarkTheme,
        isDynamicColor = isDynamicColor,
        setDarkTheme = { isDarkTheme = it },
        setDynamicColor = { isDynamicColor = it }
    )
}

data class ThemeSettings(
    val isDarkTheme: Boolean,
    val isDynamicColor: Boolean,
    val setDarkTheme: (Boolean) -> Unit,
    val setDynamicColor: (Boolean) -> Unit
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PremiumTypography,
        content = content
    )
}

// Custom Shapes
val PremiumShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Category-specific colors and themes
object ExpenseTheme {
    val StaffColor = Color(0xFF2E7D32)
    val TravelColor = Color(0xFF1565C0)
    val FoodColor = Color(0xFFE65100)
    val UtilityColor = Color(0xFF6A1B9A)

    val SuccessColor = Color(0xFF4CAF50)
    val WarningColor = Color(0xFFFF9800)
    val InfoColor = Color(0xFF2196F3)

    // Gradient colors for cards and backgrounds
    val GradientColors = listOf(
        Color(0xFF6366F1), Color(0xFF8B5CF6),
        Color(0xFFEC4899), Color(0xFFF59E0B),
        Color(0xFF10B981), Color(0xFF06B6D4)
    )
}

// Premium Card Styles
@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    elevation: Int = 4,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        content = content
    )
}

// Premium Button Styles
@Composable
fun PremiumButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    icon: @Composable (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 12.dp
        )
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PremiumOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    icon: @Composable (() -> Unit)? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

// Theme Settings Screen Component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsCard(
    themeSettings: ThemeSettings,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Theme Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Dark Theme Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dark Theme",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Use dark colors for better nighttime viewing",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Switch(
                    checked = themeSettings.isDarkTheme,
                    onCheckedChange = themeSettings.setDarkTheme
                )
            }

            // Dynamic Color Toggle (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Dynamic Colors",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Use colors from your wallpaper",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Switch(
                        checked = themeSettings.isDynamicColor,
                        onCheckedChange = themeSettings.setDynamicColor
                    )
                }
            }
        }
    }
}