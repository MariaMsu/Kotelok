package com.designdrivendevelopment.kotelok.screens.dictionaries

import com.designdrivendevelopment.kotelok.entities.WordDefinition

interface DefinitionClickListener {
    fun onClickToDefinition(wordDefinition: WordDefinition)
}
