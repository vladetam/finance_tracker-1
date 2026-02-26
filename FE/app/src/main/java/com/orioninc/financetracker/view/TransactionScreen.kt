package com.orioninc.financetracker.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orioninc.financetracker.model.Account
import com.orioninc.financetracker.model.Employee
import com.orioninc.financetracker.model.Status
import com.orioninc.financetracker.model.Transaction
import com.orioninc.financetracker.model.TransactionCreateDTO
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.filled.Edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreenRoute(
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val transactions by transactionViewModel.filteredTransactions.observeAsState(emptyList())
    val loading by transactionViewModel.loading.observeAsState(false)
    val error by transactionViewModel.error.observeAsState()
    val employees by employeeViewModel.employees.observeAsState(emptyList())
    val accounts by accountViewModel.accounts.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        accountViewModel.loadAccounts()
        transactionViewModel.loadTransactions()
        employeeViewModel.loadEmployees()
    }

    TransactionScreen(
        transactions = transactions,
        loading = loading,
        error = error,
        employees = employees,
        accounts = accounts,
        onReset = { transactionViewModel.resetFilter() },
        onFilterByEmployee = { transactionViewModel.filterByEmployee(it) },
        onFilterByAccount = { transactionViewModel.filterByAccount(it) },
        onFilterByStatus = { transactionViewModel.filterByStatus(it) },
        onFilterByDate = { transactionViewModel.filterByDate(it) },
        onCreateTransaction = { dto: TransactionCreateDTO ->
            transactionViewModel.createTransaction(dto)
        },
        onUpdateTransaction = { id, dto ->
            transactionViewModel.updateTransaction(id, dto)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    transactions: List<Transaction>,
    loading: Boolean,
    error: String?,
    employees: List<Employee>,
    accounts: List<Account>,
    onReset: () -> Unit,
    onFilterByEmployee: (Long) -> Unit,
    onFilterByAccount: (Long) -> Unit,
    onFilterByStatus: (Status) -> Unit,
    onFilterByDate: (Date) -> Unit,
    onCreateTransaction: (TransactionCreateDTO) -> Unit,
    onUpdateTransaction: (Long, TransactionCreateDTO) -> Unit
)  {
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    var menuExpanded by remember { mutableStateOf(false) }

    var showActionDialog by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEmployeeDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    val softDarkGray = Color(0xFF373737)
    val concreteLightGray = Color(0xFFE0E0E0)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Finance Tracker", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = softDarkGray),
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(text = { Text("Show All") }, onClick = { menuExpanded = false; onReset() })
                        DropdownMenuItem(text = { Text("Filter by Date") }, onClick = {
                            menuExpanded = false
                            val cal = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    val selected = Calendar.getInstance()
                                    selected.set(y, m, d)
                                    onFilterByDate(selected.time)
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        })
                        DropdownMenuItem(text = { Text("Filter by Status") }, onClick = {
                            menuExpanded = false
                            showStatusDialog = true
                        })
                        DropdownMenuItem(text = { Text("Filter by Account") }, onClick = {
                            menuExpanded = false
                            showAccountDialog = true
                        })
                        DropdownMenuItem(text = { Text("Filter by Employee") }, onClick = {
                            menuExpanded = false
                            showEmployeeDialog = true
                        })
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showActionDialog = true },
                containerColor = softDarkGray
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction", tint = Color.White)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            LazyColumn {
                items(transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        formatter = dateFormatter,
                        cardColor = concreteLightGray,
                        onEditClick = {
                            selectedTransaction = transaction
                            showUpdateDialog = true
                        }
                    )
                }
            }

            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
                )
            }
        }
    }

    // ACTION DIALOG
    if (showActionDialog) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text("Actions") },
            text = {
                Column {
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        onClick = { showActionDialog = false; showCreateDialog = true }
                    ) { Text("Create Transaction") }

                    Button(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        onClick = { showActionDialog = false; }
                    ) { Text("Delete Transaction") }

                    Button(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        onClick = { showActionDialog = false; showUpdateDialog}
                    ) { Text("Update Transaction") }
                }
            },
            confirmButton = { TextButton(onClick = { showActionDialog = false }) { Text("Cancel") } }
        )
    }
// CREATE TRANSACTION DIALOG
    if (showCreateDialog) {
        var description by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("") }
        var selectedStatus by remember { mutableStateOf(Status.PENDING) }
        var selectedDate by remember { mutableStateOf(Date()) }

        var expandedEmployee by remember { mutableStateOf(false) }
        var selectedEmployee by remember { mutableStateOf<Employee?>(null) }

        var expandedAccount by remember { mutableStateOf(false) }
        var selectedAccount by remember { mutableStateOf<Account?>(null) }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Transaction") },
            text = {
                Column {
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                    OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") })
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                val selected = Calendar.getInstance()
                                selected.set(y, m, d)
                                selectedDate = selected.time
                            },
                            selectedDate.year + 1900,
                            selectedDate.month,
                            selectedDate.date
                        ).show()
                    }) {
                        Text("Select Date: ${SimpleDateFormat("dd/MM/yyyy").format(selectedDate)}")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status Radio Buttons
                    Column {
                        Status.values().forEach { status ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = selectedStatus == status, onClick = { selectedStatus = status })
                                Text(status.name)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Employee Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedEmployee,
                        onExpandedChange = { expandedEmployee = !expandedEmployee }
                    ) {
                        OutlinedTextField(
                            value = selectedEmployee?.let { "${it.firstName} ${it.lastName}" } ?: "",
                            onValueChange = {},
                            label = { Text("Select Employee") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEmployee) },
                            modifier = Modifier.menuAnchor()
                        )
                        DropdownMenu(
                            expanded = expandedEmployee,
                            onDismissRequest = { expandedEmployee = false },
                            modifier = Modifier.heightIn(max = 300.dp)
                        ) {
                            employees.forEach { employee ->
                                DropdownMenuItem(
                                    text = { Text("${employee.firstName} ${employee.lastName}") },
                                    onClick = {
                                        selectedEmployee = employee
                                        expandedEmployee = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Account Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedAccount,
                        onExpandedChange = { expandedAccount = !expandedAccount }
                    ) {
                        OutlinedTextField(
                            value = selectedAccount?.let { "ID: ${it.idAccount}" } ?: "",
                            onValueChange = {},
                            label = { Text("Select Account") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAccount) },
                            modifier = Modifier.menuAnchor()
                        )
                        DropdownMenu(
                            expanded = expandedAccount,
                            onDismissRequest = { expandedAccount = false },
                            modifier = Modifier.heightIn(max = 300.dp)
                        ) {
                            accounts.forEach { account ->
                                DropdownMenuItem(
                                    text = { Text("ID: ${account.idAccount}") },
                                    onClick = {
                                        selectedAccount = account
                                        expandedAccount = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (selectedEmployee != null && selectedAccount != null) {
                        val dto = TransactionCreateDTO(
                            reporter = selectedEmployee!!.idEmployee,
                            account = selectedAccount!!.idAccount,
                            description = description,
                            date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(selectedDate),
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            category = category,
                            status = selectedStatus
                        )
                        Log.d("DEBUG",""+dto.reporter+dto.account+dto.description+dto.date+dto.amount+dto.category+dto.status)
                        onCreateTransaction(dto)
                        showCreateDialog = false
                    } else {
                        Toast.makeText(context, "Select employee and account", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Create") }
            },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") } }
        )
    }

    if (showUpdateDialog && selectedTransaction != null) {

        var description by remember { mutableStateOf(selectedTransaction!!.description) }
        var amount by remember { mutableStateOf(selectedTransaction!!.amount.toString()) }
        var category by remember { mutableStateOf(selectedTransaction!!.category) }
        var selectedStatus by remember { mutableStateOf(selectedTransaction!!.status) }
        var selectedDate by remember { mutableStateOf(selectedTransaction!!.date) }

        var expandedEmployee by remember { mutableStateOf(false) }
        var selectedEmployee by remember { mutableStateOf(selectedTransaction!!.reporter) }

        var expandedAccount by remember { mutableStateOf(false) }
        var selectedAccount by remember { mutableStateOf(selectedTransaction!!.account) }

        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("Update Transaction") },
            text = {
                Column {

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") }
                    )

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount") }
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column {
                        Status.values().forEach { status ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedStatus == status,
                                    onClick = { selectedStatus = status }
                                )
                                Text(status.name)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (selectedEmployee != null && selectedAccount != null) {

                        val dto = TransactionCreateDTO(
                            reporter = selectedEmployee.idEmployee,
                            account = selectedAccount.idAccount,
                            description = description,
                            date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(selectedDate),
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            category = category,
                            status = selectedStatus
                        )

                        onUpdateTransaction(selectedTransaction!!.idTransaction, dto)
                        showUpdateDialog = false
                    }
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // EMPLOYEE FILTER DIALOG
    if (showEmployeeDialog) {
        AlertDialog(
            onDismissRequest = { showEmployeeDialog = false },
            title = { Text("Select Employee") },
            text = {
                Column {
                    employees.forEach { employee ->
                        TextButton(
                            onClick = { onFilterByEmployee(employee.idEmployee); showEmployeeDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${employee.firstName} ${employee.lastName}")
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showEmployeeDialog = false }) { Text("Cancel") } }
        )
    }

    // ACCOUNT FILTER DIALOG
    if (showAccountDialog) {
        AlertDialog(
            onDismissRequest = { showAccountDialog = false },
            title = { Text("Select Account") },
            text = {
                Column {
                    accounts.forEach { account ->
                        TextButton(
                            onClick = { onFilterByAccount(account.idAccount); showAccountDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ID: ${account.idAccount}")
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAccountDialog = false }) { Text("Cancel") } }
        )
    }

    // STATUS FILTER DIALOG
    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Select Status") },
            text = {
                Column {
                    Status.values().forEach { status ->
                        TextButton(
                            onClick = { onFilterByStatus(status); showStatusDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(status.name)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showStatusDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    formatter: SimpleDateFormat,
    cardColor: Color,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(transaction.description, fontWeight = FontWeight.Bold)

                IconButton(onClick = { onEditClick() }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
            }

            Text("By: ${transaction.reporter.firstName} ${transaction.reporter.lastName}")
            Text("Amount: ${transaction.amount} RSD", color = Color(0xFF1B5E20))
            Text("Status: ${transaction.status}")
            Text("Date: ${formatter.format(transaction.date)}")
        }
    }
}