package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.ButtonItem
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.CategoryHeaderItem
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.ItemViewTypes
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.ItemWithType
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.WordDefinitionItem

class ItemWithTypesAdapter(
    private val context: Context,
    var items: List<ItemWithType>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private inner class WordDefinitionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val writingText: TextView = view.findViewById(R.id.writing_text)
        private val translationText: TextView = view.findViewById(R.id.translation_text)
        private val originalExampleText: TextView = view.findViewById(R.id.original_example_text)
        private val translationExampleText: TextView =
            view.findViewById(R.id.translation_example_text)

        private fun String.capitalize(): String {
            return this.replaceFirstChar { firstChar ->
                if (firstChar.isLowerCase()) firstChar.titlecase() else firstChar.toString()
            }
        }

        fun bind(definitionItem: WordDefinitionItem) {
            val definition = definitionItem.data
            writingText.text = definition.writing.capitalize()
            if (definition.partOfSpeech != null) {
                translationText.text = context.resources.getString(
                    R.string.translation_pos_line,
                    definition.mainTranslation.capitalize(),
                    definition.partOfSpeech
                )
            } else {
                translationText.text = definition.mainTranslation.capitalize()
            }
            if (definition.examples.isNotEmpty()) {
                val mainExample = definition.examples.first()
                originalExampleText.visibility = View.VISIBLE
                originalExampleText.text = mainExample.originalText.capitalize()
                if (mainExample.translatedText != null) {
                    translationExampleText.visibility = View.VISIBLE
                    translationExampleText.text = mainExample.translatedText.capitalize()
                } else {
                    translationExampleText.visibility = View.GONE
                }
            } else {
                originalExampleText.visibility = View.GONE
                translationExampleText.visibility = View.GONE
            }
        }
    }

    private inner class CategoryHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryHeaderText: TextView = view.findViewById(R.id.category_header_text)

        fun bind(categoryHeader: CategoryHeaderItem) {
            categoryHeaderText.text = categoryHeader.header
        }
    }

    private inner class ButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buttonText: TextView = view.findViewById(R.id.add_definition_button)

        fun bind(button: ButtonItem) {
            buttonText.text = button.buttonText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            ItemViewTypes.ITEM_WORD_DEFINITION -> {
                WordDefinitionViewHolder(
                    inflater.inflate(
                        R.layout.item_word_definition,
                        parent,
                        false
                    )
                )
            }
            ItemViewTypes.ITEM_CATEGORY_HEADER -> {
                CategoryHeaderViewHolder(
                    inflater.inflate(
                        R.layout.item_category_header,
                        parent,
                        false
                    )
                )
            }
            ItemViewTypes.ITEM_BUTTON -> {
                ButtonViewHolder(
                    inflater.inflate(
                        R.layout.add_word_def_button,
                        parent,
                        false
                    )
                )
            }
            else -> throw IllegalArgumentException("Unexpected View type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (item.viewType) {
            ItemViewTypes.ITEM_WORD_DEFINITION -> {
                (holder as WordDefinitionViewHolder).bind((item as WordDefinitionItem))
            }
            ItemViewTypes.ITEM_CATEGORY_HEADER -> {
                (holder as CategoryHeaderViewHolder).bind((item as CategoryHeaderItem))
            }
            ItemViewTypes.ITEM_BUTTON -> {
                (holder as ButtonViewHolder).bind((item as ButtonItem))
            }
            else -> throw IllegalArgumentException("Unexpected View type")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }
}
