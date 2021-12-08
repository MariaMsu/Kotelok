package com.designdrivendevelopment.kotelok.screens.dictionaries.addDictionaryScreen

import androidx.recyclerview.widget.DiffUtil

class SelectableWordDefDiffCallback(
    private val oldList: List<SelectableWordDefinition>,
    private val newList: List<SelectableWordDefinition>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].def.id == newList[newItemPosition].def.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
