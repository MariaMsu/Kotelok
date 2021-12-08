package com.designdrivendevelopment.kotelok.screens.statistics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.entities.DictionaryStat

class StatisticAdapter(private val dictionaryStatList: List<DictionaryStat>) :
    RecyclerView.Adapter<StatisticAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textDictName: TextView
        val textAverageSkillLevel: TextView

        init {
            // Define click listener for the ViewHolder's View.
            textDictName = view.findViewById(R.id.recycleItemDictName)
            textAverageSkillLevel = view.findViewById(R.id.recycleItemAverageSkillLevel)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_statisitics, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textDictName.text = dictionaryStatList[position].label
        viewHolder.textAverageSkillLevel.text =
            String.format(viewHolder.itemView.context.getString(R.string.averageSkill),
                dictionaryStatList[position].averageSkillLevel
            )
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dictionaryStatList.size
}
