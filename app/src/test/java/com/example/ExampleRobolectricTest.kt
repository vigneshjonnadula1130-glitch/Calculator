package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.model.CalculatorEngine
import com.example.presentation.state.CalculatorAction
import com.example.presentation.viewmodel.CalculatorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Calculator", appName)
  }

  @Test
  fun `calculator engine formats scientific notation correctly`() {
    val formattedPi = CalculatorEngine.format(Math.PI)
    assertTrue(formattedPi.startsWith("3.14159"))

    val infinity = CalculatorEngine.format(Double.POSITIVE_INFINITY)
    assertEquals("Infinity", infinity)

    val nan = CalculatorEngine.format(Double.NaN)
    assertEquals("Domain error", nan)
  }

  @Test
  fun `calculator engine basic evaluation matches arithmetic rules`() {
    val resAdd = CalculatorEngine.evaluate("15 + 25")
    assertEquals(40.0, resAdd.getOrThrow(), 0.0001)

    val resMul = CalculatorEngine.evaluate("2 * 3 * 4")
    assertEquals(24.0, resMul.getOrThrow(), 0.0001)

    // Div by zero handles gracefully inside evaluate returning failure
    val resDivZero = CalculatorEngine.evaluate("5 / 0")
    assertTrue(resDivZero.isFailure)
  }

  @Test
  fun `calculator viewmodel updates states and expressions correctly`() {
    val viewModel = CalculatorViewModel()
    
    // Initial State values
    assertEquals("", viewModel.state.value.expression)
    assertEquals("0", viewModel.state.value.displayValue)

    // Append digits
    viewModel.onAction(CalculatorAction.Number("5"))
    viewModel.onAction(CalculatorAction.Number("2"))
    assertEquals("52", viewModel.state.value.expression)
    assertEquals("52", viewModel.state.value.displayValue)

    // Operation addition
    viewModel.onAction(CalculatorAction.Operation("+"))
    assertEquals("52 + ", viewModel.state.value.expression)
    assertEquals("52", viewModel.state.value.displayValue)

    // Append another digit
    viewModel.onAction(CalculatorAction.Number("8"))
    assertEquals("52 + 8", viewModel.state.value.expression)
    assertEquals("60", viewModel.state.value.displayValue)

    // Delete last digit
    viewModel.onAction(CalculatorAction.Delete)
    assertEquals("52 + ", viewModel.state.value.expression)

    // Clear
    viewModel.onAction(CalculatorAction.Clear)
    assertEquals("", viewModel.state.value.expression)
    assertEquals("0", viewModel.state.value.displayValue)
  }

  @Test
  fun `scientific operations and bounds`() {
    val viewModel = CalculatorViewModel()

    // e^1 calculation (starts with adding constant e)
    viewModel.onAction(CalculatorAction.ScientificConst("e"))
    assertEquals("2.718281828", viewModel.state.value.displayValue.take(11))

    // Square function of starting val e (2.71828^2)
    viewModel.onAction(CalculatorAction.ScientificFunc("x²"))
    assertTrue(viewModel.state.value.isResultDisplayed)
    
    // LN of invalid negative value
    viewModel.onAction(CalculatorAction.Clear)
    viewModel.onAction(CalculatorAction.Number("5"))
    viewModel.onAction(CalculatorAction.ToggleSign) // -5
    viewModel.onAction(CalculatorAction.ScientificFunc("ln"))
    assertEquals("Error", viewModel.state.value.displayValue)
  }
}

