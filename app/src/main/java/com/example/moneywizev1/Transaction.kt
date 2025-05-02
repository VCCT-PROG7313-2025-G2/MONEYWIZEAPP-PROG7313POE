package com.example.moneywizev1

data class Transaction(
    val type: String, // Type of transaction income/expense
    val name: String,
    val amount: Double,
    val date: String,
    val category: String,
    val notes:  String = "",
    val imageUri: String? = null
)

