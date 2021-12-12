package com.designdrivendevelopment.kotelok.screens.recognize

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R

class WordsAdapter(
    private val context: Context,
    private val words: List<Word>
) : RecyclerView.Adapter<WordsAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordButton: TextView = view.findViewById(R.id.word_button)

        fun bind(word: Word) {
            wordButton.text = word.writing
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.item_word, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = words[position]
        holder.bind(word)
    }

    override fun getItemCount(): Int {
        return words.size
    }
}