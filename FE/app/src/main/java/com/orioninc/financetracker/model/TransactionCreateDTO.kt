package com.orioninc.financetracker.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date


data class TransactionCreateDTO(
    @SerializedName("reporterId")
    val reporter: Long,
    @SerializedName("accountId")
    val account: Long,
    @SerializedName("description")
    val description: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("category")
    val category: String,
    @SerializedName("status")
    val status: Status
): Serializable