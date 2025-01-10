package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ResultMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ResultMainBinding
    private lateinit var imageDrawer: ImageDrawer // 描画クラスのインスタンス
    private lateinit var translator: Translator

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBindingの初期化
        binding = ResultMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ImageDrawerの初期化
        imageDrawer = ImageDrawer()

        // Translator の初期化
        translator = Translator(this)

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

        // 翻訳ボタンのクリックイベント
        binding.translateButton.setOnClickListener {
            val originalText = detections!!.joinToString("\n") { detection ->
                detection.classLabel
            }
            Log.d("ResultActivity", "Original text for translation: $originalText")
            if (originalText.isNotEmpty()) {
                translateSafely(originalText)
            } else {
                Toast.makeText(this, "翻訳するテキストがありません", Toast.LENGTH_SHORT).show()
            }
        }

        // 再撮影ボタンの処理
        binding.recaptureButton.setOnClickListener {
            navigateToMainActivity()
        }
    }

    // MainActivity へ遷移するメソッド
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish() // 結果画面を終了
    }

    private fun translateSafely(text: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val translatedText = withContext(Dispatchers.IO) {
                    translator.translate(text)
                }
                if (translatedText != null) {
                    binding.translatedTextView.text = translatedText
                    Log.d("ResultActivity", "Translated text: $translatedText")
                    Toast.makeText(this@ResultActivity, "翻訳が完了しました", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ResultActivity, "翻訳に失敗しました", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ResultActivity", "Translation failed", e)
                if (e.message?.contains("Rate Limit") == true) {
                    Toast.makeText(
                        this@ResultActivity,
                        "リクエストが制限を超えました。後ほど再試行してください。",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this@ResultActivity, "翻訳エラー: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}



