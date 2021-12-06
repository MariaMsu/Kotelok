package com.designdrivendevelopment.kotelok.screens.dictionaries.definitionDetailsScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.google.android.material.textfield.TextInputLayout

class SynonymsAdapter(
    private val context: Context,
    private val deleteClickListener: DeleteSynonymListener,
    var synonyms: List<String>
) : RecyclerView.Adapter<SynonymsAdapter.ViewHolder>() {
    var isEditable = false

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val synonymField: TextInputLayout = view.findViewById(R.id.synonym_field)
        val deleteBtn: ImageButton = view.findViewById(R.id.delete_synonym_btn)

        init {
            synonymField.isEnabled = isEditable
            deleteBtn.visibility = if (isEditable && synonyms.size > 1) View.VISIBLE else View.INVISIBLE
        }

        fun onBind(translation: String) {
            synonymField.editText?.setText(translation)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_synonym, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val synonym = synonyms[position]
        holder.onBind(synonym)
        holder.deleteBtn.setOnClickListener {
            deleteClickListener.onDeleteSynonym(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return synonyms.size
    }
}

interface DeleteSynonymListener {
    fun onDeleteSynonym(position: Int)
}
