package com.example.translatorproject.ui.translate

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.translatorproject.R
import com.example.translatorproject.ui.home.HomeViewModel
import com.example.translatorproject.databinding.FragmentTranslateBinding
import com.example.translatorproject.ui.language.LanguageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.graphics.toColorInt
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

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


        /* val textView: TextView = binding.textHome
         homeViewModel.text.observe(viewLifecycleOwner) {
             textView.text = it
         }*/


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
}