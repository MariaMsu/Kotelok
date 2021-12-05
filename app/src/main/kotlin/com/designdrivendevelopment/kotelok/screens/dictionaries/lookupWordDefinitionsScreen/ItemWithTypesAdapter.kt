package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.selection.stringKey
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.CategoryHeaderItem
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.ItemViewTypes
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.ItemWithType
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.WordDefinitionItem
import com.designdrivendevelopment.kotelok.screens.screensUtils.PlaySoundBtnClickListener

class ItemWithTypesAdapter(
    var items: List<ItemWithType>,
    private val context: Context,
    private val playSoundBtnClickListener: PlaySoundBtnClickListener,
    private val definitionClickListener: DefinitionClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class WordDefinitionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val selectionMarker: CheckBox = view.findViewById(R.id.selection_checkbox)
        private val writingText: TextView = view.findViewById(R.id.writing_text)
        private val translationText: TextView = view.findViewById(R.id.translation_text)
        private val originalExampleText: TextView = view.findViewById(R.id.original_example_text)
        private val translationExampleText: TextView =
            view.findViewById(R.id.translation_example_text)
        private val playSpeechBtn: Button =
            view.findViewById(R.id.play_speech_btn)

        private fun String.capitalize(): String {
            return this.replaceFirstChar { firstChar ->
                if (firstChar.isLowerCase()) firstChar.titlecase() else firstChar.toString()
            }
        }

        init {
            selectionMarker.isClickable = false
            selectionMarker.isFocusable = false
        }

        fun bind(definitionItem: WordDefinitionItem) {
            val definition = definitionItem.data
            itemView.isActivated = definitionItem.isSelected
            selectionMarker.isChecked = definitionItem.isSelected
            selectionMarker.isVisible = definitionItem.isPartOfSelection
            playSpeechBtn.isVisible = !definitionItem.isPartOfSelection

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

//        fun isSelectionShowed(): Boolean {
//            return selectionMarker.isVisible
//        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> {
            return object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int {
                    return adapterPosition
                }

                override fun getSelectionKey(): String {
                    return items[adapterPosition].stringKey
                }
            }
        }

//        fun replaceOnCheckbox(onAnimationEnd: (() -> Unit)? = null): ObjectAnimator {
//            isAnimated = true
//            val hideButton = hideViewAnimation(playSpeechBtn, duration = 100)
//            val showCheckBox = showViewAnimation(selectionMarker, duration = 100)
//            showCheckBox.addListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator?) {
//                    onAnimationEnd?.invoke()
//                }
//            })
//            hideButton.addListener(
//                object : AnimatorListenerAdapter() {
//                    override fun onAnimationEnd(animation: Animator?) {
//                        playSpeechBtn.isVisible = false
//                        selectionMarker.isClickable = false
//                        selectionMarker.alpha = 0f
//                        selectionMarker.isVisible = true
//                        showCheckBox.start()
//                    }
//                }
//            )
//            return hideButton
//        }

//        fun replaceOnButton(onAnimationEnd: (() -> Unit)? = null): ObjectAnimator {
//            isAnimated = true
//            val hideCheckBox = hideViewAnimation(selectionMarker, duration = 100)
//            val showButton = showViewAnimation(playSpeechBtn, duration = 100)
//            showButton.addListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator?) {
//                    onAnimationEnd?.invoke()
//                }
//            })
//            hideCheckBox.addListener(
//                object : AnimatorListenerAdapter() {
//                    override fun onAnimationEnd(animation: Animator?) {
//                        selectionMarker.isVisible = false
//                        playSpeechBtn.alpha = 0f
//                        playSpeechBtn.isVisible = true
//                        showButton.start()
//                    }
//                }
//            )
//            return hideCheckBox
//        }
    }

    inner class CategoryHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val categoryHeaderText: TextView = view.findViewById(R.id.category_header_text)

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> {
            return object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int {
                    return adapterPosition
                }

                override fun getSelectionKey(): String {
                    return items[adapterPosition].stringKey
                }
            }
        }

        fun bind(categoryHeader: CategoryHeaderItem) {
            categoryHeaderText.text = categoryHeader.header
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
            else -> throw IllegalArgumentException("Unexpected View type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (item.viewType) {
            ItemViewTypes.ITEM_WORD_DEFINITION -> {
                (holder as WordDefinitionViewHolder).bind((item as WordDefinitionItem))
                val playSoundBtn: Button = holder.itemView.findViewById(R.id.play_speech_btn)
                val definition = item.data
                val text = definition.writing
                playSoundBtn.setOnClickListener {
                    playSoundBtnClickListener.onPlayBtnClick(text)
                }
                holder.itemView.setOnClickListener {
                    definitionClickListener.onClickToDefinition(definition)
                }
            }
            ItemViewTypes.ITEM_CATEGORY_HEADER -> {
                (holder as CategoryHeaderViewHolder).bind((item as CategoryHeaderItem))
            }
            else -> throw IllegalArgumentException("Unexpected View type")
        }
    }

//    Под сомнением
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }
}
