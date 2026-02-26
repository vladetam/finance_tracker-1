package com.orioninc.financetracker.repository

import com.orioninc.financetracker.model.Transaction
import com.orioninc.financetracker.model.TransactionCreateDTO

interface TransactionRepository {


    suspend fun getAllTransactions(): List<Transaction>


    suspend fun createTransaction(transaction: TransactionCreateDTO)


    suspend fun updateTransaction(id: Long, dto: TransactionCreateDTO)


    suspend fun deleteTransaction(id: Long)
}