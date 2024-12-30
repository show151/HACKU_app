package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileInputStream

@Suppress("DEPRECATION")
class CameraHandler(
    private val context: MainActivity
) {
    private var imageCapture: ImageCapture? = null

    fun startCamera(viewFinder: PreviewView, lifecycleOwner: androidx.lifecycle.LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // プレビュー用のUseCase
            val preview = Preview.Builder()
                .setTargetResolution(Size(640, 480)) // 必要に応じて解像度を変更
                .build()
                .also {
                    it.surfaceProvider = viewFinder.surfaceProvider
                }

            // 画像キャプチャ用のUseCase
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // 使用するカメラ（背面カメラ）
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // 既存のバインドを解除してから、新しいUseCaseをバインド
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e("CameraHandler", "Failed to bind use cases", exc)
                Toast.makeText(context, "カメラの起動に失敗しました: ${exc.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun takePicture(outputDirectory: File, onImageCaptured: (Bitmap) -> Unit, onError: (Exception) -> Unit) {
        val imageCapture = imageCapture ?: return

        val photoFile = File(outputDirectory, "${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraHandler", "Photo capture failed", exc)
                    onError(exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    try {
                        // 写真をBitmapとして読み込む
                        val bitmap = BitmapFactory.decodeStream(FileInputStream(photoFile))
                        val rotatedBitmap = rotateBitmap(bitmap)
                        onImageCaptured(rotatedBitmap)
                    } catch (e: Exception) {
                        Log.e("CameraHandler", "Failed to process the captured image", e)
                        onError(e)
                    }
                }
            }
        )
    }

    private fun rotateBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply {
            setRotate(0F, bitmap.width / 2F, bitmap.height / 2F)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
