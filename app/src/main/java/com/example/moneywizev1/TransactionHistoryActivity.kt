package com.example.moneywizev1

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class TransactionHistoryActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var startDateEditText: EditText
    private lateinit var endDateEditText: EditText
    private lateinit var listView: ListView
    private lateinit var searchButton: Button
    private lateinit var categorySummaryTextView: TextView

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)

        dbHelper = DatabaseHelper(this)
        categorySummaryTextView = findViewById(R.id.categorySummaryTextView)
        startDateEditText = findViewById(R.id.startDateEditText)
        endDateEditText = findViewById(R.id.endDateEditText)
        listView = findViewById(R.id.transactionListView)
        searchButton = findViewById(R.id.searchButton)

        startDateEditText.setOnClickListener { showDatePickerDialog(startDateEditText) }
        endDateEditText.setOnClickListener { showDatePickerDialog(endDateEditText) }

        searchButton.setOnClickListener {
            val startDate = startDateEditText.text.toString()
            val endDate = endDateEditText.text.toString()

            if (startDate.isBlank() || endDate.isBlank()) {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get transaction data and summaries
            val transactions = dbHelper.getTransactionsObjectsBetweenDates(startDate, endDate)
            val categoryTotals = dbHelper.getTotalSpentPerCategory(startDate, endDate)

            // Display category summary
            val summaryText = if (categoryTotals.isEmpty()) {
                "No transactions found for selected range."
            } else {
                categoryTotals.joinToString("\n")
            }
            categorySummaryTextView.text = summaryText

            // Create display strings
            val displayList = transactions.map {
                "${it.type}: ${it.name} - ${it.category} - R${it.amount} on ${it.date}"
            }

            // Set adapter once
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
            listView.adapter = adapter

            // Handle item click
            listView.setOnItemClickListener { _, _, position, _ ->
                val selected = transactions[position]

                val intent = Intent(this, TransactionDetailActivity::class.java).apply {
                    putExtra("name", selected.name)
                    putExtra("amount", selected.amount)
                    putExtra("date", selected.date)
                    putExtra("category", selected.category)
                    putExtra("notes", selected.notes)
                    putExtra("imageUri", selected.imageUri ?: "")
                }
                startActivity(intent)
            }
        }

    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                editText.setText(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}