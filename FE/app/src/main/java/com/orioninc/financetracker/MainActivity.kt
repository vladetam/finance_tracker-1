package com.orioninc.financetracker

import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.orioninc.financetracker.model.Account
import com.orioninc.financetracker.model.AccountType
import com.orioninc.financetracker.model.Employee
import com.orioninc.financetracker.model.Status
import com.orioninc.financetracker.model.Transaction
import com.orioninc.financetracker.view.TransactionScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val radnik1 = Employee(1,"marko","markovic","marko@tracker.com")
        val radnik2 = Employee(2,"uros","urosevic","uros@tracker.com")
        val racun1 = Account(1, AccountType.Gold, 50000.0, "RSD", radnik1)
        val racun2 = Account(2, AccountType.Platinum, 100000.0, "RSD", radnik1)

        val allTransactions = listOf(
            Transaction(1,radnik1,racun1,"use 1", Date(), 1200.0,"",Status.Completed),
            Transaction(2,radnik2,racun1,"use 2",juce(),4500.0,"",Status.Pending),
            Transaction(3,radnik2,racun2,"use 3",Date(),3600.0,"",Status.Completed)
        )
        setContent {
            var listForShow by remember{ mutableStateOf(allTransactions) }

            TransactionScreen(
                transactions = listForShow,
                onFilterClick = {type, data ->
                    when(type){
                        "DATE" -> {
                            showDatePicker(allTransactions) { listForShow = it}
                        }
                        "EMPLOYEE" -> {
                            showEmployeeDialog(allTransactions){ listForShow = it}
                        }
                        "TRANSACTION TYPE" -> {
                            showStatusDialog(allTransactions) { listForShow = it }
                        }
                        "ACCOUNT" -> {
                            showAccountDialog(allTransactions){ listForShow = it}
                        }
                        "RESET" -> {
                            listForShow = allTransactions
                        }
                    }
                }
            )
        }
    }

    private fun showDatePicker(allTransactions: List<Transaction>, onResult: (List<Transaction>) -> Unit) {
        val cal = java.util.Calendar.getInstance()
        android.app.DatePickerDialog(this, { _, y, m, d ->
            val selCal = java.util.Calendar.getInstance().apply { set(y, m, d) }
            val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            onResult(allTransactions.filter { fmt.format(it.date) == fmt.format(selCal.time) })
        }, cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH), cal.get(java.util.Calendar.DAY_OF_MONTH)).show()
    }

    private fun showEmployeeDialog(allTransactions: List<Transaction>, onResult: (List<Transaction>) -> Unit) {
        val workers = allTransactions.map { it.reporter }.distinctBy { it.idEmployee }
        val names = workers.map { "${it.firstName} ${it.lastName}" }.toTypedArray()
        android.app.AlertDialog.Builder(this).setTitle("Select Employee").setItems(names) { _, i ->
            onResult(allTransactions.filter { it.reporter.idEmployee == workers[i].idEmployee })
        }.show()
    }

    private fun showStatusDialog(allTransactions: List<Transaction>, onResult: (List<Transaction>) -> Unit) {
        val statuses = Status.entries.toTypedArray()
        val names = statuses.map { it.name }.toTypedArray()
        android.app.AlertDialog.Builder(this).setTitle("Select Status").setItems(names) { _, i ->
            onResult(allTransactions.filter { it.status == statuses[i] })
        }.show()
    }

    private fun showAccountDialog(allTransactions: List<Transaction>, onResult: (List<Transaction>) -> Unit) {
        val accounts = allTransactions.map { it.account }.distinctBy { it.idAccount }
        val names = accounts.map { "Account ID: ${it.idAccount} (${it.accountType})" }.toTypedArray()
        android.app.AlertDialog.Builder(this).setTitle("Select Account").setItems(names) { _, i ->
            onResult(allTransactions.filter { it.account.idAccount == accounts[i].idAccount })
        }.show()
    }

    private fun juce(): Date{
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE,-1)
        return cal.time
    }
}
