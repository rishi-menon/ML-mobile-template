package com.imashnake.aiels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
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
import com.imashnake.aiels.ui.theme.AielsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.random.Random
import kotlin.random.nextInt

const val RNG_SNACKBAR_ERROR_MESSAGE = "Please enter a valid integer!"
const val VECTOR_SNACKBAR_ERROR_MESSAGE = "Please enter a valid 3D vector!"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AielsTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    MakeVector(modifier)
}

@Composable
fun MakeVector(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { GeneratingSnackbar(it) },
                modifier = Modifier.imePadding(),
            )
        }
    ) { contentPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            imeAction = ImeAction.Done
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
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    TextField(
                        value = component3,
                        onValueChange = { component3 = it },
                        label = { Text("Component 3") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
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
                                    500L,
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
                            500L,
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

                Text(text = vector)
            }
        }
    }
}

private fun show3DVector(
    message: String,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    keyboardController: SoftwareKeyboardController?,
    delay: Long,
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

@Composable
fun NumberGenerator(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { GeneratingSnackbar(it) },
                modifier = Modifier.imePadding(),
            )
        }
    ) { contentPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                var numberOfDigits by remember { mutableStateOf(TextFieldValue("")) }
                var number by remember { mutableStateOf("") }

                TextField(
                    value = numberOfDigits,
                    onValueChange = { numberOfDigits = it },
                    label = { Text("Number of digits") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            showGeneratingSnackbar(
                                message(numberOfDigits.text.toIntOrNull()),
                                scope,
                                snackbarHostState,
                                keyboardController,
                            ) {
                                numberOfDigits.text.toIntOrNull()?.let {
                                    number = randomNumber(it).toString()
                                }
                            }
                        }
                    )
                )

                Button(
                    onClick = {
                        showGeneratingSnackbar(
                            message(numberOfDigits.text.toIntOrNull()),
                            scope,
                            snackbarHostState,
                            keyboardController,
                        ) {
                            numberOfDigits.text.toIntOrNull()?.let {
                                number = randomNumber(it).toString()
                            }
                        }
                    }
                ) {
                    Text("Generate!")
                }

                Spacer(Modifier.size(30.dp))

                Text(text = number)
            }
        }
    }
}

@Composable
fun GeneratingSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    Snackbar(
        snackbarData = snackbarData,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier,
    )
}

private fun showGeneratingSnackbar(
    message: String,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    keyboardController: SoftwareKeyboardController?,
    onDismiss: suspend () -> Unit
) {
//    // Show or hide keyboard
//    keyboardController?.hide()
    scope.launch {
        snackbarHostState.showSnackbar(message)
        onDismiss()
    }
}

private fun message(digits: Int?) = digits?.toString()?.let {
    "Generating a $it digit number..."
} ?: RNG_SNACKBAR_ERROR_MESSAGE

private fun randomNumber(n: Int) = Random.nextInt(10.0.pow(n - 1).toInt()..(10.0.pow(n) - 1).toInt())
