package com.example.translatorproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.translatorproject.utils.Event
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class HomeViewModel : ViewModel() {

    private val _sourceLang = MutableLiveData("English")
    val sourceLang: LiveData<String> get() = _sourceLang

    private val _targetLang = MutableLiveData("Urdu")
    val targetLang: LiveData<String> get() = _targetLang

    private val _sourceLangCode = MutableLiveData("en")
    private val _targetLangCode = MutableLiveData("ur")

    // ✅ Wrap translation result with Event
    private val _translatedText = MutableLiveData<Event<String>>()
    val translatedText: LiveData<Event<String>> get() = _translatedText

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun setLanguage(requestFor: String, name: String, code: String) {
        when (requestFor) {
            "button1" -> {
                _sourceLang.value = name
                _sourceLangCode.value = code
            }
            "button2" -> {
                _targetLang.value = name
                _targetLangCode.value = code
            }
        }
    }

    fun swapLanguages() {
        val tempName = _sourceLang.value
        _sourceLang.value = _targetLang.value
        _targetLang.value = tempName

        val tempCode = _sourceLangCode.value
        _sourceLangCode.value = _targetLangCode.value
        _targetLangCode.value = tempCode
    }

    fun translateText(sourceText: String) {
        val sourceCode = _sourceLangCode.value
        val targetCode = _targetLangCode.value

        if (sourceCode.isNullOrEmpty() || targetCode.isNullOrEmpty()) {
            _errorMessage.postValue("Invalid language code")
            return
        }

        val sourceMLLang = TranslateLanguage.fromLanguageTag(sourceCode)
        val targetMLLang = TranslateLanguage.fromLanguageTag(targetCode)

        if (sourceMLLang != null && targetMLLang != null) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceMLLang)
                .setTargetLanguage(targetMLLang)
                .build()

            val translator = Translation.getClient(options)

            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    translator.translate(sourceText)
                        .addOnSuccessListener { translated ->
                            _translatedText.postValue(Event(translated)) // ✅ Wrapped in Event
                        }
                        .addOnFailureListener {
                            _errorMessage.postValue("Translation failed")
                        }
                }
                .addOnFailureListener {
                    _errorMessage.postValue("Model download failed")
                }
        } else {
            _errorMessage.postValue("Invalid language code")
        }
    }
}





/*
package com.example.translatorproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.translatorproject.utils.Event
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class HomeViewModel : ViewModel() {

    private val _sourceLang = MutableLiveData("English")
    val sourceLang: LiveData<String> get() = _sourceLang

    private val _targetLang = MutableLiveData("Urdu")
    val targetLang: LiveData<String> get() = _targetLang

    private val _sourceLangCode = MutableLiveData("en")
    private val _targetLangCode = MutableLiveData("ur")

    private val _translatedText = MutableLiveData<String>()
    val translatedText: LiveData<String> get() = _translatedText

    fun setLanguage(requestFor: String, name: String, code: String) {
        when (requestFor) {
            "button1" -> {
                _sourceLang.value = name
                _sourceLangCode.value = code
            }
            "button2" -> {
                _targetLang.value = name
                _targetLangCode.value = code
            }
        }
    }

    fun swapLanguages() {
        val tempName = _sourceLang.value
        _sourceLang.value = _targetLang.value
        _targetLang.value = tempName

        val tempCode = _sourceLangCode.value
        _sourceLangCode.value = _targetLangCode.value
        _targetLangCode.value = tempCode
    }

    fun translateText(sourceText: String) {
        val sourceCode = _sourceLangCode.value
        val targetCode = _targetLangCode.value

        if (sourceCode.isNullOrEmpty() || targetCode.isNullOrEmpty()) {
            _translatedText.postValue("INVALID_LANGUAGE_CODE")
            return
        }

        val sourceMLLang = TranslateLanguage.fromLanguageTag(sourceCode)
        val targetMLLang = TranslateLanguage.fromLanguageTag(targetCode)

        if (sourceMLLang != null && targetMLLang != null) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceMLLang)
                .setTargetLanguage(targetMLLang)
                .build()

            val translator = Translation.getClient(options)

            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    translator.translate(sourceText)
                        .addOnSuccessListener { translated ->
                            _translatedText.postValue(translated)

                        }
                        .addOnFailureListener {
                            _translatedText.postValue("TRANSLATION_FAILED")
                        }
                }
                .addOnFailureListener {
                    _translatedText.postValue("MODEL_DOWNLOAD_FAILED")
                }
        } else {
            _translatedText.postValue("INVALID_LANGUAGE_CODE")
        }
    }
}
*/
