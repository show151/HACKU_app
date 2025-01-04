package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log


class ImageDrawer {

    // バウンディングボックスを描画する関数
    fun drawBoundingBoxes(bitmap: Bitmap, detections: List<ImageClassifier.Detection>): Bitmap {
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

        detections.forEach { detection ->
            val left = detection.xMin * bitmap.width
            val top = detection.yMin * bitmap.height
            val right = detection.xMax * bitmap.width
            val bottom = detection.yMax * bitmap.height

            // ログで描画内容を確認
            Log.d("BoundingBox", "Drawing box: ($left, $top) to ($right, $bottom)")

            // バウンディングボックスを描画
            canvas.drawRect(left, top, right, bottom, paint)

            // ラベルとスコアを描画
            val label = "${detection.classLabel} (${String.format("%.2f", detection.confidence)})"
            canvas.drawText(label, left, top - 10f, textPaint)
        }

        return mutableBitmap
    }
}