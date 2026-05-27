package com.example.presentation.state

data class HistoryEntry(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class CalculatorState(
    val expression: String = "",
    val displayValue: String = "0",
    val liveResult: String = "",
    val isResultDisplayed: Boolean = false,
    val error: String? = null,
    val isScientificMode: Boolean = false,
    val history: List<HistoryEntry> = emptyList(),
    val showHistory: Boolean = false
)
