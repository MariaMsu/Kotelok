package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes

class CategoryHeaderItem(val header: String) : ItemWithType {
    override val viewType: Int
        get() = ItemViewTypes.ITEM_CATEGORY_HEADER
}
