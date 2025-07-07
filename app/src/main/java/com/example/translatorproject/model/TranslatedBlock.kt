package com.example.translatorproject.model

import android.graphics.Rect

data class TranslatedBlock(
    val boundingBox: Rect,
    val translatedText: String
)
