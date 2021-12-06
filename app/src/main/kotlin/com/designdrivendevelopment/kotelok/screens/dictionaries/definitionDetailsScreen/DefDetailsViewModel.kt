package com.designdrivendevelopment.kotelok.screens.dictionaries.definitionDetailsScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.ExampleOfDefinitionUse
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.screens.dictionaries.DictionariesRepository
import com.designdrivendevelopment.kotelok.screens.dictionaries.EditWordDefinitionsRepository
import com.designdrivendevelopment.kotelok.screens.screensUtils.UiEvent
import com.designdrivendevelopment.kotelok.screens.sharedWordDefProvider.SharedWordDefinitionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("TooManyFunctions")
class DefDetailsViewModel(
    private val saveMode: Int,
    private val dictionaryId: Long,
    private val editWordDefRepository: EditWordDefinitionsRepository,
    private val dictionariesRepository: DictionariesRepository,
    private val sharedWordDefProvider: SharedWordDefinitionProvider
) : ViewModel() {
    private val _displayedDefinition: MutableLiveData<WordDefinition> =
        MutableLiveData(prepareDefinitionToShowing(sharedWordDefProvider.sharedWordDefinition))
    private val _isEditable: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _isAddTrButtonVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _isAddSynButtonVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _isAddExButtonVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _isDeleteTrButtonVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _isDeleteSynButtonVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _isDeleteExButtonVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _messageEvents = MutableLiveData<UiEvent.ShowMessage>()

    init {
        setInitialState(_displayedDefinition.value)
    }

    val displayedDefinition: LiveData<WordDefinition>
        get() = _displayedDefinition
    val isEditable: LiveData<Boolean>
        get() = _isEditable
    val isAddTrButtonVisible: LiveData<Boolean>
        get() = _isAddTrButtonVisible
    val isAddSynButtonVisible: LiveData<Boolean>
        get() = _isAddSynButtonVisible
    val isAddExButtonVisible: LiveData<Boolean>
        get() = _isAddExButtonVisible
    val isDeleteTrButtonVisible: LiveData<Boolean>
        get() = _isDeleteTrButtonVisible
    val isDeleteSynButtonVisible: LiveData<Boolean>
        get() = _isDeleteSynButtonVisible
    val isDeleteExButtonVisible: LiveData<Boolean>
        get() = _isDeleteExButtonVisible
    val messageEvents: LiveData<UiEvent.ShowMessage>
        get() = _messageEvents

    override fun onCleared() {
        sharedWordDefProvider.sharedWordDefinition = null
    }

    fun enableEditableMode() {
        _isEditable.value = true

        updateAddTrButtonVisibility()
        updateAddSynButtonVisibility()
        updateAddExButtonVisibility()

        updateDeleteTrButtonVisibility()
        updateDeleteSynButtonVisibility()
        updateDeleteExButtonVisibility()
    }

    fun disableEditableMode() {
        _isEditable.value = false

        updateAddTrButtonVisibility()
        updateAddSynButtonVisibility()
        updateAddExButtonVisibility()

        updateDeleteTrButtonVisibility()
        updateDeleteSynButtonVisibility()
        updateDeleteExButtonVisibility()
    }

    fun notifyToEventIsHandled(event: UiEvent) {
        event.isHandled = true
    }

    fun saveChanges(definition: WordDefinition) {
        val addedDefinition = if (saveMode == DefinitionDetailsFragment.SAVE_MODE_COPY) {
            definition.copy(
                id = 0L,
                allTranslations = definition.allTranslations.filter { it.isNotEmpty() },
                synonyms = definition.synonyms.filter { it.isNotEmpty() },
                examples = definition.examples.filter { it.originalText.isNotEmpty() }
            )
        } else {
            definition.copy(
                allTranslations = definition.allTranslations.filter { it.isNotEmpty() },
                synonyms = definition.synonyms.filter { it.isNotEmpty() },
                examples = definition.examples.filter { it.originalText.isNotEmpty() }
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            val dictionary = dictionariesRepository.getDictionaryById(dictionaryId)
            if (addedDefinition.id == NEW_WORD_ID) {
                _messageEvents
                    .postValue(UiEvent.ShowMessage("Сохранено в \"${dictionary.label}\""))
            } else {
                _messageEvents.postValue(UiEvent.ShowMessage("Изменения сохранены"))
            }
            editWordDefRepository.addNewWordDefinitionWithDictionaries(
                addedDefinition,
                listOf(dictionary)
            )
        }
    }

    fun addTranslationField(definition: WordDefinition) {
        val extendedDefinition = definition.copy(
            allTranslations = definition.allTranslations.toMutableList().apply {
                add(INDEX_AT_INSERT, "")
            }
        )
        _displayedDefinition.value = extendedDefinition
        updateAddTrButtonVisibility()
        updateDeleteTrButtonVisibility()
    }

    fun addSynonymField(definition: WordDefinition) {
        val extendedDefinition = definition.copy(
            synonyms = definition.synonyms.toMutableList().apply { add(INDEX_AT_INSERT, "") }
        )
        _displayedDefinition.value = extendedDefinition
        updateAddSynButtonVisibility()
        updateDeleteSynButtonVisibility()
    }

    fun addExampleField(definition: WordDefinition) {
        val extendedDefinition = definition.copy(
            examples = definition.examples.toMutableList().apply {
                add(INDEX_AT_INSERT, ExampleOfDefinitionUse(originalText = "", translatedText = ""))
            }
        )
        _displayedDefinition.value = extendedDefinition
        updateAddExButtonVisibility()
        updateDeleteExButtonVisibility()
    }

    fun deleteTranslation(position: Int, definition: WordDefinition) {
        val extendedDefinition = definition.copy(
            allTranslations = definition.allTranslations.toMutableList().apply { removeAt(position) }
        )
        _displayedDefinition.value = extendedDefinition
        updateAddTrButtonVisibility()
        updateDeleteTrButtonVisibility()
    }

    fun deleteSynonym(position: Int, definition: WordDefinition) {
        val extendedDefinition = definition.copy(
            synonyms = definition.synonyms.toMutableList().apply { removeAt(position) }
        )
        _displayedDefinition.value = extendedDefinition
        updateAddSynButtonVisibility()
        updateDeleteSynButtonVisibility()
    }

    fun deleteExample(position: Int, definition: WordDefinition) {
        val extendedDefinition = definition.copy(
            examples = definition.examples.toMutableList().apply { removeAt(position) }
        )
        _displayedDefinition.value = extendedDefinition
        updateAddExButtonVisibility()
        updateDeleteExButtonVisibility()
    }

    private fun updateAddTrButtonVisibility() {
        val prevState = _isAddTrButtonVisible.value ?: false

        val size = _displayedDefinition.value?.allTranslations?.size ?: SIZE_EMPTY
        val newState = (_isEditable.value == true) && (size < MAX_LISTS_SIZE)
        if (newState != prevState) {
            _isAddTrButtonVisible.value = newState
        }
    }

    private fun updateAddSynButtonVisibility() {
        val prevState = _isAddSynButtonVisible.value ?: false

        val size = _displayedDefinition.value?.synonyms?.size ?: SIZE_EMPTY
        val newState = (_isEditable.value == true) && (size < MAX_LISTS_SIZE)
        if (newState != prevState) {
            _isAddSynButtonVisible.value = newState
        }
    }

    private fun updateAddExButtonVisibility() {
        val prevState = _isAddExButtonVisible.value ?: false

        val size = _displayedDefinition.value?.examples?.size ?: SIZE_EMPTY
        val newState = (_isEditable.value == true) && (size < MAX_EXAMPLES_SIZE)
        if (newState != prevState) {
            _isAddExButtonVisible.value = newState
        }
    }

    private fun updateDeleteTrButtonVisibility() {
        val prevState = _isDeleteTrButtonVisible.value ?: false

        val size = _displayedDefinition.value?.allTranslations?.size ?: SIZE_EMPTY
        val newState = (_isEditable.value == true) && (size > MIN_LIST_SIZE)
        if (newState != prevState) {
            _isDeleteTrButtonVisible.value = newState
        }
    }

    private fun updateDeleteSynButtonVisibility() {
        val prevState = _isDeleteSynButtonVisible.value ?: false

        val size = _displayedDefinition.value?.synonyms?.size ?: SIZE_EMPTY
        val newState = (_isEditable.value == true) && (size > MIN_LIST_SIZE)
        if (newState != prevState) {
            _isDeleteSynButtonVisible.value = newState
        }
    }

    private fun updateDeleteExButtonVisibility() {
        val prevState = _isDeleteExButtonVisible.value ?: false

        val size = _displayedDefinition.value?.examples?.size ?: SIZE_EMPTY
        val newState = (_isEditable.value == true) && (size > MIN_LIST_SIZE)
        if (newState != prevState) {
            _isDeleteExButtonVisible.value = newState
        }
    }

    private fun setInitialState(definition: WordDefinition?) {
        _isEditable.value = definition?.writing?.isEmpty() == true

        updateAddTrButtonVisibility()
        updateAddSynButtonVisibility()
        updateAddExButtonVisibility()

        updateDeleteTrButtonVisibility()
        updateDeleteSynButtonVisibility()
        updateDeleteExButtonVisibility()
    }

    private fun createDefinitionStub(): WordDefinition {
        return WordDefinition(
            id = 0L,
            writing = "",
            partOfSpeech = null,
            transcription = null,
            synonyms = emptyList(),
            mainTranslation = "",
            allTranslations = emptyList(),
            examples = emptyList()
        )
    }

    private fun prepareDefinitionToShowing(wordDefinition: WordDefinition?): WordDefinition {
        val definition = wordDefinition ?: createDefinitionStub()
        return definition.copy(
            allTranslations = if (definition.allTranslations.isEmpty()) {
                listOf("")
            } else {
                definition.allTranslations
            },
            synonyms = if (definition.synonyms.isEmpty()) {
                listOf("")
            } else {
                definition.synonyms
            },
            examples = if (definition.examples.isEmpty()) {
                listOf(ExampleOfDefinitionUse(originalText = "", translatedText = ""))
            } else {
                definition.examples
            }
        )
    }

    companion object {
        private const val NEW_WORD_ID = 0L
        private const val INDEX_AT_INSERT = 0
        private const val SIZE_EMPTY = 0
        private const val MIN_LIST_SIZE = 1
        private const val MAX_LISTS_SIZE = 5
        private const val MAX_EXAMPLES_SIZE = 3
    }
}
