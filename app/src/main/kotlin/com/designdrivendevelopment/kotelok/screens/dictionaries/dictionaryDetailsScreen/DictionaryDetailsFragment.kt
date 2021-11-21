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
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.screens.screensUtils.FragmentResult
import com.designdrivendevelopment.kotelok.screens.screensUtils.MarginItemDecoration
import com.designdrivendevelopment.kotelok.screens.screensUtils.hideKeyboard
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

@Suppress("TooManyFunctions")
class DictionaryDetailsFragment : Fragment(), TextToSpeech.OnInitListener, PlaySoundBtnClickListener {
    private var scrollPosition = SCROLL_START_POSITION
    private var textToSpeech: TextToSpeech? = null
    private var wordDefinitionsList: RecyclerView? = null
    private var addFab: FloatingActionButton? = null
    private var viewModel: DictDetailsViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dictionary_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initViews(view)
        setupListeners()

        scrollPosition = savedInstanceState?.getInt(SCROLL_POS_KEY) ?: SCROLL_START_POSITION
        val context = requireContext()
        val adapter = createAdapter(context, emptyList())
        setupWordDefinitionsList(wordDefinitionsList, context, adapter, scrollPosition)

        textToSpeech = TextToSpeech(context, this)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_word_def_search, menu)
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val initialDefinitions = viewModel?.dictionaryDefinitions?.value
                        ?: throw NullPointerException("DictDefinitionsViewModel is null")

                    wordDefinitionsList?.adapter?.let { adapter ->
                        val wordDefinitionsAdapter = adapter as WordDefinitionsAdapter

                        if (newText.isNullOrEmpty()) {
                            wordDefinitionsList?.scrollToPosition(SCROLL_START_POSITION)
                            onDefinitionsChanged(initialDefinitions, wordDefinitionsAdapter)
                        } else {
                            val filteredDefinitions = initialDefinitions.filter { definition ->
                                definition.writing.startsWith(newText, ignoreCase = true)
                            }
                            onDefinitionsChanged(filteredDefinitions, wordDefinitionsAdapter)
                        }
                    }
                    return true
                }
            }
        )
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.US)
            textToSpeech?.setPitch(1f)
            textToSpeech?.setSpeechRate(STANDARD_SPEECH_RATE)
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
        return WordDefinitionsAdapter(context, this, definitions)
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

    private fun setupListeners() {
        addFab?.setOnClickListener {
            setFragmentResult(
                FragmentResult.DictionariesTab.OPEN_LOOKUP_WORD_DEF_FRAGMENT_KEY,
                Bundle()
            )
        }
        val onScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        scrollPosition = (recyclerView.layoutManager as LinearLayoutManager)
                            .findFirstVisibleItemPosition()
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
    }

    private fun clearViews() {
        wordDefinitionsList?.clearOnScrollListeners()
        wordDefinitionsList = null
    }

    companion object {
        private const val DISPLAY_PARTS_NUMBER = 4
        private const val STANDARD_SPEECH_RATE = 0.7f
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
