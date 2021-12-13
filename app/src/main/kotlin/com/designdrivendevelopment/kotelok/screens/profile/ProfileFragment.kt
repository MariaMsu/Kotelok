package com.designdrivendevelopment.kotelok.screens.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.db.williamchart.view.DonutChartView
import com.db.williamchart.view.HorizontalBarChartView
import com.designdrivendevelopment.kotelok.R
import com.designdrivendevelopment.kotelok.application.KotelokApplication
import com.designdrivendevelopment.kotelok.screens.screensUtils.getColorFromAttr

class ProfileFragment : Fragment() {
    private var skillChart: HorizontalBarChartView? = null
    private var sizeChart: HorizontalBarChartView? = null
    private var answersDonutChart: DonutChartView? = null
    private var totalAnswersText: TextView? = null
    private var rightAnswersText: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false).also { view ->
            view?.findViewById<DonutChartView>(R.id.answers_chart)
                ?.show(listOf())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)

        val context = requireContext()
        val activity = requireActivity()
        activity.title = getString(R.string.title_statistic)
        val factory = ProfileViewModelFactory(
            (activity.application as KotelokApplication)
                .appComponent.statisticsRepository
        )
        val viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
        viewModel.bestBySkillStat.observe(this, this::setupSkillChart)
        viewModel.bestBySizeStat.observe(this, this::setupSizeChart)
        viewModel.answersValues.observe(this) { values ->
            setupAnswersChart(values, context)
            totalAnswersText?.text = getString(R.string.total_answers, values[2].toInt())
            rightAnswersText?.text = getString(R.string.right_answers, values[1].toInt())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearViews()
    }

    private fun setupSkillChart(values: List<Pair<String, Float>>) {
        skillChart?.show(values)
    }

    private fun setupSizeChart(values: List<Pair<String, Float>>) {
        sizeChart?.show(values)
    }

    private fun setupAnswersChart(values: List<Float>, context: Context) {
        answersDonutChart?.donutTotal = values.last()
        answersDonutChart?.donutColors = intArrayOf(
            context.getColorFromAttr(R.attr.donutWrongColor),
            context.getColorFromAttr(R.attr.donutRightColor),
        )
        answersDonutChart?.show(values.take(DONUT_SECTION_NUMBER))
    }

    private fun initViews(view: View) {
        answersDonutChart = view.findViewById(R.id.answers_chart)
        sizeChart = view.findViewById(R.id.size_chart)
        skillChart = view.findViewById(R.id.skill_chart)
        totalAnswersText = view.findViewById(R.id.total_answers_num)
        rightAnswersText = view.findViewById(R.id.right_answers_num)
    }

    private fun clearViews() {
        answersDonutChart = null
        sizeChart = null
        skillChart = null
        totalAnswersText = null
        rightAnswersText = null
    }

    companion object {
        const val DONUT_SECTION_NUMBER = 2
        const val OPEN_PROFILE_TAG = "open_profile"

        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}
