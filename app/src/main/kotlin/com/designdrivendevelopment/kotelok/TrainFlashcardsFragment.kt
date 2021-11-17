package com.designdrivendevelopment.kotelok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class TrainFlashcardsFragment : Fragment() {
    var viewModel: TrainFlashcardsViewModel? = null

    private var yesButton: ImageButton? = null
    private var noButton: ImageButton? = null
    private var flashcardButton: ImageButton? = null
    private var word: TextView? = null
    private var textCompleted: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel?.currentWord?.observe(
            viewLifecycleOwner,
            Observer {
                newWord ->
                updateFlashcard()
            }
        )
        viewModel?.state?.observe(
            viewLifecycleOwner,
            Observer {
                newState ->
                if (viewModel?.state?.value != State.NOT_GUESSED) {
                    updateButtonVisibility(true)
                } else {
                    updateButtonVisibility(false)
                }
            }
        )
        return inflater.inflate(R.layout.fragment_train_flashcards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dictionaryId = arguments?.getLong("id") ?: 1

        val factory = TrainFlashcardsViewModelFactory(
            dictionaryId,
            (requireActivity().application as KotelokApplication)
                .appComponent.cardsLearnDefRepository
        )
        viewModel = ViewModelProvider(this, factory).get(TrainFlashcardsViewModel::class.java)

        word = view.findViewById(R.id.Word)
        textCompleted = view.findViewById(R.id.textCompleted)
        flashcardButton = view.findViewById(R.id.FlashcardButton)
        flashcardButton?.setOnClickListener(listener)
        yesButton = view.findViewById(R.id.YesButton)
        yesButton?.setOnClickListener(listener)
        noButton = view.findViewById(R.id.NoButton)
        noButton?.setOnClickListener(listener)

        // UpdateFlashcard()
    }

    private val listener = View.OnClickListener { view ->
        when (view.id) {
            R.id.FlashcardButton -> {
                pressFlashcard()
            }
            R.id.YesButton -> {
                pressGuessButton(true)
            }
            R.id.NoButton -> {
                pressGuessButton(false)
            }
        }
    }

    private fun updateButtonVisibility(isActive: Boolean) {
        yesButton?.isVisible = isActive
        yesButton?.isClickable = isActive
        noButton?.isVisible = isActive
        noButton?.isClickable = isActive
    }

    private fun pressFlashcard() {
        viewModel?.state?.value = when (viewModel?.state?.value) {
            State.NOT_GUESSED -> State.GUESSED_TRANSLATION
            State.GUESSED_TRANSLATION -> State.GUESSED_WORD
            State.GUESSED_WORD -> State.GUESSED_TRANSLATION
            else -> State.NOT_GUESSED
        }
        // if (viewModel?.state?.value != State.NOT_GUESSED) {
        //    updateButtonVisibility(true)
        // }
        // UpdateFlashcard()
    }

    private fun updateFlashcard() {
        word?.text = when (viewModel?.state?.value) {
            State.NOT_GUESSED -> viewModel?.currentWord?.value?.writing
            State.GUESSED_TRANSLATION -> viewModel?.currentWord?.value?.mainTranslation
            State.GUESSED_WORD -> viewModel?.currentWord?.value?.writing
            else -> "Error"
        }
    }

    private fun pressGuessButton(guess: Boolean) {
        viewModel?.state?.value = State.NOT_GUESSED
        // updateButtonVisibility(false)
        viewModel?.trainerCards?.checkUserInput(guess)
        if (!viewModel?.trainerCards?.isDone!!) {
            viewModel?.currentWord?.value = viewModel?.trainerCards?.getNext()
            updateFlashcard()
        } else {
            textCompleted?.isVisible = true
            flashcardButton?.isClickable = false
        }
    }

    enum class State {
        NOT_GUESSED, GUESSED_TRANSLATION, GUESSED_WORD
    }

    companion object {
        fun newInstance(dictionaryId: Long) = TrainFlashcardsFragment().apply {
            arguments = Bundle().apply {
                putLong("id", dictionaryId)
            }
        }
    }
}
