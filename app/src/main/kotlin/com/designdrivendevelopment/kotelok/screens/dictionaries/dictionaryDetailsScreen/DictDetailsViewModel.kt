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
    dictionaryId: Long,
    private val dictWordDefinitionsRepository: DictionaryWordDefinitionsRepository,
    private val sharedWordDefinitionProvider: SharedWordDefinitionProvider
) : ViewModel() {
    private var unfilteredDefinitions: List<WordDefinition> = emptyList()
    private val _dictionaryDefinitions = MutableLiveData<List<WordDefinition>>(emptyList())
    val dictionaryDefinitions: LiveData<List<WordDefinition>> = _dictionaryDefinitions

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dictWordDefinitionsRepository.getDefinitionsFlowByDictId(dictionaryId)
                .collect { definitions ->
                    unfilteredDefinitions = definitions.sortedBy { it.writing }
                    _dictionaryDefinitions.postValue(unfilteredDefinitions)
                }
        }
    }

    fun setDisplayedDefinition(definition: WordDefinition?) {
        sharedWordDefinitionProvider.sharedWordDefinition = definition
    }

    fun filter(text: String) {
        if (text.isEmpty()) {
            _dictionaryDefinitions.value = unfilteredDefinitions
        } else {
            val filteredDictionaries = unfilteredDefinitions.filter { definition ->
                definition.writing.startsWith(text, ignoreCase = true)
            }
            _dictionaryDefinitions.value = filteredDictionaries
        }
    }
}
