package com.example.translatorproject.viewmodels


import android.app.Application
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.translatorproject.model.TranslatedBlock
import com.example.translatorproject.repository.CropRepository
import com.google.mlkit.nl.languageid.LanguageIdentification

class CropViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CropRepository(application)

    private val _translatedBitmap = MutableLiveData<Bitmap?>()
    val translatedBitmap: LiveData<Bitmap?> = _translatedBitmap

    private val _translatedBlocks = MutableLiveData<List<TranslatedBlock>>()
    val translatedBlocks: LiveData<List<TranslatedBlock>> = _translatedBlocks

    private val _ocrError = MutableLiveData<String?>()
    val ocrError: LiveData<String?> = _ocrError

    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> get() = _saveResult

    private val _translatedText = MutableLiveData<String>()
    val translatedText: LiveData<String> = _translatedText

    private val _detectedText = MutableLiveData<String>()
    val detectedText: LiveData<String> = _detectedText

    private val _detectedLangCode = MutableLiveData<String>()
    val detectedLangCode: LiveData<String> get() = _detectedLangCode

    fun performOCR(uri: Uri) {
        repository.processOCR(uri,
            onSuccess = { originalBitmap, blocks ->
                // Combine all detected blocks into one string

                val identifier = LanguageIdentification.getClient()
                blocks.forEachIndexed { index, (rect, text) ->
                    val fullText = text

                    _detectedText.postValue(text)
                    Log.d("codeText", fullText + text)

                    identifier.identifyLanguage(text)
                        .addOnSuccessListener { langCode ->
                            if (langCode == "und") {
                            //    _detectedLangCode.postValue("Unknown")
                                Log.d("codece", langCode)

                            } else {
                                _detectedLangCode.postValue(langCode)
                                // e.g., "en", "ur", "hi"
                                Log.d("codec", langCode)

                                repository.translateBlocks(
                                    blocks,
                                    onTranslated = { translatedPairs ->
                                        val bitmap = repository.drawTranslatedTextOnImage(
                                            originalBitmap,
                                            translatedPairs
                                        )
                                        _translatedBitmap.postValue(bitmap)
                                        _translatedBlocks.postValue(translatedPairs.map {
                                            TranslatedBlock(
                                                it.first,
                                                it.second
                                            )
                                        })
                                        val fullTranslatedText = translatedPairs.joinToString(" ") { it.second }
                                        _translatedText.postValue(fullTranslatedText)

                                    }, langCode)
                            }
                        }
                        .addOnFailureListener {
                            _detectedLangCode.postValue("Error")
                        }
                }

            },
            onFailure = { error ->
                _ocrError.postValue(error)
            }
        )
    }
    fun speak(text: String, langCode: String) {
        Log.d("speak",text+ langCode+" in view")

        repository.speakText(text, langCode)
    }

    fun saveBitmap(bitmap: Bitmap, fileName: String) {
        val result = repository.saveBitmapToPictures(bitmap, fileName)
        _saveResult.postValue(result)
    }
}
