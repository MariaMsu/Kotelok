package com.designdrivendevelopment.kotelok.screens.dictionaries.dictionariesScreen

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication
import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.screens.screensUtils.FragmentResult
import com.designdrivendevelopment.kotelok.screens.screensUtils.MarginItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

@Suppress("TooManyFunctions")
class DictionariesFragment :
    Fragment(),
    DictionaryClickListener,
    IsFavoriteListener,
    LearnButtonListener {
    private var dictionariesList: RecyclerView? = null
    private var addDictionaryFab: FloatingActionButton? = null
    private var dictionariesViewModel: DictionariesViewModel? = null
    private var searchQuery = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dictionaries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchQuery = savedInstanceState?.getString(SEARCH_QUERY_KEY) ?: ""

        setHasOptionsMenu(true)
        initViews(view)
        val activity = requireActivity()
        activity.title = getString(R.string.title_dictionaries)
        val context = requireContext()
        val factory = DictionariesViewModelFactory(
            (activity.application as KotelokApplication)
                .appComponent.dictionariesRepository
        )
        dictionariesViewModel = ViewModelProvider(this, factory)[DictionariesViewModel::class.java]

        val adapter = DictionariesAdapter(
            context,
            dictionaryClickListener = this,
            isFavoriteListener = this,
            learnButtonListener = this,
        )
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        setupDictionariesList(dictionariesList, adapter, layoutManager)
        setupViewModel(dictionariesViewModel, adapter)
        setupListeners()
    }

    override fun onStop() {
        super.onStop()
        dictionariesViewModel?.saveIsFavoriteStatus()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearViews()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        if (searchQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(searchQuery, true)
        }
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchQuery = newText.orEmpty()
                    if (newText.orEmpty().isEmpty()) {
                        dictionariesList?.scrollToPosition(SCROLL_START_POSITION)
                    }
                    dictionariesViewModel?.filter(newText.orEmpty())
                    return true
                }
            }
        )
    }

    override fun onDictionaryClicked(dictionary: Dictionary) {
        val bundle = Bundle().apply {
            putLong(DICT_ID_KEY, dictionary.id)
            putString(DICT_LABEL_KEY, dictionary.label)
        }
        setFragmentResult(FragmentResult.DictionariesTab.OPEN_DICTIONARY_KEY, bundle)
    }

    override fun onIsFavoriteChanged(dictionaryId: Long, isFavorite: Boolean) {
        dictionariesViewModel?.updateIsFavoriteStatus(dictionaryId, isFavorite)
    }

    override fun onLearnClicked(dictionaryId: Long) {
        if (dictionariesViewModel?.dictionaries?.value?.first { it.id == dictionaryId }?.size == 0) {
            Snackbar.make(
                view!!,
                "Чтобы изучать словарь, добавьте в него хотя бы одно определение",
                Snackbar.LENGTH_LONG
            ).show()
            return
        }
        val bundle = Bundle().apply { putLong(DICT_ID_KEY, dictionaryId) }
        setFragmentResult(FragmentResult.DictionariesTab.OPEN_TRAINERS_DIALOG_KEY, bundle)
    }

    private fun setupViewModel(viewModel: DictionariesViewModel?, adapter: DictionariesAdapter) {
        viewModel?.dictionaries?.observe(this) { dictionaries ->
            adapter.submitList(dictionaries)
        }
    }

    private fun setupDictionariesList(
        dictionariesList: RecyclerView?,
        adapter: DictionariesAdapter,
        layoutManager: LinearLayoutManager
    ) {
        dictionariesList?.addItemDecoration(
            MarginItemDecoration(
                marginVertical = 10,
                marginHorizontal = 12
            )
        )
        dictionariesList?.adapter = adapter
        dictionariesList?.layoutManager = layoutManager
    }

    private fun setupListeners() {
        addDictionaryFab?.setOnClickListener {
            setFragmentResult(FragmentResult.DictionariesTab.OPEN_ADD_DICTIONARY_KEY, Bundle())
        }
    }

    private fun initViews(view: View) {
        dictionariesList = view.findViewById(R.id.dictionaries_list)
        addDictionaryFab = view.findViewById(R.id.add_dictionary_button)
    }

    private fun clearViews() {
        dictionariesList = null
        addDictionaryFab = null
    }

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query_key"
        private const val SIZE_EMPTY = 0
        private const val SCROLL_START_POSITION = 0
        const val DICT_ID_KEY = "dictionary_id_bundle_key"
        const val DICT_LABEL_KEY = "dictionary_label_bundle_key"

        @JvmStatic
        fun newInstance() = DictionariesFragment()
    }
}
