package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.domain.model.CalculatorEngine
import com.example.presentation.state.CalculatorAction
import com.example.presentation.state.CalculatorState
import com.example.presentation.state.HistoryEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onAction(action: CalculatorAction) {
        try {
            when (action) {
                is CalculatorAction.Number -> handleNumber(action.number)
                is CalculatorAction.Decimal -> handleDecimal()
                is CalculatorAction.Operation -> handleOperation(action.operation)
                is CalculatorAction.Calculate -> { /* Equals button is removed, unused */ }
                is CalculatorAction.Clear -> handleClear()
                is CalculatorAction.Delete -> handleDelete()
                is CalculatorAction.ToggleSign -> handleToggleSign()
                is CalculatorAction.Percentage -> handlePercentage()
                is CalculatorAction.ScientificFunc -> handleScientificFunc(action.functionName)
                is CalculatorAction.ScientificConst -> handleScientificConst(action.constantSymbol)
                is CalculatorAction.ToggleScientific -> updateState { it.copy(isScientificMode = !it.isScientificMode) }
                is CalculatorAction.ToggleHistory -> updateState { it.copy(showHistory = !it.showHistory) }
                is CalculatorAction.ClearHistory -> updateState { it.copy(history = emptyList()) }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            _state.update { currentState ->
                currentState.copy(
                    displayValue = "Error",
                    isResultDisplayed = true
                )
            }
        }
    }

    private fun updateState(block: (CalculatorState) -> CalculatorState) {
        _state.update { currentState ->
            val updated = block(currentState)
            val display = evaluateLive(updated.expression)
            updated.copy(displayValue = display)
        }
    }

    private fun evaluateLive(expression: String): String {
        val trimmed = expression.trim()
        if (trimmed.isEmpty()) return "0"

        // Handle a simple single number first
        val plainDouble = trimmed.replace(" ", "").replace("−", "-").toDoubleOrNull()
        if (plainDouble != null) {
            return CalculatorEngine.format(plainDouble)
        }

        var cleanExpr = trimmed
        // Remove trailing operators, spaces, and standalone decimals
        val operatorsAndSpaces = listOf('+', '−', '×', '÷', ' ', '.')
        while (cleanExpr.isNotEmpty() && cleanExpr.last() in operatorsAndSpaces) {
            cleanExpr = cleanExpr.dropLast(1)
        }

        if (cleanExpr.isEmpty()) return "0"

        val cleanParsed = cleanExpr.replace(" ", "").replace("−", "-").toDoubleOrNull()
        if (cleanParsed != null) {
            return CalculatorEngine.format(cleanParsed)
        }

        val result = CalculatorEngine.evaluate(cleanExpr)
        return result.fold(
            onSuccess = { res -> CalculatorEngine.format(res) },
            onFailure = { "0" } // Fallback to "0" mid-calculation typing
        )
    }

    private fun handleNumber(number: String) {
        updateState { currentState ->
            val isFresh = currentState.isResultDisplayed || currentState.expression == "0"
            val newExpr = if (isFresh) {
                number
            } else {
                val expr = currentState.expression
                if (expr.endsWith(" 0")) {
                    expr.dropLast(1) + number
                } else {
                    expr + number
                }
            }

            currentState.copy(
                expression = newExpr,
                isResultDisplayed = false
            )
        }
    }

    private fun handleDecimal() {
        updateState { currentState ->
            val isFresh = currentState.isResultDisplayed
            if (isFresh) {
                currentState.copy(
                    expression = "0.",
                    isResultDisplayed = false
                )
            } else {
                val expr = currentState.expression
                val lastNumberSegment = expr.split(Regex("[+−×÷]")).lastOrNull()?.trim() ?: ""
                if (lastNumberSegment.contains(".")) {
                    // Already contains decimal, ignore input
                    currentState
                } else {
                    val append = if (expr.isEmpty() || !expr.last().isDigit()) "0." else "."
                    currentState.copy(
                        expression = expr + append,
                        isResultDisplayed = false
                    )
                }
            }
        }
    }

    private fun handleOperation(op: String) {
        updateState { currentState ->
            val expr = currentState.expression.trim()
            val operatorList = listOf("+", "−", "×", "÷")

            var newExpr = currentState.expression
            val hasOperatorAtEnd = operatorList.any { o -> expr.endsWith(o) }

            if (hasOperatorAtEnd) {
                var cleaned = currentState.expression
                while (cleaned.isNotEmpty() && (cleaned.last() == ' ' || cleaned.last().toString() in operatorList)) {
                    cleaned = cleaned.dropLast(1)
                }
                newExpr = "$cleaned $op "
            } else {
                if (expr.isEmpty()) {
                    newExpr = "0 $op "
                } else {
                    newExpr = "${currentState.expression} $op "
                }
            }

            // Save preceding valid computation to history
            var updatedHistory = currentState.history
            val currentCleanedDisplay = evaluateLive(currentState.expression)
            val currentHasOperator = operatorList.any { o -> currentState.expression.contains(o) }

            if (!hasOperatorAtEnd && currentHasOperator && currentCleanedDisplay != "0" && currentCleanedDisplay != "Error") {
                val cleanOriginal = currentState.expression.trim()
                val entry = HistoryEntry(expression = cleanOriginal, result = currentCleanedDisplay)
                if (updatedHistory.firstOrNull()?.expression != cleanOriginal) {
                    updatedHistory = listOf(entry) + updatedHistory
                }
            }

            currentState.copy(
                expression = newExpr,
                isResultDisplayed = false,
                history = updatedHistory
            )
        }
    }

    private fun handleClear() {
        val currentState = _state.value
        val display = evaluateLive(currentState.expression)
        val hasOperator = listOf("+", "−", "×", "÷").any { o -> currentState.expression.contains(o) }
        val cleanExpr = currentState.expression.trim()

        var updatedHistory = currentState.history
        if (hasOperator && display != "0" && display != "Error" && cleanExpr != "0") {
            val entry = HistoryEntry(expression = cleanExpr, result = display)
            if (updatedHistory.firstOrNull()?.expression != cleanExpr) {
                updatedHistory = listOf(entry) + updatedHistory
            }
        }

        updateState {
            CalculatorState(
                isScientificMode = it.isScientificMode,
                history = updatedHistory
            )
        }
    }

    private fun handleDelete() {
        updateState { currentState ->
            val expr = currentState.expression
            if (expr.isNotEmpty()) {
                val newExpr = expr.dropLast(1)
                currentState.copy(
                    expression = newExpr,
                    isResultDisplayed = false
                )
            } else {
                currentState
            }
        }
    }

    private fun handleToggleSign() {
        updateState { currentState ->
            val currentDisplay = evaluateLive(currentState.expression)
            if (currentDisplay == "Error" || currentDisplay == "0") {
                currentState
            } else {
                val negated = if (currentDisplay.startsWith("-")) {
                    currentDisplay.substring(1)
                } else {
                    "-$currentDisplay"
                }
                currentState.copy(
                    expression = negated,
                    isResultDisplayed = true
                )
            }
        }
    }

    private fun handlePercentage() {
        updateState { currentState ->
            val currentDisplay = evaluateLive(currentState.expression)
            val num = currentDisplay.toDoubleOrNull()
            if (num != null) {
                val percentValue = num / 100.0
                val formattedPercent = CalculatorEngine.format(percentValue)
                currentState.copy(
                    expression = formattedPercent,
                    isResultDisplayed = true
                )
            } else {
                currentState
            }
        }
    }

    private fun handleScientificFunc(funcName: String) {
        updateState { currentState ->
            val currentDisplay = evaluateLive(currentState.expression)
            if (currentDisplay == "Error") return@updateState currentState
            val value = currentDisplay.toDoubleOrNull() ?: 0.0
            val sciResult = CalculatorEngine.applyScientific(funcName, value)

            sciResult.fold(
                onSuccess = { calculatedDouble ->
                    val formatted = CalculatorEngine.format(calculatedDouble)
                    val originalExpr = currentState.expression.trim()
                    var updatedHistory = currentState.history
                    if (originalExpr.isNotEmpty() && originalExpr != currentDisplay) {
                        val entry = HistoryEntry(expression = "$funcName($originalExpr)", result = formatted)
                        updatedHistory = listOf(entry) + updatedHistory
                    }

                    currentState.copy(
                        expression = formatted,
                        isResultDisplayed = true,
                        error = null,
                        history = updatedHistory
                    )
                },
                onFailure = { throwable ->
                    currentState.copy(
                        expression = "Error",
                        isResultDisplayed = true,
                        error = throwable.message ?: "Domain error"
                    )
                }
            )
        }
    }

    private fun handleScientificConst(constantSymbol: String) {
        updateState { currentState ->
            val constValue = if (constantSymbol == "π") Math.PI else Math.E
            val formattedConst = CalculatorEngine.format(constValue)

            val expr = currentState.expression.trim()
            val isFresh = currentState.isResultDisplayed || expr.isEmpty() || expr == "0"

            val newExpr = if (isFresh) {
                formattedConst
            } else {
                val lastChar = currentState.expression.lastOrNull()
                val operatorList = listOf('+', '−', '×', '÷')
                if (lastChar != null && (lastChar in operatorList || lastChar == ' ')) {
                    currentState.expression + formattedConst
                } else {
                    currentState.expression + " × " + formattedConst
                }
            }

            currentState.copy(
                expression = newExpr,
                isResultDisplayed = false
            )
        }
    }
}
