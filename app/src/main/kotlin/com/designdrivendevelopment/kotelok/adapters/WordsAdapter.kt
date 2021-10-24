package com.designdrivendevelopment.kotelok.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.entity.Word

class WordsAdapter(var wordList: List<Word>): RecyclerView.Adapter<WordsAdapter.WordViewHolder>(), Filterable {
    var orig: List<Word>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        Log.i("search", "есть")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(wordList.get(position))
    }

    override fun getItemCount(): Int = wordList.size

    class WordViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.word_title)
        val definitions: TextView = itemView.findViewById(R.id.definitions)
        fun bind(word: Word){
            title.text = word.title
            definitions.text = word.definitions
        }
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterRes= FilterResults()
                val results = mutableListOf<Word>()
                Log.i("search", "зашел")
                if (orig == null)
                    orig = wordList
                if(p0 != null){
                    if(orig != null && orig!!.size > 0){
                        Log.i("search", orig!!.size.toString())
                        orig!!.forEach{
                            if (it.title.lowercase().contains(p0.toString().lowercase())){
                                results.add(it)
                            }
                        }
                    }
                    filterRes.values = results
                }
                return filterRes
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                wordList = p1?.values as List<Word>
                notifyDataSetChanged()
            }
        }
    }

    fun updateAdapter(mDataList: List<Word>) {
        this.wordList = mDataList
        notifyDataSetChanged()
    }
}
