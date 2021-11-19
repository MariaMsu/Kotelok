package com.designdrivendevelopment.kotelok.screens.dictionaries.dictionaryDetailsScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.entities.WordDefinition

class WordDefinitionsAdapter(
    private val context: Context,
    private val playSoundBtnClickListener: PlaySoundBtnClickListener,
    var wordDefinitions: List<WordDefinition>
) : RecyclerView.Adapter<WordDefinitionsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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

        fun bind(definition: WordDefinition) {
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
                if (mainExample.translatedText.isNotEmpty()) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        return ViewHolder(
            layoutInflater.inflate(R.layout.item_word_definition, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(wordDefinitions[position])
        val playSoundBtn: Button = holder.itemView.findViewById(R.id.play_speech_btn)
        // Воспроизводимый текст необходимо сохранить в переменную, а затем её уже передать в
        // setOnClickListener. В обратном случае в ClickListener`е будет сохранена позиция.
        // Тогда в случае, если позиция item`а будет изменена без вызова onBind, то в листенере
        // будет сохранено старое значение позиции, а массив wordDefinitions при этом
        // будет новый. Поэтому будет впоспроизведен неправильный текст.
        val text = wordDefinitions[position].writing
        playSoundBtn.setOnClickListener {
            playSoundBtnClickListener.onPlayBtnClick(text)
        }
    }

    override fun getItemCount(): Int {
        return wordDefinitions.size
    }
}