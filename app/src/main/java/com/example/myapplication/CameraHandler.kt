package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.io.FileInputStream

@Suppress("DEPRECATION")
class CameraHandler(private val context: Context) {

    private var imageCapture: ImageCapture? = null

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }

    // カメラを開始する
    fun startCamera(viewFinder: PreviewView, lifecycleOwner: LifecycleOwner) {
        if (allPermissionsGranted()) {
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
        } else {
            // 権限がない場合はリクエスト
            ActivityCompat.requestPermissions(
                context as Activity,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    // 画像をキャプチャする
    fun takePicture(
        outputDirectory: File,
        onImageCaptured: (Bitmap) -> Unit,
        onError: (Exception) -> Unit
    ) {
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
                        onImageCaptured(bitmap)
                    } catch (e: Exception) {
                        Log.e("CameraHandler", "Failed to process the captured image", e)
                        onError(e)
                    }
                }
            }
        )
    }

    // 権限が付与されているか確認
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}

