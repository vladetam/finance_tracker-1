package com.orioninc.financetracker.repository

import com.orioninc.financetracker.model.Transaction
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImplementation @Inject constructor(
    private val api: TransactionApi
) : TransactionRepository {

    override suspend fun getAllTransactions(): List<Transaction> {
        val response = api.getAllTransactions()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw HttpException(response)
        }
    }

    override suspend fun createTransaction(transaction: Transaction): Transaction {
        val response = api.createTransaction(transaction)
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw HttpException(response)
        }
    }

    override suspend fun updateTransaction(id: Long, transaction: Transaction) {
        val response = api.updateTransaction(id, transaction)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
    }

    override suspend fun deleteTransaction(id: Long) {
        val response = api.deleteTransaction(id)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
    }
}