package com.designdrivendevelopment.kotelok.screens.dictionaries.definitionDetailsScreen

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication
import com.designdrivendevelopment.kotelok.entities.ExampleOfDefinitionUse
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.screens.screensUtils.MarginItemDecoration
import com.designdrivendevelopment.kotelok.screens.screensUtils.dpToPx
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout

@Suppress("TooManyFunctions")
class DefinitionDetailsFragment :
    Fragment(),
    DeleteTranslationListener,
    DeleteSynonymListener,
    DeleteExampleClickListener {

    private var writingField: TextInputLayout? = null
    private var translationField: TextInputLayout? = null
    private var transcriptionField: TextInputLayout? = null
    private var partOfSpeechField: TextInputLayout? = null
    private var translationsList: RecyclerView? = null
    private var synonymsList: RecyclerView? = null
    private var examplesList: RecyclerView? = null
    private var addTranslationBtn: Button? = null
    private var addSynonymBtn: Button? = null
    private var addExampleBtn: Button? = null
    private var editDefinitionFab: FloatingActionButton? = null
    private var saveDefinitionFab: FloatingActionButton? = null
    private var viewModel: DefDetailsViewModel? = null
    private var isNeedAnimate = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_definition_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dictionaryId = arguments?.getLong(DICT_ID_KEY) ?: DEFAULT_DICT_ID
        val saveMode = arguments?.getInt(SAVE_MODE_KEY) ?: SAVE_MODE_COPY
        initViews(view)

        val activity = requireActivity()
        val context = requireContext()
        val factory = DefDetailsViewModelFactory(
            saveMode,
            dictionaryId,
            (activity.application as KotelokApplication)
                .appComponent.editWordDefinitionsRepository,
            (activity.application as KotelokApplication)
                .appComponent.dictionariesRepository,
            (activity.application as KotelokApplication)
                .appComponent.sharedWordDefProvider
        )

        val translationsAdapter = TranslationsAdapter(context, this, emptyList())
        val synonymsAdapter = SynonymsAdapter(context, this, emptyList())
        val examplesAdapter = ExamplesAdapter(context, this, emptyList())
        setupTranslations(translationsAdapter)
        setupSynonyms(synonymsAdapter)
        setupExamples(examplesAdapter)

        viewModel = ViewModelProvider(this, factory)[DefDetailsViewModel::class.java]
        setupViewModel(viewModel, translationsAdapter, synonymsAdapter, examplesAdapter)
        setupListeners(viewModel)
    }

    override fun onResume() {
        super.onResume()
        isNeedAnimate = true
    }

    override fun onStop() {
        super.onStop()
        isNeedAnimate = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearViews()
    }

    override fun onDeleteTranslation(translation: String) {
        viewModel?.deleteTranslation(translation)
    }

    override fun onDeleteSynonym(synonym: String) {
        viewModel?.deleteSynonym(synonym)
    }

    override fun onDeleteExample(example: ExampleOfDefinitionUse) {
        viewModel?.deleteExample(example)
    }

    private fun setupViewModel(
        viewModel: DefDetailsViewModel?,
        translationsAdapter: TranslationsAdapter,
        synonymsAdapter: SynonymsAdapter,
        examplesAdapter: ExamplesAdapter
    ) {
        viewModel?.displayedDefinition?.observe(this) { wordDefinition ->
            if (wordDefinition != null) {
                showDefinitions(wordDefinition)
                onTranslationsChanged(wordDefinition.allTranslations, translationsAdapter)
                onSynonymsChanged(wordDefinition.synonyms, synonymsAdapter)
                onExamplesChanged(wordDefinition.examples, examplesAdapter)
            }
        }
        viewModel?.isEditable?.observe(this) { isEditable ->
            if (isEditable) {
                if (isNeedAnimate) {
                    animateEditableTransition(editDefinitionFab, saveDefinitionFab)
                } else {
                    editDefinitionFab?.isVisible = false
                    saveDefinitionFab?.isVisible = true
                }
            } else {
                if (isNeedAnimate) {
                    animateEditableTransition(saveDefinitionFab, editDefinitionFab)
                } else {
                    editDefinitionFab?.isVisible = true
                    saveDefinitionFab?.isVisible = false
                }
            }
            translationsAdapter.isEditable = isEditable
            synonymsAdapter.isEditable = isEditable
            examplesAdapter.isEditable = isEditable
            changeEditableStateForFields(isEditable)
        }
        viewModel?.isAddTrButtonVisible?.observe(this) { isVisible ->
            if (isNeedAnimate) {
                animateChangingForButton(addTranslationBtn, isVisible)
            } else {
                  addTranslationBtn?.isVisible = isVisible
            }
        }
        viewModel?.isAddSynButtonVisible?.observe(this) { isVisible ->
            if (isNeedAnimate) {
                animateChangingForButton(addSynonymBtn, isVisible)
            } else {
                addSynonymBtn?.isVisible = isVisible
            }
        }
        viewModel?.isAddExButtonVisible?.observe(this) { isVisible ->
            if (isNeedAnimate) {
                animateChangingForButton(addExampleBtn, isVisible)
            } else {
                addExampleBtn?.isVisible = isVisible
            }
        }
        viewModel?.isDeleteTrButtonVisible?.observe(this) { isVisible ->
            changeVisibilityForDeleteTrButtons(isVisible)
        }
        viewModel?.isDeleteSynButtonVisible?.observe(this) { isVisible ->
            changeVisibilityForDeleteSynButtons(isVisible)
        }
        viewModel?.isDeleteExButtonVisible?.observe(this) { isVisible ->
            changeVisibilityForDeleteExButtons(isVisible)
        }
    }

    private fun changeEditableStateForFields(newState: Boolean) {
        writingField?.isEnabled = newState
        translationField?.isEnabled = newState
        transcriptionField?.isEnabled = newState
        partOfSpeechField?.isEnabled = newState

        changeEditableStateForTranslations(newState)
        changeEditableStateForSynonyms(newState)
        changeEditableStateForExamples(newState)
    }

    private fun changeEditableStateForTranslations(newState: Boolean) {
        val translationsListSize = translationsList?.adapter?.itemCount ?: SIZE_EMPTY
        for (i in 0 until translationsListSize) {
            translationsList
                ?.findViewHolderForAdapterPosition(i)
                ?.itemView
                ?.findViewById<View>(R.id.translation_field)
                ?.isEnabled = newState
        }
    }

    private fun changeEditableStateForSynonyms(newState: Boolean) {
        val synonymsListSize = synonymsList?.adapter?.itemCount ?: SIZE_EMPTY
        for (i in 0 until synonymsListSize) {
            synonymsList
                ?.findViewHolderForAdapterPosition(i)
                ?.itemView
                ?.findViewById<View>(R.id.synonym_field)
                ?.isEnabled = newState
        }
    }

    private fun changeEditableStateForExamples(newState: Boolean) {
        val examplesListSize = examplesList?.adapter?.itemCount ?: SIZE_EMPTY
        for (i in 0 until examplesListSize) {
            val itemView = examplesList?.findViewHolderForAdapterPosition(i)?.itemView
            itemView?.findViewById<View>(R.id.example_original_field)?.isEnabled = newState
            itemView?.findViewById<View>(R.id.example_translation_field)?.isEnabled = newState
        }
    }

    private fun changeVisibilityForDeleteTrButtons(isVisible: Boolean) {
        val translationsListSize = translationsList?.adapter?.itemCount ?: SIZE_EMPTY
        for (i in 0 until translationsListSize) {
            translationsList
                ?.findViewHolderForAdapterPosition(i)
                ?.itemView
                ?.findViewById<View>(R.id.delete_translation_btn)
                ?.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun changeVisibilityForDeleteSynButtons(isVisible: Boolean) {
        val synonymsListSize = synonymsList?.adapter?.itemCount ?: SIZE_EMPTY
        for (i in 0 until synonymsListSize) {
            synonymsList
                ?.findViewHolderForAdapterPosition(i)
                ?.itemView
                ?.findViewById<View>(R.id.delete_synonym_btn)
                ?.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun changeVisibilityForDeleteExButtons(isVisible: Boolean) {
        val examplesListSize = examplesList?.adapter?.itemCount ?: SIZE_EMPTY
        for (i in 0 until examplesListSize) {
            examplesList
                ?.findViewHolderForAdapterPosition(i)
                ?.itemView
                ?.findViewById<View>(R.id.delete_example_btn)
                ?.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun setupTranslations(adapter: TranslationsAdapter) {
        translationsList
            ?.addItemDecoration(MarginItemDecoration(marginVertical = 12, marginHorizontal = 0))
        translationsList?.adapter = adapter
        translationsList?.layoutManager = object : LinearLayoutManager(context, VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return resources.configuration.orientation != Configuration.ORIENTATION_PORTRAIT
            }

            override fun supportsPredictiveItemAnimations(): Boolean {
                return false
            }
        }
    }

    private fun setupSynonyms(adapter: SynonymsAdapter) {
        synonymsList
            ?.addItemDecoration(MarginItemDecoration(marginVertical = 12, marginHorizontal = 0))
        synonymsList?.adapter = adapter
        synonymsList?.layoutManager = object : LinearLayoutManager(context, VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return resources.configuration.orientation != Configuration.ORIENTATION_PORTRAIT
            }

            override fun supportsPredictiveItemAnimations(): Boolean {
                return false
            }
        }
    }

    private fun setupExamples(adapter: ExamplesAdapter) {
        examplesList?.addItemDecoration(
            MarginItemDecoration(
                marginVertical = 24,
                marginHorizontal = 0
            )
        )
        examplesList?.adapter = adapter
        examplesList?.layoutManager = object : LinearLayoutManager(context, VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return resources.configuration.orientation != Configuration.ORIENTATION_PORTRAIT
            }

            override fun supportsPredictiveItemAnimations(): Boolean {
                return false
            }
        }
    }

    private fun setupListeners(viewModel: DefDetailsViewModel?) {
        addTranslationBtn?.setOnClickListener {
            viewModel?.addTranslationField()
        }
        addSynonymBtn?.setOnClickListener {
            viewModel?.addSynonymField()
        }
        addExampleBtn?.setOnClickListener {
            viewModel?.addExampleField()
        }
        editDefinitionFab?.setOnClickListener {
            viewModel?.enableEditableMode()
        }
        saveDefinitionFab?.setOnClickListener {
            viewModel?.disableEditableMode()
        }
    }

    private fun animateEditableTransition(hiddenView: View?, shownView: View?) {
        val showAnimation = ObjectAnimator.ofFloat(
            shownView,
            View.ROTATION,
            ROTATION_TRANSITION_ANGLE,
            ROTATION_END_ANGLE
        ).apply {
            duration = CHANGE_EDITABLE_ANIMATION_DURATION
        }
        ObjectAnimator.ofFloat(
            hiddenView,
            View.ROTATION,
            ROTATION_START_ANGLE,
            ROTATION_TRANSITION_ANGLE
        ).apply {
            duration = CHANGE_EDITABLE_ANIMATION_DURATION
            addListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        hiddenView?.isVisible = false
                        shownView?.isVisible = true
                        showAnimation.start()
                    }
                }
            )
            start()
        }
    }

    private fun animateChangingForButton(target: View?, isVisible: Boolean) {
        if (!isVisible) {
            ObjectAnimator.ofFloat(target, View.TRANSLATION_X, dpToPx(POSITION_OUT_OF_EDGE)).apply {
                duration = CHANGE_EDITABLE_ANIMATION_DURATION
                addListener(
                    object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            target?.isVisible = isVisible
                        }
                    }
                )
                startDelay = CHANGE_EDITABLE_ANIMATION_DURATION
                start()
            }
        } else {
            target?.isVisible = isVisible
            ObjectAnimator.ofFloat(target, View.TRANSLATION_X, POSITION_DEFAULT).apply {
                duration = CHANGE_EDITABLE_ANIMATION_DURATION
                startDelay = CHANGE_EDITABLE_ANIMATION_DURATION
                start()
            }
        }
    }

    private fun onTranslationsChanged(newList: List<String>, adapter: TranslationsAdapter) {
        val diffCallback = StringsDiffCallback(
            newList = newList,
            oldList = adapter.translations
        )
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(adapter)
        adapter.translations = newList
    }

    private fun onSynonymsChanged(newList: List<String>, adapter: SynonymsAdapter) {
        val diffCallback = StringsDiffCallback(
            newList = newList,
            oldList = adapter.synonyms
        )
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(adapter)
        adapter.synonyms = newList
    }

    private fun onExamplesChanged(
        newExamples: List<ExampleOfDefinitionUse>,
        adapter: ExamplesAdapter
    ) {
        val diffCallback = ExamplesDiffCallback(
            oldExamples = adapter.examples,
            newExamples = newExamples
        )
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(adapter)
        adapter.examples = newExamples
    }

    private fun showDefinitions(wordDefinition: WordDefinition) {
        writingField?.editText?.setText(wordDefinition.writing)
        translationField?.editText?.setText(wordDefinition.mainTranslation)
        transcriptionField?.editText?.setText(wordDefinition.transcription.orEmpty())
        partOfSpeechField?.editText?.setText(wordDefinition.partOfSpeech.orEmpty())
    }

    private fun initViews(view: View) {
        writingField = view.findViewById(R.id.writing_text_field)
        translationField = view.findViewById(R.id.translation_text_field)
        transcriptionField = view.findViewById(R.id.transcription_text_field)
        partOfSpeechField = view.findViewById(R.id.part_of_speech_text_field)
        translationsList = view.findViewById(R.id.other_translations)
        synonymsList = view.findViewById(R.id.synonyms)
        examplesList = view.findViewById(R.id.examples)
        addTranslationBtn = view.findViewById(R.id.add_translation_button)
        addSynonymBtn = view.findViewById(R.id.add_synonym_button)
        addExampleBtn = view.findViewById(R.id.add_example_button)
        editDefinitionFab = view.findViewById(R.id.edit_definition_fab)
        saveDefinitionFab = view.findViewById(R.id.save_definition_fab)
    }

    private fun clearViews() {
        writingField = null
        translationField = null
        transcriptionField = null
        partOfSpeechField = null
        translationsList = null
        synonymsList = null
        examplesList = null
        addTranslationBtn = null
        addSynonymBtn = null
        addExampleBtn = null
        editDefinitionFab = null
        saveDefinitionFab = null
    }

    companion object {
        private const val POSITION_DEFAULT = 0f
        private const val POSITION_OUT_OF_EDGE = -400
        private const val SIZE_EMPTY = 0
        private const val ROTATION_START_ANGLE = 0f
        private const val ROTATION_TRANSITION_ANGLE = 180f
        private const val ROTATION_END_ANGLE = 360f
        private const val CHANGE_EDITABLE_ANIMATION_DURATION = 150L
        private const val DICT_ID_KEY = "dictionary_id_key"
        private const val SAVE_MODE_KEY = "save_mode_key"
        private const val DEFAULT_DICT_ID = 1L
        const val SAVE_MODE_UPDATE = 1
        const val SAVE_MODE_COPY = 2

        fun newInstance(
            dictionaryId: Long,
            saveMode: Int
        ) = DefinitionDetailsFragment().apply {
            arguments = Bundle().apply {
                putLong(DICT_ID_KEY, dictionaryId)
                putInt(SAVE_MODE_KEY, saveMode)
            }
        }
    }
}
