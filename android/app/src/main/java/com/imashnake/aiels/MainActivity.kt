package com.imashnake.aiels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.imashnake.aiels.ui.AielsSnackbar
import com.imashnake.aiels.ui.MakeVector
import com.imashnake.aiels.ui.theme.AielsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AielsTheme {
                MainScreen()
            }
        }

        val interpreter = AielsInterpreter(
            fileName = "model",
            context = this
        )

        lifecycleScope.launch {
            interpreter.runModel(floatArrayOf(1f, 2f, 3f))
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { AielsSnackbar(it) },
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
            MakeVector(snackbarHostState, modifier)
        }
    }
}
