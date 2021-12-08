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
                unfilteredDictionaries = dictionaries.sortedBy { it.isFavorite }
                _dictionaries.postValue(unfilteredDictionaries)
            }
        }
    }

    fun saveIsFavoriteStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            dictionariesRepository.updateDictionaries(unfilteredDictionaries)
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

    fun updateIsFavoriteStatus(dictionaryId: Long, isFavorite: Boolean) {
        unfilteredDictionaries = unfilteredDictionaries.map { dictionary ->
            if (dictionary.id == dictionaryId) {
                dictionary.copy(isFavorite = isFavorite)
            } else {
                dictionary
            }
        }
    }
}