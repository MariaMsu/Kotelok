package com.designdrivendevelopment.kotelok.screens.dictionaries.addDictionaryScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designdrivendevelopment.kotelok.screens.dictionaries.DictionariesRepository
import com.designdrivendevelopment.kotelok.screens.dictionaries.dictionaryDetailsScreen.DictionaryWordDefinitionsRepository

class AddDictViewModelFactory(
    private val dictionariesRepository: DictionariesRepository,
    private val dictDefinitionsRepository: DictionaryWordDefinitionsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddDictViewModel::class.java)) {
            return AddDictViewModel(dictionariesRepository, dictDefinitionsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
