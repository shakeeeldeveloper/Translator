package com.example.translatorproject.repository


import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.example.translatorproject.database.AppDatabase
import com.example.translatorproject.model.HistoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Locale

class CropRepository(private val app: Application) {

    private val translationDao = AppDatabase.getInstance(app).translationDao()
    private var tts: TextToSpeech? = null

    fun processOCR(
        uri: Uri,
        onSuccess: (Bitmap, List<Pair<Rect, String>>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        try {
            val image = InputImage.fromFilePath(app, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            val bitmap = BitmapFactory.decodeStream(app.contentResolver.openInputStream(uri)) ?: run {
                onFailure("Failed to decode bitmap.")
                return
            }

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val blocks = visionText.textBlocks.mapNotNull { block ->
                        val rect = block.boundingBox ?: return@mapNotNull null
                        rect to block.text
                    }
                    onSuccess(bitmap, blocks)
                }
                .addOnFailureListener { e ->
                    onFailure("OCR failed: ${e.message}")
                }
        } catch (e: Exception) {
            onFailure("Exception: ${e.message}")
        }
    }

    fun translateBlocks(
        blocks: List<Pair<Rect, String>>,
        onTranslated: (List<Pair<Rect, String>>) -> Unit,
        langCode: String
    ) {
        Log.d("code",langCode)
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(langCode)
            .setTargetLanguage(TranslateLanguage.URDU)
            .build()
        val fullText = blocks.joinToString(" ") { it.second }
        Log.d("FullText", fullText)


        val translator = Translation.getClient(options)
        translator.downloadModelIfNeeded().addOnSuccessListener {
            translator.translate(fullText)
                .addOnSuccessListener { translatedText ->


                    CoroutineScope(Dispatchers.IO).launch {
                        translationDao.insertHistory(
                            HistoryEntity(
                                sourceText = fullText,
                                translatedText = translatedText,
                                sourceLangCode = "English",
                                targetLangCode = "Urdu"
                            )
                        )
                    }


                }
            val results = mutableListOf<Pair<Rect, String>>()
            blocks.forEachIndexed { index, (rect, text) ->
                translator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        results.add(rect to translatedText)
                        if (results.size == blocks.size) {
                            onTranslated(results)
                        }
                    }
            }
        }
    }

    fun drawTranslatedTextOnImage(
        bitmap: Bitmap,
        translatedTexts: List<Pair<Rect, String>>
    ): Bitmap {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        val bgPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 80f
            style = Paint.Style.FILL
            isAntiAlias = true
            typeface = Typeface.DEFAULT
        }

        translatedTexts.forEach { (rect, text) ->
            val padding = 10
            val bgRect = Rect(
                rect.left - padding,
                rect.top - padding,
                rect.right + padding,
                rect.bottom + padding
            )
            canvas.drawRect(bgRect, bgPaint)
            canvas.drawText(text, rect.left.toFloat(), rect.bottom.toFloat(), textPaint)
        }

        return mutableBitmap
    }

    fun saveBitmapToPictures(bitmap: Bitmap, fileName: String): Boolean {
        return try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyApp")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val resolver = app.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let {
                val outputStream: OutputStream? = resolver.openOutputStream(it)
                outputStream?.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }

                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
                Toast.makeText(app,"Image Saved!!", Toast.LENGTH_SHORT).show()
                return true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    fun speakText(text: String, languageCode: String) {
        //   Log.d("speak",text+ languageCode +" in repo")

        tts = TextToSpeech(app) { status ->
            Log.d("speak",text+ languageCode +" in repo")
            if (status == TextToSpeech.SUCCESS) {

                val result = tts?.setLanguage(Locale(languageCode))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(app, "TTS language not supported", Toast.LENGTH_SHORT).show()
                } else {
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    }

}
