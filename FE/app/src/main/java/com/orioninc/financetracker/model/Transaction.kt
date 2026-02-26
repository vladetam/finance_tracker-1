package com.orioninc.financetracker.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

enum class Status { Pending, Completed, Reconciled}

data class Transaction(
    @SerializedName("id")
    val idTransaction: Long,
    @SerializedName("reporter")
    val reporter: Employee,
    @SerializedName("account")
    val account: Account,
    @SerializedName("description")
    val description: String,
    @SerializedName("version")
    val version: Int,
    @SerializedName("date")
    val date: Date,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("category")
    val category: String,
    @SerializedName("status")
    val status: Status
): Serializable