package com.orioninc.financetracker.view

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.orioninc.financetracker.model.Employee
import com.orioninc.financetracker.model.Status
import com.orioninc.financetracker.model.Transaction
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreenRoute(
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    employeeViewModel: EmployeeViewModel = hiltViewModel()
) {
    val transactions by transactionViewModel.filteredTransactions.observeAsState(emptyList())
    val loading by transactionViewModel.loading.observeAsState(false)
    val error by transactionViewModel.error.observeAsState()
    val employees by employeeViewModel.employees.observeAsState(emptyList())
    val accounts = listOf("Account 1", "Account 2") // primer, može se učitati iz beka

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
        onFilterByDate = { transactionViewModel.filterByDate(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    transactions: List<Transaction>,
    loading: Boolean,
    error: String?,
    employees: List<Employee>,
    accounts: List<String>,
    onReset: () -> Unit,
    onFilterByEmployee: (Long) -> Unit,
    onFilterByAccount: (Long) -> Unit,
    onFilterByStatus: (Status) -> Unit,
    onFilterByDate: (Date) -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var menuExpanded by remember { mutableStateOf(false) }
    var accountMenuExpanded by remember { mutableStateOf(false) }
    var employeeMenuExpanded by remember { mutableStateOf(false) }
    var statusMenuExpanded by remember { mutableStateOf(false) }

    var showActionDialog by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }

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
                            statusMenuExpanded = true
                        })
                        DropdownMenuItem(text = { Text("Filter by Account") }, onClick = { accountMenuExpanded = true })
                        DropdownMenuItem(text = { Text("Filter by Employee") }, onClick = { employeeMenuExpanded = true })
                    }

                    // Dropdown za account
                    DropdownMenu(
                        expanded = accountMenuExpanded,
                        onDismissRequest = { accountMenuExpanded = false }
                    ) {
                        accounts.forEach { account ->
                            DropdownMenuItem(text = { Text(account) }, onClick = {
                                accountMenuExpanded = false
                                onFilterByAccount(account.toLong()) // prilagodi tip
                            })
                        }
                    }

                    // Dropdown za employee
                    DropdownMenu(
                        expanded = employeeMenuExpanded,
                        onDismissRequest = { employeeMenuExpanded = false }
                    ) {
                        employees.forEach { employee ->
                            DropdownMenuItem(text = { Text("${employee.firstName} ${employee.lastName}") }, onClick = {
                                employeeMenuExpanded = false
                                onFilterByEmployee(employee.idEmployee)
                            })
                        }
                    }

                    // Dropdown za status
                    DropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = { statusMenuExpanded = false }
                    ) {
                        Status.values().forEach { status ->
                            DropdownMenuItem(text = { Text(status.name) }, onClick = {
                                statusMenuExpanded = false
                                onFilterByStatus(status)
                            })
                        }
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
                    TransactionItem(transaction, dateFormatter, concreteLightGray)
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

    // ACTION DIALOG: Create / Delete / Update
    if (showActionDialog) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text("Actions") },
            text = {
                Column {
                    Button(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), onClick = {
                        showActionDialog = false
                        showCreateDialog = true
                    }) { Text("Create Transaction") }

                    Button(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), onClick = {
                        showActionDialog = false
                    }) { Text("Delete Transaction") }

                    Button(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), onClick = {
                        showActionDialog = false
                    }) { Text("Update Transaction") }
                }
            },
            confirmButton = {
                TextButton(onClick = { showActionDialog = false }) { Text("Cancel") }
            }
        )
    }

    // CREATE DIALOG
    if (showCreateDialog) {
        var description by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("") }
        var selectedStatus by remember { mutableStateOf(Status.Pending) }
        var selectedDate by remember { mutableStateOf(Date()) }
        var expandedEmployee by remember { mutableStateOf(false) }
        var selectedEmployee by remember { mutableStateOf<Employee?>(null) }

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

                    Column {
                        Status.values().forEach { status ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = selectedStatus == status, onClick = { selectedStatus = status })
                                Text(status.name)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box {
                        OutlinedTextField(
                            value = selectedEmployee?.let { "${it.firstName} ${it.lastName}" } ?: "",
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth().clickable { expandedEmployee = true },
                            label = { Text("Select Employee") },
                            readOnly = true
                        )

                        DropdownMenu(
                            expanded = expandedEmployee,
                            onDismissRequest = { expandedEmployee = false }
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
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showCreateDialog = false
                }) { Text("Create") }
            },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun TransactionItem(transaction: Transaction, formatter: SimpleDateFormat, cardColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(transaction.description, fontWeight = FontWeight.Bold)
            Text("By: ${transaction.reporter.firstName} ${transaction.reporter.lastName}")
            Text("Amount: ${transaction.amount} RSD", color = Color(0xFF1B5E20))
            Text("Status: ${transaction.status}")
            Text("Date: ${formatter.format(transaction.date)}")
        }
    }
}