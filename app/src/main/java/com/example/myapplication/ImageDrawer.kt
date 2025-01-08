package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log


class ImageDrawer {

    // バウンディングボックスを描画する関数
    fun drawBoundingBoxes(
        bitmap: Bitmap,
        detections: List<ImageClassifier.Detection>,
        viewWidth: Int,
        viewHeight: Int
    ): Bitmap {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }

        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 40f
            style = Paint.Style.FILL
        }

        // スケーリング係数を計算
        val scaleX = viewWidth.toFloat() / bitmap.width
        val scaleY = viewHeight.toFloat() / bitmap.height

        detections.forEach { detection ->
            // バウンディングボックス座標をスケーリング
            val left = detection.xMin * bitmap.width * scaleX
            val top = detection.yMin * bitmap.height * scaleY
            val right = detection.xMax * bitmap.width * scaleX
            val bottom = detection.yMax * bitmap.height * scaleY

            // ログで描画範囲を確認
            Log.d("ImageDrawer", "Drawing box: ($left, $top) to ($right, $bottom)")

            // バウンディングボックスを描画
            canvas.drawRect(left, top, right, bottom, paint)

            // クラスラベルとスコアを描画
            val label = "${detection.classLabel} (${String.format("%.2f", detection.confidence)})"
            canvas.drawText(label, left, top - 10f, textPaint)
        }

        return mutableBitmap
    }
}
