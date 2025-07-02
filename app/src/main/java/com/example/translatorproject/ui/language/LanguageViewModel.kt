package com.example.translatorproject.ui.language

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.translatorproject.model.LanguageModel
import java.util.*

class LanguageViewModel : ViewModel() {

    private val _filteredLanguages = MutableLiveData<List<LanguageModel>>()
    val filteredLanguages: LiveData<List<LanguageModel>> get() = _filteredLanguages

    private val _originalList = getAllLanguages().sortedBy { it.name }

    fun getOriginalList(): List<LanguageModel> = _originalList

    fun filterLanguages(query: String) {
        val filtered = if (query.isBlank()) {
            _originalList
        } else {
            _originalList.filter { it.name.contains(query, ignoreCase = true) }
        }
        _filteredLanguages.value = filtered
    }

    private fun getAllLanguages(): List<LanguageModel> {
        val locales = Locale.getAvailableLocales()
        val languageMap = mutableMapOf<String, String>()

        for (locale in locales) {
            val code = locale.language
            val name = locale.getDisplayLanguage(Locale.ENGLISH).trim()
            if (code.isNotEmpty() && name.isNotEmpty()) {
                languageMap[code] = name
            }
        }

        return languageMap.map { (code, name) ->
            LanguageModel(code = code, name = name)
        }.distinctBy { it.code }
    }


}
