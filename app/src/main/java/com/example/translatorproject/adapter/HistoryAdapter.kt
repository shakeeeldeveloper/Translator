package com.example.translatorproject.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.translatorproject.databinding.ItemLanguageBinding
import com.example.translatorproject.model.LanguageModel
import com.example.translatorproject.R
import com.example.translatorproject.model.BookmarkEntity
import com.example.translatorproject.model.HistoryEntity
import com.example.translatorproject.model.SettingModel
import com.google.mlkit.nl.translate.TranslateLanguage

class HistoryAdapter(
    private val onDelete: (HistoryEntity) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val historyList = mutableListOf<HistoryEntity>()

    fun submitList(newList: List<HistoryEntity>) {
        historyList.clear()
        historyList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val languageName: TextView = itemView.findViewById(R.id.languageName)
        val transSrcLangTV: TextView = itemView.findViewById(R.id.transSrcLangTV)
        val transTrgLangTV: TextView = itemView.findViewById(R.id.transTrgLangTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_layout, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]


        val langCode = normalizeToLangCode(item.sourceLangCode)
        val displayName = langCode.let { codeToNameMap[it] } ?: item.sourceLangCode
        holder.languageName.text = displayName


        holder.transSrcLangTV.text = item.sourceText
        holder.transTrgLangTV.text = item.translatedText



        val isSrcUrdu = item.sourceLangCode.lowercase() in listOf("ur", "urdu")
        val isTrgUrdu = item.targetLangCode.lowercase() in listOf("ur", "urdu")

        // Source Text Alignment
        holder.transSrcLangTV.textAlignment =
            if (isSrcUrdu) View.TEXT_ALIGNMENT_VIEW_END else View.TEXT_ALIGNMENT_VIEW_START
        holder.transSrcLangTV.textDirection =
            if (isSrcUrdu) View.TEXT_DIRECTION_RTL else View.TEXT_DIRECTION_LTR
        holder.transSrcLangTV.gravity =
            if (isSrcUrdu) Gravity.END else Gravity.START

        // Target Text Alignment
        holder.transTrgLangTV.textAlignment =
            if (isTrgUrdu) View.TEXT_ALIGNMENT_VIEW_END else View.TEXT_ALIGNMENT_VIEW_START
        holder.transTrgLangTV.textDirection =
            if (isTrgUrdu) View.TEXT_DIRECTION_RTL else View.TEXT_DIRECTION_LTR
        holder.transTrgLangTV.gravity =
            if (isTrgUrdu) Gravity.END else Gravity.START



        // Long click to delete
        holder.itemView.setOnLongClickListener {
            onDelete(item)
            true
        }


    }
    // --- Language Mapping ---
    private val nameToCodeMap = mapOf(
        "afrikaans" to "af", "arabic" to "ar", "belarusian" to "be", "bulgarian" to "bg",
        "bengali" to "bn", "catalan" to "ca", "czech" to "cs", "welsh" to "cy",
        "danish" to "da", "german" to "de", "greek" to "el", "english" to "en",
        "esperanto" to "eo", "spanish" to "es", "estonian" to "et", "persian" to "fa",
        "finnish" to "fi", "french" to "fr", "irish" to "ga", "galician" to "gl",
        "gujarati" to "gu", "hindi" to "hi", "haitian creole" to "ht", "hungarian" to "hu",
        "indonesian" to "id", "icelandic" to "is", "italian" to "it", "japanese" to "ja",
        "georgian" to "ka", "kannada" to "kn", "korean" to "ko", "lithuanian" to "lt",
        "latvian" to "lv", "macedonian" to "mk", "marathi" to "mr", "malay" to "ms",
        "maltese" to "mt", "dutch" to "nl", "norwegian" to "no", "polish" to "pl",
        "portuguese" to "pt", "romanian" to "ro", "russian" to "ru", "slovak" to "sk",
        "slovenian" to "sl", "albanian" to "sq", "swedish" to "sv", "swahili" to "sw",
        "tamil" to "ta", "telugu" to "te", "thai" to "th", "tagalog" to "tl",
        "turkish" to "tr", "ukrainian" to "uk", "urdu" to "ur", "vietnamese" to "vi",
        "chinese" to "zh"
    )

    private val codeToNameMap = nameToCodeMap.entries.associate { it.value to it.key.replaceFirstChar { c -> c.uppercaseChar() } }

    private fun normalizeToLangCode(input: String): String {
        val lower = input.lowercase()
        return when {
            nameToCodeMap.containsKey(lower) -> nameToCodeMap[lower]!!
            codeToNameMap.containsKey(lower) -> lower
            else -> lower
        }
    }

}
