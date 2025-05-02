package com.example.moneywizev1
import android.widget.EditText
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ExpensePage : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var budgetsList: List<String>
    private lateinit var budgetSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expense_page)

        dbHelper = DatabaseHelper(this)

        val addPhoto = findViewById<Button>(R.id.addPhoto)
        val saveButton = findViewById<Button>(R.id.saveButton)

        val nameField = findViewById<EditText>(R.id.editTextText8)
        val amountField = findViewById<EditText>(R.id.editTextNumber)
        val dateField = findViewById<EditText>(R.id.editTextDate2)
        budgetSpinner = findViewById(R.id.budgetSpinner)
        val categoryField = findViewById<EditText>(R.id.editTextText6)
        val notesField = findViewById<EditText>(R.id.editTextText5)
        loadBudgetsIntoSpinner()
        // This is to be able to add a photo button functional
        addPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION // ✅ persistable flag
            }
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
        }

        saveButton.setOnClickListener {
            val name = nameField.text.toString()
            val amount = amountField.text.toString().toDoubleOrNull()
            val date = dateField.text.toString()
            val budget = budgetSpinner.selectedItem?.toString() ?: ""
            val category = categoryField.text.toString()
            val notes = notesField.text.toString()

            if (name.isBlank() || amount == null || date.isBlank() || budget.isBlank()  || category.isBlank() || notes.isBlank()) {
                Toast.makeText(this, "Please fill all fields except the photo.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // This calculates the total existing expenses for this budget
            val totalExpensesCursor = dbHelper.readableDatabase.rawQuery(
                "SELECT SUM(amount) FROM expenses WHERE budget = ?",
                arrayOf(budget)
            )
            var currentTotal = 0.0
            if (totalExpensesCursor.moveToFirst()) {
                currentTotal = totalExpensesCursor.getDouble(0)
            }
            totalExpensesCursor.close()

// Get the maxspend from the budgets table
            val budgetCursor = dbHelper.readableDatabase.rawQuery(
                "SELECT maxspend FROM budgets WHERE name = ?",
                arrayOf(budget)
            )
            var maxspend = Double.MAX_VALUE
            if (budgetCursor.moveToFirst()) {
                maxspend = budgetCursor.getDouble(0)
            }
            budgetCursor.close()

// To be able to compare and act accordingly
            if (currentTotal + amount > maxspend) {
                Toast.makeText(this, "Expense exceeds the budget's maxspend limit!", Toast.LENGTH_LONG).show()
            } else {
                val success = dbHelper.insertExpense(context=this,
                    name,
                    amount,
                    date,
                    budget,
                    category,
                    notes,
                    selectedImageUri?.toString()
                )

                if (success) {
                    Toast.makeText(this, "Expense saved successfully!", Toast.LENGTH_SHORT).show()
                    // Clear the fields
                    nameField.text.clear()
                    amountField.text.clear()
                    dateField.text.clear()
                    categoryField.text.clear()
                    notesField.text.clear()
                    selectedImageUri = null
                    budgetSpinner.setSelection(0)
                } else {
                    Toast.makeText(this, "Failed to save expense.", Toast.LENGTH_SHORT).show()
                }
            }

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data

            selectedImageUri?.let { uri ->
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Could not persist URI permission", Toast.LENGTH_SHORT).show()
                }

                Toast.makeText(this, "Image selected: ${uri.lastPathSegment}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// TonikamiTV (2015). Android Studio Tutorial - Upload Picture Part 1 - User Interface. [online] YouTube. Available at: https://www.youtube.com/watch?v=e8x-nu9-_BM [Accessed 25 Apr. 2025].

