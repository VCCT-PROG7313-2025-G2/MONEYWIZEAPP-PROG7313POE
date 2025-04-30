package com.example.moneywizev1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Budget : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_budget)

        // Initialize DB helper
        val dbHelper = DatabaseHelper(this)

        // Get references to all EditTexts and Button
        val nameInput = findViewById<EditText>(R.id.editTextText)
        val amountInput = findViewById<EditText>(R.id.editTextDate)
        val minspendInput = findViewById<EditText>(R.id.minSpendEditTxt)
        val capitalInput = findViewById<EditText>(R.id.editTextNumber)
        val notesInput = findViewById<EditText>(R.id.editTextText2)
        val dateInput = findViewById<EditText>(R.id.editTextDate2)
        val confirmButton = findViewById<Button>(R.id.button)

        // Button click: Save to database
        confirmButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val amountStr = amountInput.text.toString().trim()
            val minspendStr = minspendInput.text.toString().trim()
            val capitalStr = capitalInput.text.toString().trim()
            val notes = notesInput.text.toString().trim()
            val date = dateInput.text.toString().trim()

            if (name.isEmpty() || amountStr.isEmpty() ||minspendStr.isEmpty() || capitalStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convert amount and capital to Double
            val amount = amountStr.toDoubleOrNull()
            val capital = capitalStr.toDoubleOrNull()
            val minspend = minspendStr.toDoubleOrNull()

            if (amount == null || capital == null|| minspend == null) {
                Toast.makeText(this, "Amount and Capital must be valid numbers.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = dbHelper.insertBudget(name, amount, minspend , capital, notes, date)
            if (success) {
                Toast.makeText(this, "Budget saved successfully!", Toast.LENGTH_SHORT).show()

                // Clear the form
                nameInput.text.clear()
                amountInput.text.clear()
                minspendInput.text.clear()
                capitalInput.text.clear()
                notesInput.text.clear()
                dateInput.text.clear()
            } else {
                Toast.makeText(this, "Failed to save budget.", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle system bars insets (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

