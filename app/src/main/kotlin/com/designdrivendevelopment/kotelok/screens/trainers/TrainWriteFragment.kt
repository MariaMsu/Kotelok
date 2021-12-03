package com.designdrivendevelopment.kotelok.screens.trainers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication
import com.google.android.material.textfield.TextInputLayout

class TrainWriteFragment : Fragment() {
    lateinit var viewModel : TrainWriteViewModel

    private var inputText : TextInputLayout? = null
    private var checkButton: Button? = null
    private var textCompleted: TextView? = null
    private var flashcard : TextView? = null
    private var correctWord : TextView? = null
    private var nextWordButton : Button? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_train_write, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textCompleted = view.findViewById(R.id.text_completed)
        inputText = view.findViewById(R.id.input_word)
        checkButton = view.findViewById(R.id.check_button)
        checkButton?.setOnClickListener(listener)
        flashcard = view.findViewById(R.id.flashcard)
        correctWord = view.findViewById(R.id.correct_word)
        nextWordButton = view.findViewById(R.id.next_word_button)
        nextWordButton?.setOnClickListener(listener)

        val dictionaryId = arguments?.getLong("id") ?: 1
        val factory = TrainWriteViewModelFactory(
            dictionaryId,
            (requireActivity().application as KotelokApplication)
                .appComponent.cardsLearnDefRepository
        )
        viewModel = ViewModelProvider(this, factory).get(TrainWriteViewModel::class.java)

        viewModel.currentWord.observe(
            viewLifecycleOwner,
            {
                flashcard?.text = viewModel.currentWord.value?.mainTranslation
            }
        )

        viewModel.viewState.observe(
            viewLifecycleOwner,
            {
                onStateChanged(viewModel.viewState.value)
            }
        )
    }

    private fun onStateChanged(state: State?) {
        if (state == State.NOT_GUESSED) {
            checkedVisibility(false)
        } else {
            checkedVisibility(true)
            if (state == State.GUESSED_CORRECT) {
                correctWord?.text = "Верно!"
            } else {
                correctWord?.text = viewModel.currentWord.value?.writing
            }
        }
    }

    private val listener = View.OnClickListener { view ->
        when (view.id) {
            R.id.check_button -> {
                viewModel.onGuess(inputText?.editText?.text.toString())
            }
            R.id.next_word_button -> {
                viewModel.onPressNext()
            }
        }
    }

    private fun checkedVisibility(isChecked: Boolean){
        inputText?.isEnabled = !isChecked
        checkButton?.isVisible = !isChecked
        checkButton?.isClickable = !isChecked
        correctWord?.isVisible = isChecked
        nextWordButton?.isVisible = isChecked
        nextWordButton?.isClickable = isChecked
    }

    companion object {
        fun newInstance(dictionaryId: Long) = TrainWriteFragment().apply {
            arguments = Bundle().apply {
                putLong("id", dictionaryId)
            }
        }
    }

    enum class State {
        NOT_GUESSED, GUESSED_CORRECT, GUESSED_INCORRECT
    }

}
