package com.example.translatorproject.ui.translate

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.translatorproject.R
import com.example.translatorproject.databinding.FragmentTranslateBinding
import com.example.translatorproject.ui.language.LanguageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.translatorproject.repository.TranslateRepository
import com.example.translatorproject.ui.chat.ChatViewModel
import com.example.translatorproject.utils.ClipboardUtils
import kotlin.getValue


class TranslateFragment : Fragment() {

    private var _binding: FragmentTranslateBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TranslateViewModel
    private val chatViewModel: ChatViewModel by viewModels()



    private var originalText: String? = null
    private var translatedText: String? = null

    private var selectedLang1 = "English"
    private var selectedLang2 = "Urdu"
    private var selectedLang1Code = "en"
    private var selectedLang2Code = "ur"
    var edit="0"

    private val REQUEST_CODE_SPEECH_INPUT = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            originalText = it.getString("originalText")
            translatedText = it.getString("translatedText")
            edit="0"
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTranslateBinding.inflate(inflater, container, false)

        // ViewModel setup
        val repository = TranslateRepository()
        viewModel = ViewModelProvider(this, TranslateViewModelFactory(repository)).get(TranslateViewModel::class.java)

        setupObservers()
        setupUI()
        return binding.root
    }

    private fun setupObservers() {
        viewModel.translatedText.observe(viewLifecycleOwner) { translated ->
            if (edit=="1"){
                Log.d("Frag",binding.textEditTxt.text.toString()+ "Edit____")

                binding.langSelectors.visibility = View.VISIBLE
                binding.beforeTransCV.visibility = View.VISIBLE
                if (binding.textEditTxt.text.toString()!=null)
                binding.crossImg.visibility = View.VISIBLE
                binding.originalTextCV.visibility = View.GONE
                binding.translatedCV.visibility = View.GONE
            }
            else {
                Log.d("Frag",binding.textEditTxt.text.toString()+ "Translate____")

                binding.beforeTransCV.visibility = View.GONE
                binding.langSelectors.visibility = View.GONE
                binding.originalTextCV.visibility = View.VISIBLE
                binding.translatedCV.visibility = View.VISIBLE

                binding.originalText.setText(binding.textEditTxt.text.toString().trim())
                binding.translatedText.setText(translated)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUI() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE

        //Comes from Home Fragment with args
        if (originalText != null) {
            Log.d("Frag",binding.textEditTxt.text.toString()+ "____Original")
            binding.langSelectors.visibility = View.GONE
            binding.beforeTransCV.visibility = View.GONE
            binding.originalTextCV.visibility = View.VISIBLE
            binding.translatedCV.visibility = View.VISIBLE
            binding.originalText.setText(originalText)
            binding.translatedText.setText(translatedText)
        } else {
            Log.d("Frag",binding.textEditTxt.text.toString()+ "Null____")

            binding.langSelectors.visibility = View.VISIBLE
            binding.beforeTransCV.visibility = View.VISIBLE
            binding.crossImg.visibility = View.GONE
            binding.originalTextCV.visibility = View.GONE
            binding.translatedCV.visibility = View.GONE
        }

        binding.textEditTxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrBlank()
                val iconRes = if (hasText) R.drawable.trans_svg else R.drawable.voice_icon
                binding.transBtn.setImageResource(iconRes)
                binding.crossImg.visibility = if (hasText) View.VISIBLE else View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.transBtn.setOnClickListener {
            edit="0"
            val sourceText = binding.textEditTxt.text.toString().trim()
            if (sourceText.isEmpty()) {
                startSpeechToText(binding.lang1TV.text.toString())
               // Toast.makeText(requireContext(), "Enter text to translate", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.translate(sourceText, selectedLang1Code, selectedLang2Code)
            }
        }

        binding.crossImg.setOnClickListener {
            binding.textEditTxt.text.clear()
            binding.transBtn.setImageResource(R.drawable.voice_icon)
        }

        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.langBtn1.setOnClickListener {
            edit="1"
            val langFragment = LanguageFragment().apply {
                arguments = Bundle().apply { putString("requestFor", "button1") }
            }
            parentFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, langFragment).addToBackStack(null).commit()
        }

        binding.langBtn2.setOnClickListener {
            edit="1"
            val langFragment = LanguageFragment().apply {
                arguments = Bundle().apply { putString("requestFor", "button2") }
            }
            parentFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, langFragment).addToBackStack(null).commit()
        }
        binding.editIcon.setOnClickListener {
            binding.langSelectors.visibility = View.VISIBLE
            binding.beforeTransCV.visibility = View.VISIBLE
            binding.crossImg.visibility = View.VISIBLE
            binding.originalTextCV.visibility = View.GONE
            binding.translatedCV.visibility = View.GONE
            binding.textEditTxt.setText(binding.originalText.text.toString())
        }
        binding.crossImg.setOnClickListener {
            binding.textEditTxt.text.clear()
            binding.transBtn.setImageResource(R.drawable.voice_icon)
        }
        binding.crossImg1.setOnClickListener {
            binding.langSelectors.visibility = View.VISIBLE
            binding.beforeTransCV.visibility = View.VISIBLE
            binding.crossImg.visibility = View.VISIBLE
            binding.originalTextCV.visibility = View.GONE
            binding.translatedCV.visibility = View.GONE

            binding.textEditTxt.text.clear()
            binding.transBtn.setImageResource(R.drawable.voice_icon)

        }
        binding.copyIcon.setOnClickListener {
            val textToCopy = binding.originalText.text.toString()
            ClipboardUtils.copyToClipboard(requireContext(), "Original Text", textToCopy)
        }
        binding.copyIconTrans.setOnClickListener {
            val textToCopy = binding.translatedText.text.toString()
            ClipboardUtils.copyToClipboard(requireContext(), "Translated Text", textToCopy)
        }
        binding.speakIconTr.setOnClickListener {
            Log.d("speak",binding.originalText.text.toString()+ selectedLang1Code)
            chatViewModel.speak(binding.originalText.text.toString(), selectedLang1Code)

        }


        parentFragmentManager.setFragmentResultListener("languageRequestKey", viewLifecycleOwner) { _, bundle ->
            val selectedLanguage = bundle.getString("selectedLanguage")
            val requestFor = bundle.getString("requestFor")
            val selectedLanguageCode = bundle.getString("LanguageCode")

            when (requestFor) {
                "button1" -> {
                    selectedLang1 = selectedLanguage ?: "English"
                    binding.lang1TV.text = selectedLang1
                    selectedLang1Code = selectedLanguageCode ?: "en"
                }
                "button2" -> {
                    selectedLang2 = selectedLanguage ?: "Urdu"
                    binding.lang2TV.text = selectedLang2
                    selectedLang2Code = selectedLanguageCode ?: "ur"
                }
            }
        }

        binding.lang1TV.text = selectedLang1
        binding.lang2TV.text = selectedLang2
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

            binding.textEditTxt.setText(spokenText)
            viewModel.translate(spokenText, selectedLang1Code, selectedLang2Code)

        }
    }
    companion object {
        @JvmStatic
        fun newInstance(originalText: String, translatedText: String): TranslateFragment {
            val fragment = TranslateFragment()
            val args = Bundle().apply {
                putString("originalText", originalText)
                putString("translatedText", translatedText)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




/*
class TranslateFragment : Fragment() {

    private var _binding: FragmentTranslateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    // Receive arguments
    private var originalText: String? = null
    private var translatedText: String? = null

    private var selectedLang1: String = "English"
    private var selectedLang2: String = "Urdu"
    private var selectedLang1Code: String="en"
    private var selectedLang2Code: String="ur"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            originalText = it.getString("originalText")
            translatedText = it.getString("translatedText")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentTranslateBinding.inflate(inflater, container, false)
        val root: View = binding.root

        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE


        //Translate Fragment Open from Home Fragment
        if (originalText!=null) {
            binding.langSelectors.visibility = View.GONE
            binding.beforeTransCV.visibility= View.GONE


            binding.originalTextCV.visibility= View.VISIBLE
            binding.translatedCV.visibility= View.VISIBLE

            binding.originalText.setText(originalText.toString())
            binding.translatedText.setText(translatedText)
        }
        else{
            binding.langSelectors.visibility = View.VISIBLE
            binding.beforeTransCV.visibility= View.VISIBLE


            binding.crossImg.visibility= View.GONE
            binding.originalTextCV.visibility= View.GONE
            binding.translatedCV.visibility= View.GONE


        }


        binding.textEditTxt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrBlank()

                val iconRes = if (hasText) {
                    R.drawable.trans_svg  // your translate icon

                }
                else {
                    R.drawable.voice_icon  // your mic icon


                }
                if (hasText)  binding.crossImg.visibility= View.VISIBLE
                else binding.crossImg.visibility= View.GONE

                binding.transBtn.setImageResource(iconRes)
            }


            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        */
/* val textView: TextView = binding.textHome
         homeViewModel.text.observe(viewLifecycleOwner) {
             textView.text = it
         }*//*



        parentFragmentManager.setFragmentResultListener(
            "languageRequestKey",
            viewLifecycleOwner
        ) { _, bundle ->
            val selectedLanguage = bundle.getString("selectedLanguage")
            val requestFor = bundle.getString("requestFor")
            val selectedLanguageCode=bundle.getString("LanguageCode")

            when (requestFor) {
                "button1" -> {
                    selectedLang1 = selectedLanguage ?: "English"
                    binding.lang1TV.text = selectedLang1
                    selectedLang1Code= selectedLanguageCode.toString()
                }
                "button2" -> {
                    selectedLang2 = selectedLanguage ?: "Urdu"
                    binding.lang2TV.text = selectedLang2
                    selectedLang2Code= selectedLanguageCode.toString()

                }
            }
        }
        binding.langBtn1.setOnClickListener {

            val languageFragment = LanguageFragment()
            languageFragment.arguments = Bundle().apply {
                putString("requestFor", "button1")
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, languageFragment) // container in your activity layout
                .addToBackStack(null) // optional: adds to back stack
                .commit()
        }
        binding.langBtn2.setOnClickListener {

            val languageFragment = LanguageFragment()
            languageFragment.arguments = Bundle().apply {
                putString("requestFor", "button2")
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, languageFragment) // container in your activity layout
                .addToBackStack(null) // optional: adds to back stack
                .commit()
        }
        binding.backIcon.setOnClickListener {

            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.crossImg.setOnClickListener {
            binding.textEditTxt.text.clear()
            binding.transBtn.setImageResource(R.drawable.voice_icon)


        }
        binding.transBtn.setOnClickListener {
            val sourceText = binding.textEditTxt.text.toString().trim()

            if (sourceText.isEmpty()) {
                Toast.makeText(requireContext(), "Enter text to translate", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sourceMLLang = TranslateLanguage.fromLanguageTag(selectedLang1Code)
            val targetMLLang = TranslateLanguage.fromLanguageTag(selectedLang2Code)


            if (sourceMLLang != null && targetMLLang != null) {
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(sourceMLLang)
                    .setTargetLanguage(targetMLLang)
                    .build()

                val translator = Translation.getClient(options)

                translator.downloadModelIfNeeded()
                    .addOnSuccessListener {
                        translator.translate(sourceText)
                            .addOnSuccessListener { translatedText ->
                                //openTranslateFragment(sourceText, translatedText)

                                binding.beforeTransCV.visibility= View.GONE
                                binding.langSelectors.visibility= View.GONE

                                binding.originalTextCV.visibility= View.VISIBLE
                                binding.translatedCV.visibility= View.VISIBLE

                                binding.originalText.setText(sourceText)
                                binding.translatedText.setText(translatedText)

                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Translation failed", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Model download failed", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Invalid language code", Toast.LENGTH_SHORT).show()
            }
        }



        binding.lang1TV.text = selectedLang1
        binding.lang2TV.text = selectedLang2

        return root
    }
    companion object {
        fun newInstance(originalText: String, translatedText: String): TranslateFragment {
            val fragment = TranslateFragment()
            val args = Bundle().apply {
                putString("originalText", originalText)
                putString("translatedText", translatedText)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}*/
