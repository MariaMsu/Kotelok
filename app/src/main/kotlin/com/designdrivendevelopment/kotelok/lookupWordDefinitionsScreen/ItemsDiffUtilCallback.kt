package com.designdrivendevelopment.kotelok.lookupWordDefinitionsScreen

import androidx.recyclerview.widget.DiffUtil
import com.designdrivendevelopment.kotelok.lookupWordDefinitionsScreen.viewTypes.ButtonItem
import com.designdrivendevelopment.kotelok.lookupWordDefinitionsScreen.viewTypes.CategoryHeaderItem
import com.designdrivendevelopment.kotelok.lookupWordDefinitionsScreen.viewTypes.ItemViewTypes
import com.designdrivendevelopment.kotelok.lookupWordDefinitionsScreen.viewTypes.ItemWithType
import com.designdrivendevelopment.kotelok.lookupWordDefinitionsScreen.viewTypes.WordDefinitionItem

class ItemsDiffUtilCallback(
    private val oldList: List<ItemWithType>,
    private val newList: List<ItemWithType>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].viewType == newList[newItemPosition].viewType
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldList[oldItemPosition].viewType == newList[newItemPosition].viewType) {
            val viewType = newList[newItemPosition].viewType
            return when (viewType) {
                ItemViewTypes.ITEM_WORD_DEFINITION -> {
                    val oldDefinition = (oldList[oldItemPosition] as WordDefinitionItem).data
                    val newDefinition = (newList[newItemPosition] as WordDefinitionItem).data
                    oldDefinition == newDefinition
                }
                ItemViewTypes.ITEM_CATEGORY_HEADER -> {
                    val oldHeader = (oldList[oldItemPosition] as CategoryHeaderItem).header
                    val newHeader = (newList[newItemPosition] as CategoryHeaderItem).header
                    oldHeader == newHeader
                }
                ItemViewTypes.ITEM_BUTTON -> {
                    val oldText = (oldList[oldItemPosition] as ButtonItem).buttonText
                    val newText = (newList[newItemPosition] as ButtonItem).buttonText
                    oldText == newText
                } else -> false
            }
        } else {
            return false
        }
    }
}