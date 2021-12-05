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
    private val _displayMode: MutableLiveData<Int> = MutableLiveData(DefinitionDetailsFragment.READ_ONLY)

    val displayedDefinition: LiveData<WordDefinition?>
        get() = _displayedDefinition
    val displayMode: LiveData<Int>
        get() = _displayMode

    fun setWriteAndReadMode() {
        _displayMode.value = DefinitionDetailsFragment.WRITE_AND_READ
    }

    fun setReadOnlyMode() {
        _displayMode.value = DefinitionDetailsFragment.READ_ONLY
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
    }

    fun addSynonymField() {
        val definition = displayedDefinition.value
        val extendedDefinition = definition?.copy(
            synonyms = definition.synonyms.toMutableList().apply { add("") }
        )
        _displayedDefinition.value = extendedDefinition
    }

    fun addExampleField() {
        val definition = displayedDefinition.value
        val extendedDefinition = definition?.copy(
            examples = definition.examples.toMutableList().apply {
                add(ExampleOfDefinitionUse(originalText = "", translatedText = ""))
            }
        )
        _displayedDefinition.value = extendedDefinition
    }

    fun deleteTranslation(translation: String) {
        val definition = displayedDefinition.value
        val extendedDefinition = definition?.copy(
            allTranslations = definition.allTranslations.toMutableList().apply {
                remove(translation)
            }
        )
        _displayedDefinition.value = extendedDefinition
    }

    fun deleteSynonym(synonym: String) {
        val definition = displayedDefinition.value
        val extendedDefinition = definition?.copy(
            synonyms = definition.synonyms.toMutableList().apply {
                remove(synonym)
            }
        )
        _displayedDefinition.value = extendedDefinition
    }
}
