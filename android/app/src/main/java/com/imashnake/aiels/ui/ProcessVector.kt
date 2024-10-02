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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.imashnake.aiels.Vector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val SNACKBAR_ERROR_MESSAGE = "Please enter valid components!"

@Composable
fun ProcessVectorScreen(
    snackbarHostState: SnackbarHostState,
    result: Float,
    onRequestResult: (vector: Vector) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        // region First field
        var component1 by remember { mutableStateOf(TextFieldValue("")) }
        TextField(
            value = component1,
            onValueChange = { component1 = it },
            label = { Text("Component 1") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        // endregion

        // region Second field
        var component2 by remember { mutableStateOf(TextFieldValue("")) }
        TextField(
            value = component2,
            onValueChange = { component2 = it },
            label = { Text("Component 2") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = if (component2.text.isEmpty()) ImeAction.Previous else ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onPrevious = { focusManager.moveFocus(FocusDirection.Up) }
            )
        )
        // endregion

        // region Third field
        var component3 by remember { mutableStateOf(TextFieldValue("")) }
        val showSnackbarAndRunModel = {
            val vector = buildVector(listOf(component1, component2, component3))
            showSnackbarAndRunModel(
                vector = vector,
                message = "Ran model with ${vector.toString()}",
                runModel = onRequestResult,
                showSnackbar = { showSnackbar(snackbarHostState, it, scope) }
            )
        }
        TextField(
            value = component3,
            onValueChange = { component3 = it },
            label = { Text("Component 3") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = if (component3.text.isEmpty()) ImeAction.Previous else ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { showSnackbarAndRunModel() },
                onPrevious = { focusManager.moveFocus(FocusDirection.Up) }
            )
        )
        // endregion

        // region Run button
        Button(showSnackbarAndRunModel) { Text("Run model!") }
        // endregion

        Spacer(Modifier.size(30.dp))

        // region Output
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append("add components and multiply by 4")
                }
                append("-er outputs:")
            }
        )
        AnimatedContent(result, label = "vector_anim") {
            Text(it.takeUnless { it.isNaN() }?.toString() ?: "Run the model to output something")
        }
        // endregion
    }
}

private fun buildVector(
    fieldValues: List<TextFieldValue>
): Vector? {
    assert(fieldValues.size == 3)
    return fieldValues.takeIf {
        it.all { component -> component.text.toFloatOrNull() != null }
    }?.map { it.text.toFloat() }?.let {
        Vector(it[0], it[1], it[2])
    }
}

private fun showSnackbar(
    snackbarHostState: SnackbarHostState,
    message: String,
    scope: CoroutineScope,
    showMillis: Long = 1500L,
) {
    scope.launch {
        val job = scope.launch {
            snackbarHostState.showSnackbar(message)
        }
        delay(showMillis)
        job.cancel()
    }
}

private fun showSnackbarAndRunModel(
    vector: Vector?,
    message: String,
    runModel: (vector: Vector) -> Unit,
    showSnackbar: (string: String) -> Unit,
) {
    vector?.let { runModel(vector) }
    showSnackbar(vector?.let { message } ?: SNACKBAR_ERROR_MESSAGE)
}
