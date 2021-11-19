package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LookupViewModelFactory(
    private val lookupWordDefinitionsRepository: LookupWordDefinitionsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LookupViewModel::class.java)) {
            return LookupViewModel(lookupWordDefinitionsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
