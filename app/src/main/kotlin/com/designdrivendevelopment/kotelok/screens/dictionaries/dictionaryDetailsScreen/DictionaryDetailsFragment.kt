package com.designdrivendevelopment.kotelok.screens.dictionaries.dictionaryDetailsScreen

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.screens.dictionaries.DefinitionClickListener
import com.designdrivendevelopment.kotelok.screens.dictionaries.definitionDetailsScreen.DefinitionDetailsFragment
import com.designdrivendevelopment.kotelok.screens.screensUtils.FragmentResult
import com.designdrivendevelopment.kotelok.screens.screensUtils.MarginItemDecoration
import com.designdrivendevelopment.kotelok.screens.screensUtils.PlaySoundBtnClickListener
import com.designdrivendevelopment.kotelok.screens.screensUtils.SwipeToDelete
import com.designdrivendevelopment.kotelok.screens.screensUtils.TtsPrefs
import com.designdrivendevelopment.kotelok.screens.screensUtils.getScrollPosition
import com.designdrivendevelopment.kotelok.screens.screensUtils.hideKeyboard
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

@Suppress("TooManyFunctions")
class DictionaryDetailsFragment :
    Fragment(),
    TextToSpeech.OnInitListener,
    PlaySoundBtnClickListener,
    DefinitionClickListener {
    private var scrollPosition = SCROLL_START_POSITION
    private var textToSpeech: TextToSpeech? = null
    private var wordDefinitionsList: RecyclerView? = null
    private var addFab: FloatingActionButton? = null
    private var placeholder: TextView? = null
    private var viewModel: DictDetailsViewModel? = null
    private var dictId: Long? = null
    private var searchQuery = ""
    private val currentDefinitions: MutableList<WordDefinition> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dictionary_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchQuery = savedInstanceState?.getString(SEARCH_QUERY_KEY) ?: ""
        setHasOptionsMenu(true)
        initViews(view)

        val dictionaryId = arguments?.getLong(DICT_ID_KEY, NOT_EXIST_DICT_ID) ?: NOT_EXIST_DICT_ID
        val label = arguments?.getString(DICT_LABEL_KEY) ?: getString(R.string.app_name)
        dictId = dictionaryId
        setupListeners(dictionaryId)

        scrollPosition = savedInstanceState?.getInt(SCROLL_POS_KEY) ?: SCROLL_START_POSITION
        val activity = requireActivity()
        activity.title = label
        val context = requireContext()
        val adapter = createAdapter(context, emptyList())
        setupWordDefinitionsList(wordDefinitionsList, context, adapter, scrollPosition)

        textToSpeech = TextToSpeech(context, this)

        val factory = DictDetailsViewModelFactory(
            dictionaryId,
            (activity.application as KotelokApplication)
                .appComponent.dictDefinitionsRepository,
            (activity.application as KotelokApplication)
                .appComponent.sharedWordDefProvider
        )
        viewModel = ViewModelProvider(this, factory)[DictDetailsViewModel::class.java]
        setupFragmentViewModel(adapter)
        setupSwipeToDelete()
    }

    override fun onStop() {
        super.onStop()
        viewModel?.deleteDictionaries()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCROLL_POS_KEY, scrollPosition)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearViews()
        viewModel = null
        currentDefinitions.clear()
        textToSpeech = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        if (searchQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(searchQuery, false)
        }
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchQuery = newText.orEmpty()
                    if (newText.orEmpty().isEmpty()) {
                        wordDefinitionsList?.scrollToPosition(SCROLL_START_POSITION)
                    }
                    viewModel?.filter(newText.orEmpty())
                    return true
                }
            }
        )
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(TtsPrefs.locale)
            textToSpeech?.setPitch(TtsPrefs.STANDARD_PITCH)
            textToSpeech?.setSpeechRate(TtsPrefs.STANDARD_SPEECH_RATE)
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                Log.d("TTS", "Lang is not available")
            }
        } else {
            Log.d("TTS", "TTS init error")
        }
    }

    override fun onPlayBtnClick(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
    }

    override fun onClickToDefinition(wordDefinition: WordDefinition) {
        openDefinitionDetails(wordDefinition)
    }

    private fun openDefinitionDetails(definition: WordDefinition? = null) {
        viewModel?.setDisplayedDefinition(definition)
        val bundle = Bundle().apply {
            putLong(FragmentResult.DictionariesTab.RESULT_DICT_ID_KEY, dictId!!)
            putInt(
                FragmentResult.DictionariesTab.RESULT_SAVE_MODE_KEY,
                DefinitionDetailsFragment.SAVE_MODE_UPDATE
            )
        }
        setFragmentResult(FragmentResult.DictionariesTab.OPEN_DEF_DETAILS_FRAGMENT_KEY, bundle)
    }

    private fun setupSwipeToDelete() {
        val onItemSwipedToDelete = { positionForRemove: Int ->
            val removedDefinition = currentDefinitions[positionForRemove]
            currentDefinitions.removeAt(positionForRemove)
            viewModel?.deleteDictionary(positionForRemove)
            showRestoreItemSnackbar(positionForRemove, removedDefinition)
        }
        val swipeToDeleteCallback = SwipeToDelete(onItemSwipedToDelete)
        ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(wordDefinitionsList)
    }

    private fun showRestoreItemSnackbar(position: Int, definition: WordDefinition) {
        Snackbar.make(view!!, "Словарь удален", Snackbar.LENGTH_LONG)
            .setAction("Отмена") {
                currentDefinitions.add(position, definition)
                viewModel?.restoreDictionary(position, definition)
            }.show()
    }

    private fun setupWordDefinitionsList(
        wordDefinitionsList: RecyclerView?,
        context: Context,
        adapter: WordDefinitionsAdapter,
        position: Int
    ) {
        val displayHeight = getDisplayHeight(context)
        val layoutManager = createLayoutManager(context)
        val marginItemDecoration = MarginItemDecoration(
            marginVertical = 10,
            marginHorizontal = 12,
            marginBottomInPx = displayHeight / DISPLAY_PARTS_NUMBER
        )
        wordDefinitionsList?.adapter = adapter
        wordDefinitionsList?.layoutManager = layoutManager
        wordDefinitionsList?.scrollToPosition(position)
        wordDefinitionsList?.addItemDecoration(marginItemDecoration)
    }

    private fun setupFragmentViewModel(adapter: WordDefinitionsAdapter) {
        viewModel?.dictionaryDefinitions?.observe(this) { definitions ->
            placeholder?.isVisible = definitions.isEmpty()
            currentDefinitions.clear()
            currentDefinitions.addAll(definitions)
            onDefinitionsChanged(definitions, adapter)
        }
    }

    private fun createAdapter(
        context: Context,
        definitions: List<WordDefinition>
    ): WordDefinitionsAdapter {
        return WordDefinitionsAdapter(context, this, this, definitions)
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

    private fun setupListeners(dictionaryId: Long) {
        addFab?.setOnClickListener {
            setFragmentResult(
                FragmentResult.DictionariesTab.OPEN_LOOKUP_WORD_DEF_FRAGMENT_KEY,
                Bundle().apply { putLong(RESULT_DATA_KEY, dictionaryId) }
            )
        }
        val onScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        scrollPosition = recyclerView.getScrollPosition<LinearLayoutManager>()
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        recyclerView.hideKeyboard()
                    }
                }
            }
        }
        wordDefinitionsList?.addOnScrollListener(onScrollListener)
    }

    private fun getDisplayHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
            windowManager.defaultDisplay.height
        } else {
            windowManager.currentWindowMetrics.bounds.height()
        }
    }

    private fun initViews(view: View) {
        addFab = view.findViewById(R.id.open_lookup_fragment_btn)
        wordDefinitionsList = view.findViewById(R.id.word_definitions_list)
        placeholder = view.findViewById(R.id.placeholder)
    }

    private fun clearViews() {
        wordDefinitionsList?.clearOnScrollListeners()
        addFab = null
        wordDefinitionsList = null
        placeholder = null
    }

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query_key"
        private const val DISPLAY_PARTS_NUMBER = 4
        private const val SCROLL_START_POSITION = 0
        private const val NOT_EXIST_DICT_ID = 0L
        private const val SCROLL_POS_KEY = "position"
        const val RESULT_DATA_KEY = "result_data_key"
        const val DICT_ID_KEY = "dictionary_id"
        const val DICT_LABEL_KEY = "dictionary_label"

        @JvmStatic
        fun newInstance(dictionaryId: Long, label: String) =
            DictionaryDetailsFragment().apply {
                arguments = Bundle().apply {
                    putLong(DICT_ID_KEY, dictionaryId)
                    putString(DICT_LABEL_KEY, label)
                }
            }
    }
}
