package com.example.ui.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.VaultRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CalculatorViewModel(private val repository: VaultRepository) : ViewModel() {
    private val _fullExpression = MutableStateFlow("")
    val fullExpression: StateFlow<String> = _fullExpression.asStateFlow()

    private val _liveResult = MutableStateFlow("")
    val liveResult: StateFlow<String> = _liveResult.asStateFlow()

    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history: StateFlow<List<String>> = _history.asStateFlow()

    private var currentOperator: String? = null

    private val _onUnlock = MutableStateFlow(false)
    val onUnlock: StateFlow<Boolean> = _onUnlock.asStateFlow()

    private var lastClickTime = 0L
    private val debounceTime = 200L

    fun onNumberClick(number: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < debounceTime) return
        lastClickTime = currentTime

        if (_fullExpression.value == "0") {
            _fullExpression.value = number
        } else {
            _fullExpression.value += number
        }
        calculateLiveResult()
    }

    fun onOperatorClick(operator: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < debounceTime) return
        lastClickTime = currentTime

        val currentExp = _fullExpression.value
        if (currentExp.isEmpty()) return
        
        // Replace last operator if already there
        if (currentExp.lastOrNull()?.toString() in listOf("+", "-", "×", "÷")) {
            _fullExpression.value = currentExp.dropLast(1) + operator
        } else {
            _fullExpression.value += operator
        }
        currentOperator = operator
        calculateLiveResult()
    }

    fun onEqualsClick() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < debounceTime) return
        lastClickTime = currentTime

        val currentPin = _fullExpression.value
        viewModelScope.launch {
            val savedPin = repository.pinFlow.first()
            if (savedPin == currentPin) {
                _onUnlock.value = true
                onClearClick()
                return@launch
            }

            if (_liveResult.value.isNotEmpty()) {
                val calculation = "${_fullExpression.value} = ${_liveResult.value}"
                _history.value = (listOf(calculation) + _history.value).take(50)
                _fullExpression.value = _liveResult.value
                _liveResult.value = ""
                currentOperator = null
            }
        }
    }

    private fun calculateLiveResult() {
        val expression = _fullExpression.value
        if (expression.isEmpty()) {
            _liveResult.value = ""
            return
        }

        // Evaluate expression up to the last valid part
        val evalTarget = if (expression.lastOrNull()?.toString() in listOf("+", "-", "×", "÷")) {
            expression.dropLast(1)
        } else {
            expression
        }

        if (!evalTarget.any { it in "+-×÷" }) {
            _liveResult.value = ""
            return
        }

        try {
            val result = evaluateSimpleExpression(evalTarget)
            if (result != null) {
                _liveResult.value = formatResult(result)
            } else {
                _liveResult.value = ""
            }
        } catch (e: Exception) {
            _liveResult.value = ""
        }
    }

    private fun evaluateSimpleExpression(expression: String): Double? {
        val tokens = mutableListOf<String>()
        var number = ""
        
        for (char in expression) {
            if (char in "+-×÷") {
                if (number.isNotEmpty()) tokens.add(number)
                tokens.add(char.toString())
                number = ""
            } else {
                number += char
            }
        }
        if (number.isNotEmpty()) tokens.add(number)

        if (tokens.isEmpty()) return null
        if (tokens.size == 1) return tokens[0].toDoubleOrNull()

        var result = tokens[0].toDoubleOrNull() ?: return null
        var i = 1
        while (i < tokens.size - 1) {
            val op = tokens[i]
            val nextVal = tokens[i+1].toDoubleOrNull() ?: break
            
            result = when (op) {
                "+" -> result + nextVal
                "-" -> result - nextVal
                "×" -> result * nextVal
                "÷" -> if (nextVal != 0.0) result / nextVal else return null
                else -> result
            }
            i += 2
        }
        return result
    }

    private fun formatResult(result: Double): String {
        return if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            // Check if it has many decimals
            val s = result.toString()
            if (s.contains(".") && s.split(".")[1].length > 8) {
                String.format("%.8f", result).trimEnd('0').trimEnd('.')
            } else {
                s
            }
        }
    }

    fun onClearClick() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < debounceTime) return
        lastClickTime = currentTime

        _fullExpression.value = ""
        _liveResult.value = ""
        currentOperator = null
    }

    fun onDelClick() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < debounceTime) return
        lastClickTime = currentTime

        val current = _fullExpression.value
        if (current.isNotEmpty()) {
            _fullExpression.value = current.dropLast(1)
            calculateLiveResult()
        }
    }

    fun resetUnlock() {
        _onUnlock.value = false
    }

    fun clearHistory() {
        _history.value = emptyList()
    }
}
