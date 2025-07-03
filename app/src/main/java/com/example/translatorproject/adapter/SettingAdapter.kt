package com.example.translatorproject.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.translatorproject.BookMarkActivity
import com.example.translatorproject.HistoryActivity

import com.example.translatorproject.databinding.ItemLanguageBinding
import com.example.translatorproject.model.LanguageModel
import com.example.translatorproject.R
import com.example.translatorproject.model.SettingModel
import kotlin.jvm.java

class SettingAdapter(private val items: List<SettingModel>) :
    RecyclerView.Adapter<SettingAdapter.RowViewHolder>() {

    inner class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val settingImg: ImageView = itemView.findViewById(R.id.settingImg)
        val settingName: TextView = itemView.findViewById(R.id.settingName)
        val nextIcon: ImageView = itemView.findViewById(R.id.next_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_setting_layout, parent, false)
        return RowViewHolder(view)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val item = items[position]
        holder.settingName.text = item.settingText
        holder.settingImg.setImageResource(item.settingIcon)
        holder.nextIcon.setImageResource(R.drawable.next_icon)

        holder.itemView.setOnClickListener {
            if (item.settingText.equals("Bookmark", ignoreCase = true)) {
                val context = holder.itemView.context
                val intent = Intent(context, BookMarkActivity::class.java)
                context.startActivity(intent)
            }
            else if (item.settingText.equals("History", ignoreCase = true)) {
                val context = holder.itemView.context
                val intent = Intent(context, HistoryActivity::class.java)
                context.startActivity(intent)
            }
            else{
                Log.d("settingAdapter","other activity")
            }
        }
    }

    override fun getItemCount(): Int = items.size
}


