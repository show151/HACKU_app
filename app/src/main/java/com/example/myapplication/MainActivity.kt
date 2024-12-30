package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var cameraHandler: CameraHandler
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifier: ImageClassifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ViewBindingの初期化
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // CameraHandlerの初期化
        cameraHandler = CameraHandler(this)

        // ImageClassifierの初期化
        imageClassifier = ImageClassifier(this)

        // カメラを起動
        cameraHandler.startCamera(binding.viewFinder, this)

        // キャプチャボタンの設定
        binding.imageCaptureButton.setOnClickListener {
            val outputDir = externalMediaDirs.firstOrNull()
            if (outputDir == null) {
                Toast.makeText(this, "外部ストレージが利用できません", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            cameraHandler.takePicture(
                outputDirectory = outputDir,
                onImageCaptured = { bitmap ->
                    val result = imageClassifier.classifyImage(bitmap).joinToString("\n")

                    // 結果を保存
                    ImageRepository.imageBitmap = bitmap
                    ImageRepository.inferenceResult = result

                    // 次のActivityへ遷移
                    val intent = Intent(this, ResultActivity::class.java)
                    startActivity(intent)
                },
                onError = { exc ->
                    AlertDialog.Builder(this)
                        .setTitle("エラー")
                        .setMessage("写真撮影に失敗しました: ${exc.message}")
                        .setPositiveButton("再試行") { _, _ ->
                            binding.imageCaptureButton.performClick()
                        }
                        .setNegativeButton("キャンセル", null)
                        .show()
                }
            )
        }
    }
}

