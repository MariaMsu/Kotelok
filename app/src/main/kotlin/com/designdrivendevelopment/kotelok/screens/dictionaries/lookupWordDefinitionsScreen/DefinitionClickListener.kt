package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen

import com.designdrivendevelopment.kotelok.entities.WordDefinition

interface DefinitionClickListener {
    fun onClickToDefinition(wordDefinition: WordDefinition)
}
