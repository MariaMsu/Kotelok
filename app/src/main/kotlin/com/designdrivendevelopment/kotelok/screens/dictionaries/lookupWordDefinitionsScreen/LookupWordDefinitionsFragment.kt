package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.selection.DefinitionsKeyProvider
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.selection.ItemWithTypeDetailsLookup
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.selection.selectionActionMode.SelectionModeCallBack
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.ItemWithType
import com.designdrivendevelopment.kotelok.screens.screensUtils.MarginItemDecoration
import com.designdrivendevelopment.kotelok.screens.screensUtils.PlaySoundBtnClickListener
import com.designdrivendevelopment.kotelok.screens.screensUtils.TtsPrefs
import com.designdrivendevelopment.kotelok.screens.screensUtils.UiEvent
import com.designdrivendevelopment.kotelok.screens.screensUtils.dpToPx
import com.designdrivendevelopment.kotelok.screens.screensUtils.focusAndShowKeyboard
import com.designdrivendevelopment.kotelok.screens.screensUtils.getScrollPosition
import com.designdrivendevelopment.kotelok.screens.screensUtils.hideKeyboard
import com.designdrivendevelopment.kotelok.screens.screensUtils.objectAnimation
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions")
class LookupWordDefinitionsFragment : Fragment(), PlaySoundBtnClickListener, TextToSpeech.OnInitListener {
    private var loadingProgressBar: ProgressBar? = null
    private var yandexDictHyperlink: TextView? = null
    private var enterWritingTextField: TextInputLayout? = null
    private var lookupButton: Button? = null
    private var resultList: RecyclerView? = null
    private var scrollPosition = 0
    private var addFab: ExtendedFloatingActionButton? = null
    private var textToSpeech: TextToSpeech? = null
    private var tracker: SelectionTracker<String>? = null
    private var actionMode: ActionMode? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_lookup_word_definition,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dictionaryId = arguments?.getLong(DICT_ID_KEY) ?: DEFAULT_DICT_ID

        initViews(view)
        textToSpeech = TextToSpeech(context, this)
        setHasOptionsMenu(true)
        scrollPosition = savedInstanceState?.getInt(SCROLL_POS_KEY) ?: SCROLL_START_POSITION

        val activity = requireActivity() as AppCompatActivity
        activity.title = getString(R.string.add_def_screen_title)

        val context = requireContext()
        val adapter = createAdapter(context, emptyList())
        val factory = LookupViewModelFactory(
            (activity.application as KotelokApplication)
                .appComponent.lookupWordDefRepository,
            (activity.application as KotelokApplication)
                .appComponent.editWordDefinitionsRepository,
            (activity.application as KotelokApplication)
                .appComponent.dictionariesRepository,
            dictionaryId
        )

        val lookupViewModel = setupFragmentViewModel(
            rootView = view,
            activity = activity,
            fragment = this,
            factory = factory,
            adapter = adapter
        )
        setupWordDefinitionsList(resultList, context, adapter)
        setupListeners(lookupViewModel)

        tracker = SelectionTracker.Builder(
            DEFINITIONS_SELECTION_ID,
            resultList!!,
            DefinitionsKeyProvider(adapter),
            ItemWithTypeDetailsLookup(resultList!!),
            StorageStrategy.createStringStorage()
        ).build()
        tracker?.onRestoreInstanceState(savedInstanceState)
        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<String>() {
                override fun onItemStateChanged(key: String, selected: Boolean) {
                    if (key.contains(DefinitionsKeyProvider.HEADER_KEY_SUBSTRING)) {
//                        Костыль, который позволяется избежать крэша из-за того,
//                        что deselect выполнится во время touchEvent`a
                        lifecycleScope.launch {
                            delay(HEADER_DESELECTION_DELAY)
                            tracker?.deselect(key)
                        }
                    } else {
                        lookupViewModel.onItemSelectionChanged(key, selected)
                    }
                }

                override fun onSelectionChanged() {
                    val selectionSize = tracker?.selection?.size()
                    if (selectionSize != null) {
                        lookupViewModel.onSelectionSizeChanged(selectionSize)
                    }
                }

                override fun onSelectionCleared() {
                    lookupViewModel.onSelectionCleared()
                }
            }
        )

        if (savedInstanceState == null) {
            enterWritingTextField?.editText?.focusAndShowKeyboard()
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            enterWritingTextField?.isVisible = true
            lookupButton?.isVisible = true
        } else {
            if (adapter.items.isEmpty()) {
                sendMessage(view, getString(R.string.landscape_input_warning))
            }
            enterWritingTextField?.isVisible = false
            lookupButton?.isVisible = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCROLL_POS_KEY, scrollPosition)
        tracker?.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tracker = null
        textToSpeech = null
        clearViews()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onPlayBtnClick(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
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

    private fun createAdapter(
        context: Context,
        items: List<ItemWithType>
    ): ItemWithTypesAdapter {
        return ItemWithTypesAdapter(items, context, this).apply { setHasStableIds(true) }
    }

    private fun createLayoutManager(context: Context): LinearLayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun setupWordDefinitionsList(
        resultList: RecyclerView?,
        context: Context,
        adapter: ItemWithTypesAdapter,
    ) {
        val displayHeight = getDisplayHeight(context)
        val layoutManager = createLayoutManager(context)
        val marginItemDecoration = MarginItemDecoration(
            marginVertical = 10,
            marginHorizontal = 12,
            marginBottomInPx = displayHeight / DISPLAY_PARTS_NUMBER
        )
        resultList?.adapter = adapter
        resultList?.layoutManager = layoutManager
        resultList?.addItemDecoration(marginItemDecoration)
    }

    private fun setupFragmentViewModel(
        rootView: View,
        fragment: Fragment,
        activity: AppCompatActivity,
        factory: LookupViewModelFactory,
        adapter: ItemWithTypesAdapter
    ): LookupViewModel {
        return ViewModelProvider(fragment, factory)[LookupViewModel::class.java].apply {
            foundDefinitions.observe(fragment) { newItems ->
                onItemsListChanged(newItems, adapter)
            }
            dataLoadingEvents.observe(fragment) { loadingEvent ->
                if (!loadingEvent.isHandled) {
                    when (loadingEvent) {
                        is UiEvent.Loading.ShowLoading -> {
                            showLoading()
                        }

                        is UiEvent.Loading.HideLoading -> {
                            hideLoading()
                        }
                    }
                    notifyToEventIsHandled(loadingEvent)
                }
            }
            messageEvents.observe(fragment) { event ->
                if (!event.isHandled) {
                    sendMessage(rootView, event.message)
                    notifyToEventIsHandled(event)
                }
            }
            selectionStates.observe(fragment) { isSelectionActive ->
                if (isSelectionActive) {
                    actionMode = activity.startSupportActionMode(
                        SelectionModeCallBack(tracker, this::saveSelectedDefinitions)
                    )
                } else {
                    actionMode?.finish()
                    actionMode = null
                }
            }
            selectionSize.observe(fragment) { size ->
                actionMode?.title = "Выбрано $size"
            }
        }
    }

    private fun setupListeners(lookupViewModel: LookupViewModel) {
        lookupButton?.setOnClickListener { button ->
            loadingProgressBar?.isVisible = true
            enterWritingTextField?.error = null
            tracker?.clearSelection()
            val writing = enterWritingTextField?.editText?.text?.toString()
            if (writing.isNullOrEmpty()) {
                enterWritingTextField?.error = getString(R.string.lookup_def_input_error)
            } else {
                lookupViewModel.lookupByWriting(writing)
                button.hideKeyboard()
            }
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
        resultList?.addOnScrollListener(onScrollListener)
    }

    private fun showLoading() {
        loadingProgressBar?.isVisible = true
        objectAnimation(
            target = loadingProgressBar!!,
            property = View.TRANSLATION_Y,
            endValue = dpToPx(SHOWN_LOADING_Y_POS),
            animationDuration = SPINNER_TRANSLATION_DURATION
        ).start()
    }

    private fun hideLoading() {
        objectAnimation(
            target = loadingProgressBar!!,
            property = View.TRANSLATION_Y,
            endValue = dpToPx(HIDDEN_LOADING_Y_POS),
            animationDuration = SPINNER_TRANSLATION_DURATION
        ).apply {
            addListener {
                doOnEnd {
                    loadingProgressBar?.isVisible = false
                }
            }
        }.start()
    }

    private fun sendMessage(rootView: View, message: String) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }

    private fun onItemsListChanged(
        newItems: List<ItemWithType>,
        adapter: ItemWithTypesAdapter
    ) {
        val diffCallback = ItemsDiffUtilCallback(
            oldList = adapter.items,
            newList = newItems
        )
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(adapter)
        adapter.items = newItems
    }

    // Данный API (getDefaultDisplay) устарел начиная с ANDROID R, до него он является актуальным
    @Suppress("DEPRECATION")
    private fun getDisplayHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
            windowManager.defaultDisplay.height
        } else {
            windowManager.currentWindowMetrics.bounds.height()
        }
    }

    private fun initViews(view: View) {
        loadingProgressBar = view.findViewById(R.id.loading_circle)
        yandexDictHyperlink = view.findViewById(R.id.yandex_dict_api_hyperlink)
        yandexDictHyperlink?.movementMethod = LinkMovementMethod.getInstance()
        addFab = view.findViewById(R.id.add_new_definition_fab)
        enterWritingTextField = view.findViewById(R.id.enter_writing_text)
        lookupButton = view.findViewById(R.id.lookup_button)
        resultList = view.findViewById(R.id.lookup_word_results_list)
    }

    private fun clearViews() {
        loadingProgressBar = null
        yandexDictHyperlink = null
        addFab = null
        enterWritingTextField = null
        lookupButton = null
        resultList = null
    }

    companion object {
        private const val SPINNER_TRANSLATION_DURATION = 250L
        private const val SHOWN_LOADING_Y_POS = 12
        private const val HIDDEN_LOADING_Y_POS = -52
        private const val HEADER_DESELECTION_DELAY = 100L
        private const val DEFINITIONS_SELECTION_ID = "definitions_selection"
        private const val SCROLL_START_POSITION = 0
        private const val SCROLL_POS_KEY = "position"
        private const val DISPLAY_PARTS_NUMBER = 4
        private const val DEFAULT_DICT_ID = 1L
        const val DICT_ID_KEY = "dictionary_id"

        @JvmStatic
        fun newInstance(dictionaryId: Long) = LookupWordDefinitionsFragment().apply {
            arguments = Bundle().apply {
                putLong(DICT_ID_KEY, dictionaryId)
            }
        }
    }
}
