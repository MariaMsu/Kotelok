package com.designdrivendevelopment.kotelok.screens.trainers

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication

class TrainFlashcardsFragment : Fragment() {
    lateinit var viewModel: TrainFlashcardsViewModel

    private var yesButton: ImageButton? = null
    private var noButton: ImageButton? = null
    private var flashcardButtonOrig: LinearLayout? = null
    private var flashcardButtonRu: LinearLayout? = null
    private var textCompleted: TextView? = null
    private var repeatDict: Button? = null
    private var origWordText: TextView? = null
    private var origWordExample: TextView? = null
    private var ruWordText: TextView? = null
    private var ruWordExample: TextView? = null
    private val flipAnimations: MutableList<AnimatorSet> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_train_flashcards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textCompleted = view.findViewById(R.id.text_completed)
        flashcardButtonOrig = view.findViewById(R.id.flashcardButton_orig)
        flashcardButtonRu = view.findViewById(R.id.flashcardButton_ru)
        flashcardButtonRu?.setOnClickListener {
            viewModel.onCardPressed(viewModel.viewState.value!!)
            flip(requireContext(), flashcardButtonRu, flashcardButtonOrig)
        }
        flashcardButtonOrig?.setOnClickListener {
            viewModel.onCardPressed(viewModel.viewState.value!!)
            flip(requireContext(), flashcardButtonOrig, flashcardButtonRu)
        }
        yesButton = view.findViewById(R.id.yesButton)
        yesButton?.setOnClickListener(listener)
        noButton = view.findViewById(R.id.noButton)
        noButton?.setOnClickListener(listener)
        repeatDict = view.findViewById(R.id.repeatDict)
        repeatDict?.setOnClickListener(listener)
        origWordText = view.findViewById(R.id.word_writing_orig)
        origWordExample = view.findViewById(R.id.word_example_orig)
        ruWordText = view.findViewById(R.id.word_writing_ru)
        ruWordExample = view.findViewById(R.id.word_example_ru)

        val dictionaryId = arguments?.getLong("id") ?: 1
        requireActivity().title = getString(R.string.cards_trainer_title)
        val factory = TrainFlashcardsViewModelFactory(
            dictionaryId,
            (requireActivity().application as KotelokApplication)
                .appComponent.cardsLearnDefRepository,
            (requireActivity().application as KotelokApplication)
                .appComponent.changeStatisticsRepositoryImpl
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

    override fun onStop() {
        super.onStop()
        flipAnimations.forEach { it.cancel() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        yesButton = null
        noButton = null
        flashcardButtonOrig = null
        flashcardButtonRu = null
        textCompleted = null
        repeatDict = null
        origWordText = null
        origWordExample = null
        ruWordText = null
        ruWordExample = null
    }

    private val listener = View.OnClickListener { view ->
        when (view.id) {
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

    private fun completedVisibility(isCompleted: Boolean) {
        textCompleted?.isVisible = isCompleted
        flashcardButtonOrig?.isClickable = !isCompleted
        repeatDict?.isVisible = isCompleted
        repeatDict?.isClickable = isCompleted
    }

    private fun updateFlashcard() {
        when (viewModel.viewState.value) {
            State.NOT_GUESSED -> {
                origWordText?.text = viewModel.currentWord.value?.writing
                val examples = viewModel.currentWord.value?.examples
                if (examples?.isNotEmpty() == true) {
                    origWordExample?.text = examples.first().originalText
                    origWordExample?.isVisible = true
                } else {
                    origWordExample?.isVisible = false
                    origWordExample?.text = ""
                }

                flipAnimations.forEach { it.cancel() }
                ObjectAnimator.ofFloat(
                    flashcardButtonOrig,
                    View.ALPHA,
                    ALPHA_INVISIBLE,
                    ALPHA_VISIBLE
                ).apply {
                    duration = 0L
                    start()
                }
                ObjectAnimator.ofFloat(
                    flashcardButtonOrig,
                    View.ROTATION_X,
                    -FLIP_END_POS,
                    FLIP_START_POS
                ).apply {
                    duration = 0L
                    start()
                }
                flashcardButtonRu?.isVisible = false
                flashcardButtonOrig?.isVisible = true
            }

            State.GUESSED_TRANSLATION -> {
                ruWordText?.text = viewModel.currentWord.value?.mainTranslation
                val translatedExamples =
                    viewModel.currentWord.value?.examples?.map { it.translatedText }
                if (translatedExamples?.isNotEmpty() == true) {
                    ruWordExample?.text = translatedExamples.first()
                    ruWordExample?.isVisible = true
                } else {
                    ruWordExample?.isVisible = false
                    ruWordExample?.text = ""
                }
            }

            State.GUESSED_WORD -> {
                origWordText?.text = viewModel.currentWord.value?.writing
                val examples = viewModel.currentWord.value?.examples
                if (examples?.isNotEmpty() == true) {
                    origWordExample?.text = examples.first().originalText
                    origWordExample?.isVisible = true
                } else {
                    origWordExample?.isVisible = false
                    origWordExample?.text = ""
                }
            }
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

    private fun flip(context: Context, startView: View?, endView: View?) {
        flipAnimations.clear()
        endView?.isVisible = true

        val scale = context.resources.displayMetrics.density
        val cameraDist = CAMERA_DIST_SCALE * scale
        endView?.cameraDistance = cameraDist
        startView?.cameraDistance = cameraDist

        val flipOutAnimatorSet = AnimatorInflater
            .loadAnimator(
                context,
                R.animator.start_flip_animation
            ) as AnimatorSet
        flipOutAnimatorSet.setTarget(startView)
        val flipInAnimationSet = AnimatorInflater
            .loadAnimator(
                context,
                R.animator.end_flip_animation
            ) as AnimatorSet
        flipInAnimationSet.setTarget(endView)
        flipAnimations.add(flipOutAnimatorSet)
        flipAnimations.add(flipInAnimationSet)
        flipOutAnimatorSet.start()
        flipInAnimationSet.start()
        flipInAnimationSet.doOnEnd {
            startView?.isVisible = false
        }
    }

    enum class State {
        NOT_GUESSED, GUESSED_TRANSLATION, GUESSED_WORD
    }

    companion object {
        const val CAMERA_DIST_SCALE = 8000
        const val FLIP_START_POS = 0f
        const val FLIP_END_POS = 180f
        const val ALPHA_VISIBLE = 1f
        const val ALPHA_INVISIBLE = 0f

        fun newInstance(dictionaryId: Long) = TrainFlashcardsFragment().apply {
            arguments = Bundle().apply {
                putLong("id", dictionaryId)
            }
        }
    }
}
