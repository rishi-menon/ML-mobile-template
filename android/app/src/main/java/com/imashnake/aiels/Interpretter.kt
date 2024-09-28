package com.imashnake.aiels

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil

private const val TAG = "Interpreter"

/**
 * [Digit Classifier](https://github.com/google-ai-edge/litert-samples/blob/main/examples/digit_classifier/android/app/src/main/java/com/google/aiedge/examples/digit_classifier/DigitClassificationHelper.kt)
 */
class AielsInterpreter(
    private val context: Context
) {
    /** The TFLite interpreter instance.  */
    private var interpreter: Interpreter? = null

    init { initAielsInterpreter() }

    /** Init a Interpreter from the tflite asset */
    private fun initAielsInterpreter() {
        interpreter = try {
            val litertBuffer = FileUtil.loadMappedFile(context, "digit_classifier.tflite")
            Log.i(TAG, "Created TFLite buffer from asset")
            Interpreter(litertBuffer, Interpreter.Options())
        } catch (e: Exception) {
            Log.e(TAG, "Initializing TensorFlow Lite has failed with error: ${e.message}")
            return
        }
    }
}
