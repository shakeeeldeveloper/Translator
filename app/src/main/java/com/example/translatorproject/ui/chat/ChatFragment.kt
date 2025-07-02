/*
package com.example.translatorproject.ui.chat

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.translatorproject.R
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.translatorproject.databinding.FragmentChatBinding
import com.example.translatorproject.ui.language.LanguageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ChatViewModel
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent

    private var selectedLang1 = "Urdu"
    private var selectedLang2 = "English"
    private var selectedLang1Code = "ur"
    private var selectedLang2Code = "en"



    private var micOverlay: View? = null
    private lateinit var tts: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        requireActivity().findViewById<BottomNavigationView>(com.example.translatorproject.R.id.bottom_navigation)?.visibility = View.VISIBLE

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now…")
        }

        parentFragmentManager.setFragmentResultListener("languageRequestKey", viewLifecycleOwner) { _, bundle ->
            val selectedLanguage = bundle.getString("selectedLanguage") ?: return@setFragmentResultListener
            val selectedLanguageCode = bundle.getString("LanguageCode") ?: return@setFragmentResultListener
            when (bundle.getString("requestFor")) {
                "button1" -> {
                    selectedLang1 = selectedLanguage
                    selectedLang1Code = selectedLanguageCode
                    binding.lang1TV.text = selectedLang1
                }
                "button2" -> {
                    selectedLang2 = selectedLanguage
                    selectedLang2Code = selectedLanguageCode
                    binding.lang2TV.text = selectedLang2
                }
            }
        }

        binding.voiceIcon.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            } else {
                startVoiceInput()
            }
        }

        binding.speakLayout.setOnClickListener {
            val text = binding.person1EditText.text.toString()
            viewModel.speak(text, selectedLang1Code)
        }
        binding.speakLayoutIcon.setOnClickListener {
            val text = binding.person2EditText.text.toString()
            viewModel.speak(text, selectedLang2Code)
        }

        binding.lang1Btn.setOnClickListener {
            openLanguageSelector("button1")
        }

        binding.lang2Btn.setOnClickListener {
            openLanguageSelector("button2")
        }

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.person1Text.observe(viewLifecycleOwner) {
            binding.person1EditText.setText(it)
        }

        viewModel.person2Text.observe(viewLifecycleOwner) {
            binding.person2EditText.setText(it)
        }
        viewModel.langn1Btn.observe(viewLifecycleOwner) {

        }


        viewModel.detectedLang.observe(viewLifecycleOwner) { langCode ->
            if (langCode.isNotEmpty()) {
                val spokenText = binding.person2EditText.text.toString()
                if (langCode != selectedLang2Code) {
                    viewModel.translateText(spokenText, langCode, selectedLang2Code, false)
                } else {
                    viewModel.translateText(spokenText, selectedLang2Code, selectedLang1Code, true)
                }
            } else {
                Toast.makeText(requireContext(), "Cannot identify language", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showMicOverlay() {
        val inflater = LayoutInflater.from(requireContext())
        micOverlay = inflater.inflate(R.layout.dialog_listening, null)
        val root = requireActivity().window.decorView.findViewById<ViewGroup>(android.R.id.content)
        root.addView(micOverlay)

        // Pulse Animation
        val micView = micOverlay?.findViewById<ImageView>(R.id.micPulse)
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            micView,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.3f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.3f, 1f)
        ).apply {
            duration = 800
            repeatCount = ValueAnimator.INFINITE
        }
        animator.start()

        micOverlay?.tag = animator
    }
    private fun dismissMicOverlay() {
        micOverlay?.let {
            val animator = it.tag as? ObjectAnimator
            animator?.cancel()
            val root = requireActivity().window.decorView.findViewById<ViewGroup>(android.R.id.content)
            root.removeView(it)
            micOverlay = null
        }
    }
    private fun startVoiceInput() {
        showMicOverlay()

        speechRecognizer.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                dismissMicOverlay()
            }
            override fun onError(error: Int) {
                Toast.makeText(requireContext(), "Speech error: $error", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                val spokenText = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.joinToString(" ") ?: ""
                binding.person2EditText.setText(spokenText)
                viewModel.detectLanguage(spokenText)
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(speechIntent)
    }

    private fun openLanguageSelector(requestFor: String) {
        val fragment = LanguageFragment().apply {
            arguments = Bundle().apply {
                putString("requestFor", requestFor)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(com.example.translatorproject.R.id.nav_host_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }
    private fun textToSpeech(text: String, selectedLang1Code: String){
        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale(selectedLang1Code)) // Urdu, use Locale.ENGLISH for English
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(requireContext(), "Language not supported", Toast.LENGTH_SHORT).show()
                }
                else
                    speakText(text)
            }
        }
    }
    private fun speakText(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speechRecognizer.destroy()
        _binding = null
    }
}
*/



package com.example.translatorproject.ui.chat

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import com.example.translatorproject.databinding.FragmentChatBinding
import com.example.translatorproject.ui.language.LanguageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.mlkit.nl.translate.TranslateLanguage
import java.util.Locale


class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent
    private var micOverlay: View? = null

    private var selectedLang1 = "Urdu"
    private var selectedLang2 = "English"
    private var selectedLang1Code = "ur"
    private var selectedLang2Code = "en"


    private val REQUEST_CODE_SPEECH_INPUT = 1001


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        requireActivity().findViewById<BottomNavigationView>(com.example.translatorproject.R.id.bottom_navigation)?.visibility = View.VISIBLE

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now…")
        }

        parentFragmentManager.setFragmentResultListener("languageRequestKey", viewLifecycleOwner) { _, bundle ->
            val selectedLanguage = bundle.getString("selectedLanguage") ?: return@setFragmentResultListener
            val selectedLanguageCode = bundle.getString("LanguageCode") ?: return@setFragmentResultListener
            when (bundle.getString("requestFor")) {
                "button1" -> {
                    selectedLang1 = selectedLanguage
                    selectedLang1Code = selectedLanguageCode
                    binding.lang1TV.text = selectedLang1
                }
                "button2" -> {
                    selectedLang2 = selectedLanguage
                    selectedLang2Code = selectedLanguageCode
                    binding.lang2TV.text = selectedLang2
                }
            }
        }

      /*  binding.voiceIcon.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            } else {
                startVoiceInput()
            }
        }*/
        binding.voice2Icon.setOnClickListener {

                startSpeechToText(binding.lang2TV.text.toString())

        }
        binding.voice1Icon.setOnClickListener {
            startSpeechToText(binding.lang1TV.text.toString())

        }

        binding.speakLayout.setOnClickListener {
            val text = binding.person1EditText.text.toString()
            viewModel.speak(text, selectedLang1Code)
        }
        binding.speakLayoutIcon.setOnClickListener {
            val text = binding.person2EditText.text.toString()
            viewModel.speak(text, selectedLang2Code)
        }


        binding.lang1Btn.setOnClickListener { openLanguageSelector("button1") }
        binding.lang2Btn.setOnClickListener { openLanguageSelector("button2") }

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
            viewModel.person1Text.observe(viewLifecycleOwner) { text ->
                binding.person1EditText.setText(text)
            }



            viewModel.person2Text.observe(viewLifecycleOwner) { text ->
                binding.person2EditText.setText(text)
            }



            viewModel.detectedLang.observe(viewLifecycleOwner){ langCode ->
                val spokenText = binding.person2EditText.text.toString()
                if (langCode != selectedLang2Code) {
                    viewModel.translateText(spokenText, langCode, selectedLang2Code, false)
                } else {
                    viewModel.translateText(spokenText, selectedLang2Code, selectedLang1Code, true)
                }
            }

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

            binding.person2EditText.setText(spokenText)
            viewModel.detectLanguage(spokenText)

        }
    }

    private fun startVoiceInput() {
        showMicOverlay()
        speechRecognizer.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { dismissMicOverlay() }
            override fun onError(error: Int) { Toast.makeText(requireContext(), "Speech error: $error", Toast.LENGTH_SHORT).show() }
            override fun onResults(results: Bundle?) {
                val spokenText = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.joinToString(" ") ?: ""
                binding.person2EditText.setText(spokenText)
                viewModel.detectLanguage(spokenText)
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        speechRecognizer.startListening(speechIntent)
    }

    private fun openLanguageSelector(requestFor: String) {
        val fragment = LanguageFragment().apply {
            arguments = Bundle().apply { putString("requestFor", requestFor) }
        }
        parentFragmentManager.beginTransaction()
            .replace(com.example.translatorproject.R.id.nav_host_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showMicOverlay() {
        val inflater = LayoutInflater.from(requireContext())
        micOverlay = inflater.inflate(com.example.translatorproject.R.layout.dialog_listening, null)
        val root = requireActivity().window.decorView.findViewById<ViewGroup>(android.R.id.content)
        root.addView(micOverlay)
        val micView = micOverlay?.findViewById<ImageView>(com.example.translatorproject.R.id.micPulse)
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            micView,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.3f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.3f, 1f)
        ).apply {
            duration = 800
            repeatCount = ValueAnimator.INFINITE
        }
        animator.start()
        micOverlay?.tag = animator
    }

    private fun dismissMicOverlay() {
        micOverlay?.let {
            val animator = it.tag as? ObjectAnimator
            animator?.cancel()
            val root = requireActivity().window.decorView.findViewById<ViewGroup>(android.R.id.content)
            root.removeView(it)
            micOverlay = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speechRecognizer.destroy()
        _binding = null
    }
}

