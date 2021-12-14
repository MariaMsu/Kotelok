package com.designdrivendevelopment.kotelok.screens.dictionaries.dictionaryDetailsScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.screens.sharedWordDefProvider.SharedWordDefinitionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DictDetailsViewModel(
    private val dictionaryId: Long,
    private val dictWordDefinitionsRepository: DictionaryWordDefinitionsRepository,
    private val sharedWordDefinitionProvider: SharedWordDefinitionProvider
) : ViewModel() {
    private var filteredDefinitions: List<WordDefinition> = emptyList()
    private var unfilteredDefinitions: List<WordDefinition> = emptyList()
    private val deletedDefinitions: MutableList<WordDefinition> = mutableListOf()
    private val _dictionaryDefinitions = MutableLiveData<List<WordDefinition>>(emptyList())
    val dictionaryDefinitions: LiveData<List<WordDefinition>> = _dictionaryDefinitions

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dictWordDefinitionsRepository.getDefinitionsFlowByDictId(dictionaryId)
                .collect { definitions ->
                    unfilteredDefinitions = definitions.sortedWith(compareBy({ it.writing }, { it.id }))
                    if (filteredDefinitions.isEmpty()) {
                        _dictionaryDefinitions.postValue(unfilteredDefinitions)
                    } else {
                        _dictionaryDefinitions.postValue(filteredDefinitions)
                    }
                }
        }
    }

    fun setDisplayedDefinition(definition: WordDefinition?) {
        sharedWordDefinitionProvider.sharedWordDefinition = definition
    }

    fun filter(text: String) {
        if (text.isEmpty()) {
            filteredDefinitions = emptyList()
            _dictionaryDefinitions.value = unfilteredDefinitions
        } else {
            filteredDefinitions = unfilteredDefinitions.filter { definition ->
                definition.writing.startsWith(text, ignoreCase = true)
            }
            _dictionaryDefinitions.value = filteredDefinitions
        }
    }

    fun deleteDictionaries() {
        viewModelScope.launch(Dispatchers.IO) {
            dictWordDefinitionsRepository
                .deleteDefinitionsFromDictionary(dictionaryId, deletedDefinitions)
            deletedDefinitions.clear()
        }
    }

    fun deleteDictionary(position: Int) {
        if (filteredDefinitions.isEmpty()) {
            deletedDefinitions.add(unfilteredDefinitions[position])
            unfilteredDefinitions = unfilteredDefinitions
                .toMutableList().apply { removeAt(position) }
            _dictionaryDefinitions.value = unfilteredDefinitions
        } else {
            deletedDefinitions.add(filteredDefinitions[position])
            unfilteredDefinitions = unfilteredDefinitions
                .toMutableList().apply { remove(filteredDefinitions[position]) }
            filteredDefinitions = filteredDefinitions
                .toMutableList().apply { removeAt(position) }
            _dictionaryDefinitions.value = filteredDefinitions
        }
    }

    fun restoreDictionary(position: Int, definition: WordDefinition) {
        deletedDefinitions.remove(definition)
        if (filteredDefinitions.isEmpty()) {
            unfilteredDefinitions = unfilteredDefinitions
                .toMutableList().apply { add(position, definition) }
            _dictionaryDefinitions.value = unfilteredDefinitions
        } else {
            filteredDefinitions = filteredDefinitions
                .toMutableList().apply { add(position, definition) }
            unfilteredDefinitions = unfilteredDefinitions
                .toMutableList().apply { add(definition) }.sortedWith(compareBy({ it.writing }, { it.id }))
            _dictionaryDefinitions.value = filteredDefinitions
        }
    }
}
