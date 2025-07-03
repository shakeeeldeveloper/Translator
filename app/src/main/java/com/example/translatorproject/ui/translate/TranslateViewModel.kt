package com.example.translatorproject.ui.translate

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


import androidx.lifecycle.*
import com.example.translatorproject.database.AppDatabase
import com.example.translatorproject.model.BookmarkEntity
import com.example.translatorproject.model.HistoryEntity
import com.example.translatorproject.repository.TranslateDaoRepository
import com.example.translatorproject.repository.TranslateRepository
import com.example.translatorproject.utils.Event
import kotlinx.coroutines.launch

class TranslateViewModel(
    private val repository: TranslateRepository,
    private val daoRepository: TranslateDaoRepository
) : ViewModel() {

    private val _translatedText = MutableLiveData<String>()
    val translatedText: LiveData<String> = _translatedText

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    val historyList = daoRepository.getHistory().asLiveData()


    fun translate(sourceText: String, sourceLang: String, targetLang: String) {
        viewModelScope.launch {
            try {
                val result = repository.translateText(sourceText, sourceLang, targetLang)
                _translatedText.value = result

            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    val bookmarks: LiveData<List<BookmarkEntity>> = daoRepository.getBookmarks().asLiveData()
    val history: LiveData<List<HistoryEntity>> = daoRepository.getHistory().asLiveData()


    // Bookmark
    fun addBookmark(source: String, translated: String, sourceLang: String, targetLang: String) {
        viewModelScope.launch {
            daoRepository.insertBookmark(
                BookmarkEntity(
                    sourceText = source,
                    translatedText = translated,
                    sourceLangCode = sourceLang,
                    targetLangCode = targetLang
                )
            )
        }
    }

    fun deleteBookmark(bookmark: BookmarkEntity) {
        viewModelScope.launch {
            daoRepository.deleteBookmark(bookmark)
        }
    }

    // History
    fun addHistory(source: String, translated: String, sourceLang: String, targetLang: String) {
        Log.d("History","To add")

        viewModelScope.launch {
            daoRepository.insertHistory(
                HistoryEntity(
                    sourceText = source,
                    translatedText = translated,
                    sourceLangCode = sourceLang,
                    targetLangCode = targetLang
                )

            )
            Log.d("History","Added")
        }
    }
    fun deleteHistory(item: HistoryEntity) {
        viewModelScope.launch {
            daoRepository.deleteHistory(item)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            daoRepository.clearHistory()
        }
    }
}
class TranslateViewModelFactory(
    private val repository: TranslateRepository,
    private val daoRepository: TranslateDaoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TranslateViewModel(repository, daoRepository) as T
    }
}

/*
class TranslateViewModelFactory(
    private val repository: TranslateRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TranslateViewModel(repository) as T
    }
}*/
