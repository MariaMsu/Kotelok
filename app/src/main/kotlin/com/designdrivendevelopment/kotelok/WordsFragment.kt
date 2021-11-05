package com.designdrivendevelopment.kotelok

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.adapters.WordsAdapter
import com.designdrivendevelopment.kotelok.entity.Word

class WordsFragment : Fragment() {
    val wordList = mutableListOf(
        Word("Dog", "Собака, пес"),
        Word("Cat", "Кот"),
        Word("Friend", "Друг")
    )
    val adapter = WordsAdapter(wordList)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setTitle(arguments?.getString("key"))
        return inflater.inflate(R.layout.fragment_words, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv: RecyclerView? = getView()?.findViewById(R.id.words_rv)
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
                    wordList.removeAt(position)
                    // adapter.notifyDataSetChanged()
                    adapter.updateAdapter(wordList)
                }
            }
        ).attachToRecyclerView(view.findViewById(R.id.words_rv))
        rv?.adapter = adapter
        rv?.layoutManager = LinearLayoutManager(context)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        val item = menu.findItem(R.id.search)
        val search: SearchView? = item?.actionView as SearchView?
        item?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        item?.setActionView(search)
        search?.setOnQueryTextListener(
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
    /*companion object {
        @JvmStatic
        fun newInstance() = DictionariesFragment()
    }*/
}
