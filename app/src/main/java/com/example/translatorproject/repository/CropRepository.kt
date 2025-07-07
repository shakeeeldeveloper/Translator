package com.example.translatorproject.repository


import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.os.Environment
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

class CropRepository(private val app: Application) {

    private val translationDao = AppDatabase.getInstance(app).translationDao()

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
        onTranslated: (List<Pair<Rect, String>>) -> Unit
    ) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.URDU)
            .build()

        val translator = Translation.getClient(options)
        translator.downloadModelIfNeeded().addOnSuccessListener {
            val results = mutableListOf<Pair<Rect, String>>()
            blocks.forEachIndexed { index, (rect, text) ->
                translator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        results.add(rect to translatedText)

                        CoroutineScope(Dispatchers.IO).launch {
                            translationDao.insertHistory(
                                HistoryEntity(
                                    sourceText = text,
                                    translatedText = translatedText,
                                    sourceLangCode = "English",
                                    targetLangCode = "Urdu"
                                )
                            )
                        }
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
            textSize = 120f
            style = Paint.Style.FILL
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
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
    fun saveBitmapToGallery(bitmap: Bitmap): String? {
        return try {
            val dir = File(app.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "TranslatedImages")
            if (!dir.exists()) dir.mkdirs()

            val file = File(dir, "translated_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

}
