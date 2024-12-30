package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ResultMainBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ResultMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBindingの初期化
        binding = ResultMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Repositoryから画像と推論結果を取得
        val bitmap = ImageRepository.imageBitmap
        val result = ImageRepository.inferenceResult

        // 画像が存在する場合はプレビューに表示
        if (bitmap != null) {
            binding.previewImageView.setImageBitmap(bitmap)
        } else {
            binding.previewImageView.setImageResource(R.drawable.placeholder_image) // 代替画像を設定
        }

        // 推論結果を表示
        binding.resultTextView.text = result ?: "No result available"
    }
}

