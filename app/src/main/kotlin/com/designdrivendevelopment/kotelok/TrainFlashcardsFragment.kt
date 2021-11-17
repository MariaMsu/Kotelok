package com.designdrivendevelopment.kotelok

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.trainer.TrainerCards
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class TrainFlashcardsFragment :  Fragment() {
    private var state: State = State.NOT_GUESSED
    private lateinit var trainerCards : TrainerCards
    private lateinit var currentWord: LearnableDefinition

    var yesButton: ImageButton? = null
    var noButton: ImageButton? = null
    var flashcardButton: ImageButton? = null
    var word: TextView? = null
    var textCompleted: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_train_flashcards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dictionaryId = arguments?.getLong("id") ?: 1
        word = view.findViewById<TextView>(R.id.Word)
        textCompleted = view.findViewById<TextView>(R.id.textCompleted)
        flashcardButton = view.findViewById<ImageButton>(R.id.FlashcardButton)
        flashcardButton?.setOnClickListener(listener)
        yesButton = view.findViewById<ImageButton>(R.id.YesButton)
        yesButton?.setOnClickListener(listener)
        noButton = view.findViewById<ImageButton>(R.id.NoButton)
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

    val listener = View.OnClickListener { view ->
        when (view.getId()) {
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

    fun updateButtonVisibility(isActive : Boolean) {
        yesButton?.isVisible = isActive
        yesButton?.isClickable = isActive
        noButton?.isVisible = isActive
        noButton?.isClickable = isActive
    }

    fun PressFlashcard() {
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

    fun UpdateFlashcard() {
        word?.text = when(state) {
            State.NOT_GUESSED -> currentWord.writing
            State.GUESSED_TRANSLATION -> currentWord.mainTranslation
            State.GUESSED_WORD -> currentWord.writing
        }
    }

    fun PressGuessButton(guess: Boolean) {
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
