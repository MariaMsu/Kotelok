package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes

class ButtonItem(val buttonText: String) : ItemWithType {
    override val viewType: Int
        get() = ItemViewTypes.ITEM_BUTTON
}
