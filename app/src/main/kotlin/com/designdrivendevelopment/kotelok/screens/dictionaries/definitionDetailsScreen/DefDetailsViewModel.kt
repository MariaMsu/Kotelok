package com.designdrivendevelopment.kotelok.screens.dictionaries.definitionDetailsScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.ExampleOfDefinitionUse
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.screens.dictionaries.DictionariesRepository
import com.designdrivendevelopment.kotelok.screens.dictionaries.EditWordDefinitionsRepository
import com.designdrivendevelopment.kotelok.screens.sharedWordDefProvider.SharedWordDefinitionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DefDetailsViewModel(
    private val saveMode: Int,
    private val dictionaryId: Long,
    private val editWordDefRepository: EditWordDefinitionsRepository,
    private val dictionariesRepository: DictionariesRepository,
    sharedWordDefProvider: SharedWordDefinitionProvider
) : ViewModel() {
    private val _displayedDefinition: MutableLiveData<WordDefinition?> =
        MutableLiveData(sharedWordDefProvider.sharedWordDefinition)
    private val _isEditable: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _isAddTrButtonVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _isAddSynButtonVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _isAddExButtonVisible: MutableLiveData<Boolean> = MutableLiveData(false)

    val displayedDefinition: LiveData<WordDefinition?>
        get() = _displayedDefinition
    val isEditable: LiveData<Boolean>
        get() = _isEditable
    val isAddTrButtonVisible: LiveData<Boolean>
        get() = _isAddTrButtonVisible
    val isAddSynButtonVisible: LiveData<Boolean>
        get() = _isAddSynButtonVisible
    val isAddExButtonVisible: LiveData<Boolean>
        get() = _isAddExButtonVisible

    fun enableEditableMode() {
        _isEditable.value = true
        updateAddTrButtonVisibility()
        updateAddSynButtonVisibility()
        updateAddExButtonVisibility()
    }

    fun disableEditableMode() {
        _isEditable.value = false
        updateAddTrButtonVisibility()
        updateAddSynButtonVisibility()
        updateAddExButtonVisibility()
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
            editWordDefRepository.addNewWordDefinitionWithDictionaries(
                addedDefinition,
                listOf(dictionary)
            )
        }
    }

    fun addTranslationField() {
        val definition = displayedDefinition.value
        val extendedDefinition = definition?.copy(
            allTranslations = definition.allTranslations.toMutableList().apply { add("") }
        )
        _displayedDefinition.value = extendedDefinition
        updateAddTrButtonVisibility()
    }

    fun addSynonymField() {
        val definition = displayedDefinition.value
        val extendedDefinition = definition?.copy(
            synonyms = definition.synonyms.toMutableList().apply { add("") }
        )
        _displayedDefinition.value = extendedDefinition
        updateAddSynButtonVisibility()
    }

    fun addExampleField() {
        val definition = displayedDefinition.value
        val extendedDefinition = definition?.copy(
            examples = definition.examples.toMutableList().apply {
                add(ExampleOfDefinitionUse(originalText = "", translatedText = ""))
            }
        )
        _displayedDefinition.value = extendedDefinition
        updateAddExButtonVisibility()
    }

    fun deleteTranslation(translation: String) {
        val definition = displayedDefinition.value
        val extendedDefinition = definition?.copy(
            allTranslations = definition.allTranslations.toMutableList().apply {
                remove(translation)
            }
        )
        _displayedDefinition.value = extendedDefinition
        updateAddTrButtonVisibility()
    }

    fun deleteSynonym(synonym: String) {
        val definition = displayedDefinition.value
        val extendedDefinition = definition?.copy(
            synonyms = definition.synonyms.toMutableList().apply {
                remove(synonym)
            }
        )
        _displayedDefinition.value = extendedDefinition
        updateAddSynButtonVisibility()
    }

    fun deleteExample(example: ExampleOfDefinitionUse) {
        val definition = displayedDefinition.value
        val extendedDefinition = definition?.copy(
            examples = definition.examples.toMutableList().apply {
                remove(example)
            }
        )
        _displayedDefinition.value = extendedDefinition
        updateAddExButtonVisibility()
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

    companion object {
        private const val SIZE_EMPTY = 0
        private const val MAX_LISTS_SIZE = 5
        private const val MAX_EXAMPLES_SIZE = 3
    }
}
