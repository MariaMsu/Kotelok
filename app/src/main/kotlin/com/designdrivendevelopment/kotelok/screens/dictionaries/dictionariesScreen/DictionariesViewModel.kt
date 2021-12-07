package com.designdrivendevelopment.kotelok.screens.dictionaries.dictionariesScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designdrivendevelopment.kotelok.entities.Dictionary
import com.designdrivendevelopment.kotelok.screens.dictionaries.DictionariesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DictionariesViewModel(
    private val dictionariesRepository: DictionariesRepository
) : ViewModel() {
    private val _dictionaries = MutableLiveData<List<Dictionary>>(emptyList())
    private var unfilteredDictionaries: List<Dictionary> = emptyList()
    val dictionaries: LiveData<List<Dictionary>>
        get() = _dictionaries

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dictionariesRepository.getAllDictionariesFlow().collect { dictionaries ->
                unfilteredDictionaries = dictionaries
                _dictionaries.postValue(dictionaries)
            }
        }
    }

    fun updateIsFavoriteStatus(dictionaries: List<Dictionary>) {
        viewModelScope.launch(Dispatchers.IO) {
            dictionariesRepository.updateDictionaries(dictionaries)
        }
    }

    fun filter(text: String) {
        if (text.isEmpty()) {
            _dictionaries.value = unfilteredDictionaries
        } else {
            val filteredDictionaries = unfilteredDictionaries.filter { dictionary ->
                dictionary.label.startsWith(text, ignoreCase = true)
            }
            _dictionaries.value = filteredDictionaries
        }
    }
}
