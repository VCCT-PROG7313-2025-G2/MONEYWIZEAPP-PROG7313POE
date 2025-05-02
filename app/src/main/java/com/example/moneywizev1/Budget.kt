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

        
        val dbHelper = DatabaseHelper(this)

        
        val nameInput = findViewById<EditText>(R.id.editTextText)
        val amountInput = findViewById<EditText>(R.id.editTextDate)
        val maxspendInput = findViewById<EditText>(R.id.minSpendEditTxt)
        val capitalInput = findViewById<EditText>(R.id.editTextNumber)
        val notesInput = findViewById<EditText>(R.id.editTextText2)
        val dateInput = findViewById<EditText>(R.id.editTextDate2)
        val confirmButton = findViewById<Button>(R.id.button)
        val monthlyGoalInput = findViewById<EditText>(R.id.monthlyGoalEditTxt)
         fun isValidDate(date: String): Boolean {
            return try {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                sdf.isLenient = false
                sdf.parse(date)
                true
            } catch (e: Exception) {
                false
            }
        }

        // Button click: Save to database
        confirmButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val amountStr = amountInput.text.toString().trim()
            val maxspendStr = maxspendInput.text.toString().trim()
            val capitalStr = capitalInput.text.toString().trim()
            val monthlyGoalStr = monthlyGoalInput.text.toString().trim()
            val notes = notesInput.text.toString().trim()
            val date = dateInput.text.toString().trim()

            if (name.isEmpty() || amountStr.isEmpty() || maxspendStr.isEmpty() || capitalStr.isEmpty() || monthlyGoalStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            val capital = capitalStr.toDoubleOrNull()
            val maxspend = maxspendStr.toDoubleOrNull()
            val monthlyGoal = monthlyGoalStr.toDoubleOrNull()

            if (amount == null || capital == null || maxspend == null || monthlyGoal == null) {
                Toast.makeText(this, "Amount and Capital must be valid numbers.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidDate(date)) {
                Toast.makeText(this, "Error: Date must be in format YYYY-MM-DD", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (monthlyGoal > maxspend) {
                Toast.makeText(this, "Monthly goal cannot be greater than spend limit.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val success = dbHelper.insertBudget(
                name,
                amount,
                maxspend,
                capital,
                monthlyGoal,
                notes,
                date
            )

            if (success) {
                Toast.makeText(this, "Budget saved successfully!", Toast.LENGTH_SHORT).show()

                nameInput.text.clear()
                amountInput.text.clear()
                maxspendInput.text.clear()
                capitalInput.text.clear()
                monthlyGoalInput.text.clear()
                notesInput.text.clear()
                dateInput.text.clear()
            } else {
                Toast.makeText(this, "Failed to save budget.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
