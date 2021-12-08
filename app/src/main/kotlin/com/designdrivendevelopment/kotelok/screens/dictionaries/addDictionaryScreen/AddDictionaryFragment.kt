package com.designdrivendevelopment.kotelok.screens.dictionaries.addDictionaryScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication
import com.designdrivendevelopment.kotelok.screens.screensUtils.FragmentResult
import com.designdrivendevelopment.kotelok.screens.screensUtils.MarginItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions")
class AddDictionaryFragment : Fragment(), DefinitionSelectionListener {
    private var saveDictionaryFab: FloatingActionButton? = null
    private var dictionaryLabelField: TextInputLayout? = null
    private var definitionsList: RecyclerView? = null
    private var addDictViewModel: AddDictViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_dictionary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        initViews(view)
        val activity = requireActivity()
        activity.title = getString(R.string.title_add_new_dictionary)
        val context = requireContext()

        val adapter = SelectableDefsAdapter(context, this, emptyList())
        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        setupSelectedDefinitionsList(adapter, layoutManager, definitionsList)

        val factory = AddDictViewModelFactory(
            (activity.application as KotelokApplication)
                .appComponent.dictionariesRepository,
            (activity.application as KotelokApplication)
                .appComponent.dictDefinitionsRepository,
        )
        addDictViewModel = ViewModelProvider(this, factory)[AddDictViewModel::class.java]
        setupViewModel(addDictViewModel, adapter)
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        addDictViewModel = null
        clearViews()
    }

    override fun onDefinitionSelectionChanged(selectedDefinition: SelectableWordDefinition) {
        addDictViewModel?.changeItemSelection(selectedDefinition.def, selectedDefinition.isSelected)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_search, menu)
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.orEmpty().isEmpty()) {
                        definitionsList?.scrollToPosition(SCROLL_START_POSITION)
                    }
                    addDictViewModel?.filter(newText.orEmpty())
                    return true
                }
            }
        )
    }

    private fun setupViewModel(viewModel: AddDictViewModel?, adapter: SelectableDefsAdapter) {
        viewModel?.allDefinitions?.observe(this) { definitionsList ->
            onDefinitionsChanged(definitionsList, adapter)
        }
    }

    private fun onDefinitionsChanged(
        newDefinitions: List<SelectableWordDefinition>,
        adapter: SelectableDefsAdapter
    ) {
        val diffCallback = SelectableWordDefDiffCallback(
            newList = newDefinitions,
            oldList = adapter.definitions
        )
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(adapter)
        adapter.definitions = newDefinitions
    }

    private fun setupListeners() {
        saveDictionaryFab?.setOnClickListener {
            dictionaryLabelField?.error = null
            val label = dictionaryLabelField?.editText?.text?.toString()

            if (label.isNullOrEmpty()) {
                dictionaryLabelField?.error = getString(R.string.error_dict_label_field)
                return@setOnClickListener
            } else {
                lifecycleScope.launch(Dispatchers.Main) {
                    val newDictId = addDictViewModel?.addDictionary(label)
                        ?: NOT_EXIST_DICT_ID
                    val bundle = Bundle().apply {
                        putLong(DICT_ID_KEY, newDictId)
                        putString(DICT_LABEL_KEY, label)
                    }
                    setFragmentResult(FragmentResult.DictionariesTab.OPEN_NEW_DICTIONARY_KEY, bundle)
                }
            }
        }
    }

    private fun setupSelectedDefinitionsList(
        adapter: SelectableDefsAdapter,
        layoutManager: LinearLayoutManager,
        definitionsList: RecyclerView?
    ) {
        definitionsList?.addItemDecoration(
            MarginItemDecoration(
                marginVertical = 10,
                marginHorizontal = 12
            )
        )
        definitionsList?.adapter = adapter
        definitionsList?.layoutManager = layoutManager
    }

    private fun initViews(view: View) {
        saveDictionaryFab = view.findViewById(R.id.save_dictionary_fab)
        dictionaryLabelField = view.findViewById(R.id.enter_dictionary_label)
        definitionsList = view.findViewById(R.id.added_dictionaries_list)
    }

    private fun clearViews() {
        saveDictionaryFab = null
        dictionaryLabelField = null
        definitionsList = null
    }

    companion object {
        private const val SCROLL_START_POSITION = 0
        private const val NOT_EXIST_DICT_ID = 0L
        const val DICT_ID_KEY = "dictionary_id_key"
        const val DICT_LABEL_KEY = "dictionary_label_key"

        @JvmStatic
        fun newInstance() = AddDictionaryFragment()
    }
}
