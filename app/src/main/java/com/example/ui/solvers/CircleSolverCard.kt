package com.example.ui.solvers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextbookCircleCard(
    viewModel: CalculatorViewModel,
    onShowMore: (CalculationResult) -> Unit
) {
    val inputType by viewModel.circleInputType.collectAsState()
    val inputVal by viewModel.circleValue.collectAsState()
    val angleVal by viewModel.circleAngle.collectAsState()
    val angleMode by viewModel.circleAngleMode.collectAsState()
    val solution by viewModel.circleSolution.collectAsState()

    var inputTypeMenuExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_circle_solver")
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header
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
                            imageVector = Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Circle, Arc & Sector Solver",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Solve from Radius, Diameter, Circumference, or Area",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Input Form Surface
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
                    // Known Property Dropdown (No sideways scrolling!)
                    ExposedDropdownMenuBox(
                        expanded = inputTypeMenuExpanded,
                        onExpandedChange = { inputTypeMenuExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = inputType.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Known Circle Property") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = inputTypeMenuExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                                .testTag("dropdown_circle_input_type")
                        )
                        ExposedDropdownMenu(
                            expanded = inputTypeMenuExpanded,
                            onDismissRequest = { inputTypeMenuExpanded = false }
                        ) {
                            GeometryTrigSolvers.CircleInputType.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        viewModel.circleInputType.value = option
                                        viewModel.solveCircle()
                                        inputTypeMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Value & Central Angle Slots
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        MathSlot(
                            value = inputVal,
                            onValueChange = {
                                viewModel.circleValue.value = it
                                viewModel.solveCircle()
                            },
                            label = inputType.label,
                            placeholder = "5",
                            width = 110.dp,
                            modifier = Modifier.testTag("input_circle_value")
                        )

                        MathSlot(
                            value = angleVal,
                            onValueChange = {
                                viewModel.circleAngle.value = it
                                viewModel.solveCircle()
                            },
                            label = "Sector Angle θ",
                            placeholder = "60",
                            width = 96.dp,
                            modifier = Modifier.testTag("input_circle_angle")
                        )

                        FilterChip(
                            selected = angleMode == AngleMode.DEG,
                            onClick = {
                                viewModel.circleAngleMode.value =
                                    if (angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
                                viewModel.solveCircle()
                            },
                            label = {
                                Text(
                                    text = if (angleMode == AngleMode.DEG) "DEG" else "RAD",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            modifier = Modifier.testTag("btn_circle_angle_mode")
                        )
                    }

                    // Presets (wrapped cleanly in FlowRow-like balanced Row)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
                    ) {
                        QuickSlotChip(
                            text = "r=5, 60°",
                            onClick = {
                                viewModel.circleInputType.value = GeometryTrigSolvers.CircleInputType.RADIUS
                                viewModel.circleAngleMode.value = AngleMode.DEG
                                viewModel.circleValue.value = "5"
                                viewModel.circleAngle.value = "60"
                                viewModel.solveCircle()
                            }
                        )
                        QuickSlotChip(
                            text = "d=12, 90°",
                            onClick = {
                                viewModel.circleInputType.value = GeometryTrigSolvers.CircleInputType.DIAMETER
                                viewModel.circleAngleMode.value = AngleMode.DEG
                                viewModel.circleValue.value = "12"
                                viewModel.circleAngle.value = "90"
                                viewModel.solveCircle()
                            }
                        )
                        QuickSlotChip(
                            text = "A=100, 120°",
                            onClick = {
                                viewModel.circleInputType.value = GeometryTrigSolvers.CircleInputType.AREA
                                viewModel.circleAngleMode.value = AngleMode.DEG
                                viewModel.circleValue.value = "100"
                                viewModel.circleAngle.value = "120"
                                viewModel.solveCircle()
                            }
                        )
                    }

                    // Interactive Circle & Sector Diagram Canvas
                    val sweepDeg = solution?.angleDeg?.toFloat() ?: 60f
                    CircleDiagramCanvas(
                        sweepDeg = sweepDeg,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
            }

            // Results Card
            solution?.let { sol ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!sol.isValid) {
                        Text(
                            text = sol.errorMessage ?: "Invalid circle dimensions.",
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
                                    text = "CIRCLE PROPERTIES",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Button(
                                    onClick = {
                                        onShowMore(
                                            CalculationResult(
                                                expression = "Circle (${sol.inputType.symbol}=${AdvancedSolvers.format(sol.inputValue)}, θ=${AdvancedSolvers.format(sol.angleDeg)}°)",
                                                resultNumber = sol.area,
                                                formattedResult = "A = ${AdvancedSolvers.format(sol.area)}, C = ${AdvancedSolvers.format(sol.circumference)}",
                                                angleMode = angleMode,
                                                steps = sol.steps,
                                                notes = sol.notes,
                                                alternatives = AlternativeRepresentations(
                                                    scientific = "C = ${sol.exactCircumferencePi}, A = ${sol.exactAreaPi}",
                                                    degreesToRadians = "${AdvancedSolvers.format(sol.angleRad)} rad"
                                                )
                                            )
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("btn_more_circle")
                                ) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("More: Steps & Notes", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            // Primary 2x2 Grid
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CircleMetricBox("Radius (r)", AdvancedSolvers.format(sol.radius), null, Modifier.weight(1f))
                                CircleMetricBox("Diameter (d)", AdvancedSolvers.format(sol.diameter), null, Modifier.weight(1f))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CircleMetricBox("Circumference (C)", AdvancedSolvers.format(sol.circumference), "Exact: ${sol.exactCircumferencePi}", Modifier.weight(1f))
                                CircleMetricBox("Area (A)", AdvancedSolvers.format(sol.area), "Exact: ${sol.exactAreaPi}", Modifier.weight(1f))
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            Text(
                                text = "SECTOR & ARC (θ = ${AdvancedSolvers.format(sol.angleDeg)}° / ${AdvancedSolvers.format(sol.angleRad)} rad)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            CircleMetricRow("Arc Length (s = rθ)", AdvancedSolvers.format(sol.arcLength))
                            CircleMetricRow("Sector Area (½r²θ)", AdvancedSolvers.format(sol.sectorArea))
                            CircleMetricRow("Chord Length (2r·sin(θ/2))", AdvancedSolvers.format(sol.chordLength))
                            CircleMetricRow("Segment Area", AdvancedSolvers.format(sol.segmentArea))
                            CircleMetricRow("Cartesian Equation", sol.standardEquation)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CircleMetricBox(
    label: String,
    value: String,
    subtext: String?,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
            if (subtext != null) {
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun CircleMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun CircleDiagramCanvas(
    sweepDeg: Float,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val outline = MaterialTheme.colorScheme.outlineVariant

    Canvas(modifier = modifier) {
        val radius = min(size.width, size.height) * 0.40f
        val center = Offset(size.width / 2f, size.height / 2f)
        val topLeft = Offset(center.x - radius, center.y - radius)
        val arcSize = Size(radius * 2f, radius * 2f)

        // Full reference circle
        drawCircle(
            color = outline,
            radius = radius,
            center = center,
            style = Stroke(width = 3f)
        )

        // Shaded sector wedge starting from 0 deg (3 o'clock) counter-clockwise (negative sweep in Compose Canvas)
        val clampedSweep = sweepDeg.coerceIn(0f, 360f)
        if (clampedSweep > 0f) {
            drawArc(
                color = primary.copy(alpha = 0.22f),
                startAngle = 0f,
                sweepAngle = -clampedSweep,
                useCenter = true,
                topLeft = topLeft,
                size = arcSize
            )
            // Highlighted Arc perimeter
            drawArc(
                color = primary,
                startAngle = 0f,
                sweepAngle = -clampedSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = 6f)
            )
        }

        // End point on circle
        val radAngle = clampedSweep * PI / 180.0
        val p1 = Offset(center.x + radius, center.y)
        val p2 = Offset(
            center.x + (radius * cos(radAngle)).toFloat(),
            center.y - (radius * sin(radAngle)).toFloat()
        )

        // Radius lines
        drawLine(color = secondary, start = center, end = p1, strokeWidth = 4f)
        drawLine(color = secondary, start = center, end = p2, strokeWidth = 4f)

        // Chord line (dashed)
        if (clampedSweep in 1f..359f) {
            drawLine(
                color = tertiary,
                start = p1,
                end = p2,
                strokeWidth = 3f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
            )
        }

        // Center dot
        drawCircle(color = primary, radius = 6f, center = center)
    }
}
