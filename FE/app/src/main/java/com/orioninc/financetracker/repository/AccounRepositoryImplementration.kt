package com.orioninc.financetracker.repository

import com.orioninc.financetracker.model.Account
import retrofit2.HttpException
import javax.inject.Inject

class AccountRepositoryImplementation @Inject constructor(
    private val api: AccountApi
): AccountRepository{
    override suspend fun getAllAccounts(): List<Account> {
        val response = api.getAllAccounts()
        if(response.isSuccessful){
            return response.body()?:emptyList()
        }else{
            throw HttpException(response)
        }
    }
}