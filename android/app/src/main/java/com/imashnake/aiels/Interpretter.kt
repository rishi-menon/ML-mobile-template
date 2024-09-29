package com.imashnake.aiels

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat
import java.nio.FloatBuffer

private const val TAG = "Interpreter"

/**
 * [Digit Classifier](https://github.com/google-ai-edge/litert-samples/blob/main/examples/digit_classifier/android/app/src/main/java/com/google/aiedge/examples/digit_classifier/DigitClassificationHelper.kt)
 *
 * @property fileName Name of the TFLite file (without `.tflite`)
 */
class AielsInterpreter(
    private val fileName: String,
    private val context: Context,
) {
    val scaleAndSum: SharedFlow<Float>
        get() = _scaleAndSum
    private val _scaleAndSum = MutableSharedFlow<Float>()

    /** The TFLite interpreter instance.  */
    private var interpreter: Interpreter? = null

    init { initAielsInterpreter() }

    /** Init a Interpreter from the tflite asset */
    private fun initAielsInterpreter() {
        interpreter = try {
            val litertBuffer = FileUtil.loadMappedFile(context, "model.tflite")
            Log.i(TAG, "Created TFLite buffer from asset")
            Interpreter(litertBuffer, Interpreter.Options())
        } catch (e: Exception) {
            Log.e(TAG, "Initializing TensorFlow Lite has failed with error: ${e.message}")
            return
        }
    }

    suspend fun runModel(
        vector: FloatArray
    ) = withContext(Dispatchers.IO) {
        if (interpreter == null) return@withContext

        val shape = interpreter?.getInputTensor(0)?.shape() ?: return@withContext
        val tensorBufferFloat = TensorBufferFloat.createFixedSize(shape, DataType.FLOAT32)
        tensorBufferFloat.loadArray(vector)
        val output = runModelWithTFLite(tensorBufferFloat)

        _scaleAndSum.emit(output.firstOrNull() ?: Float.NaN)
    }

    private fun runModelWithTFLite(
        tensorBuffer: TensorBuffer
    ): FloatArray {
        val outputShape = interpreter!!.getOutputTensor(0)!!.shape()
        val outputBuffer = FloatBuffer.allocate(outputShape[0])
        val inputBuffer = TensorBuffer.createFrom(tensorBuffer, DataType.FLOAT32).buffer

        inputBuffer.rewind()
        outputBuffer.rewind()
        interpreter?.run(inputBuffer, outputBuffer)
        outputBuffer.rewind()
        val output = FloatArray(outputBuffer.capacity())
        outputBuffer.get(output)
        return output
    }
}
