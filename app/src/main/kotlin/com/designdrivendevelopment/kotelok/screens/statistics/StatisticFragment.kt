package com.designdrivendevelopment.kotelok.screens.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication


class StatisticFragment : Fragment(){
    lateinit var viewModel: StatisticViewModel

    private var totalStatisticText : TextView? = null
    private var wordDefinitionsList: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        totalStatisticText = view.findViewById(R.id.totalStatisticText)

        val factory = StatisticViewModelFactory(
            (requireActivity().application as KotelokApplication)
                .appComponent.statisticsRepository
        )
        viewModel = ViewModelProvider(this, factory).get(StatisticViewModel::class.java)

        viewModel.totalStat.observe(
            viewLifecycleOwner,
            {
                totalStatisticText?.text =
                    String.format(getString(R.string.statistics),
                        viewModel.totalStat.value?.totalNumOfWordDefinitions,
                        viewModel.totalStat.value?.totalNumOfCompletedTrainings,
                        viewModel.totalStat.value?.totalNumOfSuccessfullyTrainings,
                    )
            }
        )

        wordDefinitionsList = view.findViewById(R.id.statisticRecycler)
        wordDefinitionsList?.adapter = StatisticAdapter(arrayOf("str1", "str2", "str3"))
        wordDefinitionsList?.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false)
    }
}
