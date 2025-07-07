package com.example.translatorproject.viewmodels


import android.app.Application
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.translatorproject.model.TranslatedBlock
import com.example.translatorproject.repository.CropRepository

class CropViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CropRepository(application)

    private val _translatedBitmap = MutableLiveData<Bitmap?>()
    val translatedBitmap: LiveData<Bitmap?> = _translatedBitmap

    private val _translatedBlocks = MutableLiveData<List<TranslatedBlock>>()
    val translatedBlocks: LiveData<List<TranslatedBlock>> = _translatedBlocks

    private val _ocrError = MutableLiveData<String?>()
    val ocrError: LiveData<String?> = _ocrError

    fun performOCR(uri: Uri) {
        repository.processOCR(uri,
            onSuccess = { originalBitmap, blocks ->
                repository.translateBlocks(blocks,
                    onTranslated = { translatedPairs ->
                        val bitmap = repository.drawTranslatedTextOnImage(originalBitmap, translatedPairs)
                        _translatedBitmap.postValue(bitmap)
                        _translatedBlocks.postValue(translatedPairs.map { TranslatedBlock(it.first, it.second) })
                    }
                )
            },
            onFailure = { error ->
                _ocrError.postValue(error)
            }
        )
    }
}
