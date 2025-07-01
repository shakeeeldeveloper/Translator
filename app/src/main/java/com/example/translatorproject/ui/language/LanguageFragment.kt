package com.example.translatorproject.ui.language

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.translatorproject.R
import com.example.translatorproject.adapter.LanguageAdapter
import com.example.translatorproject.databinding.FragmentLanguageBinding
import com.example.translatorproject.model.LanguageModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale



class LanguageFragment : Fragment() {

    private var _binding: FragmentLanguageBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: LanguageAdapter
    private var selectedLanguage: LanguageModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
        val launchedBy = arguments?.getString("requestFor") ?: ""


        val toolbar = requireView().findViewById<Toolbar>(R.id.toolbar)
        val titleText = requireView().findViewById<TextView>(R.id.titleText) // only if you have it

        toolbar.setNavigationIcon(R.drawable.back_icon)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        toolbar.inflateMenu(R.menu.language_menu)

        val searchItem = toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(Color.BLACK)
        searchEditText.setHintTextColor(Color.GRAY)

        // Hide "Language" when search opens
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                titleText?.visibility = View.GONE
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                titleText?.visibility = View.VISIBLE
                return true
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })


        val languages1=getAllLanguages()

// Access done menu item
        val doneMenuItem = toolbar.menu.findItem(R.id.action_done)
        val doneView = doneMenuItem.actionView?.findViewById<TextView>(R.id.doneText)


        doneView?.setOnClickListener {
            selectedLanguage?.let {
                //Toast.makeText(requireContext(), "Selected: ${it.name}", Toast.LENGTH_SHORT).show()

                val result = Bundle().apply {
                    Log.d("LANG_DEBUG", "Done View Exists? ${launchedBy}")

                    putString("selectedLanguage", it.name)
                    putString("LanguageCode",it.code)
                    putString("requestFor", launchedBy) // pass back who launched

                }

                // Send result back to parent/previous fragment
                parentFragmentManager.setFragmentResult("languageRequestKey", result)

                // Close this fragment
                parentFragmentManager.popBackStack()
            }
        }



        adapter = LanguageAdapter(languages1) { selected ->
            selectedLanguage = selected
            doneMenuItem.isVisible = true // Show the "Done" button
        }

        binding.languageRecyclerView.adapter = adapter
        binding.languageRecyclerView.layoutManager = LinearLayoutManager(requireContext())


    }
    fun getAllLanguages(): List<LanguageModel> {
        val locales = Locale.getAvailableLocales()
        val languageMap = mutableMapOf<String, String>() // code -> name

        for (locale in locales) {
            val code = locale.language
            val name = locale.getDisplayLanguage(Locale.ENGLISH).trim()

            if (code.isNotEmpty() && name.isNotEmpty()) {
                languageMap[code] = name // This will avoid duplicate codes
            }
        }

        return languageMap
            .map { (code, name) -> LanguageModel(code = code, name = name) }
            .sortedBy { it.name }
    }

 /*   fun getAllCountryLanguages(): List<LanguageModel> {
        val locales = Locale.getAvailableLocales()
        val languageSet = mutableSetOf<String>()

        for (locale in locales) {
            val language = locale.getDisplayLanguage(Locale.ENGLISH).trim()
            if (language.isNotEmpty()) {
                languageSet.add(language)
            }
        }

        return languageSet
            .distinct()
            .sorted()
            .map { LanguageModel(name = it) }
    }*/



    /*
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            */
/*val toolbar = requireView().findViewById<Toolbar>(R.id.toolbar)
        val textDone = binding.root.findViewById<TextView>(R.id.doneButton)
        val titleText=binding.root.findViewById<TextView>(R.id.titleText)

        // Setup back icon and click
        toolbar.setNavigationIcon(R.drawable.back_icon)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Inflate search menu
        toolbar.inflateMenu(R.menu.language_menu)
        val searchItem = toolbar.menu.findItem(R.id.action_search)

        // Handle search filtering
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView

// Fix input text color
        val searchText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchText.setTextColor(Color.BLACK)
        searchText.setHintTextColor(Color.GRAY)


// Listen for expand/collapse of SearchView
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                titleText.visibility = View.GONE // 👈 Hide "Language" when search expands
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                titleText.visibility = View.VISIBLE // 👈 Show it again when collapsed
                return true
            }
        })
*//*

        val toolbar = requireView().findViewById<Toolbar>(R.id.toolbar)
        val doneButton = requireView().findViewById<TextView>(R.id.doneButton)
        val titleText = requireView().findViewById<TextView>(R.id.titleText)

        toolbar.setNavigationIcon(R.drawable.back_icon)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        toolbar.inflateMenu(R.menu.language_menu)
        val searchItem = toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(Color.BLACK)
        searchEditText.setHintTextColor(Color.GRAY)

// Hide title when search expands
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                titleText.visibility = View.GONE
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                titleText.visibility = View.VISIBLE
                return true
            }
        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        // Language list
        val languages = listOf(
            LanguageModel("English"),
            LanguageModel("Urdu"),
            LanguageModel("Arabic"),
            LanguageModel("French"),
            LanguageModel("German"),
            LanguageModel("Spanish"),
            LanguageModel("Hindi"),
            LanguageModel("Chinese"),
            LanguageModel("Turkish")
        )

        // Setup adapter with callback
        adapter = LanguageAdapter(languages) { selected ->
            selectedLanguage = selected
            doneButton.visibility = View.VISIBLE
        }

        binding.languageRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.languageRecyclerView.adapter = adapter

        // Handle Done button click
        doneButton.setOnClickListener {
            selectedLanguage?.let {
                Toast.makeText(requireContext(), "Selected: ${it.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }
*/

/*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        val toolbar = requireView().findViewById<Toolbar>(R.id.toolbar)
        val textDone = toolbar.findViewById<TextView>(R.id.doneButton)

        // Setup toolbar navigation
        toolbar.setNavigationIcon(R.drawable.back_icon)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Set menu with search item
        toolbar.inflateMenu(R.menu.language_menu)
        val searchItem = toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
        val languages = listOf(
            LanguageModel("English"),
            LanguageModel("Urdu"),
            LanguageModel("Arabic"),
            LanguageModel("French"),
            LanguageModel("German"),
            LanguageModel("Spanish"),
            LanguageModel("Hindi"),
            LanguageModel("Chinese"),
            LanguageModel("Turkish")
        )


        adapter = LanguageAdapter(languages) { selected ->
            selectedLanguage = selected
            textDone.visibility = View.VISIBLE
        }

        binding.languageRecyclerView.adapter = adapter
        binding.languageRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        textDone.setOnClickListener {
            selectedLanguage?.let {
                Toast.makeText(requireContext(), "Selected: ${it.name}", Toast.LENGTH_SHORT).show()
            }
        }

    }
*/

/*
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Search languages..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_done -> {
                    selectedLanguage?.let {
                        Toast.makeText(requireContext(), "Selected: ${it.name}", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }
    }
*/

/*
    private fun setupRecyclerView() {
        val languages = listOf(
            LanguageModel("English"),
            LanguageModel("Urdu"),
            LanguageModel("Arabic"),
            LanguageModel("French"),
            LanguageModel("German"),
            LanguageModel("Spanish"),
            LanguageModel("Hindi"),
            LanguageModel("Chinese"),
            LanguageModel("Turkish")
        )

       */
/* adapter = LanguageAdapter(languages) { selected ->
            selectedLanguage = selected
          //  showDoneButton(true)
        }

        binding.languageRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.languageRecyclerView.adapter = adapter*//*

        // RecyclerView adapter
        adapter = LanguageAdapter(languages) { selected ->
            selectedLanguage = selected
            textDone.visibility = View.VISIBLE
        }

        binding.languageRecyclerView.adapter = adapter
        binding.languageRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        textDone.setOnClickListener {
            selectedLanguage?.let {
                Toast.makeText(requireContext(), "Selected: ${it.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }
*/
/*
    private fun showDoneButton(visible: Boolean) {
        binding.toolbar.menu.findItem(R.id.action_done)?.isVisible = visible
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
