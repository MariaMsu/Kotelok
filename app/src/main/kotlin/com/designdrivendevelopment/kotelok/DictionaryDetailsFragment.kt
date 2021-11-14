package com.designdrivendevelopment.kotelok

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.entities.WordDefinition

class DictionaryDetailsFragment : Fragment() {
    private var scrollPosition = SCROLL_START_POSITION
    var wordDefinitionsList: RecyclerView? = null
    var viewModel: DictDetailsViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dictionary_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollPosition = savedInstanceState?.getInt(SCROLL_POS_KEY) ?: 0
        initViews(view)

        scrollPosition = savedInstanceState?.getInt(SCROLL_POS_KEY) ?: SCROLL_START_POSITION
        val context = requireContext()
        val adapter = createAdapter(context, emptyList())
        setupWordDefinitionsList(wordDefinitionsList, context, adapter, scrollPosition)

        val dictionaryId = arguments?.getLong(DICT_ID_KEY, NOT_EXIST_DICT_ID) ?: NOT_EXIST_DICT_ID
        val factory = DictDetailsViewModelFactory(
            dictionaryId,
            (requireActivity().application as KotelokApplication)
                .appComponent.dictDefinitionsRepository
        )
        viewModel = setupFragmentViewModel(this, factory, adapter)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCROLL_POS_KEY, scrollPosition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearViews()
    }

    private fun setupWordDefinitionsList(
        wordDefinitionsList: RecyclerView?,
        context: Context,
        adapter: WordDefinitionsAdapter,
        position: Int
    ) {
        val layoutManager = createLayoutManager(context)
        val marginItemDecoration = MarginItemDecoration(
            marginVertical = 10,
            marginHorizontal = 12
        )
        wordDefinitionsList?.adapter = adapter
        wordDefinitionsList?.layoutManager = layoutManager
        wordDefinitionsList?.scrollToPosition(position)
        wordDefinitionsList?.addItemDecoration(marginItemDecoration)
    }

    private fun setupFragmentViewModel(
        fragment: Fragment,
        factory: DictDetailsViewModelFactory,
        adapter: WordDefinitionsAdapter
    ): DictDetailsViewModel {
        return ViewModelProvider(fragment, factory)[DictDetailsViewModel::class.java].apply {
            dictionaryDefinitions.observe(fragment) { definitions ->
                onDefinitionsChanged(definitions, adapter)
            }
        }
    }

    private fun createAdapter(
        context: Context,
        definitions: List<WordDefinition>
    ): WordDefinitionsAdapter {
        return WordDefinitionsAdapter(context, definitions)
    }

    private fun createLayoutManager(context: Context): LinearLayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun onDefinitionsChanged(
        newDefinitions: List<WordDefinition>,
        adapter: WordDefinitionsAdapter
    ) {
        val diffCallback = DefinitionsDiffCallback(
            oldDefinitionsList = adapter.wordDefinitions,
            newDefinitionsList = newDefinitions
        )
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(adapter)
        adapter.wordDefinitions = newDefinitions
    }

    private fun initViews(view: View) {
        wordDefinitionsList = view.findViewById(R.id.word_definitions_list)

        val onScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scrollPosition = (recyclerView.layoutManager as LinearLayoutManager)
                        .findFirstVisibleItemPosition()
                }
            }
        }
        wordDefinitionsList?.addOnScrollListener(onScrollListener)
    }

    private fun clearViews() {
        wordDefinitionsList?.clearOnScrollListeners()
        wordDefinitionsList = null
    }

    companion object {
        private const val SCROLL_START_POSITION = 0
        private const val NOT_EXIST_DICT_ID = 0L
        private const val SCROLL_POS_KEY = "position"
        const val DICT_ID_KEY = "dictionary_id"

        @JvmStatic
        fun newInstance(dictionaryId: Long) =
            DictionaryDetailsFragment().apply {
                arguments = Bundle().apply {
                    putLong(DICT_ID_KEY, dictionaryId)
                }
            }
    }
}
