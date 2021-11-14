package com.designdrivendevelopment.kotelok

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DictionaryDetailsFragment : Fragment() {
    var wordDefinitionsList: RecyclerView? = null
    private var scrollPosition = 0

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

        val context = requireContext()
        val dictionaryId = arguments?.getLong(DICT_ID_KEY, NOT_EXIST_DICT_ID) ?: NOT_EXIST_DICT_ID
        val repo = (requireActivity().application as KotelokApplication)
            .appComponent.dictDefinitionsRepository

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val definitions = repo.getDefinitionsByDictionaryId(dictionaryId)
            val adapter = createAdapter(context, definitions)
            val layoutManager = createLayoutManager(context)
            val marginItemDecoration = MarginItemDecoration(
                marginVertical = 10,
                marginHorizontal = 12
            )
            setupWordDefinitionsList(adapter, layoutManager, marginItemDecoration, scrollPosition)
        }
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
        adapter: WordDefinitionsAdapter,
        layoutManager: LinearLayoutManager,
        itemDecoration: RecyclerView.ItemDecoration,
        position: Int
    ) {
        wordDefinitionsList?.adapter = adapter
        wordDefinitionsList?.layoutManager = layoutManager
        wordDefinitionsList?.scrollToPosition(position)
        wordDefinitionsList?.addItemDecoration(itemDecoration)
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
