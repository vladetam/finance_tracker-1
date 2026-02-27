#include <iostream>
using namespace std;
#include <vector>
#include "Account.h"
#include "Employee.h"
#include "Transaction.h"
#include "logger.h"
#include <nlohmann/json.hpp>

bool running = true;
vector<Employee> employees;
vector<Account> accounts;
vector<Transaction> transactions;
void main() {
	// ucitas iz json i iz baze  ||  ucitas iz json i zoves api za proveru ?
	
	//store u vector
	while (running) {
		cout << "Choose one of the following options:" << endl;
		cout << "1. Create Transaction" << endl;
		cout << "2. Create Account" << endl;
		cout << "3. Create Employee" << endl;
		cout << "4. Store entries in DB" << endl;
		cout << "5. Track new transactions in real-time" << endl;
		cout << "6. Sync Accounts with backend" << endl;
		cout << "7. Exit" << endl;
		cout << "=====================================" << endl;
		std::string choise;
		int c;
		while (true) {
			cin >> choise;
			try {
				c = stoi(choise);
				break;
			}
			catch (std::invalid_argument&) {
				cout << "Wrong input, try again." << endl;
			}
			catch (std::out_of_range&) {
				cout << "Wrong input, try again." << endl;
			}
		}
		switch (c) {
		case 1:
			createTransaction();
			break;
		case 2:
			Account::createAccount();
			break;
		case 3:
			createEmployee();
			break;
		case 4:
			storeTransactionInDb();
			break;
		case 5:
			monitorTransactions();
			break;
		case 6:
			syncEmployees();
			break;
		case 7:
			cout << "Exiting";
			running = false;
			break;
		default:
			cout << "Wrong input, try again." << endl;
			break;
		}
	}

}