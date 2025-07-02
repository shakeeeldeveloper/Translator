package com.example.translatorproject.repository


import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.*

class ChatRepository(private val context: Context) {

    private var tts: TextToSpeech? = null

    fun detectLanguage(text: String, onResult: (String) -> Unit) {
        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                onResult(if (languageCode == "und") "" else if (languageCode== "hi-Latn") "hi" else languageCode)
            }
            .addOnFailureListener {
                onResult("")
            }
    }

    fun translateText(
        sourceText: String,
        sourceLang: String,
        targetLang: String,
        onResult: (String) -> Unit
    ) {
        val sourceCode = TranslateLanguage.fromLanguageTag(sourceLang)
        val targetCode = TranslateLanguage.fromLanguageTag(targetLang)

        if (sourceCode == null || targetCode == null) {
            Toast.makeText(context, "Invalid language codes", Toast.LENGTH_SHORT).show()
            return
        }

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceCode)
            .setTargetLanguage(targetCode)
            .build()

        val translator = Translation.getClient(options)

        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                translator.translate(sourceText)
                    .addOnSuccessListener(onResult)
                    .addOnFailureListener {
                        Toast.makeText(context, "Translation failed", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Model download failed", Toast.LENGTH_SHORT).show()
            }
    }

    fun speakText(text: String, languageCode: String) {
     //   Log.d("speak",text+ languageCode +" in repo")

        tts = TextToSpeech(context) { status ->
            Log.d("speak",text+ languageCode +" in repo")
            if (status == TextToSpeech.SUCCESS) {

                val result = tts?.setLanguage(Locale(languageCode))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "TTS language not supported", Toast.LENGTH_SHORT).show()
                } else {
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    }

    fun shutdownTTS() {
        tts?.shutdown()
        tts = null
    }
}
