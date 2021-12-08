package com.designdrivendevelopment.kotelok.screens.dictionaries.addDictionaryScreen

import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.selection.SelectableItem

data class SelectableWordDefinition(
    val def: WordDefinition,
    override val isSelected: Boolean = false,
    override val isPartOfSelection: Boolean = false
) : SelectableItem
