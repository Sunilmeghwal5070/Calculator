package com.example.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.VaultRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SetupViewModel(private val repository: VaultRepository) : ViewModel() {
    private val _step = MutableStateFlow(0) // 0: Welcome, 1: Permission, 2: PIN, 3: Confirm PIN, 4: Recovery Method, 5: Security Question
    val step: StateFlow<Int> = _step.asStateFlow()

    private val _pin = MutableStateFlow("")
    val pin: StateFlow<String> = _pin.asStateFlow()

    private var firstPin = ""

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun nextStep() {
        _step.value += 1
    }

    fun setStep(step: Int) {
        _step.value = step
        _pin.value = ""
        firstPin = ""
        _error.value = null
    }

    private var lastClickTime = 0L
    private val debounceTime = 200L

    fun onNumberClick(number: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < debounceTime) return
        lastClickTime = currentTime

        if (_pin.value.length < 4) {
            _pin.value += number
        }
    }

    fun onDelClick() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < debounceTime) return
        lastClickTime = currentTime

        if (_pin.value.isNotEmpty()) {
            _pin.value = _pin.value.dropLast(1)
        }
    }

    fun onClearPin() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < debounceTime) return
        lastClickTime = currentTime

        _pin.value = ""
    }

    fun onPinSubmit() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < debounceTime) return
        lastClickTime = currentTime

        when (_step.value) {
            2 -> {
                if (_pin.value.length == 4) {
                    firstPin = _pin.value
                    _pin.value = ""
                    _step.value = 3
                    _error.value = null
                } else {
                    _error.value = "PIN must be 4 digits"
                }
            }
            3 -> {
                if (_pin.value == firstPin) {
                    viewModelScope.launch {
                        repository.savePin(_pin.value)
                        _step.value = 4
                        _pin.value = ""
                        _error.value = null
                    }
                } else {
                    _error.value = "PINs do not match"
                }
            }
        }
    }

    fun onCompleteSetup(question: String, answer: String) {
        viewModelScope.launch {
            repository.saveSecurityInfo(question, answer)
            repository.completeSetup()
        }
    }
}
