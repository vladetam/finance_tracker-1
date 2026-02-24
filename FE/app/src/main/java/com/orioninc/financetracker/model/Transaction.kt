package com.orioninc.financetracker.model

import java.util.Date

enum class Status { Pending, Completed, Reconciled}

data class Transaction(
    val idTransaction: Long,
    val reporter: Employee,
    val account: Account,
    val description: String,
    val date: Date,
    val amount: Double,
    val category: String,
    val status: Status
    )