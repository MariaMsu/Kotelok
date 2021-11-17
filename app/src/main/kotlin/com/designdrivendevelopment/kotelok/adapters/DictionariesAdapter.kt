package com.designdrivendevelopment.kotelok.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.ChooseTrainingDialog
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.WordsFragment
import com.designdrivendevelopment.kotelok.entities.Dictionary

class DictionariesAdapter(var dictList: MutableList<Dictionary>, context: Context) :
    RecyclerView.Adapter<DictionariesAdapter.DictionaryViewHolder>(), Filterable {
    var orig: MutableList<Dictionary>? = null
    val cont = context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictionaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dictionary, parent, false)
        return DictionaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DictionaryViewHolder, position: Int) {
        holder.bind(dictList.get(position))
        val training: ImageView = holder.itemView.findViewById(R.id.shapka)
        val heart: ImageView = holder.itemView.findViewById(R.id.heart)
        if (dictList.get(position).isFavorite) {
            heart.setImageResource(R.drawable.favorite)
        } else {
            heart.setImageResource(R.drawable.favorite_border)
        }
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("key", dictList.get(position).label)
            val wf = WordsFragment()
            wf.arguments = bundle
            Log.i("search", "клик")
            (cont as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, wf).addToBackStack(null).commit()
        }
        training.setOnClickListener {
            ChooseTrainingDialog().show((cont as AppCompatActivity).supportFragmentManager, "dialog")
        }
        heart.setOnClickListener {
            // здесь нужна проверка на кол-во словарей в базе и на экране, чтоб
            // обработать нажатие при открытом поиске
            if (dictList.get(position).isFavorite) {
                heart.setImageResource(R.drawable.favorite_border)
            } else {
                heart.setImageResource(R.drawable.favorite)
            }
            dictList[position].isFavorite = !dictList[position].isFavorite
            val d: Dictionary = dictList.get(position)
            if (dictList[position].isFavorite) { // если выбранный словарь фаворит
                dictList.removeAt(position) // удаляем словарь
                dictList.add(0, d) // добавляем копию

                orig = dictList

                notifyItemMoved(position, 0) // мув
                notifyItemRangeChanged(0, position + 1)
            } else { // если не фаворит
                dictList.removeAt(position) // удаляем словарь
                dictList.add(d) // добавляем копию

                orig = dictList

                notifyItemMoved(position, dictList.size - 1) // мув
                notifyItemRangeChanged(position, dictList.size)
            }
        }
    }

    override fun getItemCount(): Int = dictList.size

    inner class DictionaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.title)
        val count: TextView = itemView.findViewById(R.id.word_count)
        fun bind(dict: Dictionary) {
            title.text = dict.label
            val word = if (dict.wordCount.length> 1 && dict.wordCount.get
                (dict.wordCount.length - 2).equals('1')
            ) "слов" else
                when (dict.wordCount.get(dict.wordCount.length - 1).digitToInt()) {
                    0, in FIVE..NINE -> "слов"
                    1 -> "слово"
                    in 2..FOUR -> "слова"
                    else -> "слов"
                }
            count.text = cont.resources.getString(R.string.word_count, dict.wordCount, word)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterRes = FilterResults()
                val results = mutableListOf<Dictionary>()
                if (orig == null) {
                    orig = dictList
                    Log.i("search", "зашел")
                }
                if (p0 != null) {
                    if (orig != null && orig!!.size > 0) {
                        Log.i("search", orig!!.size.toString())
                        orig!!.forEach {
                            if (it.label.lowercase().contains(p0.toString().lowercase())) {
                                results.add(it)
                            }
                        }
                    }
                    filterRes.values = results
                }
                return filterRes
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                dictList = p1?.values as MutableList<Dictionary>
                notifyDataSetChanged()
            }
        }
    }

    fun updateAdapter(mDataList: MutableList<Dictionary>) {
        this.dictList = mDataList
        notifyDataSetChanged()
    }

    companion object{
        private const val FIVE = 5
        private const val NINE = 9
        private const val FOUR = 4
    }
}
