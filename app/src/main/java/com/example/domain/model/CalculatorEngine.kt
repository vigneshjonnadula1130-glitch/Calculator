package com.example.domain.model

import kotlin.math.*

object CalculatorEngine {

    fun format(value: Double): String {
        if (value.isNaN()) return "Domain error"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"
        
        val str = value.toString()
        val plainStr = if (str.endsWith(".0")) {
            str.substring(0, str.length - 2)
        } else {
            str
        }

        // If the number is extremely large or long, format gracefully
        if (plainStr.length > 12 && !str.contains('E') && !str.contains('e')) {
            try {
                return String.format(java.util.Locale.US, "%.10g", value)
                    .replace(Regex("0+$"), "") // strip trailing zeroes in decimal places
                    .replace(Regex("\\.$"), "") // remove trailing stand-alone dot
            } catch (e: Exception) {
                // Fallback to plain string if formatting fails
            }
        }
        return plainStr
    }

    fun applyScientific(function: String, value: Double): Result<Double> {
        return try {
            val result = when (function) {
                "sin" -> sin(Math.toRadians(value))
                "cos" -> cos(Math.toRadians(value))
                "tan" -> {
                    val mod = (value % 180 + 180) % 180
                    if (abs(mod - 90) < 1e-9) {
                        return Result.failure(ArithmeticException("Domain error"))
                    }
                    tan(Math.toRadians(value))
                }
                "ln" -> {
                    if (value <= 0) return Result.failure(ArithmeticException("Domain error"))
                    ln(value)
                }
                "log" -> {
                    if (value <= 0) return Result.failure(ArithmeticException("Domain error"))
                    log10(value)
                }
                "√" -> {
                    if (value < 0) return Result.failure(ArithmeticException("Domain error"))
                    sqrt(value)
                }
                "x²" -> value.pow(2)
                "x³" -> value.pow(3)
                "1/x" -> {
                    if (value == 0.0) return Result.failure(ArithmeticException("Divide by zero"))
                    1.0 / value
                }
                "abs" -> abs(value)
                else -> return Result.failure(IllegalArgumentException("Unknown function: $function"))
            }
            if (result.isNaN() || result.isInfinite()) {
                Result.failure(ArithmeticException("Domain error"))
            } else {
                Result.success(result)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun evaluate(expression: String): Result<Double> {
        val sanitized = expression
            .replace("÷", "/")
            .replace("×", "*")
            .replace("−", "-")
            .replace(" ", "")
            
        if (sanitized.isEmpty()) {
            return Result.success(0.0)
        }

        return try {
            val parser = ExpressionParser(sanitized)
            val result = parser.parse()
            if (result.isNaN() || result.isInfinite()) {
                Result.failure(ArithmeticException("Domain error"))
            } else {
                Result.success(result)
            }
        } catch (e: ArithmeticException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(ArithmeticException("Malformed expression"))
        }
    }

    private class ExpressionParser(private val str: String) {
        var pos = -1
        var ch = 0

        fun nextChar() {
            ch = if (++pos < str.length) str[pos].code else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < str.length) throw RuntimeException("Unexpected character: " + ch.toChar())
            return x
        }

        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                if (eat('+'.code)) x += parseTerm()
                else if (eat('-'.code)) x -= parseTerm()
                else return x
            }
        }

        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                if (eat('*'.code)) x *= parseFactor()
                else if (eat('/'.code)) {
                    val divisor = parseFactor()
                    if (divisor == 0.0) throw ArithmeticException("Divide by zero")
                    x /= divisor
                } else return x
            }
        }

        fun parseFactor(): Double {
            if (eat('+'.code)) return +parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = this.pos
            if (eat('('.code)) {
                x = parseExpression()
                if (!eat(')'.code)) throw RuntimeException("Missing closing parenthesis")
            } else if ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code || ch == 'E'.code || ch == 'e'.code) {
                while ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code || ch == 'E'.code || ch == 'e'.code || (ch == '-'.code && (str.getOrNull(pos - 1) == 'E' || str.getOrNull(pos - 1) == 'e'))) {
                    nextChar()
                }
                val part = str.substring(startPos, this.pos)
                x = part.toDoubleOrNull() ?: throw RuntimeException("Invalid number")
            } else {
                throw RuntimeException("Unexpected character")
            }

            return x
        }
    }
}
