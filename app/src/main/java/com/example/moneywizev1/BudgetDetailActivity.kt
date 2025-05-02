package com.example.moneywizev1

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

class BudgetDetailActivity : AppCompatActivity() {

    private var currentCount = 0  // Current capital
    private var goal = 0          // Target amount (goal)
    private var celebrationDone = false
    private lateinit var budgetName: String
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_detail)

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Bind UI elements
        val tvProgress = findViewById<TextView>(R.id.tvProgress)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvPercentage = findViewById<TextView>(R.id.tvPercentage)
        val konfettiView = findViewById<KonfettiView>(R.id.konfettiView)
        val maxspendTextView = findViewById<TextView>(R.id.maxspendTextView)

        // Receive data from Intent
        budgetName = intent.getStringExtra("budgetName") ?: ""
        goal = intent.getIntExtra("budgetAmount", 100)  // Get goal (amount)

        // Fetch budget data from the database (capital, expenses, income)
        fetchBudgetData(budgetName)

        // Update UI
        updateUI(tvProgress, progressBar, tvPercentage, konfettiView)
    }
// Update the progressbar and details
    private fun updateUI(tvProgress: TextView, progressBar: ProgressBar, tvPercentage: TextView, konfettiView: KonfettiView) {
        val percentage = if (goal > 0) (currentCount.toFloat() / goal * 100).toInt() else 0
        tvProgress.text = "$budgetName Progress: $currentCount / $goal"
        progressBar.max = goal
        progressBar.progress = currentCount
        tvPercentage.text = "$percentage% Complete"

        if (currentCount >= goal && !celebrationDone) {
            celebrationDone = true
            launchConfetti(konfettiView)
            showGoalPopup(budgetName)
        }
    }

    private fun launchConfetti(konfettiView: KonfettiView) {
        val party = Party(
            speed = 5f,
            maxSpeed = 50f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(
                0xfffce18a.toInt(),
                0xffff726d.toInt(),
                0xffb48def.toInt(),
                0xfff4306d.toInt()
            ),
            emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(300),
            position = Position.Relative(0.5, 0.0),
            size = listOf(Size.SMALL, Size.LARGE),
            shapes = listOf(Shape.Square, Shape.Circle)
        )
        konfettiView.start(party)
    }

    private fun showGoalPopup(budgetName: String) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("🎉 Goal Complete!")
            .setMessage("You completed your goal for $budgetName!")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun fetchBudgetData(budgetName: String) {
        val db: SQLiteDatabase = dbHelper.readableDatabase

        // Fetch initial capital and maxspend
        val query = "SELECT capital,monthlyGoal, maxspend FROM budgets WHERE name = ?"
        val cursor = db.rawQuery(query, arrayOf(budgetName))
        if (cursor != null && cursor.moveToFirst()) {
            currentCount = cursor.getDouble(0).toInt()  // capital is REAL
            val maxspend = cursor.getDouble(2)          // maxspend is REAL
            val monthlyGoal = cursor.getDouble(1)
            // Update Min Spend TextView
            val maxspendTextView = findViewById<TextView>(R.id.maxspendTextView)
            maxspendTextView.text = "Spending limit: $maxspend //MonthlyGoal:$monthlyGoal"
        }
        cursor?.close()

        // Fetch expenses and income
        val expenses = getExpensesForBudget(budgetName)
        val income = getIncomeForBudget(budgetName)

        // Update the current capital
        currentCount = (currentCount + income) - expenses
    }

    // Fetch all expenses from expenses
    private fun getExpensesForBudget(budgetName: String): Int {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val query = "SELECT SUM(amount) FROM Expenses WHERE budget = ?"
        val cursor = db.rawQuery(query, arrayOf(budgetName))

        var expenses = 0
        if (cursor != null && cursor.moveToFirst()) {
            expenses = cursor.getDouble(0).toInt()
        }
        cursor?.close()
        return expenses
    }
// Fetch all income from expenses
    private fun getIncomeForBudget(budgetName: String): Int {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val query = "SELECT SUM(amount) FROM Income WHERE budget = ?"
        val cursor = db.rawQuery(query, arrayOf(budgetName))

        var income = 0
        if (cursor != null && cursor.moveToFirst()) {
            income = cursor.getDouble(0).toInt()
        }
        cursor?.close()
        return income
    }
}


