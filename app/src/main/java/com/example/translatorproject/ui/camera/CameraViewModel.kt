package com.example.translatorproject.ui.camera

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.ImageCapture
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.translatorproject.repository.CameraRepository
import java.io.File

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CameraRepository(application.applicationContext)

    private val _capturedImage = MutableLiveData<Bitmap?>()
    val capturedImage: LiveData<Bitmap?> = _capturedImage

    fun takePhoto(imageCapture: ImageCapture) {
        repository.takePhoto(imageCapture,
            onSuccess = { file ->
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                _capturedImage.postValue(bitmap)
            },
            onError = {
                _capturedImage.postValue(null)
            }
        )
    }

    fun clearImage() {
        _capturedImage.value = null
    }
}
