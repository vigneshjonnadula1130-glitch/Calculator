package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.presentation.state.CalculatorAction
import com.example.ui.theme.ScienceBtnBg
import com.example.ui.theme.ScienceBtnText

@Composable
fun ScientificPanel(
    visible: Boolean,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
    buttonHeight: Dp = 54.dp
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val rows = listOf(
                listOf("sin", "cos", "tan", "√"),
                listOf("ln", "log", "x²", "x³"),
                listOf("1/x", "abs", "π", "e")
            )

            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { item ->
                        val isConstant = item == "π" || item == "e"
                        val clickListener = remember(item, onAction) {
                            {
                                if (isConstant) {
                                    onAction(CalculatorAction.ScientificConst(item))
                                } else {
                                    onAction(CalculatorAction.ScientificFunc(item))
                                }
                            }
                        }
                        CalculatorButton(
                            text = item,
                            onClick = clickListener,
                            backgroundColor = ScienceBtnBg,
                            contentColor = ScienceBtnText,
                            fontSize = 18,
                            modifier = Modifier
                                .weight(1f)
                                .height(buttonHeight),
                            testTag = "btn_sci_$item"
                        )
                    }
                }
            }
        }
    }
}
