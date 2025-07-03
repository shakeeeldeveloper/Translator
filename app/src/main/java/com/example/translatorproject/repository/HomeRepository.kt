package com.example.translatorproject.repository

import android.content.Context
import com.example.translatorproject.utils.Event
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class HomeRepository(private val context: Context) {

    suspend fun translateText(sourceText: String, sourceCode: String, targetCode: String): Result<Event<String>> {
        val sourceMLLang = TranslateLanguage.fromLanguageTag(sourceCode)
        val targetMLLang = TranslateLanguage.fromLanguageTag(targetCode)

        if (sourceMLLang == null || targetMLLang == null) {
            return Result.failure(Exception("Invalid language code"))
        }

        return try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceMLLang)
                .setTargetLanguage(targetMLLang)
                .build()

            val translator = Translation.getClient(options)

            // Download model if needed (suspend using await())
            translator.downloadModelIfNeeded().await()

            // Perform translation
            val translated = translator.translate(sourceText).await()
            Result.success(Event(translated))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
