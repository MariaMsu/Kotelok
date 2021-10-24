package com.designdrivendevelopment.kotelok

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.adapters.DictionariesAdapter
import com.designdrivendevelopment.kotelok.entity.Dictionary

class DictionariesFragment(fm: FragmentManager) : Fragment() {
    val dictList = mutableListOf(Dictionary("Животные", "12"), Dictionary("Профессии", "1"),
        Dictionary("Еда", "22"), Dictionary("Айти", "35"))
    val adapter = DictionariesAdapter(dictList, fm, requireContext())
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_dictionaries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab: ImageView = view.findViewById(R.id.fab)
        (activity as AppCompatActivity).supportActionBar?.setTitle("Словари")
        val rv: RecyclerView? = getView()?.findViewById(R.id.rv)
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                dictList.removeAt(position)
                adapter.updateAdapter(dictList)
            }

        }).attachToRecyclerView(view.findViewById(R.id.rv))
        rv?.adapter = adapter
        rv?.layoutManager = LinearLayoutManager(context)
        fab.setOnClickListener{

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.search_menu, menu)
        val item: MenuItem = menu.findItem(R.id.search)
        val search: SearchView = item.actionView as SearchView
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        item.setActionView(search)
        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                if (TextUtils.isEmpty(p0)) {
                    adapter.getFilter().filter("")
                } else {
                    adapter.getFilter().filter(p0.toString())
                }
                return true
            }
        })

    }
    /*companion object {
        @JvmStatic
        fun newInstance() = DictionariesFragment()
    }*/
}
