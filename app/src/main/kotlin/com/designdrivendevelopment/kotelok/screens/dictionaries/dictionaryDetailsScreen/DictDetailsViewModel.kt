package com.designdrivendevelopment.kotelok.screens.dictionaries.dictionaryDetailsScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.screens.sharedWordDefProvider.SharedWordDefinitionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DictDetailsViewModel(
    dictionaryId: Long,
    private val dictWordDefinitionsRepository: DictionaryWordDefinitionsRepository,
    private val sharedWordDefinitionProvider: SharedWordDefinitionProvider
) : ViewModel() {
    private val _dictionaryDefinitions = MutableLiveData<List<WordDefinition>>(emptyList())
    val dictionaryDefinitions: LiveData<List<WordDefinition>> = _dictionaryDefinitions

    init {
        loadDefinitionsByDictId(dictionaryId)
    }

    private fun loadDefinitionsByDictId(dictionaryId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val definitions = dictWordDefinitionsRepository
                .getDefinitionsByDictionaryId(dictionaryId)
            _dictionaryDefinitions.postValue(definitions)
        }
    }

    fun setDisplayedDefinition(definition: WordDefinition?) {
        sharedWordDefinitionProvider.sharedWordDefinition = definition
    }
}
