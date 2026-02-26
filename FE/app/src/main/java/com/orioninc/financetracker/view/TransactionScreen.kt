package com.orioninc.financetracker.view

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.orioninc.financetracker.model.*
import java.text.SimpleDateFormat
import java.util.*

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
        onCreateTransaction = { transactionViewModel.createTransaction(it) },
        onUpdateTransaction = { id, dto -> transactionViewModel.updateTransaction(id, dto) },
        onDeleteTransaction = { id -> transactionViewModel.deleteTransaction(id) }
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
    onUpdateTransaction: (Long, TransactionCreateDTO) -> Unit,
    onDeleteTransaction: (Long) -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    var menuExpanded by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    var showEmployeeDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }

    val softDarkGray = Color(0xFF373737)
    val concreteLightGray = Color(0xFFE0E0E0)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Finance Tracker", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = softDarkGray),
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(text = { Text("Show All") }, onClick = { menuExpanded = false; onReset() })
                        DropdownMenuItem(text = { Text("Filter by Date") }, onClick = {
                            menuExpanded = false
                            val cal = Calendar.getInstance()
                            DatePickerDialog(context, { _, y, m, d ->
                                val selected = Calendar.getInstance().apply { set(y, m, d) }
                                onFilterByDate(selected.time)
                            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                        })
                        DropdownMenuItem(text = { Text("Filter by Status") }, onClick = { menuExpanded = false; showStatusDialog = true })
                        DropdownMenuItem(text = { Text("Filter by Account") }, onClick = { menuExpanded = false; showAccountDialog = true })
                        DropdownMenuItem(text = { Text("Filter by Employee") }, onClick = { menuExpanded = false; showEmployeeDialog = true })
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }, containerColor = softDarkGray) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color.White)) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        formatter = dateFormatter,
                        cardColor = concreteLightGray,
                        onEditClick = {
                            selectedTransaction = transaction
                            showUpdateDialog = true
                        },
                        onDeleteClick = {
                            transactionToDelete = transaction
                            showDeleteConfirmation = true
                        }
                    )
                }
            }

            error?.let {
                Text(text = it, color = Color.Red, modifier = Modifier.align(Alignment.TopCenter).padding(16.dp))
            }
        }
    }

    if (showDeleteConfirmation && transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete ${transactionToDelete?.description}?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteTransaction(transactionToDelete!!.idTransaction)
                    showDeleteConfirmation = false
                }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirmation = false }) { Text("Cancel") } }
        )
    }

    if (showCreateDialog) {
        TransactionEntryDialog(
            title = "New Transaction",
            employees = employees,
            accounts = accounts,
            onDismiss = { showCreateDialog = false },
            onConfirm = { dto ->
                onCreateTransaction(dto)
                showCreateDialog = false
            }
        )
    }

    if (showUpdateDialog && selectedTransaction != null) {
        TransactionEntryDialog(
            title = "Update Transaction",
            initialTransaction = selectedTransaction,
            employees = employees,
            accounts = accounts,
            onDismiss = { showUpdateDialog = false },
            onConfirm = { dto ->
                onUpdateTransaction(selectedTransaction!!.idTransaction, dto)
                showUpdateDialog = false
            }
        )
    }

    if (showEmployeeDialog) {
        FilterListDialog("Employee", employees.map { it.idEmployee to "${it.firstName} ${it.lastName}" }, { onFilterByEmployee(it) }, { showEmployeeDialog = false })
    }
    if (showAccountDialog) {
        FilterListDialog("Account", accounts.map { it.idAccount to "ID: ${it.idAccount}" }, { onFilterByAccount(it) }, { showAccountDialog = false })
    }
    if (showStatusDialog) {
        FilterListDialog("Status", Status.values().map { it.ordinal.toLong() to it.name }, { onFilterByStatus(Status.values()[it.toInt()]) }, { showStatusDialog = false })
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    formatter: SimpleDateFormat,
    cardColor: Color,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(transaction.description, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Row {
                    IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, contentDescription = null) }
                    IconButton(onClick = onDeleteClick) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFB00020)) }
                }
            }
            Text("By: ${transaction.reporter.firstName} ${transaction.reporter.lastName}")
            Text("Amount: ${transaction.amount} RSD", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
            Text("Status: ${transaction.status}", style = MaterialTheme.typography.labelMedium)
            Text("Date: ${formatter.format(transaction.date)}", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun FilterListDialog(title: String, items: List<Pair<Long, String>>, onSelect: (Long) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn {
                items(items) { item ->
                    TextButton(onClick = { onSelect(item.first); onDismiss() }, modifier = Modifier.fillMaxWidth()) {
                        Text(item.second)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEntryDialog(
    title: String,
    initialTransaction: Transaction? = null,
    employees: List<Employee>,
    accounts: List<Account>,
    onDismiss: () -> Unit,
    onConfirm: (TransactionCreateDTO) -> Unit
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf(initialTransaction?.description ?: "") }
    var amount by remember { mutableStateOf(initialTransaction?.amount?.toString() ?: "") }
    var category by remember { mutableStateOf(initialTransaction?.category ?: "") }
    var selectedStatus by remember { mutableStateOf(initialTransaction?.status ?: Status.PENDING) }
    var selectedDate by remember { mutableStateOf(initialTransaction?.date ?: Date()) }
    var selectedEmployee by remember { mutableStateOf(initialTransaction?.reporter ?: employees.firstOrNull()) }
    var selectedAccount by remember { mutableStateOf(initialTransaction?.account ?: accounts.firstOrNull()) }

    var employeeExpanded by remember { mutableStateOf(false) }
    var accountExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val cal = Calendar.getInstance().apply { time = selectedDate }
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                val selected = Calendar.getInstance().apply { set(y, m, d) }
                                selectedDate = selected.time
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Date: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)}")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Status:", fontWeight = FontWeight.Bold)
                Status.values().forEach { status ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedStatus == status, onClick = { selectedStatus = status })
                        Text(status.name)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = employeeExpanded,
                    onExpandedChange = { employeeExpanded = !employeeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedEmployee?.let { "${it.firstName} ${it.lastName}" } ?: "Choose Employee",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Employee") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = employeeExpanded) }
                    )
                    ExposedDropdownMenu(expanded = employeeExpanded, onDismissRequest = { employeeExpanded = false }) {
                        employees.forEach { emp ->
                            DropdownMenuItem(
                                text = { Text("${emp.firstName} ${emp.lastName}") },
                                onClick = { selectedEmployee = emp; employeeExpanded = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = accountExpanded,
                    onExpandedChange = { accountExpanded = !accountExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedAccount?.let { "ID: ${it.idAccount}" } ?: "Choose Account",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Account") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountExpanded) }
                    )
                    ExposedDropdownMenu(expanded = accountExpanded, onDismissRequest = { accountExpanded = false }) {
                        accounts.forEach { acc ->
                            DropdownMenuItem(
                                text = { Text("ID: ${acc.idAccount}") },
                                onClick = { selectedAccount = acc; accountExpanded = false }
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
                        date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(selectedDate),
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        category = category,
                        status = selectedStatus
                    )
                    onConfirm(dto)
                } else {
                    Toast.makeText(context, "You must choose Employee and Account", Toast.LENGTH_SHORT).show()
                }
            }) { Text("Confirm") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}