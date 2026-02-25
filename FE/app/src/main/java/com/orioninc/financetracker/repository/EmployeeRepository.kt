package com.orioninc.financetracker.repository

import com.orioninc.financetracker.model.Employee

interface EmployeeRepository {
    suspend fun getAllEmployees(): List<Employee>
}