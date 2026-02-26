package com.orioninc.financetracker.repository

import com.orioninc.financetracker.model.Transaction

interface TransactionRepository {


    suspend fun getAllTransactions(): List<Transaction>


    suspend fun createTransaction(transaction: Transaction): Transaction


    suspend fun updateTransaction(id: Long, transaction: Transaction)


    suspend fun deleteTransaction(id: Long)
}