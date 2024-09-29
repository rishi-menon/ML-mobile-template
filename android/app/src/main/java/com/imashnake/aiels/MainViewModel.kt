package com.imashnake.aiels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(
    private val interpreter: AielsInterpreter
) : ViewModel() {

    val result = interpreter.scaleAndSum

    fun runModel(vector: Vector) = viewModelScope.launch {
        interpreter.runModel(vector)
    }
}
