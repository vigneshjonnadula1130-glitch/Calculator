package com.example.presentation.state

sealed class CalculatorAction {
    data class Number(val number: String) : CalculatorAction()
    object Decimal : CalculatorAction()
    data class Operation(val operation: String) : CalculatorAction()
    object Calculate : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object ToggleSign : CalculatorAction()
    object Percentage : CalculatorAction()
    data class ScientificFunc(val functionName: String) : CalculatorAction()
    data class ScientificConst(val constantSymbol: String) : CalculatorAction()
    object ToggleScientific : CalculatorAction()
    object ToggleHistory : CalculatorAction()
    object ClearHistory : CalculatorAction()
}
