package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.selection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.ItemWithTypesAdapter

class ItemWithTypeDetailsLookup(
    private val recyclerView: RecyclerView
) : ItemDetailsLookup<String>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<String>? {
        val itemView = recyclerView.findChildViewUnder(e.x, e.y)

        if (itemView != null) {
            val viewHolder = recyclerView.getChildViewHolder(itemView)
            return when (viewHolder) {
                is ItemWithTypesAdapter.CategoryHeaderViewHolder -> viewHolder.getItemDetails()
                is ItemWithTypesAdapter.WordDefinitionViewHolder -> viewHolder.getItemDetails()
                else -> null
            }
        }

        return null
    }
}
