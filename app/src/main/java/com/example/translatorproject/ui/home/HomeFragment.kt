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
import kotlin.jvm.java

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

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

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE

       /* val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        parentFragmentManager.setFragmentResultListener(
            "languageRequestKey",
            viewLifecycleOwner
        ) { requestKey, bundle ->
          //  requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE

            val selectedLanguage = bundle.getString("selectedLanguage")

            selectedLanguage?.let {
                //Toast.makeText(requireContext(), "You selected: $it", Toast.LENGTH_SHORT).show()

                binding.langTV.text = it
            }
            val requestFor = bundle.getString("requestFor")

            when (requestFor) {
                "button1" -> binding.langTV.text = selectedLanguage
                "button2" -> binding.urduLangTV.text = selectedLanguage
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



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}