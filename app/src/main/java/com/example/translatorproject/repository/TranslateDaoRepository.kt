package com.example.translatorproject.repository

import android.content.Context
import com.example.translatorproject.dao.TranslationDao
import com.example.translatorproject.model.BookmarkEntity
import com.example.translatorproject.model.HistoryEntity
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class TranslateDaoRepository ( private val dao: TranslationDao){


    suspend fun insertBookmark(bookmark: BookmarkEntity) = dao.insertBookmark(bookmark)
    fun getBookmarks(): Flow<List<BookmarkEntity>> = dao.getBookmarks()
    suspend fun deleteBookmark(bookmark: BookmarkEntity) = dao.deleteBookmark(bookmark)

    // History
    suspend fun insertHistory(history: HistoryEntity) = dao.insertHistory(history)
    fun getHistory(): Flow<List<HistoryEntity>> = dao.getHistory()
    suspend fun clearHistory() = dao.clearHistory()
}
