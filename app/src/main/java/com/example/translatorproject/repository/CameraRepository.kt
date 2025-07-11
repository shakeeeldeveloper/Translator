package com.example.translatorproject.repository

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraRepository(private val context: Context) {
    private val selectedImageUri = MutableLiveData<Uri?>()

    fun setImageUri(uri: Uri?) {
        selectedImageUri.value = uri
    }

    fun getImageUri(): LiveData<Uri?> = selectedImageUri
    fun takePhoto(
        imageCapture: ImageCapture,
        onSuccess: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        val photoFile = File(
            context.cacheDir,
            "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onSuccess(photoFile)
                }

                override fun onError(exc: ImageCaptureException) {
                    onError(exc.message ?: "Capture failed")
                }
            }
        )
    }
}
