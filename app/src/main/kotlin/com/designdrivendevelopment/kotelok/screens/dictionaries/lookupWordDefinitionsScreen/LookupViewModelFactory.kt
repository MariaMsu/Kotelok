package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designdrivendevelopment.kotelok.screens.dictionaries.DictionariesRepository
import com.designdrivendevelopment.kotelok.screens.dictionaries.EditWordDefinitionsRepository

class LookupViewModelFactory(
    private val lookupWordDefinitionsRepository: LookupWordDefinitionsRepository,
    private val editWordDefinitionsRepository: EditWordDefinitionsRepository,
    private val dictionariesRepository: DictionariesRepository,
    private val dictionaryId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LookupViewModel::class.java)) {
            return LookupViewModel(
                lookupWordDefinitionsRepository,
                editWordDefinitionsRepository,
                dictionariesRepository,
                dictionaryId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
