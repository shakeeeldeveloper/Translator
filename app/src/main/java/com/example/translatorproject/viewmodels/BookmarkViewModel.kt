package com.example.translatorproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.translatorproject.model.BookmarkEntity
import com.example.translatorproject.repository.TranslateDaoRepository

class BookmarkViewModel(private val repository: TranslateDaoRepository) : ViewModel() {
    val bookmarks: LiveData<List<BookmarkEntity>> = repository.getBookmarks().asLiveData()
}
class BookmarkViewModelFactory(
    private val repository: TranslateDaoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookmarkViewModel(repository) as T
    }
}
