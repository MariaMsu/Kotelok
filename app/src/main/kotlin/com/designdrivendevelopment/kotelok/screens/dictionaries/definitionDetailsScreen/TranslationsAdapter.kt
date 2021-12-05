package com.designdrivendevelopment.kotelok.screens.dictionaries.definitionDetailsScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.google.android.material.textfield.TextInputLayout

class TranslationsAdapter(
    private val context: Context,
    private val deleteClickListener: DeleteTranslationListener,
    var translations: List<String>
) : RecyclerView.Adapter<TranslationsAdapter.ViewHolder>() {
    var isEditable: Boolean = false

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val translationField: TextInputLayout = view.findViewById(R.id.translation_field)
        val deleteBtn: ImageButton = view.findViewById(R.id.delete_translation_btn)

        init {
            translationField.isEnabled = isEditable
        }

        fun onBind(translation: String) {
            translationField.editText?.setText(translation)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_translation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val translation = translations[position]
        holder.onBind(translation)
        holder.deleteBtn.setOnClickListener { deleteClickListener.onDeleteTranslation(translation) }
    }

    override fun getItemCount(): Int {
        return translations.size
    }
}

interface DeleteTranslationListener {
    fun onDeleteTranslation(translation: String)
}
