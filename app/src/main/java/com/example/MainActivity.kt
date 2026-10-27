package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CalculatorViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val accentPalette by viewModel.accentPalette.collectAsState()

            MyApplicationTheme(
                themeMode = themeMode,
                accentPalette = accentPalette
            ) {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(
    viewModel: CalculatorViewModel = viewModel()
) {
    val currentDestination by viewModel.currentDestination.collectAsState()

    // Handle back button on secondary screens to return to previous destination
    if (currentDestination != AppNavDestination.CALCULATOR) {
        BackHandler {
            viewModel.setDestination(AppNavDestination.CALCULATOR)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth >= 600.dp

        if (isWide) {
            // Tablet / Landscape Navigation Rail Layout (Decluttered: History moved to top-right on screens)
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    modifier = Modifier.testTag("app_navigation_rail"),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationRailItem(
                        selected = currentDestination == AppNavDestination.CALCULATOR,
                        onClick = { viewModel.setDestination(AppNavDestination.CALCULATOR) },
                        icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculator") },
                        label = { Text("Calc") },
                        modifier = Modifier.testTag("nav_rail_calculator")
                    )
                    NavigationRailItem(
                        selected = currentDestination == AppNavDestination.ADVANCED_MATH,
                        onClick = { viewModel.setDestination(AppNavDestination.ADVANCED_MATH) },
                        icon = { Icon(Icons.Default.Functions, contentDescription = "Equations & Advanced Math") },
                        label = { Text("Equations") },
                        modifier = Modifier.testTag("nav_rail_advanced")
                    )
                    NavigationRailItem(
                        selected = currentDestination == AppNavDestination.UNIT_CONVERTER,
                        onClick = { viewModel.setDestination(AppNavDestination.UNIT_CONVERTER) },
                        icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Unit Converter") },
                        label = { Text("Converter") },
                        modifier = Modifier.testTag("nav_rail_converter")
                    )
                    NavigationRailItem(
                        selected = currentDestination == AppNavDestination.FORMULAS,
                        onClick = { viewModel.setDestination(AppNavDestination.FORMULAS) },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "Notes & Formulas") },
                        label = { Text("Formulas") },
                        modifier = Modifier.testTag("nav_rail_formulas")
                    )
                    NavigationRailItem(
                        selected = currentDestination == AppNavDestination.SETTINGS,
                        onClick = { viewModel.setDestination(AppNavDestination.SETTINGS) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings & Customization") },
                        label = { Text("Settings") },
                        modifier = Modifier.testTag("nav_rail_settings")
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    ScreenContent(currentDestination, viewModel)
                }
            }
        } else {
            // Mobile Portrait Bottom Navigation Bar Layout (Decluttered to clean 4 primary modes)
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar(
                        modifier = Modifier.testTag("app_bottom_bar"),
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.CALCULATOR,
                            onClick = { viewModel.setDestination(AppNavDestination.CALCULATOR) },
                            icon = { Icon(Icons.Default.Calculate, contentDescription = "Calculator") },
                            label = { Text("Calculator") },
                            modifier = Modifier.testTag("nav_bar_calculator")
                        )
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.ADVANCED_MATH,
                            onClick = { viewModel.setDestination(AppNavDestination.ADVANCED_MATH) },
                            icon = { Icon(Icons.Default.Functions, contentDescription = "Equations & Solvers") },
                            label = { Text("Equations") },
                            modifier = Modifier.testTag("nav_bar_advanced")
                        )
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.UNIT_CONVERTER,
                            onClick = { viewModel.setDestination(AppNavDestination.UNIT_CONVERTER) },
                            icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Unit Converter") },
                            label = { Text("Converter") },
                            modifier = Modifier.testTag("nav_bar_converter")
                        )
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.FORMULAS,
                            onClick = { viewModel.setDestination(AppNavDestination.FORMULAS) },
                            icon = { Icon(Icons.Default.MenuBook, contentDescription = "Notes & Formulas") },
                            label = { Text("Formulas") },
                            modifier = Modifier.testTag("nav_bar_formulas")
                        )
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.SETTINGS,
                            onClick = { viewModel.setDestination(AppNavDestination.SETTINGS) },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings & Customization") },
                            label = { Text("Settings") },
                            modifier = Modifier.testTag("nav_bar_settings")
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    ScreenContent(currentDestination, viewModel)
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(
    destination: AppNavDestination,
    viewModel: CalculatorViewModel
) {
    when (destination) {
        AppNavDestination.CALCULATOR -> {
            CalculatorScreen(
                viewModel = viewModel,
                onOpenHistory = { viewModel.setDestination(AppNavDestination.HISTORY) },
                onOpenAdvancedMath = { viewModel.setDestination(AppNavDestination.ADVANCED_MATH) },
                onOpenSettings = { viewModel.setDestination(AppNavDestination.SETTINGS) }
            )
        }
        AppNavDestination.ADVANCED_MATH -> {
            AdvancedMathScreen(
                viewModel = viewModel,
                onOpenHistory = { viewModel.setDestination(AppNavDestination.HISTORY) }
            )
        }
        AppNavDestination.UNIT_CONVERTER -> {
            UnitConverterScreen(
                viewModel = viewModel,
                onOpenHistory = { viewModel.setDestination(AppNavDestination.HISTORY) }
            )
        }
        AppNavDestination.FORMULAS -> {
            FormulasScreen(
                onOpenHistory = { viewModel.setDestination(AppNavDestination.HISTORY) }
            )
        }
        AppNavDestination.HISTORY -> {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateToCalculator = { viewModel.setDestination(AppNavDestination.CALCULATOR) }
            )
        }
        AppNavDestination.SETTINGS -> {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { viewModel.setDestination(AppNavDestination.CALCULATOR) }
            )
        }
    }
}
