package com.example.translatorproject.ui.chat

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.translatorproject.R
import com.example.translatorproject.databinding.FragmentChatBinding
import com.example.translatorproject.ui.language.LanguageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.nl.languageid.LanguageIdentification

import java.util.Locale

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var selectedLang1: String = "Urdu"
    private var selectedLang2: String = "English"
    private var selectedLang1Code: String="ur"
    private var selectedLang2Code: String="en"
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent
    private var listeningDialog: AlertDialog? = null
    private var micOverlay: View? = null
    private lateinit var tts: TextToSpeech






    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val chatViewModel =
            ViewModelProvider(this).get(ChatViewModel::class.java)

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val root: View = binding.root
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE

       /*// val textView: TextView = binding.textDashboard
        chatViewModel.text.observe(viewLifecycleOwner) {
            //textView.text = it
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
                    Toast.makeText(requireContext(),selectedLang1.toString(), Toast.LENGTH_SHORT).show()
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
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now…")
        }
        binding.voiceIcon.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            } else {
                startVoiceInput()
            }
        }
        binding.speakLayout.setOnClickListener {
            if (binding.lang1TV.text!=null)
            {
                textToSpeech(binding.person1EditText.text.toString(),selectedLang1Code)
            }
        }



        binding.lang1Btn.setOnClickListener {

            val languageFragment = LanguageFragment()
            languageFragment.arguments = Bundle().apply {
                putString("requestFor", "button1")
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, languageFragment) // container in your activity layout
                .addToBackStack(null) // optional: adds to back stack
                .commit()
        }
        binding.lang2Btn.setOnClickListener {

            val languageFragment = LanguageFragment()
            languageFragment.arguments = Bundle().apply {
                putString("requestFor", "button2")
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, languageFragment) // container in your activity layout
                .addToBackStack(null) // optional: adds to back stack
                .commit()
        }



        binding.lang1TV.text = selectedLang1
        binding.lang2TV.text = selectedLang2

        return root
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

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                dismissMicOverlay()
            }

            override fun onError(error: Int) {
                dismissMicOverlay()
                Toast.makeText(requireContext(), "Speech error: $error", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                dismissMicOverlay()
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.let {
                    val spokenText = it.joinToString(", ")
                    view?.findViewById<EditText>(R.id.person2EditText)?.setText(spokenText)
                    val languageIdentifier = LanguageIdentification.getClient()

                    languageIdentifier.identifyLanguage(spokenText)
                        .addOnSuccessListener { languageCode ->
                            if (languageCode == "und") {
                                Toast.makeText(context, "Cannot identify language", Toast.LENGTH_SHORT).show()
                            } else {
                                // Use this languageCode as source language for TranslatorOptions
                                val options = TranslatorOptions.Builder()
                                    .setSourceLanguage(languageCode)
                                    .setTargetLanguage("ur") // or your selected target
                                    .build()

                                if (selectedLang2Code != languageCode) {

                                //Toast.makeText(context, "Change $languageCode", Toast.LENGTH_SHORT) .show()
                                translate(spokenText, "person2", languageCode, selectedLang2Code)
                            }
                                else {
                                    translate(spokenText,"person1",selectedLang2Code,selectedLang1Code)
                                   // Toast.makeText(context, "No. Change", Toast.LENGTH_SHORT).show()
                                }

                                // proceed with translation
                            }
                        }


                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(speechIntent)
    }
    fun translate(sourceText: String, sourcePerson: String, srcLang: String,trLang: String){
        if (sourceText.isEmpty()) {
            Toast.makeText(requireContext(), "Enter text to translate", Toast.LENGTH_SHORT).show()
            return
        }

        val sourceMLLang = TranslateLanguage.fromLanguageTag(srcLang)
        val targetMLLang = TranslateLanguage.fromLanguageTag(trLang)


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
                            if (sourcePerson.equals("person2")) {
                                binding.person2EditText.setText(translatedText)
                                translate(sourceText,"person1",selectedLang2Code,selectedLang1Code)
                            }

                            else
                            binding.person1EditText.setText(translatedText)

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


    /*  private fun startVoiceInput() {
          showListeningDialog()

          speechRecognizer.setRecognitionListener(object : RecognitionListener {
              override fun onReadyForSpeech(params: Bundle?) {}
              override fun onBeginningOfSpeech() {
                  // Already showing dialog
              }

              override fun onRmsChanged(rmsdB: Float) {}
              override fun onBufferReceived(buffer: ByteArray?) {}
              override fun onEndOfSpeech() {
                  dismissListeningDialog()
              }

              override fun onError(error: Int) {
                  dismissListeningDialog()
                  Toast.makeText(requireContext(), "Speech error: $error", Toast.LENGTH_SHORT).show()
              }

              override fun onResults(results: Bundle?) {
                  dismissListeningDialog()
                  val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                  matches?.let {
                      val spokenText = it[0]
                      view?.findViewById<EditText>(R.id.person2EditText)?.setText(spokenText)
                  }
              }

              override fun onPartialResults(partialResults: Bundle?) {}
              override fun onEvent(eventType: Int, params: Bundle?) {}
          })

          speechRecognizer.startListening(speechIntent)
      }
      private fun showListeningDialog() {
          val builder = AlertDialog.Builder(requireContext())
          builder.setView(layoutInflater.inflate(R.layout.dialog_listening, null))
          builder.setCancelable(false)
          listeningDialog = builder.create()
          listeningDialog?.show()
      }

      private fun dismissListeningDialog() {
          listeningDialog?.dismiss()
      }*/


    override fun onDestroyView() {
        super.onDestroyView()
        speechRecognizer.destroy()
        _binding = null
    }
}