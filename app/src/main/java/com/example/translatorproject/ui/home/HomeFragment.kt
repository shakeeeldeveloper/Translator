package com.example.translatorproject.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.translatorproject.R
import com.example.translatorproject.SettingActivity
import com.example.translatorproject.ui.home.HomeViewModel
import com.example.translatorproject.databinding.FragmentHomeBinding
import com.example.translatorproject.ui.language.LanguageFragment
import com.example.translatorproject.ui.translate.TranslateFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlin.jvm.java

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var selectedLang1: String = "English"
    private var selectedLang2: String = "Urdu"
    private var selectedLang1Code: String="en"
    private var selectedLang2Code: String="ur"




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE



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


        binding.engLangBtn.setOnClickListener {

            val languageFragment = LanguageFragment()
            languageFragment.arguments = Bundle().apply {
                putString("requestFor", "button1")
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, languageFragment) // container in your activity layout
                .addToBackStack(null) // optional: adds to back stack
                .commit()
        }
        binding.urduLangBtn.setOnClickListener {

            val languageFragment = LanguageFragment()
            languageFragment.arguments = Bundle().apply {
                putString("requestFor", "button2")
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, languageFragment) // container in your activity layout
                .addToBackStack(null) // optional: adds to back stack
                .commit()
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        binding.customSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), "Switch ON", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Switch OFF", Toast.LENGTH_SHORT).show()
            }
        }
        binding.settingImageView.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }
        binding.transTV.setOnClickListener {
            val translateFragment = TranslateFragment()


            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, translateFragment) // container in your activity layout
                .addToBackStack(null) // optional: adds to back stack
                .commit()

        }
        binding.rotateIcon.setOnClickListener {

            val tempLang = binding.lang1TV.text
            binding.lang1TV.text = binding.lang2TV.text
            binding.lang2TV.text = tempLang

            val tempLangCode=selectedLang1Code
            selectedLang1Code=selectedLang2Code
            selectedLang2Code=tempLangCode




        }

        binding.transBtn.setOnClickListener {
            val sourceText = binding.textTrans.text.toString().trim()

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
                                openTranslateFragment(sourceText, translatedText)
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



        binding.textTrans.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrBlank()

                val iconRes = if (hasText) {
                    R.drawable.trans_svg  // your translate icon
                } else {
                    R.drawable.voice_icon  // your mic icon
                }

                binding.transBtn.setImageResource(iconRes)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.lang1TV.text = selectedLang1
        binding.lang2TV.text = selectedLang2


        return root
    }
    private fun openTranslateFragment(originalText: String, translatedText: String) {
        val fragment = TranslateFragment.newInstance(originalText, translatedText)
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}