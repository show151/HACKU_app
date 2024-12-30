package com.example.myapplication

import android.graphics.Bitmap

object ImageRepository {
    var imageBitmap: Bitmap? = null
    var inferenceResult: List<ImageClassifier.Detection>? = null
}
