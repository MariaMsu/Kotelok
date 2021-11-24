package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.selection.DefinitionsKeyProvider
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.selection.ItemWithTypeDetailsLookup
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.ItemWithType
import com.designdrivendevelopment.kotelok.screens.screensUtils.MarginItemDecoration
import com.designdrivendevelopment.kotelok.screens.screensUtils.focusAndShowKeyboard
import com.designdrivendevelopment.kotelok.screens.screensUtils.getScrollPosition
import com.designdrivendevelopment.kotelok.screens.screensUtils.hideKeyboard
import com.google.android.material.snackbar.Snackbar

@Suppress("TooManyFunctions")
class LookupWordDefinitionsFragment : Fragment() {
    private var enterWritingText: EditText? = null
    private var lookupButton: Button? = null
    private var resultList: RecyclerView? = null
    private var scrollPosition = 0
    private var tracker: SelectionTracker<String>? = null

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
        initViews(view)
        setHasOptionsMenu(true)
        scrollPosition = savedInstanceState?.getInt(SCROLL_POS_KEY) ?: SCROLL_START_POSITION

        val context = requireContext()
        val adapter = createAdapter(context, emptyList())
        val factory = LookupViewModelFactory(
            (requireActivity().application as KotelokApplication)
                .appComponent.lookupWordDefRepository
        )

        val lookupViewModel = setupFragmentViewModel(
            rootView = view,
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

        enterWritingText?.focusAndShowKeyboard()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCROLL_POS_KEY, scrollPosition)
        tracker?.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tracker = null
        clearViews()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    private fun createAdapter(
        context: Context,
        items: List<ItemWithType>
    ): ItemWithTypesAdapter {
        return ItemWithTypesAdapter(context, items).apply { setHasStableIds(true) }
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
        factory: LookupViewModelFactory,
        adapter: ItemWithTypesAdapter
    ): LookupViewModel {
        return ViewModelProvider(fragment, factory)[LookupViewModel::class.java].apply {
            foundDefinitions.observe(fragment) { newItems ->
                onItemsListChanged(newItems, adapter)
            }
            events.observe(fragment) { event ->
                if (!event.isHandled) {
                    sendMessage(rootView, event.message)
                    notifyToEventIsHandled(event)
                }
            }
        }
    }

    private fun setupListeners(lookupViewModel: LookupViewModel) {
        lookupButton?.setOnClickListener { button ->
            val writing = enterWritingText?.text?.toString() ?: throw NullPointerException()
            lookupViewModel.lookupByWriting(writing)
            button.hideKeyboard()
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
        enterWritingText = view.findViewById(R.id.enter_writing_text)
        lookupButton = view.findViewById(R.id.lookup_button)
        resultList = view.findViewById(R.id.lookup_word_results_list)
    }

    private fun clearViews() {
        enterWritingText = null
        lookupButton = null
        resultList = null
    }

    companion object {
        private const val DEFINITIONS_SELECTION_ID = "definitions_selection"
        private const val SCROLL_START_POSITION = 0
        private const val SCROLL_POS_KEY = "position"
        private const val DISPLAY_PARTS_NUMBER = 4

        @JvmStatic
        fun newInstance() = LookupWordDefinitionsFragment()
    }
}
