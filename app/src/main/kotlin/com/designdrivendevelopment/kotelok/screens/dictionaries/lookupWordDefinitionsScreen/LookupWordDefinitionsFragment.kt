package com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication
import com.designdrivendevelopment.kotelok.screens.dictionaries.lookupWordDefinitionsScreen.viewTypes.ItemWithType
import com.designdrivendevelopment.kotelok.screens.screensUtils.MarginItemDecoration

class LookupWordDefinitionsFragment : Fragment() {
    private var enterWritingText: EditText? = null
    private var lookupButton: Button? = null
    private var resultList: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_lookup_word_definition,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)

        val context = requireContext()
        val adapter = createAdapter(context, emptyList())
        val factory = LookupViewModelFactory(
            (requireActivity().application as KotelokApplication)
                .appComponent.lookupWordDefRepository
        )
        val lookupViewModel = setupFragmentViewModel(context, this, factory, adapter)
        resultList?.adapter = adapter
        resultList?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val marginItemDecoration = MarginItemDecoration(
            marginVertical = 10,
            marginHorizontal = 12
        )
        resultList?.addItemDecoration(marginItemDecoration)

        lookupButton?.setOnClickListener {
            val writing = enterWritingText?.text?.toString() ?: throw NullPointerException()
            lookupViewModel.lookupByWriting(writing)
            if (resultList?.visibility != View.VISIBLE) {
                resultList?.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearViews()
    }

    private fun createAdapter(
        context: Context,
        items: List<ItemWithType>
    ): ItemWithTypesAdapter {
        return ItemWithTypesAdapter(context, items)
    }

    private fun setupFragmentViewModel(
        context: Context,
        fragment: Fragment,
        factory: LookupViewModelFactory,
        adapter: ItemWithTypesAdapter
    ): LookupViewModel {
        return ViewModelProvider(fragment, factory)[LookupViewModel::class.java].apply {
            foundDefinitions.observe(fragment) { newItems ->
                onItemsListChanged(newItems, adapter)
            }
            events.observe(fragment) { event ->
                if (!event.isHandled) {
                    sendMessage(context, event.message)
                    notifyToEventIsHandled(event)
                }
            }
        }
    }

    private fun sendMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun onItemsListChanged(
        newItems: List<ItemWithType>,
        adapter: ItemWithTypesAdapter
    ) {
        val diffCallback = ItemsDiffUtilCallback(
            oldList = adapter.items,
            newList = newItems
        )
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(adapter)
        adapter.items = newItems
    }

    private fun initViews(view: View) {
        enterWritingText = view.findViewById(R.id.enter_writing_text)
        lookupButton = view.findViewById(R.id.lookup_button)
        resultList = view.findViewById(R.id.lookup_word_results_list)
    }

    private fun clearViews() {
        enterWritingText = null
        lookupButton = null
        resultList = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = LookupWordDefinitionsFragment()
    }
}
