package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ResultMainBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ResultMainBinding
    private val imageDrawer = ImageDrawer() // 描画クラスのインスタンス

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBindingの初期化
        binding = ResultMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Repositoryから画像と推論結果を取得
        val bitmap = ImageRepository.imageBitmap
        val detections = ImageRepository.inferenceResult

        if (bitmap != null) {
            if (!detections.isNullOrEmpty()) {
                // バウンディングボックス付き画像を生成
                val resultBitmap = imageDrawer.drawBoundingBoxes(bitmap, detections)
                binding.previewImageView.setImageBitmap(resultBitmap)
            } else {
                // 推論結果がない場合は元の画像を表示
                binding.previewImageView.setImageBitmap(bitmap)
            }
        } else {
            // 画像がない場合は代替画像を表示
            binding.previewImageView.setImageResource(R.drawable.placeholder_image)
        }

        // 推論結果をテキスト表示
        if (!detections.isNullOrEmpty()) {
            val resultText = detections.joinToString(separator = "\n") { detection ->
                "クラス: ${detection.classLabel}, 信頼度: ${String.format("%.2f", detection.confidence)}"
            }
            binding.resultTextView.text = resultText
        } else {
            binding.resultTextView.text = "No result available"
        }
    }
}


