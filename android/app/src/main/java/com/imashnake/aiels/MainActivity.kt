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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.imashnake.aiels.ui.theme.AielsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.random.Random
import kotlin.random.nextInt

const val SNACKBAR_ERROR_MESSAGE = "Please enter a valid integer!"

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
    NumberGenerator(modifier)
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
                snackbar = { GeneratingSnackbar(it) }
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

                Row(verticalAlignment = Alignment.CenterVertically) {
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
                }

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
    keyboardController?.hide()
    scope.launch {
        snackbarHostState.showSnackbar(message)
        onDismiss()
    }
}

private fun message(digits: Int?) = digits?.toString()?.let {
    "Generating a $it digit number..."
} ?: SNACKBAR_ERROR_MESSAGE

private fun randomNumber(n: Int) = Random.nextInt(10.0.pow(n - 1).toInt()..(10.0.pow(n) - 1).toInt())
