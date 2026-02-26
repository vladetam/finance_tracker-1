package com.orioninc.financetracker.view

import androidx.lifecycle.*
import com.orioninc.financetracker.model.Account
import com.orioninc.financetracker.model.Employee
import com.orioninc.financetracker.model.Transaction
import com.orioninc.financetracker.model.Status
import com.orioninc.financetracker.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _allTransactions = MutableLiveData<List<Transaction>>()
    private val _filteredTransactions = MutableLiveData<List<Transaction>>()
    val filteredTransactions: LiveData<List<Transaction>> = _filteredTransactions

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val transactions = repository.getAllTransactions()
                _allTransactions.value = transactions
                _filteredTransactions.value = transactions
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Cannot connect to server"
            } finally {
                _loading.value = false
            }
        }
    }
    fun createTransaction(
        description: String,
        amount: Double,
        category: String,
        date: Date,
        status: Status,
        reporter: Employee,
        selectedAccount: Account
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val transaction = Transaction(
                    idTransaction = 0L,
                    description = description,
                    amount = amount,
                    category = category,
                    date = date,
                    status = status,
                    reporter = reporter,
                    version = 3,
                    account = selectedAccount
                )
                repository.createTransaction(transaction)
                loadTransactions()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Cannot create transaction: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun resetFilter() {
        _filteredTransactions.value = _allTransactions.value
    }

    fun filterByEmployee(employeeId: Long) {
        _filteredTransactions.value =
            _allTransactions.value?.filter { it.reporter.idEmployee == employeeId }
    }

    fun filterByAccount(accountId: Long) {
        _filteredTransactions.value =
            _allTransactions.value?.filter { it.account.idAccount == accountId }
    }

    fun filterByStatus(status: Status) {
        _filteredTransactions.value =
            _allTransactions.value?.filter { it.status == status }
    }

    fun filterByDate(selectedDate: Date) {
        val formatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        _filteredTransactions.value =
            _allTransactions.value?.filter {
                formatter.format(it.date) == formatter.format(selectedDate)
            }
    }
}