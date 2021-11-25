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
    lateinit var viewModel: TrainFlashcardsViewModel

    private var yesButton: ImageButton? = null
    private var noButton: ImageButton? = null
    private var flashcardButton: ImageButton? = null
    private var word: TextView? = null
    private var textCompleted: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = inflater.inflate(R.layout.fragment_train_flashcards, container, false)

        word = binding.findViewById(R.id.Word)
        textCompleted = binding.findViewById(R.id.textCompleted)
        flashcardButton = binding.findViewById(R.id.FlashcardButton)
        flashcardButton?.setOnClickListener(listener)
        yesButton = binding.findViewById(R.id.YesButton)
        yesButton?.setOnClickListener(listener)
        noButton = binding.findViewById(R.id.NoButton)
        noButton?.setOnClickListener(listener)

        val dictionaryId = arguments?.getLong("id") ?: 1
        val factory = TrainFlashcardsViewModelFactory(
            dictionaryId,
            (requireActivity().application as KotelokApplication)
                .appComponent.cardsLearnDefRepository
        )
        viewModel = ViewModelProvider(this, factory).get(TrainFlashcardsViewModel::class.java)
        viewModel.viewState.observe(
            viewLifecycleOwner,
            {
            updateFlashcard()
            if (viewModel.viewState.value != State.NOT_GUESSED) {
                    updateButtonVisibility(true)
                } else {
                    updateButtonVisibility(false)
                }
            }
        )
        viewModel.currentWord.observe(
            viewLifecycleOwner,
            {
                updateFlashcard()
            }
        )
        return binding
    }

    private val listener = View.OnClickListener { view ->
        when (view.id) {
            R.id.FlashcardButton -> {
                viewModel.onCardPressed(viewModel.viewState.value!!)
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

    private fun updateFlashcard() {
        word?.text = when (viewModel.viewState.value) {
            State.NOT_GUESSED -> viewModel.currentWord.value?.writing
            State.GUESSED_TRANSLATION -> viewModel.currentWord.value?.mainTranslation
            State.GUESSED_WORD -> viewModel.currentWord.value?.writing
            else -> "Error"
        }
    }

    private fun pressGuessButton(guess: Boolean) {
        viewModel.onGuessPressed(guess)
        if (!viewModel.trainerCards.isDone) {
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
