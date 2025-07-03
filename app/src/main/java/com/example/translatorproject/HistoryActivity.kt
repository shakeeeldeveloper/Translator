package com.example.translatorproject

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.translatorproject.database.AppDatabase
import com.example.translatorproject.databinding.ActivityHistoryBinding
import com.example.translatorproject.repository.TranslateDaoRepository
import com.example.translatorproject.repository.TranslateRepository
import com.example.translatorproject.ui.translate.TranslateViewModel
import com.example.translatorproject.ui.translate.TranslateViewModelFactory
import com.example.translatorproject.adapter.HistoryAdapter

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var viewModel: TranslateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Room and Repositories
        val dao = AppDatabase.getInstance(application).translationDao()
        val daoRepo = TranslateDaoRepository(dao)
        val translateRepo = TranslateRepository() // Pass DAO here
        val translateDaoRepo = TranslateDaoRepository(dao) // Pass DAO here
        val factory = TranslateViewModelFactory(translateRepo, translateDaoRepo)

        viewModel = ViewModelProvider(this, factory)[TranslateViewModel::class.java]

        // Setup RecyclerView
        historyAdapter = HistoryAdapter { historyItem ->
            viewModel.deleteHistory(historyItem)
        }
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter

        // Observe LiveData
        viewModel.historyList.observe(this) { list ->
            historyAdapter.submitList(list)
        }
        binding.backIcon.setOnClickListener {
            onBackPressed()
        }
    }
}
