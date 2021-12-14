package com.designdrivendevelopment.kotelok.screens.trainers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication
import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.google.android.material.textfield.TextInputLayout

class TrainWriteFragment : Fragment() {
    lateinit var viewModel: TrainWriteViewModel
    private var inputText: TextInputLayout? = null
    private var checkButton: Button? = null
    private var textCompleted: TextView? = null
    private var flashcard: LinearLayout? = null
    private var correctWord: TextView? = null
    private var nextWordButton: Button? = null
    private var repeatDict: Button? = null
    private var backToDictionariesButton: Button? = null
    private var wordWriting: TextView? = null
    private var wordExample: TextView? = null

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
        repeatDict = view.findViewById(R.id.repeat_button)
        repeatDict?.setOnClickListener(listener)
        wordExample = view.findViewById(R.id.word_example_ru)
        wordWriting = view.findViewById(R.id.word_writing_ru)
        backToDictionariesButton = view.findViewById(R.id.to_dictionaries_button)
        backToDictionariesButton?.setOnClickListener { requireActivity().onBackPressed() }

        val dictionaryId = arguments?.getLong("id") ?: Dictionary.DEFAULT_DICT_ID
        val factory = TrainWriteViewModelFactory(
            dictionaryId,
            (requireActivity().application as KotelokApplication)
                .appComponent.writerLearnDefRepository,
            (requireActivity().application as KotelokApplication)
                .appComponent.changeStatisticsRepositoryImpl
        )
        viewModel = ViewModelProvider(this, factory).get(TrainWriteViewModel::class.java)

        requireActivity().title = getString(R.string.writer_trainer_title)
        viewModel.currentWord.observe(
            this,
            {
                wordWriting?.text = viewModel.currentWord.value?.mainTranslation
                if (viewModel.currentWord.value?.examples?.isNotEmpty() == true) {
                    wordExample?.text = viewModel.currentWord.value?.examples?.get(0)?.translatedText
                    wordExample?.isVisible = true
                } else {
                    wordExample?.text = ""
                    wordExample?.isVisible = false
                }
            }
        )

        viewModel.viewState.observe(this) { state ->
            onStateChanged(state)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textCompleted = null
        inputText = null
        checkButton = null
        flashcard = null
        correctWord = null
        nextWordButton = null
        repeatDict = null
        wordExample = null
        wordWriting = null
        backToDictionariesButton = null
    }

    private fun onStateChanged(state: State?) {
        when (state) {
            State.NOT_GUESSED -> {
                inputText?.editText?.text?.clear()
                completedVisibility(false)
                checkedVisibility(false)
            }

            State.GUESSED_CORRECT -> {
                completedVisibility(false)
                checkedVisibility(true)
                correctWord?.text = getString(R.string.correct_guess)
            }

            State.GUESSED_INCORRECT -> {
                completedVisibility(false)
                checkedVisibility(true)
                correctWord?.text =
                    String.format(getString(R.string.incorrect_guess), viewModel.currentWord.value?.writing)
            }

            State.DONE -> {
                completedVisibility(true)
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
            R.id.repeat_button -> {
                completedVisibility(false)
                viewModel.restartDict()
            }
        }
    }

    private fun checkedVisibility(isChecked: Boolean) {
        inputText?.isEnabled = !isChecked
        checkButton?.isVisible = !isChecked
        correctWord?.isVisible = isChecked
        nextWordButton?.isVisible = isChecked
    }

    private fun completedVisibility(isCompleted: Boolean) {
        inputText?.isVisible = !isCompleted
        flashcard?.isVisible = !isCompleted

        inputText?.isEnabled = !isCompleted
        checkButton?.isVisible = !isCompleted
        checkButton?.isClickable = !isCompleted
        correctWord?.isVisible = !isCompleted
        nextWordButton?.isVisible = !isCompleted
        nextWordButton?.isClickable = !isCompleted
        textCompleted?.isVisible = isCompleted
        repeatDict?.isVisible = isCompleted
        repeatDict?.isClickable = isCompleted
        backToDictionariesButton?.isVisible = isCompleted
    }

    companion object {
        fun newInstance(dictionaryId: Long) = TrainWriteFragment().apply {
            arguments = Bundle().apply {
                putLong("id", dictionaryId)
            }
        }
    }

    enum class State {
        NOT_GUESSED, GUESSED_CORRECT, GUESSED_INCORRECT, DONE
    }
}
