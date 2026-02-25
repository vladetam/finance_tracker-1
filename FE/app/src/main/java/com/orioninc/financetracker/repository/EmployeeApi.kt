package com.orioninc.financetracker.repository

import com.orioninc.financetracker.model.Employee
import retrofit2.Response
import retrofit2.http.*

interface EmployeeApi {
    @GET("employees")
    suspend fun getAllEmployees(): Response<List<Employee>>
}