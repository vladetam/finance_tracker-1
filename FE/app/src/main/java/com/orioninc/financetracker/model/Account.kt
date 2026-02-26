package com.orioninc.financetracker.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class AccountType {Silver , Gold , Platinum}

data class Account(
    @SerializedName("id")
    val idAccount: Long,
    @SerializedName("type")
    val accountType: AccountType,
    @SerializedName("balance")
    val balance: Double,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("version")
    val version: Int,
    @SerializedName("employee")
    val assignedEmployee: Employee
): Serializable