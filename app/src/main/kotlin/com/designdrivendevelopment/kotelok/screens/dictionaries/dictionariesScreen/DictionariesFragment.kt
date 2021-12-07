package com.designdrivendevelopment.kotelok.screens.dictionaries.dictionariesScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication
import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.screens.screensUtils.FragmentResult
import com.designdrivendevelopment.kotelok.screens.screensUtils.MarginItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DictionariesFragment : Fragment(), DictionaryClickListener {
    private var dictionariesList: RecyclerView? = null
    private var addDictionaryFab: FloatingActionButton? = null
    private var dictionariesViewModel: DictionariesViewModel? = null

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

        initViews(view)
        val activity = requireActivity()
        val context = requireContext()
        val factory = DictionariesViewModelFactory(
            (activity.application as KotelokApplication)
                .appComponent.dictionariesRepository
        )
        dictionariesViewModel = ViewModelProvider(this, factory)[DictionariesViewModel::class.java]

        val adapter = DictionariesAdapter(context, this, emptyList())
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        setupDictionariesList(dictionariesList, adapter, layoutManager)
        setupViewModel(dictionariesViewModel, adapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearViews()
    }

    override fun onDictionaryClicked(dictionary: Dictionary) {
        val bundle = Bundle().apply { putLong(DICT_ID_KEY, dictionary.id) }
        setFragmentResult(FragmentResult.DictionariesTab.OPEN_DICTIONARY_KEY, bundle)
    }

    private fun setupViewModel(viewModel: DictionariesViewModel?, adapter: DictionariesAdapter) {
        viewModel?.dictionaries?.observe(this) { dictionaries ->
            onDictionariesChanged(dictionaries, adapter)
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

    private fun onDictionariesChanged(
        newDictionaries: List<Dictionary>,
        adapter: DictionariesAdapter
    ) {
        val diffCallback = DictionariesDiffCallback(
            oldDictionaries = adapter.dictionaries,
            newDictionaries = newDictionaries
        )
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(adapter)
        adapter.dictionaries = newDictionaries
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
        const val DICT_ID_KEY = "dictionary_id_bundle_key"

        @JvmStatic
        fun newInstance() = DictionariesFragment()
    }
}
