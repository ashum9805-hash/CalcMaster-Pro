package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.math.AlternativeRepresentations
import com.example.math.CalculationResult
import com.example.science.ScienceSolvers
import com.example.ui.components.MathSlot
import com.example.ui.components.QuickSlotChip
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PhysicsFormulaWorkstation(
    viewModel: CalculatorViewModel,
    onShowSolution: (CalculationResult) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 6.dp, bottom = 24.dp)
    ) {
        item {
            PhysicsFormulaWorkstationBody(
                viewModel = viewModel,
                onShowSolution = onShowSolution
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhysicsFormulaWorkstationBody(
    viewModel: CalculatorViewModel,
    onShowSolution: (CalculationResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val formulaType by viewModel.physicsFormulaType.collectAsState()
    val v1 by viewModel.physVal1.collectAsState()
    val v2 by viewModel.physVal2.collectAsState()
    val v3 by viewModel.physVal3.collectAsState()
    val v4 by viewModel.physVal4.collectAsState()
    val solution by viewModel.physicsFormulaSolution.collectAsState()
    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Categorized Physics Expression Dropdown Selector
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = formulaType.label,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text("${formulaType.category} • ${formulaType.formulaSummary}") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                shape = RoundedCornerShape(14.dp),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
                    .testTag("dropdown_physics_formula")
            )
            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                modifier = Modifier.heightIn(max = 420.dp)
            ) {
                val grouped = ScienceSolvers.PhysicsFormulaType.entries.groupBy { it.category }
                grouped.entries.forEachIndexed { idx, (categoryName, items) ->
                    if (idx > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = categoryName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                    items.forEach { opt ->
                        val isSelected = opt == formulaType
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = opt.label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = opt.formulaSummary,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            trailingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary) }
                            } else null,
                            onClick = {
                                viewModel.selectPhysicsFormula(opt)
                                dropdownExpanded = false
                            },
                            modifier = Modifier.testTag("physics_menu_${opt.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Input Card & Quick Presets
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formulaType.formulaSummary,
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Dynamic Visual Canvas Diagram for Circular Motion & Projectile Motion
                if (formulaType == ScienceSolvers.PhysicsFormulaType.CIRCULAR_MOTION) {
                    CircularMotionDiagram(
                        speed = v2.toDoubleOrNull() ?: 20.0,
                        radius = v3.toDoubleOrNull() ?: 50.0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(126.dp)
                    )
                } else if (formulaType == ScienceSolvers.PhysicsFormulaType.PROJECTILE) {
                    ProjectileTrajectoryDiagram(
                        angleDeg = v2.toDoubleOrNull() ?: 45.0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    )
                }

                // Clean Input Rows
                PhysicsFormulaInputs(
                    formulaType = formulaType,
                    v1 = v1,
                    v2 = v2,
                    v3 = v3,
                    v4 = v4,
                    onV1Change = { viewModel.physVal1.value = it; viewModel.solvePhysicsFormula() },
                    onV2Change = { viewModel.physVal2.value = it; viewModel.solvePhysicsFormula() },
                    onV3Change = { viewModel.physVal3.value = it; viewModel.solvePhysicsFormula() },
                    onV4Change = { viewModel.physVal4.value = it; viewModel.solvePhysicsFormula() }
                )

                // Contextual Quick Preset Chips
                PhysicsQuickPresets(
                    formulaType = formulaType,
                    onApply = { p1, p2, p3, p4 ->
                        viewModel.physVal1.value = p1
                        if (p2 != null) viewModel.physVal2.value = p2
                        if (p3 != null) viewModel.physVal3.value = p3
                        if (p4 != null) viewModel.physVal4.value = p4
                        viewModel.solvePhysicsFormula()
                    }
                )
            }
        }

        // Physical Quantities Output Card
        solution?.let { sol ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!sol.isValid) {
                    Text(
                        text = sol.errorMessage ?: "Invalid physical parameters",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(14.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "PHYSICAL QUANTITIES",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = formulaType.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.useScienceValueInCalculator(sol.primaryRawNumber.toString()) },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .heightIn(min = 40.dp)
                                        .testTag("btn_physics_use_in_calc")
                                ) {
                                    Text("Use in Calc", style = MaterialTheme.typography.labelSmall)
                                }
                                Button(
                                    onClick = {
                                        onShowSolution(
                                            CalculationResult(
                                                expression = sol.type.formulaSummary,
                                                resultNumber = sol.primaryRawNumber,
                                                formattedResult = sol.primaryValue,
                                                angleMode = viewModel.angleMode.value,
                                                steps = sol.steps,
                                                notes = sol.notes,
                                                alternatives = AlternativeRepresentations(scientific = sol.secondaryValue)
                                            )
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .heightIn(min = 40.dp)
                                        .testTag("btn_physics_more")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("More", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ScienceResultTile(
                                label = sol.primaryLabel,
                                value = sol.primaryValue,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                            ScienceResultTile(
                                label = sol.secondaryLabel,
                                value = sol.secondaryValue,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        val chunked = sol.extraMetrics.chunked(2)
                        chunked.forEachIndexed { rowIdx, pairRow ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                pairRow.forEachIndexed { colIdx, (mLabel, mVal) ->
                                    val bg = if ((rowIdx + colIdx) % 2 == 0) {
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                    ScienceResultTile(
                                        label = mLabel,
                                        value = mVal,
                                        containerColor = bg,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (pairRow.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhysicsFormulaInputs(
    formulaType: ScienceSolvers.PhysicsFormulaType,
    v1: String,
    v2: String,
    v3: String,
    v4: String,
    onV1Change: (String) -> Unit,
    onV2Change: (String) -> Unit,
    onV3Change: (String) -> Unit,
    onV4Change: (String) -> Unit
) {
    when (formulaType) {
        ScienceSolvers.PhysicsFormulaType.KINEMATICS -> {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                MathSlot(v1, onV1Change, "Initial u (m/s)", "0", 98.dp)
                MathSlot(v2, onV2Change, "Accel a (m/s²)", "9.81", 98.dp)
                MathSlot(v3, onV3Change, "Time t (s)", "5", 86.dp)
            }
        }
        ScienceSolvers.PhysicsFormulaType.PROJECTILE -> {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                MathSlot(v1, onV1Change, "Speed v₀ (m/s)", "30", 98.dp)
                MathSlot(v2, onV2Change, "Angle θ (°)", "45", 92.dp)
                MathSlot(v3, onV3Change, "Gravity g (m/s²)", "9.81", 98.dp)
            }
        }
        ScienceSolvers.PhysicsFormulaType.FORCE_WORK -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MathSlot(v1, onV1Change, "Mass m (kg)", "10", 132.dp)
                    MathSlot(v2, onV2Change, "Accel a (m/s²)", "4.5", 132.dp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MathSlot(v3, onV3Change, "Distance d (m)", "12", 132.dp)
                    MathSlot(v4, onV4Change, "Time t (s)", "3", 132.dp)
                }
            }
        }
        ScienceSolvers.PhysicsFormulaType.CIRCULAR_MOTION -> {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                MathSlot(v1, onV1Change, "Mass m (kg)", "1200", 96.dp)
                MathSlot(v2, onV2Change, "Speed v (m/s)", "20", 96.dp)
                MathSlot(v3, onV3Change, "Radius r (m)", "50", 96.dp)
            }
        }
        ScienceSolvers.PhysicsFormulaType.MOMENTUM_IMPULSE -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MathSlot(v1, onV1Change, "Mass m (kg)", "1500", 132.dp)
                    MathSlot(v2, onV2Change, "Initial u (m/s)", "25", 132.dp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MathSlot(v3, onV3Change, "Final v (m/s)", "0", 132.dp)
                    MathSlot(v4, onV4Change, "Impact Δt (s)", "0.25", 132.dp)
                }
            }
        }
        ScienceSolvers.PhysicsFormulaType.ENERGY -> {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                MathSlot(v1, onV1Change, "Mass m (kg)", "2.5", 96.dp)
                MathSlot(v2, onV2Change, "Speed v (m/s)", "12", 96.dp)
                MathSlot(v3, onV3Change, "Height h (m)", "10", 96.dp)
            }
        }
        ScienceSolvers.PhysicsFormulaType.GRAVITATION -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MathSlot(v1, onV1Change, "Mass M₁ (kg)", "5.972E24", 136.dp)
                    MathSlot(v2, onV2Change, "Mass M₂ (kg)", "1000", 132.dp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    MathSlot(v3, onV3Change, "Separation Distance r (m)", "6.771E6", 200.dp)
                }
            }
        }
        ScienceSolvers.PhysicsFormulaType.OSCILLATIONS -> {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                MathSlot(v1, onV1Change, "Pendulum L (m)", "1.0", 96.dp)
                MathSlot(v2, onV2Change, "Spring k (N/m)", "250", 96.dp)
                MathSlot(v3, onV3Change, "Mass m (kg)", "2.5", 96.dp)
            }
        }
        ScienceSolvers.PhysicsFormulaType.FLUIDS_PRESSURE -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MathSlot(v1, onV1Change, "Mass m (kg)", "1000", 132.dp)
                    MathSlot(v2, onV2Change, "Volume V (m³)", "1.0", 132.dp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MathSlot(v3, onV3Change, "Area A (m²)", "0.5", 132.dp)
                    MathSlot(v4, onV4Change, "Depth h (m)", "10", 132.dp)
                }
            }
        }
        ScienceSolvers.PhysicsFormulaType.OHMS_LAW -> {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                MathSlot(v1, onV1Change, "Voltage V (V)", "12", 132.dp)
                MathSlot(v2, onV2Change, "Resistance R (Ω)", "470", 132.dp)
            }
        }
        ScienceSolvers.PhysicsFormulaType.ELECTROSTATICS -> {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                MathSlot(v1, onV1Change, "Charge q₁ (C)", "2E-6", 98.dp)
                MathSlot(v2, onV2Change, "Charge q₂ (C)", "-3E-6", 98.dp)
                MathSlot(v3, onV3Change, "Distance r (m)", "0.15", 92.dp)
            }
        }
        ScienceSolvers.PhysicsFormulaType.PHOTON -> {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                MathSlot(v1, onV1Change, "Frequency f (Hz)", "5.5E14", 200.dp)
            }
        }
    }
}

@Composable
private fun PhysicsQuickPresets(
    formulaType: ScienceSolvers.PhysicsFormulaType,
    onApply: (String, String?, String?, String?) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(top = 2.dp)
    ) {
        when (formulaType) {
            ScienceSolvers.PhysicsFormulaType.CIRCULAR_MOTION -> {
                QuickSlotChip("Car Turn", { onApply("1200", "20", "50", null) })
                QuickSlotChip("Centrifuge", { onApply("0.05", "45", "0.12", null) })
                QuickSlotChip("ISS Orbit", { onApply("420000", "7660", "6.771E6", null) })
            }
            ScienceSolvers.PhysicsFormulaType.FORCE_WORK -> {
                QuickSlotChip("10kg Push", { onApply("10", "4.5", "12", "3") })
                QuickSlotChip("Elevator", { onApply("800", "2.0", "15", "6") })
            }
            ScienceSolvers.PhysicsFormulaType.MOMENTUM_IMPULSE -> {
                QuickSlotChip("Car Brake", { onApply("1500", "25", "0", "0.25") })
                QuickSlotChip("Baseball Bat", { onApply("0.145", "-40", "48", "0.001") })
            }
            ScienceSolvers.PhysicsFormulaType.GRAVITATION -> {
                QuickSlotChip("Earth Surface", { onApply("5.972E24", "70", "6.371E6", null) })
                QuickSlotChip("Earth-Moon", { onApply("5.972E24", "7.342E22", "3.844E8", null) })
            }
            ScienceSolvers.PhysicsFormulaType.PROJECTILE -> {
                QuickSlotChip("45° Max Range", { onApply("30", "45", "9.80665", null) })
                QuickSlotChip("Moon Shot", { onApply("30", "45", "1.62", null) })
            }
            ScienceSolvers.PhysicsFormulaType.PHOTON -> {
                QuickSlotChip("Green 550THz", { onApply("5.5E14", null, null, null) })
                QuickSlotChip("Blue 660THz", { onApply("6.6E14", null, null, null) })
                QuickSlotChip("X-Ray 1E18", { onApply("1E18", null, null, null) })
            }
            else -> {}
        }
    }
}

@Composable
private fun CircularMotionDiagram(
    speed: Double,
    radius: Double,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.outlineVariant
    val radiusColor = MaterialTheme.colorScheme.secondary
    val velocityColor = MaterialTheme.colorScheme.primary
    val forceColor = MaterialTheme.colorScheme.error
    val centerColor = MaterialTheme.colorScheme.onSurface

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val rPx = (size.minDimension / 2f) * 0.72f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Circular path
            drawCircle(
                color = trackColor,
                radius = rPx,
                center = center,
                style = Stroke(width = 2.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f)))
            )
            // Center pivot
            drawCircle(color = centerColor, radius = 4.dp.toPx(), center = center)

            // Object position at 35° top-right
            val theta = Math.toRadians(35.0)
            val obj = Offset(
                center.x + (rPx * cos(theta)).toFloat(),
                center.y - (rPx * sin(theta)).toFloat()
            )

            // Radius line r
            drawLine(color = radiusColor, start = center, end = obj, strokeWidth = 2.dp.toPx())

            // Inward centripetal force / accel arrow (Fc, ac)
            val inwardEnd = Offset(
                obj.x - (rPx * 0.52f * cos(theta)).toFloat(),
                obj.y + (rPx * 0.52f * sin(theta)).toFloat()
            )
            drawLine(color = forceColor, start = obj, end = inwardEnd, strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)

            // Tangential velocity vector v (perpendicular counterclockwise)
            val velLen = rPx * 0.65f
            val velEnd = Offset(
                obj.x - (velLen * sin(theta)).toFloat(),
                obj.y - (velLen * cos(theta)).toFloat()
            )
            drawLine(color = velocityColor, start = obj, end = velEnd, strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)

            // Moving mass dot
            drawCircle(color = velocityColor, radius = 6.5.dp.toPx(), center = obj)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp)
        ) {
            Text("• v = ${ScienceSolvers.fmt(speed)} m/s (tangent)", style = MaterialTheme.typography.labelSmall, color = velocityColor)
            Text("• F_c, a_c inward (r = ${ScienceSolvers.fmt(radius)} m)", style = MaterialTheme.typography.labelSmall, color = forceColor)
        }
    }
}

@Composable
private fun ProjectileTrajectoryDiagram(
    angleDeg: Double,
    modifier: Modifier = Modifier
) {
    val pathColor = MaterialTheme.colorScheme.primary
    val groundColor = MaterialTheme.colorScheme.outlineVariant
    val apexColor = MaterialTheme.colorScheme.tertiary

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val padX = 24.dp.toPx()
            val groundY = size.height - 16.dp.toPx()
            val spanX = size.width - 2 * padX
            val clampedFrac = (sin(Math.toRadians(angleDeg.coerceIn(5.0, 85.0)))).toFloat().coerceIn(0.2f, 0.95f)
            val peakY = groundY - (size.height - 28.dp.toPx()) * clampedFrac

            drawLine(
                color = groundColor,
                start = Offset(padX, groundY),
                end = Offset(size.width - padX, groundY),
                strokeWidth = 2.dp.toPx()
            )

            val path = Path().apply {
                moveTo(padX, groundY)
                quadraticTo(size.width / 2f, 2f * peakY - groundY, size.width - padX, groundY)
            }
            drawPath(path = path, color = pathColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))

            // Apex marker H_max
            drawLine(
                color = apexColor,
                start = Offset(size.width / 2f, groundY),
                end = Offset(size.width / 2f, peakY),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
            )
            drawCircle(color = apexColor, radius = 4.5.dp.toPx(), center = Offset(size.width / 2f, peakY))
        }
        Text(
            text = "θ = ${ScienceSolvers.fmt(angleDeg)}°",
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
        )
    }
}
