package com.example.ui.solvers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.math.*
import com.example.ui.CalculatorViewModel
import com.example.ui.components.MathSlot
import com.example.ui.components.QuickSlotChip
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun TextbookTrigonometryCard(
    viewModel: CalculatorViewModel,
    onShowMore: (CalculationResult) -> Unit
) {
    val subMode by viewModel.trigSubMode.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_trigonometry_solver")
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
                            imageVector = Icons.Default.ChangeHistory,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Trigonometry & Triangle Solver",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Unit circle ratios, SOH-CAH-TOA & Law of Cosines",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 3 Sub-Mode Buttons (Fit cleanly without sideways scrolling)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = subMode == 0,
                    onClick = { viewModel.trigSubMode.value = 0 },
                    label = { Text("Unit Circle", maxLines = 1) },
                    modifier = Modifier.weight(1f).testTag("chip_trig_unit_circle")
                )
                FilterChip(
                    selected = subMode == 1,
                    onClick = { viewModel.trigSubMode.value = 1 },
                    label = { Text("Right △", maxLines = 1) },
                    modifier = Modifier.weight(1f).testTag("chip_trig_right_tri")
                )
                FilterChip(
                    selected = subMode == 2,
                    onClick = { viewModel.trigSubMode.value = 2 },
                    label = { Text("Law Cos/Sin", maxLines = 1) },
                    modifier = Modifier.weight(1.1f).testTag("chip_trig_general_tri")
                )
            }

            when (subMode) {
                0 -> UnitCircleTrigSection(viewModel, onShowMore)
                1 -> RightTriangleSection(viewModel, onShowMore)
                else -> GeneralTriangleSection(viewModel, onShowMore)
            }
        }
    }
}

@Composable
private fun UnitCircleTrigSection(
    viewModel: CalculatorViewModel,
    onShowMore: (CalculationResult) -> Unit
) {
    val angleInput by viewModel.trigAngleInput.collectAsState()
    val angleMode by viewModel.trigAngleMode.collectAsState()
    val solution by viewModel.trigAngleSolution.collectAsState()

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MathSlot(
                    value = angleInput,
                    onValueChange = {
                        viewModel.trigAngleInput.value = it
                        viewModel.solveTrigAngle()
                    },
                    label = "Angle θ",
                    placeholder = "45",
                    width = 120.dp,
                    modifier = Modifier.testTag("input_trig_angle")
                )

                FilterChip(
                    selected = angleMode == AngleMode.DEG,
                    onClick = {
                        viewModel.trigAngleMode.value =
                            if (angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
                        viewModel.solveTrigAngle()
                    },
                    label = {
                        Text(
                            text = if (angleMode == AngleMode.DEG) "DEG (°)" else "RAD",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.testTag("btn_trig_angle_mode")
                )
            }

            // Quick Angle Presets (2 clean rows, no sideways scroll needed)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("0", "30", "45", "60", "90").forEach { deg ->
                    QuickSlotChip(
                        text = "${deg}°",
                        isHighlighted = angleMode == AngleMode.DEG && angleInput == deg,
                        onClick = {
                            viewModel.trigAngleMode.value = AngleMode.DEG
                            viewModel.trigAngleInput.value = deg
                            viewModel.solveTrigAngle()
                        }
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("120", "135", "180", "270", "315").forEach { deg ->
                    QuickSlotChip(
                        text = "${deg}°",
                        isHighlighted = angleMode == AngleMode.DEG && angleInput == deg,
                        onClick = {
                            viewModel.trigAngleMode.value = AngleMode.DEG
                            viewModel.trigAngleInput.value = deg
                            viewModel.solveTrigAngle()
                        }
                    )
                }
            }

            // Live Unit Circle Canvas
            UnitCircleCanvas(
                angleRad = solution?.angleRad ?: 0.0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ALL 6 TRIGONOMETRIC FUNCTIONS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${sol.quadrant} • ${sol.piMultiple} rad",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Button(
                        onClick = {
                            onShowMore(
                                CalculationResult(
                                    expression = "Trig(${AdvancedSolvers.format(sol.angleDeg)}° / ${sol.piMultiple} rad)",
                                    resultNumber = sol.sinVal,
                                    formattedResult = "sin=${AdvancedSolvers.format(sol.sinVal)}, cos=${AdvancedSolvers.format(sol.cosVal)}",
                                    angleMode = angleMode,
                                    steps = sol.steps,
                                    notes = sol.notes,
                                    alternatives = AlternativeRepresentations(
                                        scientific = "tan = ${sol.tanVal?.let { AdvancedSolvers.format(it) } ?: "Undefined"}",
                                        degreesToRadians = "${AdvancedSolvers.format(sol.angleRad)} rad (${sol.piMultiple})",
                                        radiansToDegrees = "${AdvancedSolvers.format(sol.angleDeg)}°"
                                    )
                                )
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("btn_more_trig")
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("More: Steps & Notes", style = MaterialTheme.typography.labelSmall)
                    }
                }

                // 3x2 Grid of Trig Functions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TrigValueTile("sin(θ)", AdvancedSolvers.format(sol.sinVal), sol.exactSinHint, Modifier.weight(1f))
                    TrigValueTile("cos(θ)", AdvancedSolvers.format(sol.cosVal), sol.exactCosHint, Modifier.weight(1f))
                    TrigValueTile("tan(θ)", sol.tanVal?.let { AdvancedSolvers.format(it) } ?: "Undefined", null, Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TrigValueTile("csc(θ)", sol.cscVal?.let { AdvancedSolvers.format(it) } ?: "Undefined", null, Modifier.weight(1f))
                    TrigValueTile("sec(θ)", sol.secVal?.let { AdvancedSolvers.format(it) } ?: "Undefined", null, Modifier.weight(1f))
                    TrigValueTile("cot(θ)", sol.cotVal?.let { AdvancedSolvers.format(it) } ?: "Undefined", null, Modifier.weight(1f))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                Text(
                    text = "Reference Angle: ${AdvancedSolvers.format(sol.referenceAngleDeg)}°  |  Coordinates: (${AdvancedSolvers.format(sol.cosVal)}, ${AdvancedSolvers.format(sol.sinVal)})",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RightTriangleSection(
    viewModel: CalculatorViewModel,
    onShowMore: (CalculationResult) -> Unit
) {
    val mode by viewModel.rightTriMode.collectAsState()
    val v1 by viewModel.rightTriVal1.collectAsState()
    val v2 by viewModel.rightTriVal2.collectAsState()
    val solution by viewModel.rightTriSolution.collectAsState()

    var modeExpanded by remember { mutableStateOf(false) }

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
                expanded = modeExpanded,
                onExpandedChange = { modeExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = mode.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Known Right Triangle Values") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                        .testTag("dropdown_right_tri_mode")
                )
                ExposedDropdownMenu(
                    expanded = modeExpanded,
                    onDismissRequest = { modeExpanded = false }
                ) {
                    GeometryTrigSolvers.RightTriMode.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                viewModel.rightTriMode.value = option
                                viewModel.solveRightTriangle()
                                modeExpanded = false
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
                    value = v1,
                    onValueChange = {
                        viewModel.rightTriVal1.value = it
                        viewModel.solveRightTriangle()
                    },
                    label = mode.field1Label,
                    placeholder = "3",
                    width = 120.dp,
                    modifier = Modifier.testTag("input_right_tri_1")
                )
                MathSlot(
                    value = v2,
                    onValueChange = {
                        viewModel.rightTriVal2.value = it
                        viewModel.solveRightTriangle()
                    },
                    label = mode.field2Label,
                    placeholder = "4",
                    width = 120.dp,
                    modifier = Modifier.testTag("input_right_tri_2")
                )
            }

            RightTriangleCanvas(modifier = Modifier.fillMaxWidth().height(110.dp))
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
                    text = sol.errorMessage ?: "Invalid right triangle.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(14.dp)
                )
            } else {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RIGHT TRIANGLE SOLUTION (∠C = 90°)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(
                            onClick = {
                                onShowMore(
                                    CalculationResult(
                                        expression = "Right △: a=${AdvancedSolvers.format(sol.a)}, b=${AdvancedSolvers.format(sol.b)}, c=${AdvancedSolvers.format(sol.c)}",
                                        resultNumber = sol.c,
                                        formattedResult = "c = ${AdvancedSolvers.format(sol.c)}, ∠A = ${AdvancedSolvers.format(sol.angleADeg)}°",
                                        angleMode = AngleMode.DEG,
                                        steps = sol.steps,
                                        notes = sol.notes,
                                        alternatives = AlternativeRepresentations(scientific = "Area = ${AdvancedSolvers.format(sol.area)}")
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

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TrigValueTile("Side a", AdvancedSolvers.format(sol.a), "Opp A", Modifier.weight(1f))
                        TrigValueTile("Side b", AdvancedSolvers.format(sol.b), "Adj A", Modifier.weight(1f))
                        TrigValueTile("Hyp c", AdvancedSolvers.format(sol.c), "√(a²+b²)", Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TrigValueTile("Angle A", "${AdvancedSolvers.format(sol.angleADeg)}°", null, Modifier.weight(1f))
                        TrigValueTile("Angle B", "${AdvancedSolvers.format(sol.angleBDeg)}°", null, Modifier.weight(1f))
                        TrigValueTile("Area", AdvancedSolvers.format(sol.area), "P=${AdvancedSolvers.format(sol.perimeter)}", Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GeneralTriangleSection(
    viewModel: CalculatorViewModel,
    onShowMore: (CalculationResult) -> Unit
) {
    val mode by viewModel.generalTriMode.collectAsState()
    val v1 by viewModel.generalTriVal1.collectAsState()
    val v2 by viewModel.generalTriVal2.collectAsState()
    val v3 by viewModel.generalTriVal3.collectAsState()
    val solution by viewModel.generalTriSolution.collectAsState()

    var modeExpanded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ExposedDropdownMenuBox(
                expanded = modeExpanded,
                onExpandedChange = { modeExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = mode.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Law of Cosines / Sines Case") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = modeExpanded,
                    onDismissRequest = { modeExpanded = false }
                ) {
                    GeometryTrigSolvers.GeneralTriMode.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                viewModel.generalTriMode.value = option
                                viewModel.solveGeneralTriangle()
                                modeExpanded = false
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
                    value = v1,
                    onValueChange = { viewModel.generalTriVal1.value = it; viewModel.solveGeneralTriangle() },
                    label = "Side a",
                    placeholder = "5",
                    width = 86.dp
                )
                MathSlot(
                    value = v2,
                    onValueChange = { viewModel.generalTriVal2.value = it; viewModel.solveGeneralTriangle() },
                    label = "Side b",
                    placeholder = "7",
                    width = 86.dp
                )
                MathSlot(
                    value = v3,
                    onValueChange = { viewModel.generalTriVal3.value = it; viewModel.solveGeneralTriangle() },
                    label = if (mode == GeometryTrigSolvers.GeneralTriMode.SSS) "Side c" else "Angle C (°)",
                    placeholder = if (mode == GeometryTrigSolvers.GeneralTriMode.SSS) "8" else "60",
                    width = 86.dp
                )
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
                    text = sol.errorMessage ?: "Invalid triangle.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(14.dp)
                )
            } else {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SOLVED TRIANGLE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(
                            onClick = {
                                onShowMore(
                                    CalculationResult(
                                        expression = "Triangle (a=${AdvancedSolvers.format(sol.a)}, b=${AdvancedSolvers.format(sol.b)}, c=${AdvancedSolvers.format(sol.c)})",
                                        resultNumber = sol.area,
                                        formattedResult = "Area = ${AdvancedSolvers.format(sol.area)}",
                                        angleMode = AngleMode.DEG,
                                        steps = sol.steps,
                                        notes = sol.notes,
                                        alternatives = AlternativeRepresentations(scientific = "P = ${AdvancedSolvers.format(sol.perimeter)}")
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

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TrigValueTile("Angle A", "${AdvancedSolvers.format(sol.angleADeg)}°", "a=${AdvancedSolvers.format(sol.a)}", Modifier.weight(1f))
                        TrigValueTile("Angle B", "${AdvancedSolvers.format(sol.angleBDeg)}°", "b=${AdvancedSolvers.format(sol.b)}", Modifier.weight(1f))
                        TrigValueTile("Angle C", "${AdvancedSolvers.format(sol.angleCDeg)}°", "c=${AdvancedSolvers.format(sol.c)}", Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TrigValueTile("Area (Heron)", AdvancedSolvers.format(sol.area), null, Modifier.weight(1f))
                        TrigValueTile("Perimeter", AdvancedSolvers.format(sol.perimeter), null, Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun TrigValueTile(
    label: String,
    value: String,
    hint: String?,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
            if (hint != null) {
                Text(
                    text = "($hint)",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun UnitCircleCanvas(
    angleRad: Double,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val outline = MaterialTheme.colorScheme.outlineVariant

    Canvas(modifier = modifier) {
        val radius = min(size.width, size.height) * 0.40f
        val cx = size.width / 2f
        val cy = size.height / 2f
        val center = Offset(cx, cy)

        // Axes
        drawLine(outline, Offset(cx - radius * 1.2f, cy), Offset(cx + radius * 1.2f, cy), strokeWidth = 2f)
        drawLine(outline, Offset(cx, cy - radius * 1.2f), Offset(cx, cy + radius * 1.2f), strokeWidth = 2f)

        // Unit Circle
        drawCircle(color = outline, radius = radius, center = center, style = Stroke(width = 3f))

        val px = cx + (radius * cos(angleRad)).toFloat()
        val py = cy - (radius * sin(angleRad)).toFloat()
        val point = Offset(px, py)
        val projX = Offset(px, cy)

        // Cosine projection (horizontal)
        drawLine(color = secondary, start = center, end = projX, strokeWidth = 5f)
        // Sine projection (vertical)
        drawLine(color = tertiary, start = projX, end = point, strokeWidth = 5f)
        // Terminal ray
        drawLine(color = primary, start = center, end = point, strokeWidth = 4f)
        // Dot on circle
        drawCircle(color = primary, radius = 7f, center = point)
    }
}

@Composable
private fun RightTriangleCanvas(modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val outline = MaterialTheme.colorScheme.outlineVariant

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val left = w * 0.25f
        val right = w * 0.75f
        val top = h * 0.15f
        val bottom = h * 0.85f

        val pTop = Offset(left, top)
        val pRightAngle = Offset(left, bottom)
        val pRight = Offset(right, bottom)

        val path = Path().apply {
            moveTo(pTop.x, pTop.y)
            lineTo(pRightAngle.x, pRightAngle.y)
            lineTo(pRight.x, pRight.y)
            close()
        }
        drawPath(path, color = primary.copy(alpha = 0.12f))
        drawLine(secondary, pTop, pRightAngle, strokeWidth = 5f)
        drawLine(secondary, pRightAngle, pRight, strokeWidth = 5f)
        drawLine(primary, pTop, pRight, strokeWidth = 5f)

        // Right angle square marker
        val sq = 16f
        drawPath(
            Path().apply {
                moveTo(left, bottom - sq)
                lineTo(left + sq, bottom - sq)
                lineTo(left + sq, bottom)
            },
            color = outline,
            style = Stroke(width = 2.5f, pathEffect = PathEffect.cornerPathEffect(1f))
        )
    }
}
