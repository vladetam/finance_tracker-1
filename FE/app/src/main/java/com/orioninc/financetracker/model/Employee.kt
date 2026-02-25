package com.orioninc.financetracker.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Employee(
    @SerializedName("id")
    val idEmployee: Long,
    @SerializedName("firstname")
    val firstName: String,
    @SerializedName("lastname")
    val lastName: String,
    @SerializedName("email")
    val email: String
): Serializable