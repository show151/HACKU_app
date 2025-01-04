package com.example.myapplication

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ResultMainBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ResultMainBinding
    private lateinit var imageDrawer: ImageDrawer // 描画クラスのインスタンス

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBindingの初期化
        binding = ResultMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ImageDrawerの初期化
        imageDrawer = ImageDrawer()

        // Repositoryから画像と推論結果を取得
        val bitmap = ImageRepository.imageBitmap
        val detections = ImageRepository.inferenceResult

        if (bitmap != null) {
            if (!detections.isNullOrEmpty()) {
                // バウンディングボックス付き画像を生成
                val resultBitmap = imageDrawer.drawBoundingBoxes(bitmap, detections)

                // 画像をリサイズ
                val resizedBitmap = resizeBitmap(resultBitmap, 1080, 1920) // 必要に応じてサイズを調整
                binding.previewImageView.setImageBitmap(resizedBitmap)

                Log.d("ResultActivity", "Bitmap resized and displayed: ${resizedBitmap.width}x${resizedBitmap.height}")
            } else {
                // 推論結果がない場合は元の画像をリサイズして表示
                val resizedBitmap = resizeBitmap(bitmap, 1080, 1920)
                binding.previewImageView.setImageBitmap(resizedBitmap)
            }
        } else {
            // 画像がない場合はプレースホルダー画像を表示
            binding.previewImageView.setImageResource(R.drawable.placeholder_image)
        }

        // 推論結果をテキストとして表示
        val resultText = if (!detections.isNullOrEmpty()) {
            detections.joinToString(separator = "\n") { detection ->
                "クラス: ${detection.classLabel}, 信頼度: ${String.format("%.2f", detection.confidence)}"
            }
        } else {
            "No results available"
        }
        binding.resultTextView.text = resultText
    }

    // 画像リサイズ関数
    private fun resizeBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }
}



