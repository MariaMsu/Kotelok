package com.designdrivendevelopment.kotelok.screens.trainers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication

class TrainFlashcardsFragment : Fragment() {
    lateinit var viewModel: TrainFlashcardsViewModel

    private var yesButton: ImageButton? = null
    private var noButton: ImageButton? = null
    private var flashcardButton: TextView? = null
    private var textCompleted: TextView? = null
    private var repeatDict: ImageButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_train_flashcards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textCompleted = view.findViewById(R.id.text_completed)
        flashcardButton = view.findViewById(R.id.flashcardButton)
        flashcardButton?.setOnClickListener(listener)
        yesButton = view.findViewById(R.id.yesButton)
        yesButton?.setOnClickListener(listener)
        noButton = view.findViewById(R.id.noButton)
        noButton?.setOnClickListener(listener)
        repeatDict = view.findViewById(R.id.repeatDict)
        repeatDict?.setOnClickListener(listener)

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
    }

    private val listener = View.OnClickListener { view ->
        when (view.id) {
            R.id.flashcardButton -> {
                viewModel.onCardPressed(viewModel.viewState.value!!)
            }
            R.id.yesButton -> {
                pressGuessButton(true)
            }
            R.id.noButton -> {
                pressGuessButton(false)
            }
            R.id.repeatDict -> {
                completedVisibility(false)
                viewModel.restartDict()
            }
        }
    }

    private fun updateButtonVisibility(isActive: Boolean) {
        yesButton?.isVisible = isActive
        yesButton?.isClickable = isActive
        noButton?.isVisible = isActive
        noButton?.isClickable = isActive
    }

    private fun completedVisibility(isCompleted: Boolean){
        textCompleted?.isVisible = isCompleted
        flashcardButton?.isClickable = !isCompleted
        repeatDict?.isVisible = isCompleted
        repeatDict?.isClickable = isCompleted
    }

    private fun updateFlashcard() {
        flashcardButton?.text = when (viewModel.viewState.value) {
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
            completedVisibility(true)
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
