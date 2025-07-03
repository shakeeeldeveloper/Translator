package com.example.translatorproject.ui.home

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.translatorproject.R
import com.example.translatorproject.SettingActivity
import com.example.translatorproject.databinding.FragmentHomeBinding
import com.example.translatorproject.repository.HomeRepository
import com.example.translatorproject.ui.chat.ChatViewModel
import com.example.translatorproject.ui.language.LanguageFragment
import com.example.translatorproject.ui.translate.TranslateFragment
import com.example.translatorproject.ui.translate.TranslateViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.translatorproject.utils.Event
import kotlin.getValue


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    private val REQUEST_CODE_SPEECH_INPUT = 1001


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Show bottom nav
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility =
            View.VISIBLE

      //  AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        val repository = HomeRepository(requireContext()) // or applicationContext
        val viewModelFactory = HomeViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        // ViewModel setup
     //   viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Observers
        viewModel.sourceLang.observe(viewLifecycleOwner) {
            binding.lang1TV.text = it
        }

        viewModel.targetLang.observe(viewLifecycleOwner) {
            binding.lang2TV.text = it
        }

        viewModel.translatedText.observe(viewLifecycleOwner) {
          /*  when (it) {
                "TRANSLATION_FAILED" -> showToast("Translation failed")
                "MODEL_DOWNLOAD_FAILED" -> showToast("Model download failed")
                "INVALID_LANGUAGE_CODE" -> showToast("Invalid language code")
                else -> openTranslateFragment(binding.textTrans.text.toString(), it)
                    val translated = event.getContentIfNotHandled() ?: return@observe
                openTranslateFragment(binding.textTrans.text.toString(), translated)

            }*/
            viewModel.translatedText.observe(viewLifecycleOwner) { event ->
                val translated = event.getContentIfNotHandled() ?: return@observe
                Log.d("source", binding.lang1TV.text.toString())
                openTranslateFragment(binding.textTrans.text.toString(), translated,binding.lang1TV.text.toString(),binding.lang2TV.text.toString())

            }
            viewModel.errorMessage.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }

        }

        // Language selection result listener
        parentFragmentManager.setFragmentResultListener("languageRequestKey", viewLifecycleOwner) { _, bundle ->
            val name = bundle.getString("selectedLanguage") ?: return@setFragmentResultListener
            val code = bundle.getString("LanguageCode") ?: return@setFragmentResultListener
            val requestFor = bundle.getString("requestFor") ?: return@setFragmentResultListener

            viewModel.setLanguage(requestFor, name, code)
        }

        // Button listeners
        binding.engLangBtn.setOnClickListener {
            val langFragment = LanguageFragment().apply {
                arguments = Bundle().apply { putString("requestFor", "button1") }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, langFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.urduLangBtn.setOnClickListener {
            val langFragment = LanguageFragment().apply {
                arguments = Bundle().apply { putString("requestFor", "button2") }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, langFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rotateIcon.setOnClickListener {
            viewModel.swapLanguages()
        }

        binding.settingImageView.setOnClickListener {
            startActivity(Intent(requireContext(), SettingActivity::class.java))
        }

        binding.transTV.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, TranslateFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.transBtn.setOnClickListener {
            val sourceText = binding.textTrans.text.toString().trim()
            if (sourceText.isEmpty()) {
                startSpeechToText(binding.lang1TV.text.toString())
                //showToast("Enter text to translate")
            } else {
                viewModel.translateText(sourceText)

            }
        }

        // Toggle icon on input
        binding.textTrans.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrBlank()
                val iconRes = if (hasText) R.drawable.trans_svg else R.drawable.voice_icon
                binding.transBtn.setImageResource(iconRes)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return root
    }

    private fun openTranslateFragment(originalText: String, translatedText: String,srcLang: String,trgLang: String) {
        Log.d("source", srcLang+" in fun")

        val fragment = TranslateFragment.newInstance(originalText, translatedText,srcLang,trgLang)
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun startSpeechToText(language: String ) {

        val languageCodeMap = mapOf(
            "Afrikaans" to "af-ZA",
            "Albanian" to "sq-AL",
            "Arabic" to "ar-SA",
            "Bengali" to "bn-IN",
            "Bulgarian" to "bg-BG",
            "Catalan" to "ca-ES",
            "Chinese" to "zh-CN",
            "Croatian" to "hr-HR",
            "Czech" to "cs-CZ",
            "Danish" to "da-DK",
            "Dutch" to "nl-NL",
            "English" to "en-US",
            "Estonian" to "et-EE",
            "Finnish" to "fi-FI",
            "French" to "fr-FR",
            "Galician" to "gl-ES",
            "German" to "de-DE",
            "Greek" to "el-GR",
            "Gujarati" to "gu-IN",
            "Hebrew" to "he-IL",
            "Hindi" to "hi-IN",
            "Hungarian" to "hu-HU",
            "Icelandic" to "is-IS",
            "Indonesian" to "id-ID",
            "Italian" to "it-IT",
            "Japanese" to "ja-JP",
            "Kannada" to "kn-IN",
            "Korean" to "ko-KR",
            "Latvian" to "lv-LV",
            "Lithuanian" to "lt-LT",
            "Macedonian" to "mk-MK",
            "Malay" to "ms-MY",
            "Marathi" to "mr-IN",
            "Norwegian" to "no-NO",
            "Persian" to "fa-IR",
            "Polish" to "pl-PL",
            "Portuguese" to "pt-PT",
            "Romanian" to "ro-RO",
            "Russian" to "ru-RU",
            "Slovak" to "sk-SK",
            "Slovenian" to "sl-SI",
            "Spanish" to "es-ES",
            "Swedish" to "sv-SE",
            "Swahili" to "sw-TZ",
            "Tamil" to "ta-IN",
            "Telugu" to "te-IN",
            "Thai" to "th-TH",
            "Turkish" to "tr-TR",
            "Ukrainian" to "uk-UA",
            "Urdu" to "ur-PK",
            "Vietnamese" to "vi-VN",
            "Welsh" to "cy-GB"
        )

        val languageCode=languageCodeMap[language]

        //Log.d("TAG", "$language -> $code -> $mlKitLangCode")

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak in selected language")
        }

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "Speech-to-text not supported", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results?.get(0) ?: return

            binding.textTrans.setText(spokenText)

        }
    }


}
