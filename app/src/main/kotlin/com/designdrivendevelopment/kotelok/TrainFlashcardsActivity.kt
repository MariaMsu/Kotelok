package com.designdrivendevelopment.kotelok

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.core.view.isVisible

class TrainFlashcardsActivity : AppCompatActivity() {
    private var state: State = State.NOT_GUESSED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train_flashcards)
        var FlashcardButton = findViewById<ImageButton>(R.id.FlashcardButton)
        FlashcardButton.setOnClickListener(listener)
    }

    val listener= View.OnClickListener { view ->
        when (view.getId()) {
            R.id.FlashcardButton -> {
                PressFlashcard()
            }
        }
    }

    fun PressFlashcard() {
        state =  when(state) {
            State.NOT_GUESSED -> State.GUESSED_TRANSLATION
            State.GUESSED_TRANSLATION -> State.GUESSED_WORD
            State.GUESSED_WORD -> State.GUESSED_TRANSLATION
        }
        if (state != State.NOT_GUESSED) {
            var YesButton = findViewById<ImageButton>(R.id.YesButton)
            YesButton.isVisible = true
            YesButton.isClickable = true
            var NoButton = findViewById<ImageButton>(R.id.NoButton)
            NoButton.isVisible = true
            NoButton.isClickable = true
        }
    }

    enum class State {
        NOT_GUESSED, GUESSED_TRANSLATION, GUESSED_WORD
    }
}
