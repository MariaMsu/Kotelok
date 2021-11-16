package com.designdrivendevelopment.kotelok

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import com.designdrivendevelopment.kotelok.entities.LearnableDefinition
import com.designdrivendevelopment.kotelok.trainer.TrainerCards
import androidx.lifecycle.lifecycleScope
import com.designdrivendevelopment.kotelok.persistence.roomEntities.WordDefinitionEntity
import kotlinx.coroutines.launch


class TrainFlashcardsActivity : AppCompatActivity() {
    private var state: State = State.NOT_GUESSED
    private lateinit var trainerCards : TrainerCards
    private lateinit var currentWord: LearnableDefinition


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train_flashcards)
        val flashcardButton = findViewById<ImageButton>(R.id.FlashcardButton)
        flashcardButton.setOnClickListener(listener)
        val yesButton = findViewById<ImageButton>(R.id.YesButton)
        yesButton.setOnClickListener(listener)
        val noButton = findViewById<ImageButton>(R.id.NoButton)
        noButton.setOnClickListener(listener)

        val repository = (application as KotelokApplication).appComponent.learnableDefinitionsRepository
        trainerCards = TrainerCards(repository)
        lifecycleScope.launch {
            trainerCards.loadDictionary(
                1,
                true
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
                PressGuessButton(true)
            }
            R.id.NoButton -> {
                PressGuessButton(false)
            }
        }
    }

    fun updateButtonVisibility(isActive : Boolean) {
        var YesButton = findViewById<ImageButton>(R.id.YesButton)
        var NoButton = findViewById<ImageButton>(R.id.NoButton)
        YesButton.isVisible = isActive
        YesButton.isClickable = isActive
        NoButton.isVisible = isActive
        NoButton.isClickable = isActive
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
        val Word = findViewById<TextView>(R.id.Word)
        Word.text = when(state) {
            State.NOT_GUESSED -> currentWord.writing
            State.GUESSED_TRANSLATION -> currentWord.mainTranslation
            State.GUESSED_WORD -> currentWord.writing
        }
    }

    fun PressGuessButton(guess: Boolean) {
        state =  State.NOT_GUESSED
        updateButtonVisibility(false)
        trainerCards.checkUserInput(guess)
        currentWord = trainerCards.getNext()
        UpdateFlashcard()
    }

    enum class State {
        NOT_GUESSED, GUESSED_TRANSLATION, GUESSED_WORD
    }
}
