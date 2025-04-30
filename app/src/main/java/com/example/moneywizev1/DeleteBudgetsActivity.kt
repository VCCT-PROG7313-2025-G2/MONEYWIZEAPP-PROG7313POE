package com.example.moneywizev1

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DeleteBudgetsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_budgets)


        val dbHelper = DatabaseHelper(this)


        val listView = findViewById<ListView>(R.id.listViewBudgets)


        val budgetNames = dbHelper.getAllBudgets3()


        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, budgetNames)
        listView.adapter = adapter


        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedBudgetName = budgetNames[position]


            showConfirmationDialog(selectedBudgetName, dbHelper)
        }

    }

    private fun showConfirmationDialog(budgetName: String, dbHelper: DatabaseHelper) {

        val dialog = AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete this budget: $budgetName?")
            .setPositiveButton("Yes") { _, _ ->

                val success = dbHelper.deleteBudget(budgetName)

                if (success) {
                    Toast.makeText(this, "Budget deleted successfully!", Toast.LENGTH_SHORT).show()

                    recreate()
                } else {
                    Toast.makeText(this, "Failed to delete budget.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }
}