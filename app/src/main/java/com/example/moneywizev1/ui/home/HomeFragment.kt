package com.example.moneywizev1.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.moneywizev1.DatabaseHelper
import com.example.moneywizev1.databinding.FragmentHomeBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
//Apadted from tutorial by Furqan Yasin (2025). 
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var lineChart: LineChart
    private lateinit var databaseHelper: DatabaseHelper

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        databaseHelper = DatabaseHelper(requireContext())

        setupLineChart()
        updateChartData()
        updateCategoryInfo()
        setupDataObserver()

        return binding.root
    }

    private fun setupLineChart() {
        lineChart = binding.lineChart
        lineChart.apply {
            description.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            legend.isEnabled = true
            setDrawGridBackground(false)
        }
    }

    private fun updateChartData() {
        val expensesData = databaseHelper.getExpensesOverTime()
        val incomeData = databaseHelper.getIncomeOverTime()

        val expenseEntries = expensesData.mapIndexed { index, value ->
            Entry(index.toFloat(), value.toFloat())
        }

        val incomeEntries = incomeData.mapIndexed { index, value ->
            Entry(index.toFloat(), value.toFloat())
        }

        val expenseDataSet = LineDataSet(expenseEntries, "Expenses").apply {
            color = Color.RED
            setCircleColor(Color.RED)
            valueTextColor = Color.BLACK
        }

        val incomeDataSet = LineDataSet(incomeEntries, "Income").apply {
            color = Color.GREEN
            setCircleColor(Color.GREEN)
            valueTextColor = Color.BLACK
        }

        lineChart.data = LineData(expenseDataSet, incomeDataSet)
        lineChart.invalidate()
    }

    private fun updateCategoryInfo() {
        val highestExpenseCategory = databaseHelper.getCategoryWithMostExpenses()
        val highestIncomeCategory = databaseHelper.getCategoryWithMostIncome()
        val bestBudget = databaseHelper.getBudgetWithHighestCompletion()

        val infoText = """
            📊 Category Analysis
            
            💸 Highest Expense Category: $highestExpenseCategory
            💰 Highest Income Category: $highestIncomeCategory
            🎯 Best Performing Budget: $bestBudget
        """.trimIndent()

        binding.textHome.text = infoText
    }

    private fun setupDataObserver() {
        lifecycleScope.launch {
            while (true) {
                try {
                    updateChartData()
                    updateCategoryInfo()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(5000)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

