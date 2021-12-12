package com.designdrivendevelopment.kotelok.screens.recognize

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.setFragmentResult
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.screens.screensUtils.FragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout

class RecognizedTextBottomSheet : BottomSheetDialogFragment() {
    private var recognizedTextField: TextInputLayout? = null
    private var continueButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_recognized_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        val text = arguments?.getString(FragmentResult.RecognizeTab.RESULT_TEXT_KEY, "")
        recognizedTextField?.editText?.setText(text)
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearViews()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        recognizedTextField?.error = null
        continueButton?.setOnClickListener {
            val text = recognizedTextField?.editText?.text?.toString().orEmpty()
            if (text.isEmpty()) {
                recognizedTextField?.error = getString(R.string.error_field_required)
                return@setOnClickListener
            }
            val bundle = Bundle().apply {
                putString(FragmentResult.RecognizeTab.RESULT_TEXT_KEY, text)
            }
            setFragmentResult(FragmentResult.RecognizeTab.OPEN_RECOGNIZED_WORDS_FRAGMENT_KEY, bundle)
            dismiss()
        }
        recognizedTextField?.editText?.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP ->
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }

    private fun initViews(view: View) {
        recognizedTextField = view.findViewById(R.id.edited_text)
        continueButton = view.findViewById(R.id.continue_recognition)
    }

    private fun clearViews() {
        recognizedTextField = null
        continueButton = null
    }

    companion object {

        @JvmStatic
        fun newInstance(text: String): RecognizedTextBottomSheet {
            return RecognizedTextBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(FragmentResult.RecognizeTab.RESULT_TEXT_KEY, text)
                }
            }
        }
    }
}
