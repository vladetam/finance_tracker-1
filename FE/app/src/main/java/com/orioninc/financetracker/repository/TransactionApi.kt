package com.orioninc.financetracker.repository

import com.orioninc.financetracker.model.Transaction
import com.orioninc.financetracker.model.TransactionCreateDTO
import retrofit2.Response
import retrofit2.http.*

interface TransactionApi {


    @GET("transactions")
    suspend fun getAllTransactions(): Response<List<Transaction>>


    @POST("transactions")
    suspend fun createTransaction(@Body transaction: TransactionCreateDTO): Response<TransactionCreateDTO>


    @PUT("transactions/{id}")
    suspend fun updateTransaction(
        @Path("id") id: Long,
        @Body transaction: Transaction
    ): Response<Unit>


    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Long): Response<Unit>
}