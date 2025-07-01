package com.example.translatorproject.model


data class LanguageModel(
    val code: String,
    val name: String,
    var isSelected: Boolean = false
)

