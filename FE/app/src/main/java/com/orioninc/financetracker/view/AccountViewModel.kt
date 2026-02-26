package com.orioninc.financetracker.view

import androidx.lifecycle.*
import com.orioninc.financetracker.model.Account
import com.orioninc.financetracker.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {

    private val _accounts = MutableLiveData<List<Account>>()
    val accounts: LiveData<List<Account>> = _accounts

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadAccounts() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _accounts.value = repository.getAllAccounts()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Cannot fetch accounts"
            } finally {
                _loading.value = false
            }
        }
    }
}