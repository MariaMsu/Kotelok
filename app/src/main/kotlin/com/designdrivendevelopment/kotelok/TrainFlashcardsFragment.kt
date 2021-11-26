package com.designdrivendevelopment.kotelok

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class TrainFlashcardsFragment : Fragment() {
    lateinit var viewModel: TrainFlashcardsViewModel

    private var yesButton: ImageButton? = null
    private var noButton: ImageButton? = null
    private var flashcardButton: ImageButton? = null
    private var flashcardButtonBack: ImageButton? = null
    private var frontWord: TextView? = null
    private var backWord: TextView? = null
    private var textCompleted: TextView? = null
    private var repeatDict: ImageButton? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = inflater.inflate(R.layout.fragment_train_flashcards, container, false)

        frontWord = binding.findViewById(R.id.frontWord)
        backWord = binding.findViewById(R.id.backWord)
        textCompleted = binding.findViewById(R.id.textCompleted)
        flashcardButton = binding.findViewById(R.id.flashcardButton)
        flashcardButton?.setOnClickListener(listener)
        yesButton = binding.findViewById(R.id.yesButton)
        yesButton?.setOnClickListener(listener)
        noButton = binding.findViewById(R.id.noButton)
        noButton?.setOnClickListener(listener)
        repeatDict = binding.findViewById(R.id.repeatDict)
        repeatDict?.setOnClickListener(listener)
        flashcardButtonBack = binding.findViewById(R.id.flashcardButtonBack)


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

    private fun animateFlip(forwardFlip: ImageButton, backFlip: ImageButton, forwardWord: TextView, backWord: TextView) {
        //backFlip.isVisible = true
        //backWord.isVisible = true

        val front_animation = AnimatorInflater.loadAnimator(activity?.applicationContext, R.animator.flip_in) as AnimatorSet
        val back_animation = AnimatorInflater.loadAnimator(activity?.applicationContext, R.animator.flip_out) as AnimatorSet
        val front_text_animation = AnimatorInflater.loadAnimator(activity?.applicationContext, R.animator.flip_in) as AnimatorSet
        val back_text_animation = AnimatorInflater.loadAnimator(activity?.applicationContext, R.animator.flip_out) as AnimatorSet

        front_animation.setTarget(forwardFlip)
        back_animation.setTarget(backFlip)
        front_text_animation.setTarget(forwardWord)
        back_text_animation.setTarget(backWord)

//        front_animation.doOnEnd {
//            forwardFlip.isVisible = false
//        }
//        front_text_animation.doOnEnd {
//            forwardWord.isVisible = false
//        }

        val allAnimatorSet = AnimatorSet()
        allAnimatorSet.playTogether(front_animation, back_animation, front_text_animation, back_text_animation)
        allAnimatorSet.start()
    }

    private val listener = View.OnClickListener { view ->
        when (view.id) {
            R.id.flashcardButton -> {
                if (viewModel.viewState.value == State.GUESSED_TRANSLATION) {
                    animateFlip(flashcardButtonBack!!, flashcardButton!!, backWord!!, frontWord!!)
                } else {
                    animateFlip(flashcardButton!!, flashcardButtonBack!!, frontWord!!, backWord!!)
                }
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
        frontWord?.text = when (viewModel.viewState.value) {
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
