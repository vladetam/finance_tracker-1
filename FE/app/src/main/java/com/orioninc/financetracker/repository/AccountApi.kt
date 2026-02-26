package com.orioninc.financetracker.repository

import com.orioninc.financetracker.model.Account
import com.orioninc.financetracker.model.Transaction
import retrofit2.Response
import retrofit2.http.GET

interface AccountApi {


    @GET("accounts/getAccounts")
    suspend fun getAllAccounts(): Response<List<Account>>

}