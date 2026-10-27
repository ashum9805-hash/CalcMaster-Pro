package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class ThemeMode(val title: String) {
    SYSTEM("Follow System"),
    LIGHT("Light"),
    DARK("Dark")
}

enum class AccentPalette(val title: String) {
    DEFAULT_BLUE("Classic Blue"),
    EMERALD("Emerald Green"),
    VIOLET("Cyber Violet"),
    SUNSET("Sunset Amber"),
    OLED_MINIMAL("OLED Pitch Black")
}

private fun buildLightScheme(accent: AccentPalette): ColorScheme {
    return when (accent) {
        AccentPalette.DEFAULT_BLUE -> lightColorScheme(
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
            background = BackgroundLight,
            onBackground = OnBackgroundLight,
            surface = SurfaceLight,
            onSurface = OnSurfaceLight,
            surfaceVariant = SurfaceVariantLight,
            onSurfaceVariant = OnSurfaceVariantLight
        )
        AccentPalette.EMERALD -> lightColorScheme(
            primary = EmeraldPrimaryLight,
            onPrimary = Color.White,
            primaryContainer = EmeraldPrimaryContainerLight,
            onPrimaryContainer = Color(0xFF002111),
            secondary = Color(0xFF4D6353),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFD0E8D4),
            onSecondaryContainer = Color(0xFF0B1F13),
            tertiary = TertiaryLight,
            onTertiary = Color.White,
            tertiaryContainer = TertiaryContainerLight,
            onTertiaryContainer = OnTertiaryContainerLight,
            background = Color(0xFFF6FBF4),
            onBackground = Color(0xFF181D19),
            surface = Color.White,
            onSurface = Color(0xFF181D19),
            surfaceVariant = Color(0xFFDCE5DC),
            onSurfaceVariant = Color(0xFF404942)
        )
        AccentPalette.VIOLET -> lightColorScheme(
            primary = VioletPrimaryLight,
            onPrimary = Color.White,
            primaryContainer = VioletPrimaryContainerLight,
            onPrimaryContainer = Color(0xFF260059),
            secondary = Color(0xFF625B71),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFE8DEF8),
            onSecondaryContainer = Color(0xFF1E192B),
            tertiary = Color(0xFF7E525D),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFFFD9E1),
            onTertiaryContainer = Color(0xFF31101B),
            background = Color(0xFFFCF7FF),
            onBackground = Color(0xFF1D1A22),
            surface = Color.White,
            onSurface = Color(0xFF1D1A22),
            surfaceVariant = Color(0xFFE6E0EB),
            onSurfaceVariant = Color(0xFF48454D)
        )
        AccentPalette.SUNSET -> lightColorScheme(
            primary = SunsetPrimaryLight,
            onPrimary = Color.White,
            primaryContainer = SunsetPrimaryContainerLight,
            onPrimaryContainer = Color(0xFF2E1500),
            secondary = Color(0xFF715B2E),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFDE0A6),
            onSecondaryContainer = Color(0xFF261900),
            tertiary = Color(0xFF8B4F58),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFFFD9DD),
            onTertiaryContainer = Color(0xFF3B0813),
            background = Color(0xFFFFF8F4),
            onBackground = Color(0xFF211A13),
            surface = Color.White,
            onSurface = Color(0xFF211A13),
            surfaceVariant = Color(0xFFEFE0CF),
            onSurfaceVariant = Color(0xFF4F4539)
        )
        AccentPalette.OLED_MINIMAL -> lightColorScheme(
            primary = Color(0xFF1E293B),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE2E8F0),
            onPrimaryContainer = Color(0xFF0F172A),
            secondary = Color(0xFF475569),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFF1F5F9),
            onSecondaryContainer = Color(0xFF1E293B),
            tertiary = Color(0xFF334155),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFE2E8F0),
            onTertiaryContainer = Color(0xFF0F172A),
            background = Color(0xFFF8FAFC),
            onBackground = Color(0xFF0F172A),
            surface = Color.White,
            onSurface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFFE2E8F0),
            onSurfaceVariant = Color(0xFF475569)
        )
    }
}

private fun buildDarkScheme(accent: AccentPalette): ColorScheme {
    return when (accent) {
        AccentPalette.DEFAULT_BLUE -> darkColorScheme(
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
            background = BackgroundDark,
            onBackground = OnBackgroundDark,
            surface = SurfaceDark,
            onSurface = OnSurfaceDark,
            surfaceVariant = SurfaceVariantDark,
            onSurfaceVariant = OnSurfaceVariantDark
        )
        AccentPalette.EMERALD -> darkColorScheme(
            primary = EmeraldPrimaryDark,
            onPrimary = Color(0xFF003920),
            primaryContainer = EmeraldPrimaryContainerDark,
            onPrimaryContainer = EmeraldPrimaryContainerLight,
            secondary = Color(0xFFB4CCB8),
            onSecondary = Color(0xFF203527),
            secondaryContainer = Color(0xFF364B3C),
            onSecondaryContainer = Color(0xFFD0E8D4),
            tertiary = TertiaryDark,
            onTertiary = OnTertiaryDark,
            tertiaryContainer = TertiaryContainerDark,
            onTertiaryContainer = OnTertiaryContainerDark,
            background = Color(0xFF0D1510),
            onBackground = Color(0xFFE0E5DF),
            surface = Color(0xFF121B15),
            onSurface = Color(0xFFE0E5DF),
            surfaceVariant = Color(0xFF212E24),
            onSurfaceVariant = Color(0xFFC0C9C0)
        )
        AccentPalette.VIOLET -> darkColorScheme(
            primary = VioletPrimaryDark,
            onPrimary = Color(0xFF3F048E),
            primaryContainer = VioletPrimaryContainerDark,
            onPrimaryContainer = VioletPrimaryContainerLight,
            secondary = Color(0xFFCBC2DB),
            onSecondary = Color(0xFF332D41),
            secondaryContainer = Color(0xFF4A4458),
            onSecondaryContainer = Color(0xFFE8DEF8),
            tertiary = Color(0xFFEFB8C4),
            onTertiary = Color(0xFF49252F),
            tertiaryContainer = Color(0xFF633B45),
            onTertiaryContainer = Color(0xFFFFD9E1),
            background = Color(0xFF120E18),
            onBackground = Color(0xFFE6E0EB),
            surface = Color(0xFF1A1424),
            onSurface = Color(0xFFE6E0EB),
            surfaceVariant = Color(0xFF2C2438),
            onSurfaceVariant = Color(0xFFC9C4D0)
        )
        AccentPalette.SUNSET -> darkColorScheme(
            primary = SunsetPrimaryDark,
            onPrimary = Color(0xFF4A2800),
            primaryContainer = SunsetPrimaryContainerDark,
            onPrimaryContainer = SunsetPrimaryContainerLight,
            secondary = Color(0xFFDFBE86),
            onSecondary = Color(0xFF3F2D06),
            secondaryContainer = Color(0xFF57431A),
            onSecondaryContainer = Color(0xFFFDE0A6),
            tertiary = Color(0xFFFFB2BC),
            onTertiary = Color(0xFF541D26),
            tertiaryContainer = Color(0xFF70333E),
            onTertiaryContainer = Color(0xFFFFD9DD),
            background = Color(0xFF18120C),
            onBackground = Color(0xFFEBE0D7),
            surface = Color(0xFF221A12),
            onSurface = Color(0xFFEBE0D7),
            surfaceVariant = Color(0xFF352B20),
            onSurfaceVariant = Color(0xFFD2C4B4)
        )
        AccentPalette.OLED_MINIMAL -> darkColorScheme(
            primary = Color(0xFFE2E8F0),
            onPrimary = Color(0xFF0F172A),
            primaryContainer = Color(0xFF334155),
            onPrimaryContainer = Color(0xFFF8FAFC),
            secondary = Color(0xFF94A3B8),
            onSecondary = Color(0xFF0F172A),
            secondaryContainer = Color(0xFF1E293B),
            onSecondaryContainer = Color(0xFFE2E8F0),
            tertiary = Color(0xFFCBD5E1),
            onTertiary = Color(0xFF0F172A),
            tertiaryContainer = Color(0xFF334155),
            onTertiaryContainer = Color(0xFFF1F5F9),
            background = OledBackground,
            onBackground = Color(0xFFF8FAFC),
            surface = OledSurface,
            onSurface = Color(0xFFF8FAFC),
            surfaceVariant = OledSurfaceVariant,
            onSurfaceVariant = Color(0xFF94A3B8)
        )
    }
}

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accentPalette: AccentPalette = AccentPalette.DEFAULT_BLUE,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val context = LocalContext.current
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> buildDarkScheme(accentPalette)
        else -> buildLightScheme(accentPalette)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
