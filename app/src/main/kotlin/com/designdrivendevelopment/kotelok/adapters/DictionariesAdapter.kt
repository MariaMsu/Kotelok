package com.designdrivendevelopment.kotelok.adapters

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.*
import com.designdrivendevelopment.kotelok.entity.Dictionary

class DictionariesAdapter(var dictList: MutableList<Dictionary>, fm: FragmentManager, context: Context): RecyclerView.Adapter<DictionariesAdapter.DictionaryViewHolder>(), Filterable {
    var orig: List<Dictionary>? = null
    val fragM = fm
    val cont = context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictionaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dictionary, parent, false)
        return DictionaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DictionaryViewHolder, position: Int) {
        holder.bind(dictList.get(position))
        val training: ImageView = holder.itemView.findViewById(R.id.shapka)
        val heart: ImageView = holder.itemView.findViewById(R.id.heart)
        if(dictList.get(position).isFavorite) {
            heart.setImageResource(R.drawable.favorite)
        }else{
            heart.setImageResource(R.drawable.favorite_border)
        }
        holder.itemView.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("key", dictList.get(position).title)
            val wf = WordsFragment()
            wf.arguments = bundle
            Log.i("search", "клик")
            fragM.beginTransaction().replace(R.id.fl, wf).addToBackStack(null).commit()

        }
        training.setOnClickListener{
            ChooseTrainingDialog().show(fragM, "dialog")
        }
        heart.setOnClickListener {
            //здесь нужна проверка на кол-во словарей в базе и на экране, чтоб
            // обработать нажатие при открытом поиске
            if (dictList.get(position).isFavorite) {
                heart.setImageResource(R.drawable.favorite_border)
            } else {
                heart.setImageResource(R.drawable.favorite)
            }
            dictList[position].isFavorite = !dictList[position].isFavorite
            val d: Dictionary = dictList.get(position)
            if (dictList[position].isFavorite) {//если выбранный словарь фаворит
                dictList.removeAt(position)//удаляем словарь
                dictList.add(0, d)//добавляем копию
                notifyItemMoved(position, 0)//мув
                notifyItemRangeChanged(0, position + 1)
            } else {//если не фаворит
                dictList.removeAt(position)//удаляем словарь
                dictList.add(d)//добавляем копию
                notifyItemMoved(position, dictList.size - 1)//мув
                notifyItemRangeChanged(position, dictList.size)
            }
        }
    }

    override fun getItemCount(): Int = dictList.size

    inner class DictionaryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.title)
        val count: TextView = itemView.findViewById(R.id.word_count)
        fun bind(dict: Dictionary){
            title.text = dict.title
            val word = if (dict.wordCount.length>1 && dict.wordCount.get(dict.wordCount.length-2).equals('1')) "слов" else
                when(dict.wordCount.get(dict.wordCount.length-1).digitToInt()){
                0, in 5..9 -> "слов"
                1 -> "слово"
                in 2..4 -> "слова"
                else -> "слов"}
            count.text = cont.resources.getString(R.string.word_count, dict.wordCount, word)
        }
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterRes= FilterResults()
                val results = mutableListOf<Dictionary>()
                if (orig == null)
                    orig = dictList
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
                dictList = p1?.values as  MutableList<Dictionary>
                notifyDataSetChanged()
            }
        }
    }

    fun updateAdapter(mDataList:  MutableList<Dictionary>) {
        this.dictList = mDataList
        notifyDataSetChanged()
    }
}
