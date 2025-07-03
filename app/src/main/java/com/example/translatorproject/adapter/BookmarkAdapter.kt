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
import com.example.translatorproject.model.SettingModel
class BookmarkAdapter(
    private val onBookmarkRemoveClick: (BookmarkEntity) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {


    private val bookmarks = mutableListOf<BookmarkEntity>()

    fun submitList(newList: List<BookmarkEntity>) {
        bookmarks.clear()
        bookmarks.addAll(newList)
        notifyDataSetChanged()
    }

    inner class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val originalText: TextView = itemView.findViewById(R.id.transSrcLangTV)
        val translatedText: TextView = itemView.findViewById(R.id.transTrgLangTV)
        val bookmarkIcon: ImageView = itemView.findViewById(R.id.bookmarkIcon)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bookmark_layout, parent, false)
        return BookmarkViewHolder(view)
    }

    override fun getItemCount(): Int = bookmarks.size

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val item = bookmarks[position]
        holder.originalText.text = item.sourceText
        holder.translatedText.text = item.translatedText



        val isSrcUrdu = item.sourceLangCode.lowercase() in listOf("ur", "urdu")
        val isTrgUrdu = item.targetLangCode.lowercase() in listOf("ur", "urdu")

        // Source Text Alignment
        holder.translatedText.textAlignment =
            if (isSrcUrdu) View.TEXT_ALIGNMENT_VIEW_END else View.TEXT_ALIGNMENT_VIEW_START
        holder.translatedText.textDirection =
            if (isSrcUrdu) View.TEXT_DIRECTION_RTL else View.TEXT_DIRECTION_LTR
        holder.translatedText.gravity =
            if (isSrcUrdu) Gravity.END else Gravity.START

        // Target Text Alignment
        holder.translatedText.textAlignment =
            if (isTrgUrdu) View.TEXT_ALIGNMENT_VIEW_END else View.TEXT_ALIGNMENT_VIEW_START
        holder.translatedText.textDirection =
            if (isTrgUrdu) View.TEXT_DIRECTION_RTL else View.TEXT_DIRECTION_LTR
        holder.translatedText.gravity =
            if (isTrgUrdu) Gravity.END else Gravity.START


        // Remove bookmark on icon click
        holder.bookmarkIcon.setOnClickListener {
            onBookmarkRemoveClick(item)
        }

    }
}
