package com.orioninc.financetracker.model

enum class AccountType {Silver , Gold , Platinum}

data class Account(
    val idAccount: Long,
    val accountType: AccountType,
    val balance: Double,
    val currency: String,
    val assignedEmployee: Employee
)