package com.orioninc.financetracker.repository

import com.orioninc.financetracker.model.Employee
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeRepositoryImplementation @Inject constructor(
    private val api: EmployeeApi
) : EmployeeRepository{
    override suspend fun getAllEmployees(): List<Employee> {
        val response = api.getAllEmployees()
        if(response.isSuccessful){
            return response.body() ?: emptyList()
        }else{
            throw HttpException(response)
        }
    }
}