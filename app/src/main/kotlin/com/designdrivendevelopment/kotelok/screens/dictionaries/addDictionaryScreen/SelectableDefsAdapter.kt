package com.designdrivendevelopment.kotelok.screens.dictionaries.addDictionaryScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.entities.WordDefinition
import com.designdrivendevelopment.kotelok.screens.screensUtils.capitalizeFirstChar

class SelectableDefsAdapter(
    private val context: Context,
    private val definitionSelectionListener: DefinitionSelectionListener,
    var definitions: List<SelectableWordDefinition>
) : RecyclerView.Adapter<SelectableDefsAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val writingText: TextView = view.findViewById(R.id.writing_text)
        private val translationText: TextView = view.findViewById(R.id.translation_text)
        private val originalExampleText: TextView = view.findViewById(R.id.original_example_text)
        private val translationExampleText: TextView =
            view.findViewById(R.id.translation_example_text)
        private val selectionCheckBox: CheckBox = view.findViewById(R.id.selection_checkbox)
        private val playSpeechButton: Button = view.findViewById(R.id.play_speech_btn)

        init {
            playSpeechButton.isVisible = false
            selectionCheckBox.apply {
                isVisible = true
                isClickable = false
                isFocusable = false
            }
        }

        fun onSelectionChanged(isSelected: Boolean) {
            selectionCheckBox.isChecked = isSelected
            itemView.isActivated = isSelected
        }

        fun bind(definition: WordDefinition, isSelected: Boolean) {
            selectionCheckBox.isChecked = isSelected
            itemView.isActivated = isSelected
            writingText.text = definition.writing.capitalizeFirstChar()
            if (definition.partOfSpeech != null) {
                translationText.text = context.resources.getString(
                    R.string.translation_pos_line,
                    definition.mainTranslation.capitalizeFirstChar(),
                    definition.partOfSpeech
                )
            } else {
                translationText.text = definition.mainTranslation.capitalizeFirstChar()
            }
            if (definition.examples.isNotEmpty()) {
                val mainExample = definition.examples.first()
                originalExampleText.visibility = View.VISIBLE
                originalExampleText.text = mainExample.originalText.capitalizeFirstChar()
                if (mainExample.translatedText != null) {
                    translationExampleText.visibility = View.VISIBLE
                    translationExampleText.text = mainExample.translatedText.capitalizeFirstChar()
                } else {
                    translationExampleText.visibility = View.GONE
                }
            } else {
                originalExampleText.visibility = View.GONE
                translationExampleText.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.item_word_definition, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var selectableDefinition = definitions[position]
        holder.bind(selectableDefinition.def, selectableDefinition.isSelected)
        holder.itemView.setOnClickListener {
            val handledDefinition = selectableDefinition
                .copy(isSelected = !selectableDefinition.isSelected)

            definitions = definitions.toMutableList().map { selectableWordDefinition ->
                if (selectableWordDefinition.def.id == handledDefinition.def.id) {
                    handledDefinition
                } else {
                    selectableWordDefinition
                }
            }
            selectableDefinition = handledDefinition
            holder.onSelectionChanged(handledDefinition.isSelected)
            definitionSelectionListener.onDefinitionSelectionChanged(handledDefinition)
        }
    }

    override fun getItemCount(): Int {
        return definitions.size
    }
}

interface DefinitionSelectionListener {
    fun onDefinitionSelectionChanged(selectedDefinition: SelectableWordDefinition)
}
