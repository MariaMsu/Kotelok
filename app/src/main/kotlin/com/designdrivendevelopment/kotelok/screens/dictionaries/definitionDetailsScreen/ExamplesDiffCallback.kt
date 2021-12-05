package com.designdrivendevelopment.kotelok.screens.dictionaries.definitionDetailsScreen

import androidx.recyclerview.widget.DiffUtil
import com.designdrivendevelopment.kotelok.entities.ExampleOfDefinitionUse

class ExamplesDiffCallback(
    private val oldExamples: List<ExampleOfDefinitionUse>,
    private val newExamples: List<ExampleOfDefinitionUse>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldExamples.size
    }

    override fun getNewListSize(): Int {
        return newExamples.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldExamples[oldItemPosition].originalText == newExamples[newItemPosition].originalText
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldExamples[oldItemPosition] == newExamples[newItemPosition]
    }
}
