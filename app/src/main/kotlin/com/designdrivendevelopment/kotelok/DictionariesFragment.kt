package com.designdrivendevelopment.kotelok

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.adapters.DictionariesAdapter
import com.designdrivendevelopment.kotelok.entities.Dictionary

class DictionariesFragment : Fragment() {
    var dictList = mutableListOf(
        Dictionary(0, "Животные", "12", listOf()),
        Dictionary(1, "Профессии", "1", listOf()),
        Dictionary(2, "Еда", "22", listOf()),
        Dictionary(THREE, "Айти", "35", listOf())
    )
    lateinit var adapter: DictionariesAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_dictionaries, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab: ImageView = view.findViewById(R.id.fab)
        (activity as AppCompatActivity).supportActionBar?.setTitle("Словари")
        val rv: RecyclerView? = getView()?.findViewById(R.id.rv)
        adapter = DictionariesAdapter(dictList, requireContext())
        ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder,
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.absoluteAdapterPosition
                    dictList.removeAt(position)
                    adapter.updateAdapter(dictList)
                }
            }
        ).attachToRecyclerView(view.findViewById(R.id.rv))
        rv?.adapter = adapter
        rv?.layoutManager = LinearLayoutManager(context)
        fab.setOnClickListener {
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
        search.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
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
            }
        )
    }
    companion object {
        const val OPEN_DICTIONARIES_TAG = "open_dictionaries"
        const val THREE: Long = 3;
        @JvmStatic
        fun newInstance() = DictionariesFragment()
    }

    override fun onStop() {
        super.onStop()
        Log.i("search", "onstop")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("search", "save")
    }

    override fun onStart() {
        super.onStart()
        Log.i("search", "start")
    }
}
