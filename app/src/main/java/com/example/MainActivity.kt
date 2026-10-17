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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
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
            MyApplicationTheme {
                MainAppContainer()
            }
        }
    }
}

@Composable
fun MainAppContainer(
    viewModel: CalculatorViewModel = viewModel()
) {
    val currentDestination by viewModel.currentDestination.collectAsState()

    // Handle back button on secondary screens
    if (currentDestination != AppNavDestination.CALCULATOR) {
        BackHandler {
            viewModel.setDestination(AppNavDestination.CALCULATOR)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth >= 600.dp

        if (isWide) {
            // Tablet / Landscape Navigation Rail Layout
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
                        selected = currentDestination == AppNavDestination.HISTORY,
                        onClick = { viewModel.setDestination(AppNavDestination.HISTORY) },
                        icon = { Icon(Icons.Default.History, contentDescription = "History") },
                        label = { Text("History") },
                        modifier = Modifier.testTag("nav_rail_history")
                    )
                    NavigationRailItem(
                        selected = currentDestination == AppNavDestination.FORMULAS,
                        onClick = { viewModel.setDestination(AppNavDestination.FORMULAS) },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "Notes & Formulas") },
                        label = { Text("Formulas") },
                        modifier = Modifier.testTag("nav_rail_formulas")
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
            // Mobile Portrait Bottom Navigation Bar Layout
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
                            selected = currentDestination == AppNavDestination.HISTORY,
                            onClick = { viewModel.setDestination(AppNavDestination.HISTORY) },
                            icon = { Icon(Icons.Default.History, contentDescription = "History") },
                            label = { Text("History") },
                            modifier = Modifier.testTag("nav_bar_history")
                        )
                        NavigationBarItem(
                            selected = currentDestination == AppNavDestination.FORMULAS,
                            onClick = { viewModel.setDestination(AppNavDestination.FORMULAS) },
                            icon = { Icon(Icons.Default.MenuBook, contentDescription = "Notes & Formulas") },
                            label = { Text("Formulas") },
                            modifier = Modifier.testTag("nav_bar_formulas")
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
                onOpenAdvancedMath = { viewModel.setDestination(AppNavDestination.ADVANCED_MATH) }
            )
        }
        AppNavDestination.ADVANCED_MATH -> {
            AdvancedMathScreen(viewModel = viewModel)
        }
        AppNavDestination.UNIT_CONVERTER -> {
            UnitConverterScreen(viewModel = viewModel)
        }
        AppNavDestination.HISTORY -> {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateToCalculator = { viewModel.setDestination(AppNavDestination.CALCULATOR) }
            )
        }
        AppNavDestination.FORMULAS -> {
            FormulasScreen()
        }
    }
}
