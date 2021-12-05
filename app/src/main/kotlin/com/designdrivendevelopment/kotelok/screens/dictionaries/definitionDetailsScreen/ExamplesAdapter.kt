package com.designdrivendevelopment.kotelok.screens.dictionaries.definitionDetailsScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.entities.ExampleOfDefinitionUse
import com.google.android.material.textfield.TextInputLayout

class ExamplesAdapter(
    private val context: Context,
    private val deleteExampleClickListener: DeleteExampleClickListener,
    var examples: List<ExampleOfDefinitionUse>
) : RecyclerView.Adapter<ExamplesAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deleteBtn: ImageButton = view.findViewById(R.id.delete_example_btn)
        val exampleOriginalField: TextInputLayout = view.findViewById(R.id.example_original_field)
        val exampleTranslationField: TextInputLayout = view.findViewById(R.id.example_translation_field)

        fun bind(example: ExampleOfDefinitionUse) {
            exampleOriginalField.editText?.setText(example.originalText)
            if (example.translatedText != null) {
                exampleTranslationField.editText?.setText(example.translatedText)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_example, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val example = examples[position]
        holder.bind(example)
        holder.deleteBtn.setOnClickListener {
            deleteExampleClickListener.onDeleteExample(example)
        }
    }

    override fun getItemCount(): Int {
        return examples.size
    }
}

interface DeleteExampleClickListener {
    fun onDeleteExample(example: ExampleOfDefinitionUse)
}
