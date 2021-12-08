package com.designdrivendevelopment.kotelok.screens.dictionaries.dictionariesScreen

import androidx.recyclerview.widget.DiffUtil
import com.designdrivendevelopment.kotelok.entities.Dictionary

class DictionariesDiffCallback(
    private val oldDictionaries: List<Dictionary>,
    private val newDictionaries: List<Dictionary>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldDictionaries.size
    }

    override fun getNewListSize(): Int {
        return newDictionaries.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldDictionaries[oldItemPosition].id == newDictionaries[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldDictionaries[oldItemPosition] == newDictionaries[newItemPosition]
    }
}
