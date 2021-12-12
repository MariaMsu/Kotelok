package com.designdrivendevelopment.kotelok.screens.recognize

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.screens.screensUtils.FragmentResult
import com.designdrivendevelopment.kotelok.screens.screensUtils.MarginItemDecoration
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.textfield.TextInputLayout

class RecognizedWordsFragment : Fragment() {
    private var recognizedText: TextInputLayout? = null
    private var recognizedWords: RecyclerView? = null
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
        val text = arguments?.getString(FragmentResult.RecognizeTab.RESULT_TEXT_KEY) ?: ""
        recognizedText?.editText?.setText(text)

        viewModel.breakTextIntoWords(text)
        viewModel.recognizedWords.observe(this) { words ->
            recognizedWords?.adapter = WordsAdapter(requireContext(), words)
            recognizedWords?.addItemDecoration(
                MarginItemDecoration(
                    marginHorizontal = 2,
                    marginVertical = 4
                )
            )
            recognizedWords?.layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
                alignItems = AlignItems.STRETCH
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearViews()
    }

    private fun initViews(view: View) {
        recognizedText = view.findViewById(R.id.recognized_text)
        recognizedWords = view.findViewById(R.id.recognized_words_list)
    }

    private fun clearViews() {
        recognizedText = null
        recognizedWords = null
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
