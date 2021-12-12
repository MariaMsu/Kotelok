package com.designdrivendevelopment.kotelok.screens.recognize

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.screens.screensUtils.FragmentResult
import com.google.android.material.textfield.TextInputLayout

class RecognizedWordsFragment : Fragment() {
    private var recognizedText: TextInputLayout? = null
    private var recognizedWords: RecyclerView? = null

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
        val text = arguments?.getString(FragmentResult.RecognizeTab.RESULT_TEXT_KEY, "")
        recognizedText?.editText?.setText(text)
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