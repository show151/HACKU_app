package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.DecorationMainBinding

class DecorationActivity: AppCompatActivity() {
    private lateinit var binding: DecorationMainBinding
    private lateinit var imageDrawer: ImageDrawer
    private var touchCounter = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.decoration_main)

        // ViewBindingの初期化
        binding = DecorationMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ImageDrawerの初期化
        imageDrawer = ImageDrawer()

        // Repositoryから画像を取得
        val bitmap = ImageRepository.imageBitmap
        // レイアウト描画後に幅と高さを取得して処理を実行
        if (bitmap != null) {
            // 画像をImageViewに表示
            binding.previewImageView.setImageBitmap(bitmap)
        } else {
            // 画像が存在しない場合は代替画像を表示
            binding.previewImageView.setImageResource(R.drawable.placeholder_image)
        }
        binding.stampImage1.setImageResource(R.drawable.star_stamp)
        binding.stampImage2.setImageResource(R.drawable.star_stamp)
        binding.stampImage3.setImageResource(R.drawable.heart_stamp)
        binding.stampImage4.setImageResource(R.drawable.heart_stamp)


        binding.previewImageView.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                touchCounter++
                // タッチ位置にstampImage1の位置を設定
                if (touchCounter % 2 == 1) {
                    val x = motionEvent.x
                    val y = motionEvent.y

                    binding.stampImage1.x = x - binding.stampImage1.width / 2  // 中心に位置を合わせる
                    binding.stampImage1.y = y - binding.stampImage1.height / 2  // 中心に位置を合わせる
                    binding.stampImage1.visibility = View.VISIBLE  // 画像を表示
                    view.performClick()
                }else{
                    val x = motionEvent.x
                    val y = motionEvent.y

                    binding.stampImage3.x = x - binding.stampImage3.width / 2  // 中心に位置を合わせる
                    binding.stampImage3.y = y - binding.stampImage3.height / 2  // 中心に位置を合わせる
                    binding.stampImage3.visibility = View.VISIBLE  // 画像を表示
                    view.performClick()
                }
            }
                true  // イベント処理済み
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
}


