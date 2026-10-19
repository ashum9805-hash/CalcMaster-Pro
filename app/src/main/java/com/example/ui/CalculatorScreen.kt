package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.math.AngleMode
import com.example.math.MathEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    onOpenHistory: () -> Unit,
    onOpenAdvancedMath: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expression by viewModel.expression.collectAsState()
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

    var showEditVariableDialog by remember { mutableStateOf<String?>(null) } // "x" or "y"
    var tempVarInput by remember { mutableStateOf("") }

    val exprScrollState = rememberScrollState()

    LaunchedEffect(expression) {
        exprScrollState.animateScrollTo(exprScrollState.maxValue)
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth > 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top Toolbar / Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // DEG / RAD Toggle Pill
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

                    // 2nd Function Toggle
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

                    // Exponent/Power Mode Indicator (creates ² ³ ⁴ ... superscripts)
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

                    // Quick Jump to Advanced Solvers
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
                            Text("Equations & Σ", style = MaterialTheme.typography.labelSmall)
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

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
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

                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier.size(36.dp).testTag("calculator_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "View calculation history",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Display & Viewing Area (Reserved 35% to 48% of screen for inserting numbers & viewing results)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (isWideScreen) 0.40f else if (isScientificExpanded) 0.35f else 0.48f)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                // Expression display
                Text(
                    text = if (expression.isEmpty()) "0" else expression,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = if (expression.length > 20) 20.sp else 26.sp
                    ),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    color = if (expression.isEmpty()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(exprScrollState)
                        .testTag("calculator_expression_display")
                )

                // Live Preview or Result
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
                            .testTag("calculator_result_display")
                    )
                } else {
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Row with Variables and "More" button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Variable value chips: [ x = 2 ] [ y = 3 ]
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    showEditVariableDialog = "x"
                                    tempVarInput = MathEngine.formatNumber(xVal)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "x = ${MathEngine.formatNumber(xVal)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit x",
                                    modifier = Modifier.size(11.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    showEditVariableDialog = "y"
                                    tempVarInput = MathEngine.formatNumber(yVal)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "y = ${MathEngine.formatNumber(yVal)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit y",
                                    modifier = Modifier.size(11.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }

                    // "More" Button for step-by-step breakdown
                    AnimatedVisibility(
                        visible = calculationResult != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Button(
                            onClick = { viewModel.openSolutionSheet() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp).testTag("more_solution_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
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

            Spacer(modifier = Modifier.height(4.dp))

            // Keypad Section (Lowered and sized proportionally to avoid cramping display space)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (isWideScreen) 0.60f else if (isScientificExpanded) 0.65f else 0.52f),
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
        // Row 1: C, DEL, %, ÷
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
                icon = Icons.AutoMirrored.Filled.Backspace,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                onClick = { viewModel.onDelete() },
                modifier = Modifier.weight(1f).testTag("btn_backspace")
            )
            CalcButton(
                text = "%",
                textColor = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                onClick = { viewModel.onAppend("%") },
                modifier = Modifier.weight(1f).testTag("btn_percent")
            )
            CalcButton(
                text = "÷",
                textColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { viewModel.onAppend("÷") },
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

        // Row 5: ±, 0, ., =
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
                text = "=",
                textColor = MaterialTheme.colorScheme.onTertiary,
                containerColor = MaterialTheme.colorScheme.tertiary,
                onClick = { viewModel.onEvaluate() },
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

@Composable
private fun CalcButton(
    text: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    textColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
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
