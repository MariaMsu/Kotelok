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
    private val _dictionaries = MutableLiveData<List<Dictionary>>()
    private var unfilteredDictionaries: List<Dictionary> = emptyList()
    private var filteredDictionaries: List<Dictionary> = emptyList()
    private val deletedDictionaries: MutableList<Dictionary> = mutableListOf()
    val dictionaries: LiveData<List<Dictionary>>
        get() = _dictionaries

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dictionariesRepository.getAllDictionariesFlow().collect { dictionaries ->
                unfilteredDictionaries = dictionaries
                    .sortedWith(compareBy({ !it.isFavorite }, { it.label }))
                if (filteredDictionaries.isEmpty()) {
                    _dictionaries.postValue(unfilteredDictionaries)
                } else {
                    _dictionaries.postValue(filteredDictionaries)
                }
            }
        }
    }

    fun saveIsFavoriteStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            dictionariesRepository.updateDictionaries(unfilteredDictionaries)
        }
    }

    fun deleteDictionaries() {
        viewModelScope.launch(Dispatchers.IO) {
            if (deletedDictionaries.isNotEmpty()) {
                dictionariesRepository.deleteDictionaries(deletedDictionaries)
            }
            deletedDictionaries.clear()
        }
    }

    fun deleteDictionary(position: Int) {
        if (filteredDictionaries.isEmpty()) {
            deletedDictionaries.add(unfilteredDictionaries[position])
            unfilteredDictionaries = unfilteredDictionaries
                .toMutableList().apply { removeAt(position) }
            _dictionaries.value = unfilteredDictionaries
        } else {
            deletedDictionaries.add(filteredDictionaries[position])
            unfilteredDictionaries = unfilteredDictionaries
                .toMutableList().apply { remove(filteredDictionaries[position]) }
            filteredDictionaries = filteredDictionaries
                .toMutableList().apply { removeAt(position) }
            _dictionaries.value = filteredDictionaries
        }
    }

    fun restoreDictionary(position: Int, dictionary: Dictionary) {
        deletedDictionaries.remove(dictionary)
        if (filteredDictionaries.isEmpty()) {
            unfilteredDictionaries = unfilteredDictionaries
                .toMutableList().apply { add(position, dictionary) }
            _dictionaries.value = unfilteredDictionaries
        } else {
            filteredDictionaries = filteredDictionaries
                .toMutableList().apply { add(position, dictionary) }
            unfilteredDictionaries = unfilteredDictionaries
                .toMutableList().apply { add(dictionary) }
                .sortedWith(compareBy({ !it.isFavorite }, { it.label }))
            _dictionaries.value = filteredDictionaries
        }
    }

    fun filter(text: String) {
        if (text.isEmpty()) {
            filteredDictionaries = emptyList()
            _dictionaries.value = unfilteredDictionaries
        } else {
            filteredDictionaries = unfilteredDictionaries.filter { dictionary ->
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
