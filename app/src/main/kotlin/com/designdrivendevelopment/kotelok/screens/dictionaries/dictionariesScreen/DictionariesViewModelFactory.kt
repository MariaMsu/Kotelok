package com.designdrivendevelopment.kotelok.screens.dictionaries.dictionariesScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designdrivendevelopment.kotelok.screens.dictionaries.DictionariesRepository

class DictionariesViewModelFactory(
    private val dictionariesRepository: DictionariesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DictionariesViewModel::class.java)) {
            return DictionariesViewModel(dictionariesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
