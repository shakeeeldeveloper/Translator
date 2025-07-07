package com.example.translatorproject

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.translatorproject.databinding.ActivityCropBinding
import com.example.translatorproject.databinding.ActivityImageTranslateBinding

class ImageTranslateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageTranslateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageTranslateBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val sourceText = intent.getStringExtra("source_text")
        val translatedText = intent.getStringExtra("translated_text")

        binding.originalText.setText(sourceText)
        binding.translatedText.setText(translatedText)
        Log.d("ReceivedText", "Source: $sourceText \nTranslated: $translatedText")

    }
}