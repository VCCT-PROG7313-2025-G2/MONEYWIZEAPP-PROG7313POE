package com.example.moneywizev1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class IncomePage : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var budgetSpinner: Spinner
    private lateinit var budgetsList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_income_page)

        dbHelper = DatabaseHelper(this)

        val addPhoto = findViewById<Button>(R.id.addPhoto)
        val saveButton = findViewById<Button>(R.id.saveButton)

        val nameField = findViewById<EditText>(R.id.editTextText8)
        val amountField = findViewById<EditText>(R.id.editTextNumber)
        val dateField = findViewById<EditText>(R.id.editTextDate2)
        budgetSpinner = findViewById(R.id.budgetSpinner) // Spinner, not EditText
        val categoryField = findViewById<EditText>(R.id.editTextText6)
        val notesField = findViewById<EditText>(R.id.editTextText5)

        loadBudgetsIntoSpinner()

        addPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
        }

        saveButton.setOnClickListener {
            val name = nameField.text.toString()
            val amount = amountField.text.toString().toDoubleOrNull()
            val date = dateField.text.toString()
            val budget = budgetSpinner.selectedItem?.toString() ?: ""
            val category = categoryField.text.toString()
            val notes = notesField.text.toString()

            if (name.isBlank() || amount == null || date.isBlank() || budget.isBlank() || category.isBlank() || notes.isBlank()) {
                Toast.makeText(this, "Please fill all fields except the photo.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = dbHelper.insertIncome(context=this,
                name,
                amount,
                date,
                budget,
                category,
                notes,
                selectedImageUri?.toString()
            )

            if (success) {
                Toast.makeText(this, "Income saved successfully!", Toast.LENGTH_SHORT).show()
                nameField.text.clear()
                amountField.text.clear()
                dateField.text.clear()
                categoryField.text.clear()
                notesField.text.clear()
                selectedImageUri = null
                budgetSpinner.setSelection(0) // Reset Spinner
            } else {
                Toast.makeText(this, "Failed to save income.", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            view.setPadding(0, insets.getInsets(WindowInsetsCompat.Type.systemBars()).top, 0, 0)
            insets
        }
    }

    private fun loadBudgetsIntoSpinner() {
        budgetsList = dbHelper.getAllBudgets2() // <- You need this function in your DatabaseHelper
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, budgetsList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        budgetSpinner.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            Toast.makeText(this, "Image selected.", Toast.LENGTH_SHORT).show()
        }
    }
}
