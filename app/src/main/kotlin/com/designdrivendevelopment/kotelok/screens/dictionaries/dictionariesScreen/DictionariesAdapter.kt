package com.designdrivendevelopment.kotelok.screens.dictionaries.dictionariesScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.entities.Dictionary

class DictionariesAdapter(
    private val context: Context,
    private val dictionaryClickListener: DictionaryClickListener,
    private val isFavoriteListener: IsFavoriteListener,
    private val learnButtonListener: LearnButtonListener,
) : ListAdapter<Dictionary, DictionariesAdapter.ViewHolder>(
    DictionariesDiffUtil()
) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val label: TextView = view.findViewById(R.id.dictionary_title)
        private val dictionarySize: TextView = view.findViewById(R.id.dictionary_size_text)
        val isFavoriteCheckBox: CheckBox = view.findViewById(R.id.is_favorite_checkbox)
        val learnButton: Button = view.findViewById(R.id.learn_button)

        fun bind(dictionary: Dictionary) {
            label.text = dictionary.label
            dictionarySize.text = when (dictionary.size % RADIX) {
                LAST_NUMBER_ONE -> {
                    context.resources.getString(R.string.size_1, dictionary.size)
                }
                in LAST_NUMBERS_RANGE -> {
                    context.resources.getString(R.string.size_2_4, dictionary.size)
                }
                else -> {
                    context.resources.getString(R.string.size_5_10, dictionary.size)
                }
            }
            isFavoriteCheckBox.isChecked = dictionary.isFavorite
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.item_dictionary, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dictionary = getItem(position)
        holder.bind(dictionary)
        holder.itemView.setOnClickListener {
            dictionaryClickListener.onDictionaryClicked(dictionary)
        }

        holder.isFavoriteCheckBox.setOnClickListener {
            isFavoriteListener.onIsFavoriteChanged(dictionary.id, holder.isFavoriteCheckBox.isChecked)
        }
        holder.learnButton.setOnClickListener {
            learnButtonListener.onLearnClicked(dictionary.id)
        }
    }

    companion object {
        private const val RADIX = 10
        private const val LAST_NUMBER_ONE = 1
        private val LAST_NUMBERS_RANGE = 2..3
    }
}

interface IsFavoriteListener {
    fun onIsFavoriteChanged(dictionaryId: Long, isFavorite: Boolean)
}

interface DictionaryClickListener {
    fun onDictionaryClicked(dictionary: Dictionary)
}

interface LearnButtonListener {
    fun onLearnClicked(dictionaryId: Long)
}
