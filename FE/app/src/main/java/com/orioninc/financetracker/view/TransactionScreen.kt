package com.orioninc.financetracker.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.orioninc.financetracker.model.Transaction
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    transactions: List<Transaction>,
    onFilterClick: (String, Any?) -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance Tracker") },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Filter")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Filter by Date") },
                            onClick = {
                                menuExpanded = false
                                onFilterClick("DATE", null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Filter by Employee") },
                            onClick = {
                                menuExpanded = false
                                onFilterClick("EMPLOYEE", null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Filter by Transaction Type") },
                            onClick = {
                                menuExpanded = false
                                onFilterClick("TRANSACTION TYPE", null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Filter by Account") },
                            onClick = {
                                menuExpanded = false
                                onFilterClick("ACCOUNT", null)
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Show All") },
                            onClick = {
                                menuExpanded = false
                                onFilterClick("RESET", null)
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(transactions) { item ->
                    TransactionItem(item, dateFormatter)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, formatter: SimpleDateFormat) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Gray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = transaction.description, fontWeight = FontWeight.Bold)
            Text(text = "By: ${transaction.reporter.firstName} ${transaction.reporter.lastName}")
            Text(
                text = "Amount: ${transaction.amount} RSD",
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Date: ${formatter.format(transaction.date)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}