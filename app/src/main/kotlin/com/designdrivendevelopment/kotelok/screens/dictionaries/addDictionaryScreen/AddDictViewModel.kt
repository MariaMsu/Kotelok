package com.designdrivendevelopment.kotelok.screens.dictionaries.addDictionaryScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.screens.dictionaries.DictionariesRepository
import com.designdrivendevelopment.kotelok.screens.dictionaries.dictionaryDetailsScreen.DictionaryWordDefinitionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddDictViewModel(
    private val dictionariesRepository: DictionariesRepository,
    private val dictDefinitionsRepository: DictionaryWordDefinitionsRepository
) : ViewModel() {
    private val _allDefinitions: MutableLiveData<List<SelectableWordDefinition>> = MutableLiveData()
    private var unfilteredDefinitions: List<SelectableWordDefinition> = emptyList()
    private var selectedDefinitions: MutableList<WordDefinition> = mutableListOf()
    val allDefinitions: LiveData<List<SelectableWordDefinition>>
        get() = _allDefinitions

    init {
        loadAllDefinitions()
    }

    private fun loadAllDefinitions() {
        viewModelScope.launch(Dispatchers.IO) {
            unfilteredDefinitions = dictDefinitionsRepository.getAllDefinitions()
                .map { SelectableWordDefinition(it) }
            _allDefinitions.postValue(unfilteredDefinitions)
        }
    }

    fun changeItemSelection(definition: WordDefinition, isSelected: Boolean) {
        if (isSelected) {
            selectedDefinitions.add(definition)
        } else {
            selectedDefinitions.remove(definition)
        }
        unfilteredDefinitions = unfilteredDefinitions.map { selectableDefinition ->
            if (selectableDefinition.def == definition) {
                selectableDefinition.copy(isSelected = isSelected)
            } else {
                selectableDefinition
            }
        }
        _allDefinitions.value = unfilteredDefinitions
    }

    fun filter(text: String) {
        if (text.isEmpty()) {
            _allDefinitions.value = unfilteredDefinitions
        } else {
            val filteredDictionaries = unfilteredDefinitions.filter { selectableDefinition ->
                selectableDefinition.def.writing.startsWith(text, ignoreCase = true)
            }
            _allDefinitions.value = filteredDictionaries
        }
    }

    fun addDictionary(label: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dictionariesRepository.addDictionary(
                Dictionary(
                    id = Dictionary.NEW_DICTIONARY_ID,
                    label = label,
                    size = Dictionary.SIZE_EMPTY,
                    isFavorite = false
                ),
                selectedDefinitions
            )
        }
    }
}
