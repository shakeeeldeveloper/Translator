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

class TranslateFragment : Fragment() {

    private var _binding: FragmentTranslateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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


        binding.textEditTxt.addTextChangedListener(object : TextWatcher {
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


        /* val textView: TextView = binding.textHome
         homeViewModel.text.observe(viewLifecycleOwner) {
             textView.text = it
         }*/
        parentFragmentManager.setFragmentResultListener(
            "languageRequestKey",
            viewLifecycleOwner
        ) { requestKey, bundle ->
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE

            val selectedLanguage = bundle.getString("selectedLanguage")

            /*selectedLanguage?.let {
                //Toast.makeText(requireContext(), "You selected: $it", Toast.LENGTH_SHORT).show()

                binding.langTV.text = it
            }*/
            val requestFor = bundle.getString("requestFor")

            when (requestFor) {
                "button1" -> {binding.lang1TV.text = selectedLanguage
                    binding.langTV.text = selectedLanguage}

                        "button2" -> binding.lang2TV.text = selectedLanguage
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



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}