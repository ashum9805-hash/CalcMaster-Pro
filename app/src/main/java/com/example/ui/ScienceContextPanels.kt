package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.math.*
import com.example.science.ScienceSolvers
import com.example.science.ScientificConstant
import com.example.ui.components.MathSlot
import com.example.ui.components.QuickSlotChip

// ===================================================================
// PHYSICS CONTEXT: CONSTANTS LOOKUP DECK & FORMULA SOLVER DECK
// ===================================================================

@Composable
fun PhysicsConstantsDeck(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.physicsSearchQuery.collectAsState()
    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) ScienceSolvers.physicsConstants
        else ScienceSolvers.physicsConstants.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                it.symbol.contains(searchQuery, ignoreCase = true) ||
                it.unit.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.physicsSearchQuery.value = it },
                placeholder = { Text("Search physics constants (c, h, G, mₑ…)", style = MaterialTheme.typography.bodySmall) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { viewModel.physicsSearchQuery.value = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(16.dp))
                        }
                    }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .testTag("physics_constant_search")
            )

            FilledTonalButton(
                onClick = { viewModel.onAppend("E") },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("btn_physics_sci_e")
            ) {
                Text("×10ⁿ", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(filtered, key = { it.symbol }) { constant ->
                ConstantInsertCard(
                    constant = constant,
                    onClick = { viewModel.onInsertConstant(constant.valueStr) }
                )
            }
        }
    }
}

// (PhysicsFormulaWorkstation is modularized in PhysicsExpressionsPanel.kt)

// ===================================================================
// CHEMISTRY CONTEXT: pH/pOH, GAS & DILUTION, CONSTANTS & MOLAR MASSES
// ===================================================================

@Composable
fun ChemistryConstantsDeck(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.chemSearchQuery.collectAsState()
    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) ScienceSolvers.chemistryConstants
        else ScienceSolvers.chemistryConstants.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                it.symbol.contains(searchQuery, ignoreCase = true) ||
                it.unit.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.chemSearchQuery.value = it },
                placeholder = { Text("Search N_A, R, F, H₂O, NaCl…", style = MaterialTheme.typography.bodySmall) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { viewModel.chemSearchQuery.value = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(16.dp))
                        }
                    }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .testTag("chem_constant_search")
            )

            FilledTonalButton(
                onClick = { viewModel.onAppend("E") },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("btn_chem_sci_e")
            ) {
                Text("×10ⁿ", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(filtered, key = { it.symbol + it.name }) { constant ->
                ConstantInsertCard(
                    constant = constant,
                    onClick = { viewModel.onInsertConstant(constant.valueStr) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChemistryPhWorkstation(
    viewModel: CalculatorViewModel,
    onShowSolution: (CalculationResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val inputType by viewModel.phInputType.collectAsState()
    val inputVal by viewModel.phInputValue.collectAsState()
    val solution by viewModel.phSolution.collectAsState()
    var dropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 6.dp, bottom = 20.dp)
    ) {
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = inputType.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Known Acid-Base Property (${inputType.unitHint})") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                                .testTag("dropdown_ph_input_type")
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            ScienceSolvers.PhInputType.entries.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text("${opt.label} (${opt.unitHint})") },
                                    onClick = {
                                        viewModel.phInputType.value = opt
                                        viewModel.solvePh()
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    MathSlot(
                        value = inputVal,
                        onValueChange = {
                            viewModel.phInputValue.value = it
                            viewModel.solvePh()
                        },
                        label = "Enter ${inputType.label} (supports E notation e.g. 1E-4)",
                        placeholder = inputType.placeholder,
                        width = 210.dp,
                        modifier = Modifier.testTag("input_ph_value")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        QuickSlotChip("pH=1", {
                            viewModel.phInputType.value = ScienceSolvers.PhInputType.PH
                            viewModel.phInputValue.value = "1"
                            viewModel.solvePh()
                        })
                        QuickSlotChip("pH=7", {
                            viewModel.phInputType.value = ScienceSolvers.PhInputType.PH
                            viewModel.phInputValue.value = "7"
                            viewModel.solvePh()
                        })
                        QuickSlotChip("pH=12.5", {
                            viewModel.phInputType.value = ScienceSolvers.PhInputType.PH
                            viewModel.phInputValue.value = "12.5"
                            viewModel.solvePh()
                        })
                        QuickSlotChip("[H⁺]=1E-4", {
                            viewModel.phInputType.value = ScienceSolvers.PhInputType.H_PLUS
                            viewModel.phInputValue.value = "1E-4"
                            viewModel.solvePh()
                        })
                    }
                }
            }
        }

        solution?.let { sol ->
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!sol.isValid) {
                        Text(
                            text = sol.errorMessage ?: "Invalid value",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(14.dp)
                        )
                    } else {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = sol.classification.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "25°C Equilibrium (K_w = 1.0×10⁻¹⁴)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = { viewModel.useScienceValueInCalculator(ScienceSolvers.fmt(sol.pH)) },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Use pH", style = MaterialTheme.typography.labelSmall)
                                    }
                                    Button(
                                        onClick = {
                                            onShowSolution(
                                                CalculationResult(
                                                    expression = "pH = ${ScienceSolvers.fmt(sol.pH)}, pOH = ${ScienceSolvers.fmt(sol.pOH)}",
                                                    resultNumber = sol.pH,
                                                    formattedResult = "pH=${ScienceSolvers.fmt(sol.pH)}, pOH=${ScienceSolvers.fmt(sol.pOH)}",
                                                    angleMode = viewModel.angleMode.value,
                                                    steps = sol.steps,
                                                    notes = sol.notes,
                                                    alternatives = AlternativeRepresentations(
                                                        scientific = "[H⁺] = ${ScienceSolvers.fmtSci(sol.hPlus)} M, [OH⁻] = ${ScienceSolvers.fmtSci(sol.ohMinus)} M"
                                                    )
                                                )
                                            )
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("More", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }

                            // Visual 0..14 pH Spectrum Bar
                            PhSpectrumBar(pH = sol.pH)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ScienceResultTile("pH", ScienceSolvers.fmt(sol.pH), MaterialTheme.colorScheme.primaryContainer, Modifier.weight(1f))
                                ScienceResultTile("pOH", ScienceSolvers.fmt(sol.pOH), MaterialTheme.colorScheme.secondaryContainer, Modifier.weight(1f))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ScienceResultTile("[H⁺] (M)", ScienceSolvers.fmtSci(sol.hPlus), MaterialTheme.colorScheme.tertiaryContainer, Modifier.weight(1f))
                                ScienceResultTile("[OH⁻] (M)", ScienceSolvers.fmtSci(sol.ohMinus), MaterialTheme.colorScheme.surface, Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChemistryGasAndDilutionWorkstation(
    viewModel: CalculatorViewModel,
    onShowSolution: (CalculationResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val gasTarget by viewModel.gasTarget.collectAsState()
    val gasP by viewModel.gasP.collectAsState()
    val gasV by viewModel.gasV.collectAsState()
    val gasN by viewModel.gasN.collectAsState()
    val gasT by viewModel.gasT.collectAsState()
    val gasSolution by viewModel.gasSolution.collectAsState()

    val dilTarget by viewModel.dilutionTarget.collectAsState()
    val dilM1 by viewModel.dilM1.collectAsState()
    val dilV1 by viewModel.dilV1.collectAsState()
    val dilM2 by viewModel.dilM2.collectAsState()
    val dilV2 by viewModel.dilV2.collectAsState()
    val dilSolution by viewModel.dilutionSolution.collectAsState()

    var gasMenuExpanded by remember { mutableStateOf(false) }
    var dilMenuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 6.dp, bottom = 20.dp)
    ) {
        // 1. Ideal Gas Law: PV = nRT
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Ideal Gas Law (PV = nRT)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    ExposedDropdownMenuBox(
                        expanded = gasMenuExpanded,
                        onExpandedChange = { gasMenuExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = gasTarget.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Solve For") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gasMenuExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = gasMenuExpanded,
                            onDismissRequest = { gasMenuExpanded = false }
                        ) {
                            ScienceSolvers.GasSolveTarget.entries.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text("${t.label} (${t.unit})") },
                                    onClick = {
                                        viewModel.gasTarget.value = t
                                        viewModel.solveIdealGas()
                                        gasMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        if (gasTarget != ScienceSolvers.GasSolveTarget.PRESSURE) {
                            MathSlot(gasP, { viewModel.gasP.value = it; viewModel.solveIdealGas() }, "P (atm)", "1.0", 80.dp)
                        }
                        if (gasTarget != ScienceSolvers.GasSolveTarget.VOLUME) {
                            MathSlot(gasV, { viewModel.gasV.value = it; viewModel.solveIdealGas() }, "V (L)", "22.4", 80.dp)
                        }
                        if (gasTarget != ScienceSolvers.GasSolveTarget.MOLES) {
                            MathSlot(gasN, { viewModel.gasN.value = it; viewModel.solveIdealGas() }, "n (mol)", "1.0", 80.dp)
                        }
                        if (gasTarget != ScienceSolvers.GasSolveTarget.TEMP) {
                            MathSlot(gasT, { viewModel.gasT.value = it; viewModel.solveIdealGas() }, "T (K)", "273.15", 80.dp)
                        }
                    }

                    gasSolution?.let { sol ->
                        if (!sol.isValid) {
                            Text(sol.errorMessage ?: "Invalid", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${sol.target.label} = ${ScienceSolvers.fmtSci(sol.resultValue)} ${sol.target.unit}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                TextButton(onClick = {
                                    onShowSolution(
                                        CalculationResult(
                                            expression = "PV = nRT (${sol.target.label})",
                                            resultNumber = sol.resultValue,
                                            formattedResult = "${ScienceSolvers.fmtSci(sol.resultValue)} ${sol.target.unit}",
                                            angleMode = viewModel.angleMode.value,
                                            steps = sol.steps,
                                            notes = sol.notes,
                                            alternatives = AlternativeRepresentations(scientific = ScienceSolvers.fmtSci(sol.resultValue))
                                        )
                                    )
                                }) {
                                    Text("More")
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Solution Dilution: M1*V1 = M2*V2
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Solution Dilution (M₁V₁ = M₂V₂)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    ExposedDropdownMenuBox(
                        expanded = dilMenuExpanded,
                        onExpandedChange = { dilMenuExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = dilTarget.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Solve For") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dilMenuExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dilMenuExpanded,
                            onDismissRequest = { dilMenuExpanded = false }
                        ) {
                            ScienceSolvers.DilutionTarget.entries.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t.label) },
                                    onClick = {
                                        viewModel.dilutionTarget.value = t
                                        viewModel.solveDilution()
                                        dilMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        if (dilTarget != ScienceSolvers.DilutionTarget.M1) {
                            MathSlot(dilM1, { viewModel.dilM1.value = it; viewModel.solveDilution() }, "M₁ (M)", "6.0", 80.dp)
                        }
                        if (dilTarget != ScienceSolvers.DilutionTarget.V1) {
                            MathSlot(dilV1, { viewModel.dilV1.value = it; viewModel.solveDilution() }, "V₁", "25", 80.dp)
                        }
                        if (dilTarget != ScienceSolvers.DilutionTarget.M2) {
                            MathSlot(dilM2, { viewModel.dilM2.value = it; viewModel.solveDilution() }, "M₂ (M)", "0.5", 80.dp)
                        }
                        if (dilTarget != ScienceSolvers.DilutionTarget.V2) {
                            MathSlot(dilV2, { viewModel.dilV2.value = it; viewModel.solveDilution() }, "V₂", "300", 80.dp)
                        }
                    }

                    dilSolution?.let { sol ->
                        if (!sol.isValid) {
                            Text(sol.errorMessage ?: "Invalid", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${sol.target.label} = ${ScienceSolvers.fmtSci(sol.resultValue)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                TextButton(onClick = {
                                    onShowSolution(
                                        CalculationResult(
                                            expression = "M₁V₁ = M₂V₂ (${sol.target.label})",
                                            resultNumber = sol.resultValue,
                                            formattedResult = ScienceSolvers.fmtSci(sol.resultValue),
                                            angleMode = viewModel.angleMode.value,
                                            steps = sol.steps,
                                            notes = sol.notes,
                                            alternatives = AlternativeRepresentations(scientific = ScienceSolvers.fmtSci(sol.resultValue))
                                        )
                                    )
                                }) {
                                    Text("More")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ===================================================================
// SHARED SCIENCE UI COMPONENTS
// ===================================================================

@Composable
private fun ConstantInsertCard(
    constant: ScientificConstant,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .width(156.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("constant_card_${constant.symbol}")
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = constant.symbol,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = constant.unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = constant.name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = constant.displayValue,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }
    }
}

@Composable
internal fun ScienceResultTile(
    label: String,
    value: String,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = containerColor.copy(alpha = 0.55f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun PhSpectrumBar(pH: Double) {
    val indicatorColor = MaterialTheme.colorScheme.onSurface
    val trackColors = remember {
        (0..14 step 2).map { i ->
            val hue = (i / 14f) * 240f // Red (acidic) -> Green (neutral) -> Blue/Violet (basic)
            Color.hsv(hue, 0.78f, 0.88f)
        }
    }
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("0 Acidic", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
            Text("7 Neutral", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
            Text("14 Basic", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
        ) {
            val barHeight = 10.dp.toPx()
            val top = (size.height - barHeight) / 2f
            drawRoundRect(
                brush = Brush.horizontalGradient(trackColors),
                topLeft = Offset(0f, top),
                size = Size(size.width, barHeight),
                cornerRadius = CornerRadius(barHeight / 2f, barHeight / 2f)
            )
            val frac = (pH.coerceIn(0.0, 14.0) / 14.0).toFloat()
            val cx = frac * size.width
            drawCircle(
                color = indicatorColor,
                radius = 7.dp.toPx(),
                center = Offset(cx, size.height / 2f)
            )
        }
    }
}
