package com.orioninc.financetracker.view

import androidx.lifecycle.*
import com.orioninc.financetracker.model.Employee
import com.orioninc.financetracker.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val repository: EmployeeRepository
) : ViewModel() {

    private val _employees = MutableLiveData<List<Employee>>()
    val employees: LiveData<List<Employee>> = _employees

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadEmployees() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _employees.value = repository.getAllEmployees()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Cannot fetch employees"
            } finally {
                _loading.value = false
            }
        }
    }
}