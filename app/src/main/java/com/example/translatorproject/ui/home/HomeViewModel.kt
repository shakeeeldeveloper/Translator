package com.example.translatorproject.ui.home

import androidx.lifecycle.*
import com.example.translatorproject.repository.HomeRepository
import com.example.translatorproject.utils.Event
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    private val _sourceLang = MutableLiveData("English")
    val sourceLang: LiveData<String> get() = _sourceLang

    private val _targetLang = MutableLiveData("Urdu")
    val targetLang: LiveData<String> get() = _targetLang

    private val _sourceLangCode = MutableLiveData("en")
    private val _targetLangCode = MutableLiveData("ur")

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

        viewModelScope.launch {
            val result = repository.translateText(sourceText, sourceCode, targetCode)
            result
                .onSuccess { event -> _translatedText.postValue(event) }
                .onFailure { e -> _errorMessage.postValue(e.message ?: "Translation failed") }
        }
    }
}
class HomeViewModelFactory(private val repository: HomeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}




/*
package com.example.translatorproject.ui.home

import android.util.Log
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





*/

