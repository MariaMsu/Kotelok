package com.designdrivendevelopment.kotelok

import androidx.recyclerview.widget.DiffUtil
import com.designdrivendevelopment.kotelok.entities.WordDefinition

class DefinitionsDiffCallback(
    private val oldDefinitionsList: List<WordDefinition>,
    private val newDefinitionsList: List<WordDefinition>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldDefinitionsList.size
    }

    override fun getNewListSize(): Int {
        return newDefinitionsList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldDefinitionsList[oldItemPosition].id == newDefinitionsList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldDefinitionsList[oldItemPosition] == newDefinitionsList[newItemPosition]
    }
}
