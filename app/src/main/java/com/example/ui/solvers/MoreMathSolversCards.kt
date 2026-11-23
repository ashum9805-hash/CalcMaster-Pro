package com.example.ui.solvers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Superscript
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.*
import com.example.ui.CalculatorViewModel
import com.example.ui.components.MathSlot
import com.example.ui.components.QuickSlotChip

// -------------------------------------------------------------
// 3D SOLIDS GEOMETRY TEMPLATE (SPHERE, CYLINDER, CONE, PRISM)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextbookSolids3DCard(
    viewModel: CalculatorViewModel,
    onShowMore: (CalculationResult) -> Unit
) {
    val solidType by viewModel.solidType.collectAsState()
    val dim1 by viewModel.solidDim1.collectAsState()
    val dim2 by viewModel.solidDim2.collectAsState()
    val dim3 by viewModel.solidDim3.collectAsState()
    val solution by viewModel.solidSolution.collectAsState()

    var solidMenuExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_solids_3d")
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "3D Solids Geometry",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Volume, surface area & exact π forms",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = solidMenuExpanded,
                        onExpandedChange = { solidMenuExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = solidType.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("3D Geometric Solid") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = solidMenuExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                                .testTag("dropdown_solid_type")
                        )
                        ExposedDropdownMenu(
                            expanded = solidMenuExpanded,
                            onDismissRequest = { solidMenuExpanded = false }
                        ) {
                            GeometryTrigSolvers.SolidType.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        viewModel.solidType.value = option
                                        viewModel.solveSolid3D()
                                        solidMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MathSlot(
                            value = dim1,
                            onValueChange = { viewModel.solidDim1.value = it; viewModel.solveSolid3D() },
                            label = solidType.label1,
                            placeholder = "4",
                            width = 92.dp
                        )
                        if (solidType.dimsCount >= 2) {
                            MathSlot(
                                value = dim2,
                                onValueChange = { viewModel.solidDim2.value = it; viewModel.solveSolid3D() },
                                label = solidType.label2,
                                placeholder = "10",
                                width = 92.dp
                            )
                        }
                        if (solidType.dimsCount >= 3) {
                            MathSlot(
                                value = dim3,
                                onValueChange = { viewModel.solidDim3.value = it; viewModel.solveSolid3D() },
                                label = solidType.label3,
                                placeholder = "6",
                                width = 92.dp
                            )
                        }
                    }
                }
            }

            solution?.let { sol ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${sol.type.label.uppercase()} RESULTS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Button(
                                onClick = {
                                    onShowMore(
                                        CalculationResult(
                                            expression = "${sol.type.label} Volume & Surface Area",
                                            resultNumber = sol.volume,
                                            formattedResult = "V = ${AdvancedSolvers.format(sol.volume)}, A = ${AdvancedSolvers.format(sol.surfaceArea)}",
                                            angleMode = viewModel.angleMode.value,
                                            steps = sol.steps,
                                            notes = sol.notes,
                                            alternatives = AlternativeRepresentations(
                                                scientific = sol.exactPiVolume?.let { "Exact V = $it" } ?: "N/A"
                                            )
                                        )
                                    )
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

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Volume (V)", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        text = AdvancedSolvers.format(sol.volume),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    sol.exactPiVolume?.let {
                                        Text("Exact: $it", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Surface Area (A)", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        text = AdvancedSolvers.format(sol.surfaceArea),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    sol.exactPiSurfaceArea?.let {
                                        Text("Exact: $it", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(sol.lateralOrExtraLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(AdvancedSolvers.format(sol.lateralOrExtraValue), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// ARBITRARY BASE LOGARITHM TEMPLATE: log_b(x)
// -------------------------------------------------------------
@Composable
fun TextbookLogarithmCard(
    viewModel: CalculatorViewModel,
    onShowMore: (CalculationResult) -> Unit
) {
    val base by viewModel.logBase.collectAsState()
    val arg by viewModel.logArg.collectAsState()
    val solution by viewModel.logSolution.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_logarithm")
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Superscript,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Logarithm Solver: log_b(x)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Compute any base logarithm via Change of Base",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "log",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(modifier = Modifier.padding(top = 18.dp, start = 4.dp, end = 4.dp)) {
                            MathSlot(
                                value = base,
                                onValueChange = { viewModel.logBase.value = it; viewModel.solveLogarithm() },
                                label = "Base (b)",
                                placeholder = "2",
                                width = 64.dp,
                                fontSize = 16
                            )
                        }
                        Text(
                            text = "(",
                            style = MaterialTheme.typography.displaySmall.copy(fontSize = 38.sp),
                            fontWeight = FontWeight.Thin,
                            color = MaterialTheme.colorScheme.outline
                        )
                        MathSlot(
                            value = arg,
                            onValueChange = { viewModel.logArg.value = it; viewModel.solveLogarithm() },
                            label = "Value (x)",
                            placeholder = "64",
                            width = 96.dp
                        )
                        Text(
                            text = ")",
                            style = MaterialTheme.typography.displaySmall.copy(fontSize = 38.sp),
                            fontWeight = FontWeight.Thin,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        QuickSlotChip("log₂(64)", { viewModel.logBase.value = "2"; viewModel.logArg.value = "64"; viewModel.solveLogarithm() })
                        QuickSlotChip("log₁₀(1000)", { viewModel.logBase.value = "10"; viewModel.logArg.value = "1000"; viewModel.solveLogarithm() })
                        QuickSlotChip("log₃(81)", { viewModel.logBase.value = "3"; viewModel.logArg.value = "81"; viewModel.solveLogarithm() })
                        QuickSlotChip("log₅(125)", { viewModel.logBase.value = "5"; viewModel.logArg.value = "125"; viewModel.solveLogarithm() })
                    }
                }
            }

            solution?.let { sol ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!sol.isValid) {
                        Text(
                            text = sol.errorMessage ?: "Invalid logarithm inputs.",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(14.dp)
                        )
                    } else {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("LOGARITHM RESULT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Button(
                                    onClick = {
                                        onShowMore(
                                            CalculationResult(
                                                expression = "log_${AdvancedSolvers.format(sol.base)}(${AdvancedSolvers.format(sol.argument)})",
                                                resultNumber = sol.result,
                                                formattedResult = sol.formattedResult,
                                                angleMode = viewModel.angleMode.value,
                                                steps = sol.steps,
                                                notes = sol.notes,
                                                alternatives = AlternativeRepresentations(scientific = "ln(x)/ln(b) = ${sol.formattedResult}")
                                            )
                                        )
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
                                text = "= ${sol.formattedResult}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Change of base: ln(${AdvancedSolvers.format(sol.argument)}) / ln(${AdvancedSolvers.format(sol.base)}) = ${AdvancedSolvers.format(sol.lnArg)} / ${AdvancedSolvers.format(sol.lnBase)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMBINATORICS TEMPLATE: nPr & nCr
// -------------------------------------------------------------
@Composable
fun TextbookCombinatoricsCard(
    viewModel: CalculatorViewModel,
    onShowMore: (CalculationResult) -> Unit
) {
    val nVal by viewModel.combN.collectAsState()
    val rVal by viewModel.combR.collectAsState()
    val solution by viewModel.combSolution.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_combinatorics")
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Combinatorics (nPr & nCr)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Permutations, combinations & selection odds",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MathSlot(
                            value = nVal,
                            onValueChange = { viewModel.combN.value = it.filter { c -> c.isDigit() }; viewModel.solveCombinatorics() },
                            label = "Total Items (n)",
                            placeholder = "10",
                            width = 110.dp
                        )
                        MathSlot(
                            value = rVal,
                            onValueChange = { viewModel.combR.value = it.filter { c -> c.isDigit() }; viewModel.solveCombinatorics() },
                            label = "Chosen Items (r)",
                            placeholder = "3",
                            width = 110.dp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        QuickSlotChip("n=10, r=3", { viewModel.combN.value = "10"; viewModel.combR.value = "3"; viewModel.solveCombinatorics() })
                        QuickSlotChip("52C5 (Poker)", { viewModel.combN.value = "52"; viewModel.combR.value = "5"; viewModel.solveCombinatorics() })
                        QuickSlotChip("n=49, r=6", { viewModel.combN.value = "49"; viewModel.combR.value = "6"; viewModel.solveCombinatorics() })
                    }
                }
            }

            solution?.let { sol ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!sol.isValid) {
                        Text(
                            text = sol.errorMessage ?: "Invalid n or r.",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(14.dp)
                        )
                    } else {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("ARRANGEMENTS & SUBSETS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Button(
                                    onClick = {
                                        onShowMore(
                                            CalculationResult(
                                                expression = "P(${sol.n}, ${sol.r}) & C(${sol.n}, ${sol.r})",
                                                resultNumber = sol.nCr,
                                                formattedResult = "nPr=${AdvancedSolvers.format(sol.nPr)}, nCr=${AdvancedSolvers.format(sol.nCr)}",
                                                angleMode = viewModel.angleMode.value,
                                                steps = sol.steps,
                                                notes = sol.notes,
                                                alternatives = AlternativeRepresentations(scientific = "Odds: ${sol.singleOutcomeProb}")
                                            )
                                        )
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

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Permutations (${sol.n}P${sol.r})", style = MaterialTheme.typography.labelSmall)
                                        Text(
                                            text = AdvancedSolvers.format(sol.nPr),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text("Order matters", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Combinations (${sol.n}C${sol.r})", style = MaterialTheme.typography.labelSmall)
                                        Text(
                                            text = AdvancedSolvers.format(sol.nCr),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text("Order ignored", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }

                            Text(
                                text = "Single combination odds: ${sol.singleOutcomeProb}",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
