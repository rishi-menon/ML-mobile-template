package com.imashnake.aiels.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.imashnake.aiels.VECTOR_SNACKBAR_ERROR_MESSAGE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MakeVector(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        var component1 by remember { mutableStateOf(TextFieldValue("")) }
        var component2 by remember { mutableStateOf(TextFieldValue("")) }
        var component3 by remember { mutableStateOf(TextFieldValue("")) }

        var vector by remember { mutableStateOf("") }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TextField(
                value = component1,
                onValueChange = { component1 = it },
                label = { Text("Component 1") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (component1.text.isEmpty()) ImeAction.Previous else ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
            TextField(
                value = component2,
                onValueChange = { component2 = it },
                label = { Text("Component 2") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (component2.text.isEmpty()) ImeAction.Previous else ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.moveFocus(FocusDirection.Down) },
                    onPrevious = { focusManager.moveFocus(FocusDirection.Up) }
                )
            )
            TextField(
                value = component3,
                onValueChange = { component3 = it },
                label = { Text("Component 3") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (component3.text.isEmpty()) ImeAction.Previous else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        show3DVector(
                            Triple(component1, component2, component3).takeIf {
                                it.toList().all { component ->
                                    component.text.toIntOrNull() != null
                                }
                            }?.run { "Generated 3D vector!" } ?: VECTOR_SNACKBAR_ERROR_MESSAGE,
                            scope,
                            snackbarHostState,
                            keyboardController,
                        ) {
                            Triple(component1.text, component2.text, component3.text).takeIf {
                                it.toList().all { component ->
                                    component.toIntOrNull() != null
                                }
                            }?.run {
                                vector = "($first, $second, $third)"
                            }
                        }
                    }
                )
            )
        }

        Button(
            onClick = {
                show3DVector(
                    Triple(component1, component2, component3).takeIf {
                        it.toList().all { component ->
                            component.text.toIntOrNull() != null
                        }
                    }?.run { "Generated 3D vector!" } ?: VECTOR_SNACKBAR_ERROR_MESSAGE,
                    scope,
                    snackbarHostState,
                    keyboardController,
                ) {
                    Triple(component1.text, component2.text, component3.text).takeIf {
                        it.toList().all { component ->
                            component.toIntOrNull() != null
                        }
                    }?.run {
                        vector = "($first, $second, $third)"
                    }
                }
            }
        ) {
            Text("Get 3D Vector!")
        }

        Spacer(Modifier.size(30.dp))

        AnimatedContent(vector, label = "vector_anim") { v ->
            Text(text = v)
        }
    }
}

private fun show3DVector(
    message: String,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    keyboardController: SoftwareKeyboardController?,
    delay: Long = 500L,
    onDismiss: suspend (delay: Long) -> Unit
) {
//    // Show or hide keyboard
//    keyboardController?.hide()
    scope.launch {
        val job = scope.launch {
            snackbarHostState.showSnackbar(message)
        }
        delay(delay)
        job.cancel()
        onDismiss(delay)
    }
}
