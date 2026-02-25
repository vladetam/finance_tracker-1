package com.orioninc.financetracker.view

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import com.orioninc.financetracker.model.Transaction
import com.orioninc.financetracker.model.Status
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreenRoute(
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val transactions by viewModel.filteredTransactions.observeAsState(emptyList())
    val loading by viewModel.loading.observeAsState(false)
    val error by viewModel.error.observeAsState()

    TransactionScreen(
        transactions = transactions,
        loading = loading,
        error = error,
        onReset = { viewModel.resetFilter() },
        onFilterByEmployee = { viewModel.filterByEmployee(it) },
        onFilterByAccount = { viewModel.filterByAccount(it) },
        onFilterByStatus = { viewModel.filterByStatus(it) },
        onFilterByDate = { viewModel.filterByDate(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    transactions: List<Transaction>,
    loading: Boolean,
    error: String?,
    onReset: () -> Unit,
    onFilterByEmployee: (Long) -> Unit,
    onFilterByAccount: (Long) -> Unit,
    onFilterByStatus: (Status) -> Unit,
    onFilterByDate: (Date) -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var menuExpanded by remember { mutableStateOf(false) }

    val softDarkGray = Color(0xFF373737)
    val concreteLightGray = Color(0xFFE0E0E0)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Finance Tracker", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = softDarkGray
                ),
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {

                        // Date filter
                        DropdownMenuItem(
                            text = { Text("Filter by Date") },
                            onClick = {
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
                            }
                        )

                        Divider()

                        // Status filters
                        DropdownMenuItem(
                            text = { Text("Status: Pending") },
                            onClick = {
                                menuExpanded = false
                                onFilterByStatus(Status.Pending)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Status: Completed") },
                            onClick = {
                                menuExpanded = false
                                onFilterByStatus(Status.Completed)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Status: Reconciled") },
                            onClick = {
                                menuExpanded = false
                                onFilterByStatus(Status.Reconciled)
                            }
                        )

                        Divider()

                        // Employee filters (example IDs)
                        DropdownMenuItem(
                            text = { Text("Employee 1") },
                            onClick = {
                                menuExpanded = false
                                onFilterByEmployee(1)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Employee 2") },
                            onClick = {
                                menuExpanded = false
                                onFilterByEmployee(2)
                            }
                        )

                        Divider()

                        // Account filters (example IDs)
                        DropdownMenuItem(
                            text = { Text("Account 1") },
                            onClick = {
                                menuExpanded = false
                                onFilterByAccount(1)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Account 2") },
                            onClick = {
                                menuExpanded = false
                                onFilterByAccount(2)
                            }
                        )

                        Divider()

                        DropdownMenuItem(
                            text = { Text("Show All") },
                            onClick = {
                                menuExpanded = false
                                onReset()
                            }
                        )
                    }
                }
            )
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
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    formatter: SimpleDateFormat,
    cardColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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