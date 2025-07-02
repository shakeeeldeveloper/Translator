package com.example.translatorproject.ui.chat

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.translatorproject.repository.ChatRepository

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ChatRepository(application)

    private val _person1Text = MutableLiveData<String>()
    val person1Text: LiveData<String> get() = _person1Text

    private val _person2Text = MutableLiveData<String>()
    val person2Text: LiveData<String> get() = _person2Text

    private val _detectedLang = MutableLiveData<String>()
    val detectedLang: LiveData<String> get() = _detectedLang

    fun detectLanguage(text: String) {
        repository.detectLanguage(text) { langCode ->
            _detectedLang.postValue(langCode)
        }
    }

    fun translateText(sourceText: String, fromLang: String, toLang: String, updatePerson1: Boolean) {
        repository.translateText(sourceText, fromLang, toLang) { translated ->
            if (updatePerson1) {
                _person1Text.postValue(translated)
            } else {
                _person2Text.postValue(translated)
                // Now translate back to person1
                translateText(translated, toLang, fromLang, true)
            }
        }
    }

    fun speak(text: String, langCode: String) {
        Log.d("speak",text+ langCode+" in view")

        repository.speakText(text, langCode)
    }

    override fun onCleared() {
        super.onCleared()
        repository.shutdownTTS()
    }
}
