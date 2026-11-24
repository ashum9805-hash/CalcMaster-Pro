package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.ScrollState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.awaitCancellation
import com.example.math.AngleMode
import com.example.math.CalculationResult
import com.example.math.MathEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    onOpenHistory: () -> Unit,
    onOpenAdvancedMath: () -> Unit,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val expression by viewModel.expression.collectAsState()
    val expressionField by viewModel.expressionField.collectAsState()
    val livePreview by viewModel.livePreview.collectAsState()
    val calculationResult by viewModel.calculationResult.collectAsState()
    val angleMode by viewModel.angleMode.collectAsState()
    val isInverse by viewModel.isInverse.collectAsState()
    val isScientificExpanded by viewModel.isScientificExpanded.collectAsState()
    val showSolutionDialog by viewModel.showSolutionDialog.collectAsState()
    val hasMemory by viewModel.hasMemory.collectAsState()
    val isPowerMode by viewModel.isPowerMode.collectAsState()
    val xVal by viewModel.xVariable.collectAsState()
    val yVal by viewModel.yVariable.collectAsState()

    val contextMode by viewModel.contextMode.collectAsState()
    val physicsSubTab by viewModel.physicsSubTab.collectAsState()
    val chemistrySubTab by viewModel.chemistrySubTab.collectAsState()

    var showEditVariableDialog by remember { mutableStateOf<String?>(null) } // "x" or "y"
    var tempVarInput by remember { mutableStateOf("") }
    var scienceSolutionForModal by remember { mutableStateOf<com.example.math.CalculationResult?>(null) }

    val exprScrollState = rememberScrollState()

    LaunchedEffect(expression) {
        if (expressionField.selection.end >= expression.length - 1) {
            exprScrollState.animateScrollTo(exprScrollState.maxValue)
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth > 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top Mode Selector Bar (Math / Physics / Chemistry) + History & Settings Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("calculator_mode_selector")
                ) {
                    CalculatorContextMode.entries.forEachIndexed { index, mode ->
                        val icon = when (mode) {
                            CalculatorContextMode.MATH -> Icons.Default.Calculate
                            CalculatorContextMode.PHYSICS -> Icons.Default.Bolt
                            CalculatorContextMode.CHEMISTRY -> Icons.Default.Science
                        }
                        SegmentedButton(
                            selected = contextMode == mode,
                            onClick = { viewModel.setContextMode(mode) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = CalculatorContextMode.entries.size
                            ),
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = mode.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            },
                            modifier = Modifier.testTag("mode_selector_${mode.name.lowercase()}")
                        )
                    }
                }

                IconButton(
                    onClick = onOpenHistory,
                    modifier = Modifier.size(48.dp).testTag("calculator_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "View calculation history",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.size(48.dp).testTag("calculator_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Open Settings & Themes",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            when (contextMode) {
                CalculatorContextMode.MATH -> {
                    // Math Context Toolbar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = angleMode == AngleMode.DEG,
                                onClick = { viewModel.toggleAngleMode() },
                                label = {
                                    Text(
                                        text = if (angleMode == AngleMode.DEG) "DEG" else "RAD",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.testTag("angle_mode_toggle")
                            )

                            FilterChip(
                                selected = isInverse,
                                onClick = { viewModel.toggleInverse() },
                                label = {
                                    Text(
                                        text = "2nd",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                modifier = Modifier.testTag("inverse_function_toggle")
                            )

                            FilterChip(
                                selected = isPowerMode,
                                onClick = { viewModel.togglePowerMode() },
                                label = {
                                    Text(
                                        text = if (isPowerMode) "xʸ (ON)" else "xʸ",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                                ),
                                modifier = Modifier.testTag("power_mode_toggle")
                            )

                            AssistChip(
                                onClick = onOpenAdvancedMath,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Functions,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                label = {
                                    Text("Methods", style = MaterialTheme.typography.labelSmall)
                                },
                                modifier = Modifier.testTag("btn_quick_advanced_math")
                            )

                            if (hasMemory) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    Text(
                                        text = "M",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }

                        if (!isWideScreen) {
                            IconButton(
                                onClick = { viewModel.toggleScientificKeyboard() },
                                modifier = Modifier.size(36.dp).testTag("toggle_scientific_button")
                            ) {
                                Icon(
                                    imageVector = if (isScientificExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Toggle scientific keypad",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Display & Viewing Area
                    CalculatorDisplayArea(
                        expressionField = expressionField,
                        onExpressionFieldChange = { viewModel.onExpressionFieldChange(it) },
                        onMoveCursorLeft = { viewModel.moveCursorLeft() },
                        onMoveCursorRight = { viewModel.moveCursorRight() },
                        livePreview = livePreview,
                        calculationResult = calculationResult,
                        xVal = xVal,
                        yVal = yVal,
                        exprScrollState = exprScrollState,
                        showVariables = true,
                        onEditVariable = { varName, value ->
                            showEditVariableDialog = varName
                            tempVarInput = value
                        },
                        onIndent = { viewModel.onIndent() },
                        onInsertSciE = { viewModel.onAppend("E") },
                        onCommitResult = { viewModel.commitResult() },
                        onShowMore = {
                            if (calculationResult == null && livePreview != null) {
                                viewModel.onEvaluate()
                            }
                            viewModel.openSolutionSheet()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(if (isWideScreen) 0.36f else if (isScientificExpanded) 0.31f else 0.44f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Keypad Section
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(if (isWideScreen) 0.64f else if (isScientificExpanded) 0.69f else 0.56f),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        tonalElevation = 2.dp
                    ) {
                        if (isWideScreen) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ScientificKeypad(
                                    viewModel = viewModel,
                                    isInverse = isInverse,
                                    isPowerMode = isPowerMode,
                                    modifier = Modifier.weight(1.1f)
                                )
                                MainKeypad(
                                    viewModel = viewModel,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isScientificExpanded) {
                                    ScientificKeypadCompact(
                                        viewModel = viewModel,
                                        isInverse = isInverse,
                                        isPowerMode = isPowerMode,
                                        modifier = Modifier.weight(0.33f)
                                    )
                                }
                                MainKeypad(
                                    viewModel = viewModel,
                                    modifier = Modifier.weight(if (isScientificExpanded) 0.67f else 1f)
                                )
                            }
                        }
                    }
                }

                CalculatorContextMode.PHYSICS -> {
                    // Physics Sub-Context Bar (Constants & Calc vs Formula Solvers)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = physicsSubTab == 0,
                            onClick = { viewModel.physicsSubTab.value = 0 },
                            leadingIcon = { Icon(Icons.Default.Functions, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text("Constants & Calc") },
                            modifier = Modifier.weight(1f).testTag("chip_physics_constants")
                        )
                        FilterChip(
                            selected = physicsSubTab == 1,
                            onClick = { viewModel.physicsSubTab.value = 1 },
                            leadingIcon = { Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text("Physics Expressions") },
                            modifier = Modifier.weight(1f).testTag("chip_physics_formulas")
                        )
                    }

                    if (physicsSubTab == 0) {
                        CalculatorDisplayArea(
                            expressionField = expressionField,
                            onExpressionFieldChange = { viewModel.onExpressionFieldChange(it) },
                            onMoveCursorLeft = { viewModel.moveCursorLeft() },
                            onMoveCursorRight = { viewModel.moveCursorRight() },
                            livePreview = livePreview,
                            calculationResult = calculationResult,
                            xVal = xVal,
                            yVal = yVal,
                            exprScrollState = exprScrollState,
                            showVariables = false,
                            onEditVariable = { _, _ -> },
                            onIndent = { viewModel.onIndent() },
                            onInsertSciE = { viewModel.onAppend("E") },
                            onCommitResult = { viewModel.commitResult() },
                            onShowMore = {
                                if (calculationResult == null && livePreview != null) {
                                    viewModel.onEvaluate()
                                }
                                viewModel.openSolutionSheet()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.24f)
                        )

                        PhysicsConstantsDeck(viewModel = viewModel)

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.50f),
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                            tonalElevation = 2.dp
                        ) {
                            MainKeypad(
                                viewModel = viewModel,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    } else {
                        PhysicsFormulaWorkstation(
                            viewModel = viewModel,
                            onShowSolution = { scienceSolutionForModal = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }

                CalculatorContextMode.CHEMISTRY -> {
                    // Chemistry Sub-Context Bar (pH/pOH, Gas & Dilution, Constants & Calc)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = chemistrySubTab == 0,
                            onClick = { viewModel.chemistrySubTab.value = 0 },
                            label = { Text("pH / pOH", maxLines = 1) },
                            modifier = Modifier.weight(1f).testTag("chip_chem_ph")
                        )
                        FilterChip(
                            selected = chemistrySubTab == 1,
                            onClick = { viewModel.chemistrySubTab.value = 1 },
                            label = { Text("Gas & Dilute", maxLines = 1) },
                            modifier = Modifier.weight(1.1f).testTag("chip_chem_gas")
                        )
                        FilterChip(
                            selected = chemistrySubTab == 2,
                            onClick = { viewModel.chemistrySubTab.value = 2 },
                            label = { Text("Constants", maxLines = 1) },
                            modifier = Modifier.weight(1f).testTag("chip_chem_constants")
                        )
                    }

                    when (chemistrySubTab) {
                        0 -> ChemistryPhWorkstation(
                            viewModel = viewModel,
                            onShowSolution = { scienceSolutionForModal = it },
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        )
                        1 -> ChemistryGasAndDilutionWorkstation(
                            viewModel = viewModel,
                            onShowSolution = { scienceSolutionForModal = it },
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        )
                        else -> {
                            CalculatorDisplayArea(
                                expressionField = expressionField,
                                onExpressionFieldChange = { viewModel.onExpressionFieldChange(it) },
                                onMoveCursorLeft = { viewModel.moveCursorLeft() },
                                onMoveCursorRight = { viewModel.moveCursorRight() },
                                livePreview = livePreview,
                                calculationResult = calculationResult,
                                xVal = xVal,
                                yVal = yVal,
                                exprScrollState = exprScrollState,
                                showVariables = false,
                                onEditVariable = { _, _ -> },
                                onIndent = { viewModel.onIndent() },
                                onInsertSciE = { viewModel.onAppend("E") },
                                onCommitResult = { viewModel.commitResult() },
                                onShowMore = {
                                    if (calculationResult == null && livePreview != null) {
                                        viewModel.onEvaluate()
                                    }
                                    viewModel.openSolutionSheet()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.24f)
                            )

                            ChemistryConstantsDeck(viewModel = viewModel)

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.50f),
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                                tonalElevation = 2.dp
                            ) {
                                MainKeypad(
                                    viewModel = viewModel,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Variable Edit Dialog
        if (showEditVariableDialog != null) {
            val varName = showEditVariableDialog!!
            AlertDialog(
                onDismissRequest = { showEditVariableDialog = null },
                title = { Text("Set Variable $varName Value") },
                text = {
                    OutlinedTextField(
                        value = tempVarInput,
                        onValueChange = { tempVarInput = it },
                        label = { Text("Value of $varName") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val v = tempVarInput.toDoubleOrNull() ?: 1.0
                                if (varName == "x") viewModel.setXVariable(v) else viewModel.setYVariable(v)
                                showEditVariableDialog = null
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val v = tempVarInput.toDoubleOrNull() ?: 1.0
                            if (varName == "x") viewModel.setXVariable(v) else viewModel.setYVariable(v)
                            showEditVariableDialog = null
                        }
                    ) {
                        Text("Apply")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditVariableDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Solution Bottom Sheet dialog
        if (showSolutionDialog && calculationResult != null) {
            SolutionDetailSheet(
                result = calculationResult!!,
                onDismiss = { viewModel.closeSolutionSheet() }
            )
        }

        if (scienceSolutionForModal != null) {
            SolutionDetailSheet(
                result = scienceSolutionForModal!!,
                onDismiss = { scienceSolutionForModal = null }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CalculatorDisplayArea(
    expressionField: TextFieldValue,
    onExpressionFieldChange: (TextFieldValue) -> Unit,
    onMoveCursorLeft: () -> Unit,
    onMoveCursorRight: () -> Unit,
    livePreview: String?,
    calculationResult: CalculationResult?,
    xVal: Double,
    yVal: Double,
    exprScrollState: ScrollState,
    showVariables: Boolean,
    onEditVariable: (String, String) -> Unit,
    onIndent: () -> Unit,
    onInsertSciE: () -> Unit,
    onCommitResult: () -> Unit,
    onShowMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expression = expressionField.text
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        val lineCount = expression.count { it == '\n' } + 1
        val dynamicFontSize = if (lineCount > 2) 18.sp else if (expression.length > 20) 20.sp else 26.sp
        val dynamicLineHeight = if (lineCount > 2) 24.sp else if (expression.length > 20) 26.sp else 32.sp

        InterceptPlatformTextInput(
            interceptor = { _, _ ->
                // Suppress Android soft keyboard while keeping full tap-to-place-cursor & selection
                awaitCancellation()
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(exprScrollState),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (expression.isEmpty()) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = dynamicFontSize,
                            lineHeight = dynamicLineHeight
                        ),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                BasicTextField(
                    value = expressionField,
                    onValueChange = onExpressionFieldChange,
                    textStyle = TextStyle(
                        fontSize = dynamicFontSize,
                        lineHeight = dynamicLineHeight,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    maxLines = 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("calculator_expression_display")
                )
            }
        }

        val displayResult = calculationResult?.formattedResult ?: livePreview
        if (!displayResult.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = displayResult,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = if (displayResult.length > 14) 24.sp else 32.sp
                ),
                fontWeight = FontWeight.Bold,
                color = if (calculationResult?.isSuccess == false) {
                    MaterialTheme.colorScheme.error
                } else if (calculationResult != null) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onCommitResult() }
                    .testTag("calculator_result_display")
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cursor left / right precision nudge controls
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onMoveCursorLeft() }
                        .testTag("btn_cursor_left")
                ) {
                    Text(
                        text = "◀",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onMoveCursorRight() }
                        .testTag("btn_cursor_right")
                ) {
                    Text(
                        text = "▶",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (showVariables) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onEditVariable("x", MathEngine.formatNumber(xVal)) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "x=${MathEngine.formatNumber(xVal)}",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit x",
                                modifier = Modifier.size(10.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onEditVariable("y", MathEngine.formatNumber(yVal)) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "y=${MathEngine.formatNumber(yVal)}",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit y",
                                modifier = Modifier.size(10.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onIndent() }
                            .testTag("chip_indent")
                    ) {
                        Text(
                            text = "⇥",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onInsertSciE() }
                            .testTag("chip_sci_e")
                    ) {
                        Text(
                            text = "×10ⁿ (E)",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            val isFinished = calculationResult != null || livePreview != null
            AnimatedVisibility(
                visible = isFinished,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Button(
                    onClick = onShowMore,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    modifier = Modifier.height(34.dp).testTag("more_solution_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "View step-by-step breakdown and notes",
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "More",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun MainKeypad(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // Row 1: C, %, a/b (Fraction), DEL, ÷ (long-press /)
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            CalcButton(
                text = "C",
                textColor = MaterialTheme.colorScheme.error,
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                onClick = { viewModel.onClear() },
                modifier = Modifier.weight(1f).testTag("btn_clear")
            )
            CalcButton(
                text = "%",
                textColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                onClick = { viewModel.onAppend("%") },
                modifier = Modifier.weight(1f).testTag("btn_percent")
            )
            CalcButton(
                text = "a/b",
                textColor = MaterialTheme.colorScheme.secondary,
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                onClick = { viewModel.onInsertFraction() },
                onLongClick = { viewModel.onAppend("/") },
                modifier = Modifier.weight(1f).testTag("btn_fraction")
            )
            CalcButton(
                icon = Icons.AutoMirrored.Filled.Backspace,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                onClick = { viewModel.onDelete() },
                modifier = Modifier.weight(1f).testTag("btn_backspace")
            )
            CalcButton(
                text = "÷",
                textColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { viewModel.onAppend("÷") },
                onLongClick = { viewModel.onAppend("/") },
                modifier = Modifier.weight(1f).testTag("btn_divide")
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            NumberKey(number = "7", onClick = { viewModel.onAppend("7") }, modifier = Modifier.weight(1f))
            NumberKey(number = "8", onClick = { viewModel.onAppend("8") }, modifier = Modifier.weight(1f))
            NumberKey(number = "9", onClick = { viewModel.onAppend("9") }, modifier = Modifier.weight(1f))
            CalcButton(
                text = "×",
                textColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { viewModel.onAppend("×") },
                modifier = Modifier.weight(1f).testTag("btn_multiply")
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            NumberKey(number = "4", onClick = { viewModel.onAppend("4") }, modifier = Modifier.weight(1f))
            NumberKey(number = "5", onClick = { viewModel.onAppend("5") }, modifier = Modifier.weight(1f))
            NumberKey(number = "6", onClick = { viewModel.onAppend("6") }, modifier = Modifier.weight(1f))
            CalcButton(
                text = "−",
                textColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { viewModel.onAppend("−") },
                modifier = Modifier.weight(1f).testTag("btn_subtract")
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            NumberKey(number = "1", onClick = { viewModel.onAppend("1") }, modifier = Modifier.weight(1f))
            NumberKey(number = "2", onClick = { viewModel.onAppend("2") }, modifier = Modifier.weight(1f))
            NumberKey(number = "3", onClick = { viewModel.onAppend("3") }, modifier = Modifier.weight(1f))
            CalcButton(
                text = "+",
                textColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { viewModel.onAppend("+") },
                modifier = Modifier.weight(1f).testTag("btn_add")
            )
        }

        // Row 5: ±, 0, ., ↵ (Enter), =
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            CalcButton(
                text = "±",
                textColor = MaterialTheme.colorScheme.onSurface,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                onClick = { viewModel.onAppend("(-1)*") },
                modifier = Modifier.weight(1f).testTag("btn_plus_minus")
            )
            NumberKey(number = "0", onClick = { viewModel.onAppend("0") }, modifier = Modifier.weight(1f))
            CalcButton(
                text = ".",
                textColor = MaterialTheme.colorScheme.onSurface,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                onClick = { viewModel.onAppend(".") },
                modifier = Modifier.weight(1f).testTag("btn_dot")
            )
            CalcButton(
                text = "↵",
                textColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                onClick = { viewModel.onEnter() },
                onLongClick = { viewModel.onIndent() },
                modifier = Modifier.weight(1f).testTag("btn_enter")
            )
            CalcButton(
                text = "=",
                textColor = MaterialTheme.colorScheme.onTertiary,
                containerColor = MaterialTheme.colorScheme.tertiary,
                onClick = { viewModel.onEqualsPress() },
                onLongClick = { viewModel.onEvaluate() },
                modifier = Modifier.weight(1f).testTag("btn_equals")
            )
        }
    }
}

@Composable
private fun ScientificKeypadCompact(
    viewModel: CalculatorViewModel,
    isInverse: Boolean,
    isPowerMode: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        // Row 1: x, y, sin/asin, cos/acos, tan/atan
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SciButton(text = "x", onClick = { viewModel.onAppend("x") }, modifier = Modifier.weight(1f), isSpecial = true)
            SciButton(text = "y", onClick = { viewModel.onAppend("y") }, modifier = Modifier.weight(1f), isSpecial = true)
            SciButton(
                text = if (isInverse) "sin⁻¹" else "sin",
                onClick = { viewModel.onAppend(if (isInverse) "asin(" else "sin(") },
                modifier = Modifier.weight(1f)
            )
            SciButton(
                text = if (isInverse) "cos⁻¹" else "cos",
                onClick = { viewModel.onAppend(if (isInverse) "acos(" else "cos(") },
                modifier = Modifier.weight(1f)
            )
            SciButton(
                text = if (isInverse) "tan⁻¹" else "tan",
                onClick = { viewModel.onAppend(if (isInverse) "atan(" else "tan(") },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: ln/eˣ, log/10ˣ, x²/x³, √/∛, xʸ
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SciButton(
                text = if (isInverse) "eˣ" else "ln",
                onClick = { viewModel.onAppend(if (isInverse) "e^(" else "ln(") },
                modifier = Modifier.weight(1f)
            )
            SciButton(
                text = if (isInverse) "10ˣ" else "log",
                onClick = { viewModel.onAppend(if (isInverse) "10^(" else "log(") },
                modifier = Modifier.weight(1f)
            )
            SciButton(
                text = if (isInverse) "x³" else "x²",
                onClick = { viewModel.onAppend(if (isInverse) "³" else "²") },
                modifier = Modifier.weight(1f)
            )
            SciButton(
                text = if (isInverse) "∛" else "√",
                onClick = { viewModel.onAppend(if (isInverse) "∛(" else "√(") },
                modifier = Modifier.weight(1f)
            )
            SciButton(
                text = "xʸ",
                onClick = { viewModel.togglePowerMode() },
                modifier = Modifier.weight(1f),
                isSpecial = isPowerMode
            )
        }

        // Row 3: Σ (Textbook slots), !, (, ), π
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SciButton(
                text = "Σ",
                onClick = {
                    viewModel.setAdvancedTab(AdvancedMathTab.SUMMATION)
                    viewModel.setDestination(AppNavDestination.ADVANCED_MATH)
                },
                modifier = Modifier.weight(1f),
                isSpecial = true
            )
            SciButton(text = "!", onClick = { viewModel.onAppend("!") }, modifier = Modifier.weight(1f))
            SciButton(text = "(", onClick = { viewModel.onAppend("(") }, modifier = Modifier.weight(1f))
            SciButton(text = ")", onClick = { viewModel.onAppend(")") }, modifier = Modifier.weight(1f))
            SciButton(text = "π", onClick = { viewModel.onAppend("π") }, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ScientificKeypad(
    viewModel: CalculatorViewModel,
    isInverse: Boolean,
    isPowerMode: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Row 1: x, y, sin, cos, tan
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SciButton(text = "x", onClick = { viewModel.onAppend("x") }, modifier = Modifier.weight(1f), isSpecial = true)
            SciButton(text = "y", onClick = { viewModel.onAppend("y") }, modifier = Modifier.weight(1f), isSpecial = true)
            SciButton(
                text = if (isInverse) "sin⁻¹" else "sin",
                onClick = { viewModel.onAppend(if (isInverse) "asin(" else "sin(") },
                modifier = Modifier.weight(1f)
            )
            SciButton(
                text = if (isInverse) "cos⁻¹" else "cos",
                onClick = { viewModel.onAppend(if (isInverse) "acos(" else "cos(") },
                modifier = Modifier.weight(1f)
            )
            SciButton(
                text = if (isInverse) "tan⁻¹" else "tan",
                onClick = { viewModel.onAppend(if (isInverse) "atan(" else "tan(") },
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: ln, log, log2, e^x, 10^x
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SciButton(text = "ln", onClick = { viewModel.onAppend("ln(") }, modifier = Modifier.weight(1f))
            SciButton(text = "log", onClick = { viewModel.onAppend("log(") }, modifier = Modifier.weight(1f))
            SciButton(text = "log₂", onClick = { viewModel.onAppend("log2(") }, modifier = Modifier.weight(1f))
            SciButton(text = "eˣ", onClick = { viewModel.onAppend("e^(") }, modifier = Modifier.weight(1f))
            SciButton(text = "10ˣ", onClick = { viewModel.onAppend("10^(") }, modifier = Modifier.weight(1f))
        }

        // Row 3: √, ∛, xʸ (Power mode), x², x³
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SciButton(text = "√", onClick = { viewModel.onAppend("√(") }, modifier = Modifier.weight(1f))
            SciButton(text = "∛", onClick = { viewModel.onAppend("∛(") }, modifier = Modifier.weight(1f))
            SciButton(
                text = "xʸ",
                onClick = { viewModel.togglePowerMode() },
                modifier = Modifier.weight(1f),
                isSpecial = isPowerMode
            )
            SciButton(text = "x²", onClick = { viewModel.onAppend("²") }, modifier = Modifier.weight(1f))
            SciButton(text = "x³", onClick = { viewModel.onAppend("³") }, modifier = Modifier.weight(1f))
        }

        // Row 4: Σ (Textbook template), nCr, nPr, (, )
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SciButton(
                text = "Σ",
                onClick = {
                    viewModel.setAdvancedTab(AdvancedMathTab.SUMMATION)
                    viewModel.setDestination(AppNavDestination.ADVANCED_MATH)
                },
                modifier = Modifier.weight(1f),
                isSpecial = true
            )
            SciButton(text = "nCr", onClick = { viewModel.onAppend("nCr(") }, modifier = Modifier.weight(1f))
            SciButton(text = "nPr", onClick = { viewModel.onAppend("nPr(") }, modifier = Modifier.weight(1f))
            SciButton(text = "(", onClick = { viewModel.onAppend("(") }, modifier = Modifier.weight(1f))
            SciButton(text = ")", onClick = { viewModel.onAppend(")") }, modifier = Modifier.weight(1f))
        }

        // Row 5: π, e, n!, MR, M+
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SciButton(text = "π", onClick = { viewModel.onAppend("π") }, modifier = Modifier.weight(1f))
            SciButton(text = "e", onClick = { viewModel.onAppend("e") }, modifier = Modifier.weight(1f))
            SciButton(text = "n!", onClick = { viewModel.onAppend("!") }, modifier = Modifier.weight(1f))
            SciButton(text = "MR", onClick = { viewModel.memoryRecall() }, modifier = Modifier.weight(1f))
            SciButton(text = "M+", onClick = { viewModel.memoryAdd() }, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun NumberKey(
    number: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CalcButton(
        text = number,
        textColor = MaterialTheme.colorScheme.onSurface,
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        onClick = onClick,
        modifier = modifier.testTag("btn_num_$number")
    )
}

@Composable
private fun SciButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSpecial: Boolean = false
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .testTag("btn_sci_$text"),
        color = if (isSpecial) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                fontWeight = FontWeight.Bold,
                color = if (isSpecial) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CalcButton(
    text: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    textColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        color = containerColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
            } else if (text != null) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 19.sp),
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
