package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes

import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.selection.SelectableItem

data class WordDefinitionItem(
    private val definition: WordDefinition,
    override val isSelected: Boolean = false,
    override val isPartOfSelection: Boolean = false
) : CommonItem<WordDefinition>(), ItemWithType, SelectableItem {
    override val data: WordDefinition
        get() = definition

    override val viewType: Int
        get() = ItemViewTypes.ITEM_WORD_DEFINITION
}
