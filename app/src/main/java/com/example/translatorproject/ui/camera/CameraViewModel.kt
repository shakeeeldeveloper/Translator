package com.example.translatorproject.ui.camera

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.translatorproject.repository.CameraRepository
import java.io.File
import kotlin.jvm.java

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CameraRepository(application.applicationContext)

    private val _capturedImage = MutableLiveData<Bitmap?>()
    val capturedImage: LiveData<Bitmap?> = _capturedImage

    val imageUri: LiveData<Uri?> = repository.getImageUri()

    fun setImage(uri: Uri?) {
        repository.setImageUri(uri)
    }

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
class CameraViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            return CameraViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



