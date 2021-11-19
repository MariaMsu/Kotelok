package com.designdrivendevelopment.kotelok.screens.wordDefinitions.lookupWordDefinitionsScreen.viewTypes

import com.designdrivendevelopment.kotelok.entities.WordDefinition

class WordDefinitionItem(
    private val definition: WordDefinition,
) : CommonItem<WordDefinition>(), ItemWithType {
    override val data: WordDefinition
        get() = definition

    override val viewType: Int
        get() = ItemViewTypes.ITEM_WORD_DEFINITION
}
