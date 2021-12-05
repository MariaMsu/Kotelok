package com.designdrivendevelopment.kotelok.screens.dictionaries.definitionDetailsScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.screens.screensUtils.MarginItemDecoration
import com.google.android.material.textfield.TextInputLayout

class DefinitionDetailsFragment :
    Fragment(),
    DeleteTranslationListener,
    DeleteSynonymListener {

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
    private var viewModel: DefDetailsViewModel? = null

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
        setupTranslations(translationsAdapter)
        setupSynonyms(synonymsAdapter)

        viewModel = ViewModelProvider(this, factory)[DefDetailsViewModel::class.java]
        setupViewModel(viewModel, translationsAdapter, synonymsAdapter)
        setupListeners(viewModel)
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

    private fun setupViewModel(
        viewModel: DefDetailsViewModel?,
        translationsAdapter: TranslationsAdapter,
        synonymsAdapter: SynonymsAdapter
    ) {
        viewModel?.displayedDefinition?.observe(this) { wordDefinition ->
            if (wordDefinition != null) {
                showDefinitions(wordDefinition)
                onTranslationsChanged(wordDefinition.allTranslations, translationsAdapter)
                onSynonymsChanged(wordDefinition.synonyms, synonymsAdapter)
            }
            addTranslationBtn?.isVisible = translationsAdapter.translations.size < MAX_LISTS_SIZE
            addSynonymBtn?.isVisible = synonymsAdapter.synonyms.size < MAX_LISTS_SIZE
        }
    }

    private fun setupTranslations(adapter: TranslationsAdapter) {
        translationsList
            ?.addItemDecoration(MarginItemDecoration(marginVertical = 12, marginHorizontal = 0))
        translationsList?.adapter = adapter
        translationsList?.layoutManager = object : LinearLayoutManager(context, VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return false
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
                return false
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
    }

    companion object {
        private const val DICT_ID_KEY = "dictionary_id_key"
        private const val SAVE_MODE_KEY = "save_mode_key"
        private const val DEFAULT_DICT_ID = 1L
        private const val MAX_LISTS_SIZE = 5
        const val READ_ONLY = 1
        const val WRITE_AND_READ = 2
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
