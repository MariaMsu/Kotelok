package com.designdrivendevelopment.kotelok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.trainer.TrainerCards
import kotlinx.coroutines.launch

class TrainFlashcardsFragment :  Fragment() {
    private var state: State = State.NOT_GUESSED
    private lateinit var trainerCards : TrainerCards
    private lateinit var currentWord: LearnableDefinition

    private var yesButton: ImageButton? = null
    private var noButton: ImageButton? = null
    private var flashcardButton: ImageButton? = null
    private var word: TextView? = null
    private var textCompleted: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_train_flashcards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dictionaryId = arguments?.getLong("id") ?: 1
        word = view.findViewById(R.id.Word)
        textCompleted = view.findViewById(R.id.textCompleted)
        flashcardButton = view.findViewById(R.id.FlashcardButton)
        flashcardButton?.setOnClickListener(listener)
        yesButton = view.findViewById(R.id.YesButton)
        yesButton?.setOnClickListener(listener)
        noButton = view.findViewById(R.id.NoButton)
        noButton?.setOnClickListener(listener)

        val repository = (requireActivity().application as KotelokApplication).appComponent.cardsLearnDefRepository
        trainerCards = TrainerCards(repository)
        lifecycleScope.launch {
            trainerCards.loadDictionary(
                dictionaryId,
                false
            )
            currentWord = trainerCards.getNext()
            UpdateFlashcard()
        }
    }

    private val listener = View.OnClickListener { view ->
        when (view.id) {
            R.id.FlashcardButton -> {
                PressFlashcard()
            }
            R.id.YesButton -> {
                PressGuessButton( true)
            }
            R.id.NoButton -> {
                PressGuessButton(false)
            }
        }
    }

    private fun updateButtonVisibility(isActive : Boolean) {
        yesButton?.isVisible = isActive
        yesButton?.isClickable = isActive
        noButton?.isVisible = isActive
        noButton?.isClickable = isActive
    }

    private fun PressFlashcard() {
        state =  when(state) {
            State.NOT_GUESSED -> State.GUESSED_TRANSLATION
            State.GUESSED_TRANSLATION -> State.GUESSED_WORD
            State.GUESSED_WORD -> State.GUESSED_TRANSLATION
        }
        if (state != State.NOT_GUESSED) {
            updateButtonVisibility(true)
        }
        UpdateFlashcard()
    }

    private fun UpdateFlashcard() {
        word?.text = when(state) {
            State.NOT_GUESSED -> currentWord.writing
            State.GUESSED_TRANSLATION -> currentWord.mainTranslation
            State.GUESSED_WORD -> currentWord.writing
        }
    }

    private fun PressGuessButton(guess: Boolean) {
        state =  State.NOT_GUESSED
        updateButtonVisibility(false)
        trainerCards.checkUserInput(guess)
        if (!trainerCards.isDone) {
            currentWord = trainerCards.getNext()
            UpdateFlashcard()
        } else {
            textCompleted?.isVisible = true
            flashcardButton?.isClickable = false
        }
    }

    enum class State {
        NOT_GUESSED, GUESSED_TRANSLATION, GUESSED_WORD
    }

    companion object {
        fun newInstance(dictionaryId : Long) = TrainFlashcardsFragment().apply {
            arguments = Bundle().apply {
                putLong("id", dictionaryId)
            }
        }
    }
}
