package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.channels.FileChannel

class ImageClassifier(private val context: Context) {
    private val interpreter: Interpreter
    private val labels: List<String>

    init {
        // モデルをロード
        interpreter = loadModel("test_float32.tflite")

        // ラベルをロード
        labels = loadLabels("classes.txt")
    }

    // モデルをロードする
    private fun loadModel(modelFileName: String): Interpreter {
        val assetFileDescriptor = context.assets.openFd(modelFileName)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        val mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        return Interpreter(mappedByteBuffer)
    }

    // ラベルをロードする
    private fun loadLabels(labelFileName: String): List<String> {
        val labels = mutableListOf<String>()
        context.assets.open(labelFileName).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    labels.add(line!!)
                }
            }
        }
        return labels
    }

    // 画像を分類し、整形された結果を返す
    fun classifyImage(bitmap: Bitmap): List<Detection> {
        // 1. 画像をリサイズ
        val resizedBitmap = resizeBitmap(bitmap, 640, 640)

        // 2. 画像をRGB形式に変換
        val rgbBitmap = convertToRGB(resizedBitmap)

        // 3. TensorFlow Lite形式の画像データを準備
        val tensorImage = prepareImage(rgbBitmap, DataType.FLOAT32)

        // 4. 出力バッファを作成
        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 116, 8400), DataType.FLOAT32)

        // 5. 推論を実行
        interpreter.run(tensorImage.buffer, outputBuffer.buffer)

        // 6. 推論結果を解析
        val detections = processOutput(outputBuffer)

        // 7. 信頼度が 0.75 以上の結果のみフィルタリングし、信頼度順にソートし、重複クラスを排除
        return detections
            .filter { it.confidence > 0.75f }          // 信頼度が 0.75 を超えるもののみ
            .sortedByDescending { it.confidence }    // 信頼度でソート
            .distinctBy { it.classLabel }            // クラスラベルで重複を排除
    }

    // 出力を解析して検出結果をリストに変換
    fun processOutput(outputBuffer: TensorBuffer): List<Detection> {
        val outputArray = outputBuffer.floatArray

        val numDetections = 116
        val attributesPerDetection = 8400 / numDetections
        val classCount = labels.size

        val detections = mutableListOf<Detection>()

        for (i in 0 until numDetections) {
            val startIndex = i * attributesPerDetection

            val xMin = outputArray[startIndex]
            val yMin = outputArray[startIndex + 1]
            val xMax = outputArray[startIndex + 2]
            val yMax = outputArray[startIndex + 3]

            val confidence = outputArray[startIndex + 4]

            val classScores = outputArray.copyOfRange(startIndex + 5, startIndex + 5 + classCount)

            // 正規化を無効化して元のスコアを使用
            val maxClassIndex = classScores.indices.maxByOrNull { classScores[it] } ?: -1
            val maxClassScore = if (maxClassIndex != -1) classScores[maxClassIndex] else 0.0f

            Log.d("Detection", "ラベル: ${labels.getOrNull(maxClassIndex)}, 信頼度: $confidence, スコア: $maxClassScore")

            // フィルタリング条件を緩和
            if (confidence > 0.6f && maxClassScore > 0.6f) {
                val label = if (maxClassIndex in labels.indices) labels[maxClassIndex] else "Unknown"
                detections.add(
                    Detection(
                        xMin = xMin,
                        yMin = yMin,
                        xMax = xMax,
                        yMax = yMax,
                        confidence = confidence,
                        classLabel = label,
                        classScore = maxClassScore
                    )
                )
            }
        }

        return detections
    }

    // ユーティリティ: 画像をリサイズ
    private fun resizeBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    // ユーティリティ: RGB形式に変換
    private fun convertToRGB(bitmap: Bitmap): Bitmap {
        return bitmap.copy(Bitmap.Config.ARGB_8888, true)
    }

    // ユーティリティ: TensorFlow Lite用データに変換
    private fun prepareImage(bitmap: Bitmap, dataType: DataType): TensorImage {
        val tensorImage = TensorImage(dataType)
        tensorImage.load(bitmap)
        return tensorImage
    }

    // 検出結果を表すデータクラス
    data class Detection(
        val xMin: Float,
        val yMin: Float,
        val xMax: Float,
        val yMax: Float,
        val confidence: Float,
        val classLabel: String,
        val classScore: Float
    )
}


