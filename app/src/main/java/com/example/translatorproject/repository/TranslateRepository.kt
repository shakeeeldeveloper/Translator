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

class TranslateRepository {


    suspend fun translateText(sourceText: String, sourceLangCode: String, targetLangCode: String): String {
        val sourceLang = TranslateLanguage.fromLanguageTag(sourceLangCode)
        val targetLang = TranslateLanguage.fromLanguageTag(targetLangCode)

        if (sourceLang == null || targetLang == null) {
            throw IllegalArgumentException("Invalid language code")
        }

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLang)
            .setTargetLanguage(targetLang)
            .build()

        val translator = Translation.getClient(options)

        try {
            translator.downloadModelIfNeeded().await()
            val result = translator.translate(sourceText).await()
            return result
        } finally {
            translator.close()
        }
    }
    /*suspend fun insertBookmark(bookmark: BookmarkEntity) = dao.insertBookmark(bookmark)
    fun getBookmarks(): Flow<List<BookmarkEntity>> = dao.getBookmarks()
    suspend fun deleteBookmark(bookmark: BookmarkEntity) = dao.deleteBookmark(bookmark)

    // History
    suspend fun insertHistory(history: HistoryEntity) = dao.insertHistory(history)
    fun getHistory(): Flow<List<HistoryEntity>> = dao.getHistory()
    suspend fun clearHistory() = dao.clearHistory()*/
}
