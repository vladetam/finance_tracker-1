package com.orioninc.financetracker.repository

import com.orioninc.financetracker.model.Account

interface AccountRepository {

    suspend fun getAllAccounts(): List<Account>
}