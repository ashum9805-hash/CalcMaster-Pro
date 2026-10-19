package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.AlternativeRepresentations
import com.example.math.CalculationResult
import com.example.ui.components.MathSlot
import com.example.ui.components.QuickSlotChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedMathScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val activeTab by viewModel.advancedTab.collectAsState()
    val tabScrollState = rememberScrollState()

    var activeSolutionForModal by remember { mutableStateOf<CalculationResult?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Textbook Math Formulas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Fill dedicated mathematical slots directly",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Tab Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(tabScrollState)
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeTab == AdvancedMathTab.SUMMATION,
                onClick = { viewModel.setAdvancedTab(AdvancedMathTab.SUMMATION) },
                label = { Text("Summation (Σ)") },
                leadingIcon = { Icon(Icons.Default.Functions, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_summation")
            )
            FilterChip(
                selected = activeTab == AdvancedMathTab.QUADRATIC,
                onClick = { viewModel.setAdvancedTab(AdvancedMathTab.QUADRATIC) },
                label = { Text("Quadratic (ax²+bx+c)") },
                leadingIcon = { Icon(Icons.Default.SquareFoot, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_quadratic")
            )
            FilterChip(
                selected = activeTab == AdvancedMathTab.LINEAR_SYSTEM,
                onClick = { viewModel.setAdvancedTab(AdvancedMathTab.LINEAR_SYSTEM) },
                label = { Text("2×2 Linear System") },
                leadingIcon = { Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_linear_system")
            )
            FilterChip(
                selected = activeTab == AdvancedMathTab.EXPONENTIAL,
                onClick = { viewModel.setAdvancedTab(AdvancedMathTab.EXPONENTIAL) },
                label = { Text("Exponential (bˣ = c)") },
                leadingIcon = { Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_exponential")
            )
            FilterChip(
                selected = activeTab == AdvancedMathTab.CALCULUS,
                onClick = { viewModel.setAdvancedTab(AdvancedMathTab.CALCULUS) },
                label = { Text("Calculus (∫ & d/dx)") },
                leadingIcon = { Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_calculus")
            )
            FilterChip(
                selected = activeTab == AdvancedMathTab.STATISTICS,
                onClick = { viewModel.setAdvancedTab(AdvancedMathTab.STATISTICS) },
                label = { Text("Statistics") },
                leadingIcon = { Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("tab_statistics")
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Active Solver Content in Scrollable Column
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            when (activeTab) {
                AdvancedMathTab.SUMMATION -> item {
                    TextbookSummationCard(
                        viewModel = viewModel,
                        onShowMore = { res -> activeSolutionForModal = res }
                    )
                }
                AdvancedMathTab.QUADRATIC -> item {
                    TextbookQuadraticCard(
                        viewModel = viewModel,
                        onShowMore = { res -> activeSolutionForModal = res }
                    )
                }
                AdvancedMathTab.LINEAR_SYSTEM -> item {
                    TextbookLinearSystemCard(
                        viewModel = viewModel,
                        onShowMore = { res -> activeSolutionForModal = res }
                    )
                }
                AdvancedMathTab.EXPONENTIAL -> item {
                    TextbookExponentialCard(
                        viewModel = viewModel,
                        onShowMore = { res -> activeSolutionForModal = res }
                    )
                }
                AdvancedMathTab.CALCULUS -> item {
                    TextbookCalculusCard(
                        viewModel = viewModel,
                        onShowMore = { res -> activeSolutionForModal = res }
                    )
                }
                AdvancedMathTab.STATISTICS -> item {
                    TextbookStatisticsCard(
                        viewModel = viewModel,
                        onShowMore = { res -> activeSolutionForModal = res }
                    )
                }
            }
        }
    }

    if (activeSolutionForModal != null) {
        SolutionDetailSheet(
            result = activeSolutionForModal!!,
            onDismiss = { activeSolutionForModal = null }
        )
    }
}

// -------------------------------------------------------------
// 1. TEXTBOOK SUMMATION TEMPLATE (Σ with visual dedicated slots)
// -------------------------------------------------------------
@Composable
private fun TextbookSummationCard(
    viewModel: CalculatorViewModel,
    onShowMore: (CalculationResult) -> Unit
) {
    val formula by viewModel.sumFormula.collectAsState()
    val start by viewModel.sumStart.collectAsState()
    val end by viewModel.sumEnd.collectAsState()
    val result by viewModel.summationResult.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Functions, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "Summation Formula Template",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Tap any slot to edit. Tap ∞ to evaluate an infinite series.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Dedicated Visual Sigma Template
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Row 1: Upper limit slot with Infinity helper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MathSlot(
                            value = end,
                            onValueChange = {
                                viewModel.sumEnd.value = it
                                viewModel.solveSummation()
                            },
                            label = "Upper Bound (n or ∞)",
                            placeholder = "∞",
                            width = 90.dp,
                            isSpecial = end == "∞"
                        )

                        // Quick Infinity & Limit chips
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            QuickSlotChip(
                                text = "∞",
                                onClick = {
                                    viewModel.sumEnd.value = "∞"
                                    viewModel.solveSummation()
                                },
                                isHighlighted = end == "∞"
                            )
                            QuickSlotChip(text = "5", onClick = { viewModel.sumEnd.value = "5"; viewModel.solveSummation() })
                            QuickSlotChip(text = "10", onClick = { viewModel.sumEnd.value = "10"; viewModel.solveSummation() })
                            QuickSlotChip(text = "100", onClick = { viewModel.sumEnd.value = "100"; viewModel.solveSummation() })
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 2: Big Greek Sigma + Function Slot inside brackets
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Σ",
                            style = MaterialTheme.typography.displayMedium.copy(fontSize = 58.sp),
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )

                        Text(
                            text = "(",
                            style = MaterialTheme.typography.displayMedium.copy(fontSize = 46.sp),
                            fontWeight = FontWeight.Thin,
                            color = MaterialTheme.colorScheme.outline
                        )

                        MathSlot(
                            value = formula,
                            onValueChange = {
                                viewModel.sumFormula.value = it
                                viewModel.solveSummation()
                            },
                            label = "Term Formula f(x)",
                            placeholder = "x²",
                            width = 160.dp,
                            minHeight = 44.dp,
                            fontSize = 20
                        )

                        Text(
                            text = ")",
                            style = MaterialTheme.typography.displayMedium.copy(fontSize = 46.sp),
                            fontWeight = FontWeight.Thin,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 3: Lower limit slot: x = [ start ]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "x =",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )

                        MathSlot(
                            value = start,
                            onValueChange = {
                                viewModel.sumStart.value = it
                                viewModel.solveSummation()
                            },
                            label = "Start Index (a)",
                            placeholder = "1",
                            width = 80.dp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            QuickSlotChip(text = "0", onClick = { viewModel.sumStart.value = "0"; viewModel.solveSummation() })
                            QuickSlotChip(text = "1", onClick = { viewModel.sumStart.value = "1"; viewModel.solveSummation() })
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick formula presets row
                    Text(
                        text = "Quick Series Presets:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        QuickSlotChip(
                            text = "(1/2)ˣ",
                            onClick = {
                                viewModel.sumFormula.value = "(1/2)ˣ"
                                viewModel.sumStart.value = "1"
                                viewModel.sumEnd.value = "∞"
                                viewModel.solveSummation()
                            }
                        )
                        QuickSlotChip(
                            text = "1/x²",
                            onClick = {
                                viewModel.sumFormula.value = "1/x²"
                                viewModel.sumStart.value = "1"
                                viewModel.sumEnd.value = "∞"
                                viewModel.solveSummation()
                            }
                        )
                        QuickSlotChip(
                            text = "1/x!",
                            onClick = {
                                viewModel.sumFormula.value = "1/x!"
                                viewModel.sumStart.value = "0"
                                viewModel.sumEnd.value = "∞"
                                viewModel.solveSummation()
                            }
                        )
                        QuickSlotChip(
                            text = "x²",
                            onClick = {
                                viewModel.sumFormula.value = "x²"
                                viewModel.sumStart.value = "1"
                                viewModel.sumEnd.value = "5"
                                viewModel.solveSummation()
                            }
                        )
                        QuickSlotChip(
                            text = "2x - 1",
                            onClick = {
                                viewModel.sumFormula.value = "2x - 1"
                                viewModel.sumStart.value = "1"
                                viewModel.sumEnd.value = "10"
                                viewModel.solveSummation()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Result Display Card
            if (result != null) {
                val res = result!!
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (res.isInfinite) "SERIES LIMIT (N → ∞)" else "TOTAL SUM",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Button(
                                onClick = {
                                    val calcRes = CalculationResult(
                                        expression = "Σ (x=${res.startValStr} to ${res.endValStr}) of (${res.expression})",
                                        resultNumber = res.totalSum,
                                        formattedResult = res.formattedSum,
                                        angleMode = viewModel.angleMode.value,
                                        steps = res.steps,
                                        notes = res.notes,
                                        alternatives = AlternativeRepresentations(scientific = "N/A")
                                    )
                                    onShowMore(calcRes)
                                },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp).testTag("btn_more_summation")
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("More: Steps & Notes", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Text(
                            text = "= ${res.formattedSum}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Text(text = "Expanded Series Terms:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        res.termsList.forEach { term ->
                            Text(text = "• $term", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------------
// 2. TEXTBOOK QUADRATIC TEMPLATE: [ a ] x²  +  [ b ] x  +  [ c ] = 0
// ------------------------------------------------------------------
@Composable
private fun TextbookQuadraticCard(
    viewModel: CalculatorViewModel,
    onShowMore: (CalculationResult) -> Unit
) {
    val a by viewModel.quadA.collectAsState()
    val b by viewModel.quadB.collectAsState()
    val c by viewModel.quadC.collectAsState()
    val solution by viewModel.quadSolution.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.SquareFoot, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "Quadratic Polynomial Template",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Fill in the coefficient slots for a, b, and c",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Visual Equation Line: [ a ] x²  +  [ b ] x  +  [ c ]  =  0
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 18.dp)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    MathSlot(
                        value = a,
                        onValueChange = { viewModel.quadA.value = it; viewModel.solveQuadratic() },
                        label = "a",
                        placeholder = "1",
                        width = 64.dp
                    )
                    Text(
                        text = " x² + ",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    MathSlot(
                        value = b,
                        onValueChange = { viewModel.quadB.value = it; viewModel.solveQuadratic() },
                        label = "b",
                        placeholder = "-5",
                        width = 64.dp
                    )
                    Text(
                        text = " x + ",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    MathSlot(
                        value = c,
                        onValueChange = { viewModel.quadC.value = it; viewModel.solveQuadratic() },
                        label = "c",
                        placeholder = "6",
                        width = 64.dp
                    )
                    Text(
                        text = " = 0",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (solution != null) {
                val sol = solution!!
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "QUADRATIC ROOTS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Button(
                                onClick = {
                                    val calcRes = CalculationResult(
                                        expression = "${a}x² + (${b})x + (${c}) = 0",
                                        resultNumber = 0.0,
                                        formattedResult = "x₁ = ${sol.root1Str}, x₂ = ${sol.root2Str}",
                                        angleMode = viewModel.angleMode.value,
                                        steps = sol.steps,
                                        notes = sol.notes,
                                        alternatives = AlternativeRepresentations(scientific = "N/A")
                                    )
                                    onShowMore(calcRes)
                                },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp).testTag("btn_more_quadratic")
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("More: Steps & Notes", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "x₁ = ${sol.root1Str}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (sol.root2Str != "N/A") {
                            Text(
                                text = "x₂ = ${sol.root2Str}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(text = "Discriminant (Δ): ${sol.discriminant} (${sol.discriminantDesc})", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Vertex: (${sol.vertexX}, ${sol.vertexY})", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Factored Form: ${sol.factoredForm}", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. TEXTBOOK 2X2 LINEAR SYSTEM TEMPLATE: { a1 x + b1 y = c1
// -------------------------------------------------------------
@Composable
private fun TextbookLinearSystemCard(
    viewModel: CalculatorViewModel,
    onShowMore: (CalculationResult) -> Unit
) {
    val a1 by viewModel.sysA1.collectAsState()
    val b1 by viewModel.sysB1.collectAsState()
    val c1 by viewModel.sysC1.collectAsState()
    val a2 by viewModel.sysA2.collectAsState()
    val b2 by viewModel.sysB2.collectAsState()
    val c2 by viewModel.sysC2.collectAsState()
    val solution by viewModel.systemSolution.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.GridOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "Linear System Template",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Two equations in two unknowns, aligned mathematically",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bracket { holding both equations
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "{",
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                        fontWeight = FontWeight.Thin,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Equation 1
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            MathSlot(value = a1, onValueChange = { viewModel.sysA1.value = it; viewModel.solveLinearSystem() }, label = "a₁", placeholder = "2", width = 56.dp)
                            Text(" x + ", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            MathSlot(value = b1, onValueChange = { viewModel.sysB1.value = it; viewModel.solveLinearSystem() }, label = "b₁", placeholder = "1", width = 56.dp)
                            Text(" y = ", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            MathSlot(value = c1, onValueChange = { viewModel.sysC1.value = it; viewModel.solveLinearSystem() }, label = "c₁", placeholder = "8", width = 56.dp)
                        }

                        // Equation 2
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            MathSlot(value = a2, onValueChange = { viewModel.sysA2.value = it; viewModel.solveLinearSystem() }, label = "a₂", placeholder = "1", width = 56.dp)
                            Text(" x + ", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            MathSlot(value = b2, onValueChange = { viewModel.sysB2.value = it; viewModel.solveLinearSystem() }, label = "b₂", placeholder = "-1", width = 56.dp)
                            Text(" y = ", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            MathSlot(value = c2, onValueChange = { viewModel.sysC2.value = it; viewModel.solveLinearSystem() }, label = "c₂", placeholder = "1", width = 56.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (solution != null) {
                val sol = solution!!
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INTERSECTION POINT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Button(
                                onClick = {
                                    val calcRes = CalculationResult(
                                        expression = "System: { ${a1}x + ${b1}y = ${c1}; ${a2}x + ${b2}y = ${c2} }",
                                        resultNumber = 0.0,
                                        formattedResult = "x = ${sol.xResult}, y = ${sol.yResult}",
                                        angleMode = viewModel.angleMode.value,
                                        steps = sol.steps,
                                        notes = sol.notes,
                                        alternatives = AlternativeRepresentations(scientific = "N/A")
                                    )
                                    onShowMore(calcRes)
                                },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("More: Steps & Notes", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "x = ${sol.xResult}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "y = ${sol.yResult}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(text = "Status: ${sol.status}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Cramer's Determinants: D = ${sol.detD}, Dx = ${sol.detDx}, Dy = ${sol.detDy}", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. TEXTBOOK CALCULUS TEMPLATE: ∫ & d/dx with visual slots
// -------------------------------------------------------------
@Composable
private fun TextbookCalculusCard(
    viewModel: CalculatorViewModel,
    onShowMore: (CalculationResult) -> Unit
) {
    val formula by viewModel.calcFormula.collectAsState()
    val point by viewModel.calcPoint.collectAsState()
    val lower by viewModel.calcLower.collectAsState()
    val upper by viewModel.calcUpper.collectAsState()
    val result by viewModel.calculusResult.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Timeline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "Calculus Templates",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Definite Integral Visual Slot Template
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Definite Integral ∫ₐᵇ f(x) dx",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Upper bound slot
                    MathSlot(
                        value = upper,
                        onValueChange = { viewModel.calcUpper.value = it },
                        label = "Upper bound (b)",
                        placeholder = "3",
                        width = 80.dp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "∫",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp),
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        MathSlot(
                            value = formula,
                            onValueChange = { viewModel.calcFormula.value = it },
                            label = "Integrand f(x)",
                            placeholder = "x²",
                            width = 150.dp,
                            fontSize = 18
                        )

                        Text(
                            text = " dx",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Lower bound slot
                    MathSlot(
                        value = lower,
                        onValueChange = { viewModel.calcLower.value = it },
                        label = "Lower bound (a)",
                        placeholder = "0",
                        width = 80.dp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.solveIntegral() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Evaluate Integral ∫")
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Derivative Visual Slot Template
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Derivative d/dx evaluated at x₀",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "d/dx [ ",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )

                        MathSlot(
                            value = formula,
                            onValueChange = { viewModel.calcFormula.value = it },
                            label = "f(x)",
                            placeholder = "x³",
                            width = 110.dp
                        )

                        Text(
                            text = " ] at x = ",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )

                        MathSlot(
                            value = point,
                            onValueChange = { viewModel.calcPoint.value = it },
                            label = "x₀",
                            placeholder = "2",
                            width = 60.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.solveDerivative() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Evaluate Derivative d/dx")
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (result != null) {
                val res = result!!
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = res.operation.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Button(
                                onClick = {
                                    val calcRes = CalculationResult(
                                        expression = "${res.operation} [${res.function}]",
                                        resultNumber = res.resultValue,
                                        formattedResult = res.formattedResult,
                                        angleMode = viewModel.angleMode.value,
                                        steps = res.steps,
                                        notes = res.notes,
                                        alternatives = AlternativeRepresentations(scientific = "N/A")
                                    )
                                    onShowMore(calcRes)
                                },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("More: Steps & Notes", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Text(
                            text = "= ${res.formattedResult}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. TEXTBOOK STATISTICS TEMPLATE
// -------------------------------------------------------------
@Composable
private fun TextbookStatisticsCard(
    viewModel: CalculatorViewModel,
    onShowMore: (CalculationResult) -> Unit
) {
    val statsInput by viewModel.statsInput.collectAsState()
    val statsResult by viewModel.statsResult.collectAsState()
    val focusManager = LocalFocusManager.current

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "Descriptive Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = statsInput,
                onValueChange = {
                    viewModel.statsInput.value = it
                    viewModel.solveStatistics()
                },
                label = { Text("Data Set (comma separated values)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (statsResult != null) {
                val st = statsResult!!
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DATA SUMMARY (N = ${st.count})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Button(
                                onClick = {
                                    val calcRes = CalculationResult(
                                        expression = "Dataset (N=${st.count}): $statsInput",
                                        resultNumber = st.mean,
                                        formattedResult = "Mean = ${st.mean}, StdDev = ${st.standardDeviation}",
                                        angleMode = viewModel.angleMode.value,
                                        steps = st.steps,
                                        notes = st.notes,
                                        alternatives = AlternativeRepresentations(scientific = "N/A")
                                    )
                                    onShowMore(calcRes)
                                },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("More: Steps & Notes", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Mean (Average):", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "${st.mean}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Median:", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "${st.median}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Mode:", style = MaterialTheme.typography.bodyMedium)
                            Text(text = st.mode, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Standard Deviation (s):", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "${st.standardDeviation}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Variance (s²):", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "${st.variance}", style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 6. TEXTBOOK EXPONENTIAL EQUATION TEMPLATE (a · bˣ = c)
// -------------------------------------------------------------
@Composable
private fun TextbookExponentialCard(
    viewModel: CalculatorViewModel,
    onShowMore: (CalculationResult) -> Unit
) {
    val a by viewModel.expA.collectAsState()
    val base by viewModel.expBase.collectAsState()
    val c by viewModel.expC.collectAsState()
    val solution by viewModel.exponentialSolution.collectAsState()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("textbook_exponential_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "Exponential Equation: a · bˣ = c",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Solves for x using logarithmic reduction: x = ln(c / a) / ln(b).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Dedicated Visual Slots: [ a ] · [ b ]ˣ = [ c ]
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MathSlot(
                            value = a,
                            onValueChange = {
                                viewModel.expA.value = it
                                viewModel.solveExponential()
                            },
                            label = "Coeff (a)",
                            placeholder = "1",
                            width = 65.dp
                        )

                        Text(
                            text = " · ",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        MathSlot(
                            value = base,
                            onValueChange = {
                                viewModel.expBase.value = it
                                viewModel.solveExponential()
                            },
                            label = "Base (b)",
                            placeholder = "2",
                            width = 65.dp
                        )

                        Text(
                            text = "ˣ = ",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        MathSlot(
                            value = c,
                            onValueChange = {
                                viewModel.expC.value = it
                                viewModel.solveExponential()
                            },
                            label = "Result (c)",
                            placeholder = "16",
                            width = 75.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Quick Presets:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        QuickSlotChip(
                            text = "2ˣ = 16",
                            onClick = {
                                viewModel.expA.value = "1"
                                viewModel.expBase.value = "2"
                                viewModel.expC.value = "16"
                                viewModel.solveExponential()
                            }
                        )
                        QuickSlotChip(
                            text = "eˣ = 10",
                            onClick = {
                                viewModel.expA.value = "1"
                                viewModel.expBase.value = "2.71828"
                                viewModel.expC.value = "10"
                                viewModel.solveExponential()
                            }
                        )
                        QuickSlotChip(
                            text = "3·2ˣ = 24",
                            onClick = {
                                viewModel.expA.value = "3"
                                viewModel.expBase.value = "2"
                                viewModel.expC.value = "24"
                                viewModel.solveExponential()
                            }
                        )
                        QuickSlotChip(
                            text = "10ˣ = 1000",
                            onClick = {
                                viewModel.expA.value = "1"
                                viewModel.expBase.value = "10"
                                viewModel.expC.value = "1000"
                                viewModel.solveExponential()
                            }
                        )
                        QuickSlotChip(
                            text = "(0.5)ˣ = 0.125",
                            onClick = {
                                viewModel.expA.value = "1"
                                viewModel.expBase.value = "0.5"
                                viewModel.expC.value = "0.125"
                                viewModel.solveExponential()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Solution Card
            if (solution != null) {
                val sol = solution!!
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "EXPONENTIAL SOLUTION",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Button(
                                onClick = {
                                    val calcRes = CalculationResult(
                                        expression = "${a} · (${base})ˣ = $c",
                                        resultNumber = if (sol.xResult.isNaN()) 0.0 else sol.xResult,
                                        formattedResult = "x = ${sol.formattedResult}",
                                        angleMode = viewModel.angleMode.value,
                                        steps = sol.steps,
                                        notes = sol.notes,
                                        alternatives = AlternativeRepresentations(scientific = sol.formattedResult)
                                    )
                                    onShowMore(calcRes)
                                },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp).testTag("btn_more_exponential")
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("More: Steps & Notes", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "x = ${sol.formattedResult}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

