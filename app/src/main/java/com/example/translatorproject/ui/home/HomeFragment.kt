package com.example.translatorproject.ui.home

import android.content.Intent
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
import com.example.translatorproject.SettingActivity
import com.example.translatorproject.databinding.FragmentHomeBinding
import com.example.translatorproject.ui.language.LanguageFragment
import com.example.translatorproject.ui.translate.TranslateFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.translatorproject.utils.Event


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

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

        // ViewModel setup
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

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
                openTranslateFragment(binding.textTrans.text.toString(), translated)
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
                showToast("Enter text to translate")
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

    private fun openTranslateFragment(originalText: String, translatedText: String) {
        val fragment = TranslateFragment.newInstance(originalText, translatedText)
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


}
