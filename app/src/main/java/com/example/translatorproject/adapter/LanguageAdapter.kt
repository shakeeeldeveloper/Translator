package com.example.translatorproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.translatorproject.databinding.ItemLanguageBinding
import com.example.translatorproject.model.LanguageModel
import com.example.translatorproject.R

class LanguageAdapter(
    private val originalList: List<LanguageModel>,
    private val onLanguageSelected: (LanguageModel) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>(), Filterable {

    private var filteredList = originalList.toMutableList()
    private var selectedPosition = -1

    inner class LanguageViewHolder(val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root) {



        fun bind(language: LanguageModel, position: Int) {
            binding.languageName.text = language.name
            binding.radioButton.isChecked = selectedPosition == position

            // Apply different background
            val bgRes = if (selectedPosition == position) {
                R.drawable.bg_selected_language_border
            } else {
                R.drawable.bg_language_default
            }
            binding.itemRoot.setBackgroundResource(bgRes)

            binding.root.setOnClickListener {
                updateSelection(position)
            }

            binding.radioButton.setOnClickListener {
                updateSelection(position)
            }
        }


        private fun updateSelection(position: Int) {
            val selectedLanguage = filteredList[position]
            val oldSelectedPosition = selectedPosition
            selectedPosition = filteredList.indexOf(selectedLanguage)

            notifyDataSetChanged()
            onLanguageSelected(selectedLanguage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(filteredList[position], position)
    }

    override fun getItemCount(): Int = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                val result = if (query.isNullOrBlank()) {
                    originalList
                } else {
                    originalList.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                }

                return FilterResults().apply { values = result }
            }

            override fun publishResults(charSequence: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as? List<LanguageModel>)?.toMutableList() ?: mutableListOf()
                notifyDataSetChanged()
            }
        }
    }
}

