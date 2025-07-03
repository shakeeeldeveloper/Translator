package com.example.translatorproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.translatorproject.adapter.BookmarkAdapter
import com.example.translatorproject.database.AppDatabase
import com.example.translatorproject.databinding.ActivityBookMarkBinding
import com.example.translatorproject.repository.TranslateDaoRepository
import com.example.translatorproject.repository.TranslateRepository
import com.example.translatorproject.ui.translate.TranslateViewModel
import com.example.translatorproject.ui.translate.TranslateViewModelFactory

class BookMarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookMarkBinding
    private lateinit var adapter: BookmarkAdapter
    private lateinit var viewModel: TranslateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate binding
        binding = ActivityBookMarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView using binding
        adapter = BookmarkAdapter { bookmarkItem ->
            viewModel.deleteBookmark(bookmarkItem)
        }

        binding.bookMarkRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookMarkRecyclerView.adapter = adapter

        // Set up ViewModel
        val dao = AppDatabase.getInstance(applicationContext).translationDao()
        val daoRepository = TranslateDaoRepository(dao)
        val translateRepository = TranslateRepository()
        val factory = TranslateViewModelFactory(translateRepository, daoRepository)
        viewModel = ViewModelProvider(this, factory)[TranslateViewModel::class.java]

        // Observe bookmark data
        viewModel.bookmarks.observe(this) { bookmarks ->
            adapter.submitList(bookmarks)
        }
        binding.backIcon.setOnClickListener {
            onBackPressed()
        }
    }
}
