package com.designdrivendevelopment.kotelok.screens.recognize

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.LookupWordDefinitionsFragment
import com.designdrivendevelopment.kotelok.screens.screensUtils.FragmentResult
import com.designdrivendevelopment.kotelok.screens.screensUtils.StringsDiffCallback
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.textfield.TextInputLayout

class RecognizedWordsFragment : Fragment(), WordClickListener {
    private var recognizedText: TextInputLayout? = null
    private var recognizedWords: RecyclerView? = null
    private var applyChangesBtn: Button? = null
    private val viewModel: RecognizedWordsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recognized_words, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        val activity = requireActivity()
        activity.title = getString(R.string.title_recognized_words)
        val context = requireContext()
        val text = arguments?.getString(FragmentResult.RecognizeTab.RESULT_TEXT_KEY) ?: ""

        recognizedText?.editText?.setText(text)

        val adapter = WordsAdapter(context, wordClickListener = this, emptyList())
        val layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.STRETCH
        }
        setupWordsList(adapter, layoutManager)

        viewModel.breakTextIntoWords(text)
        viewModel.recognizedWords.observe(this) { words ->
            onWordsChanged(words, adapter)
        }
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearViews()
    }

    override fun onWordClicked(word: Word) {
        val bundle = Bundle().apply {
            putString(LookupWordDefinitionsFragment.LOOKUP_WORD_KEY, word.writing)
        }
        setFragmentResult(FragmentResult.DictionariesTab.OPEN_LOOKUP_WORD_DEF_FRAGMENT_KEY, bundle)
    }

    private fun setupListeners() {
        applyChangesBtn?.setOnClickListener {
            val text = recognizedText?.editText?.text?.toString().orEmpty()
            recognizedText?.error = null
            if (text.isEmpty()) {
                recognizedText?.error = getString(R.string.error_field_required)
                return@setOnClickListener
            }
            viewModel.breakTextIntoWords(text)
        }
    }

    private fun setupWordsList(adapter: WordsAdapter, layoutManager: FlexboxLayoutManager) {
        recognizedWords?.adapter = adapter
        recognizedWords?.layoutManager = layoutManager
    }

    private fun onWordsChanged(newWords: List<Word>, adapter: WordsAdapter) {
        val diffCallback = StringsDiffCallback(
            newList = newWords.map { it.writing },
            oldList = adapter.words.map { it.writing }
        )
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(adapter)
        adapter.words = newWords
    }

    private fun initViews(view: View) {
        recognizedText = view.findViewById(R.id.recognized_text)
        recognizedWords = view.findViewById(R.id.recognized_words_list)
        applyChangesBtn = view.findViewById(R.id.apply_changes_button)
    }

    private fun clearViews() {
        recognizedText = null
        recognizedWords = null
        applyChangesBtn = null
    }

    companion object {
        @JvmStatic
        fun newInstance(text: String): RecognizedWordsFragment {
            return RecognizedWordsFragment().apply {
                arguments = Bundle().apply {
                    putString(FragmentResult.RecognizeTab.RESULT_TEXT_KEY, text)
                }
            }
        }
    }
}
