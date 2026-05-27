package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.state.CalculatorAction
import com.example.presentation.viewmodel.CalculatorViewModel
import com.example.ui.components.CalculatorButton
import com.example.ui.components.DisplayPanel
import com.example.ui.components.HistorySheet
import com.example.ui.components.ScientificPanel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    // Stable remembered lambdas to optimize layout and skip button recompositions
    val onAction = remember(viewModel) { { action: CalculatorAction -> viewModel.onAction(action) } }
    val onClear = remember(onAction) { { onAction(CalculatorAction.Clear) } }
    val onDelete = remember(onAction) { { onAction(CalculatorAction.Delete) } }
    val onPercentage = remember(onAction) { { onAction(CalculatorAction.Percentage) } }
    val onDivide = remember(onAction) { { onAction(CalculatorAction.Operation("÷")) } }
    val onMultiply = remember(onAction) { { onAction(CalculatorAction.Operation("×")) } }
    val onSubtract = remember(onAction) { { onAction(CalculatorAction.Operation("−")) } }
    val onAdd = remember(onAction) { { onAction(CalculatorAction.Operation("+")) } }
    val onToggleSign = remember(onAction) { { onAction(CalculatorAction.ToggleSign) } }
    val onDecimal = remember(onAction) { { onAction(CalculatorAction.Decimal) } }
    val onScienceToggle = remember(onAction) { { onAction(CalculatorAction.ToggleScientific) } }
    val onHistoryToggle = remember(onAction) { { onAction(CalculatorAction.ToggleHistory) } }
    val onClearHistory = remember(onAction) { { onAction(CalculatorAction.ClearHistory) } }

    val onNum0 = remember(onAction) { { onAction(CalculatorAction.Number("0")) } }
    val onNum1 = remember(onAction) { { onAction(CalculatorAction.Number("1")) } }
    val onNum2 = remember(onAction) { { onAction(CalculatorAction.Number("2")) } }
    val onNum3 = remember(onAction) { { onAction(CalculatorAction.Number("3")) } }
    val onNum4 = remember(onAction) { { onAction(CalculatorAction.Number("4")) } }
    val onNum5 = remember(onAction) { { onAction(CalculatorAction.Number("5")) } }
    val onNum6 = remember(onAction) { { onAction(CalculatorAction.Number("6")) } }
    val onNum7 = remember(onAction) { { onAction(CalculatorAction.Number("7")) } }
    val onNum8 = remember(onAction) { { onAction(CalculatorAction.Number("8")) } }
    val onNum9 = remember(onAction) { { onAction(CalculatorAction.Number("9")) } }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Calculator",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    // Scientific Toggle Icon
                    IconButton(
                        onClick = onScienceToggle,
                        modifier = Modifier.testTag("science_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = "Toggle Scientific Panel",
                            tint = if (state.isScientificMode) ScienceBtnText else Color.White
                        )
                    }

                    // History Icon
                    IconButton(
                        onClick = onHistoryToggle,
                        modifier = Modifier.testTag("history_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Open History",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = BackgroundDark,
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) { innerPadding ->
        val regButtonHeight = if (state.isScientificMode) 44.dp else 64.dp
        val sciButtonHeight = 36.dp
        val keypadSpacing = if (state.isScientificMode) 4.dp else 10.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Expanded upper display area
            DisplayPanel(
                expression = state.expression,
                displayValue = state.displayValue,
                liveResult = state.liveResult,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )

            // Scientific collapsible panel
            ScientificPanel(
                visible = state.isScientificMode,
                onAction = onAction,
                buttonHeight = sciButtonHeight
            )

            // Primary standard keyboard buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(keypadSpacing)
            ) {
                // Row 1: AC, Backspace, Percentage, Division
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CalculatorButton(
                        text = "AC",
                        onClick = onClear,
                        backgroundColor = ControlBtnBg,
                        contentColor = ControlBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_clear"
                    )
                    CalculatorButton(
                        text = "⌫",
                        onClick = onDelete,
                        backgroundColor = ControlBtnBg,
                        contentColor = ControlBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_delete"
                    )
                    CalculatorButton(
                        text = "%",
                        onClick = onPercentage,
                        backgroundColor = ControlBtnBg,
                        contentColor = ControlBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_percentage"
                    )
                    CalculatorButton(
                        text = "÷",
                        onClick = onDivide,
                        backgroundColor = OperatorBtnBg,
                        contentColor = OperatorBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_op_divide"
                    )
                }

                // Row 2: 7, 8, 9, Multiplication
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CalculatorButton(
                        text = "7",
                        onClick = onNum7,
                        backgroundColor = NumberBtnBg,
                        contentColor = NumberBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_num_7"
                    )
                    CalculatorButton(
                        text = "8",
                        onClick = onNum8,
                        backgroundColor = NumberBtnBg,
                        contentColor = NumberBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_num_8"
                    )
                    CalculatorButton(
                        text = "9",
                        onClick = onNum9,
                        backgroundColor = NumberBtnBg,
                        contentColor = NumberBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_num_9"
                    )
                    CalculatorButton(
                        text = "×",
                        onClick = onMultiply,
                        backgroundColor = OperatorBtnBg,
                        contentColor = OperatorBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_op_multiply"
                    )
                }

                // Row 3: 4, 5, 6, Subtraction
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CalculatorButton(
                        text = "4",
                        onClick = onNum4,
                        backgroundColor = NumberBtnBg,
                        contentColor = NumberBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_num_4"
                    )
                    CalculatorButton(
                        text = "5",
                        onClick = onNum5,
                        backgroundColor = NumberBtnBg,
                        contentColor = NumberBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_num_5"
                    )
                    CalculatorButton(
                        text = "6",
                        onClick = onNum6,
                        backgroundColor = NumberBtnBg,
                        contentColor = NumberBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_num_6"
                    )
                    CalculatorButton(
                        text = "−",
                        onClick = onSubtract,
                        backgroundColor = OperatorBtnBg,
                        contentColor = OperatorBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_op_subtract"
                    )
                }

                // Row 4: 1, 2, 3, Addition
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CalculatorButton(
                        text = "1",
                        onClick = onNum1,
                        backgroundColor = NumberBtnBg,
                        contentColor = NumberBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_num_1"
                    )
                    CalculatorButton(
                        text = "2",
                        onClick = onNum2,
                        backgroundColor = NumberBtnBg,
                        contentColor = NumberBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_num_2"
                    )
                    CalculatorButton(
                        text = "3",
                        onClick = onNum3,
                        backgroundColor = NumberBtnBg,
                        contentColor = NumberBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_num_3"
                    )
                    CalculatorButton(
                        text = "+",
                        onClick = onAdd,
                        backgroundColor = OperatorBtnBg,
                        contentColor = OperatorBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_op_add"
                    )
                }

                // Row 5: ToggleSign, 0, Dot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CalculatorButton(
                        text = "+/−",
                        onClick = onToggleSign,
                        backgroundColor = NumberBtnBg,
                        contentColor = NumberBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_toggle_sign"
                    )
                    CalculatorButton(
                        text = "0",
                        onClick = onNum0,
                        backgroundColor = NumberBtnBg,
                        contentColor = NumberBtnText,
                        modifier = Modifier.weight(2f).height(regButtonHeight),
                        testTag = "btn_num_0"
                    )
                    CalculatorButton(
                        text = ".",
                        onClick = onDecimal,
                        backgroundColor = NumberBtnBg,
                        contentColor = NumberBtnText,
                        modifier = Modifier.weight(1f).height(regButtonHeight),
                        testTag = "btn_decimal"
                    )
                }
            }
        }
    }

    // Modal Sheet representation of calculation logs
    HistorySheet(
        visible = state.showHistory,
        history = state.history,
        onDismissRequest = onHistoryToggle,
        onClearHistory = onClearHistory
    )
}
