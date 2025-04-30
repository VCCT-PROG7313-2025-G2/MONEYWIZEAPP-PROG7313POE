package com.example.moneywizev1.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moneywizev1.BudgetDetailActivity
import com.example.moneywizev1.DatabaseHelper
import com.example.moneywizev1.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        dbHelper = DatabaseHelper(requireContext())

        val listView = binding.budgetListView

        val budgets = dbHelper.getAllBudgets()
        val budgetNames = budgets.map { it.second }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, budgetNames)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedBudget = budgets[position]
            val budgetName = selectedBudget.second
            val budgetDetails = dbHelper.getBudgetDetails(budgetName) // fetch goal and capital

            val intent = Intent(requireContext(), BudgetDetailActivity::class.java)
            intent.putExtra("budgetName", budgetName)
            intent.putExtra("budgetAmount", budgetDetails.first) // goal amount
            intent.putExtra("budgetCapital", budgetDetails.second) // current capital
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

