package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver
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

        // レイアウト描画後に幅と高さを取得して処理を実行
        binding.previewImageView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.previewImageView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val viewWidth = binding.previewImageView.width
                val viewHeight = binding.previewImageView.height

                if (bitmap != null && detections != null && detections.isNotEmpty()) {
                    // バウンディングボックス付き画像を生成
                    val resultBitmap =
                        imageDrawer.drawBoundingBoxes(bitmap, detections, viewWidth, viewHeight)
                    binding.previewImageView.setImageBitmap(resultBitmap)
                } else if (bitmap != null) {
                    // 推論結果がない場合、元の画像を表示
                    binding.previewImageView.setImageBitmap(bitmap)
                } else {
                    // 画像がない場合は代替画像を表示
                    binding.previewImageView.setImageResource(R.drawable.placeholder_image)
                }
            }
        })

        // 推論結果をテキストとして表示
        if (!detections.isNullOrEmpty()) {
            val resultText = detections.joinToString(separator = "\n") { detection ->
                "クラス: ${detection.classLabel}, 信頼度: ${
                    String.format(
                        "%.2f",
                        detection.confidence
                    )
                }"
            }
            binding.resultTextView.text = resultText
        } else {
            binding.resultTextView.text = "No results available"
        }

        binding.imageDecorationButton.setOnClickListener {
            val intent = Intent(this, DecorationActivity::class.java)
            startActivity(intent)
        }
    }
}



