#include "Account.h"

Account::Account(long id,
    AccountType type,
    double balance,
    const std::string& currency,
    int version,
    const Employee& employee)
    : id(id), type(type), balance(balance), currency(currency), version(version), employee(employee) {
}

json Account::to_json() const {
    return json{
        {"id", id},
        {"type", accountTypeToString(type)},
        {"balance", balance},
        {"currency", currency},
        {"version", version},
        {"employee", employee.to_json()}
    };
}

std::string Account::accountTypeToString(AccountType type) {
    switch (type) {
    case AccountType::GOLD: return "GOLD";
    case AccountType::SILVER: return "SILVER";
    case AccountType::PLATINUM: return "PLATINUM";
    }
    return "Unknown";
}

bool Account::createAccount() {
    std::string balanceStr, currency;
    int acctyp;
    AccountType accType;

    std::cout << "\n=== Create Account ===\n";

    while (true) {
        std::cout << "\nChoose account type (type number):\n";
        std::cout << " 1. GOLD\n 2. SILVER\n 3. PLATINUM\n";
        std::cout << "Enter number: ";
        std::cin >> acctyp;

        if (acctyp == 1) { accType = AccountType::GOLD; break; }
        else if (acctyp == 2) { accType = AccountType::SILVER; break; }
        else if (acctyp == 3) { accType = AccountType::PLATINUM; break; }
        else std::cout << "Invalid choice. Please enter 1, 2, or 3.\n";
    }
 
    double balance;
    while (true) {
        std::cout << "Balance: ";
        std::cin >> balanceStr;
        std::cin.ignore();

        std::stringstream ss(balanceStr);
        if (ss >> balance) break;  
        std::cout << "Invalid balance. Please enter a numeric value.\n";
    }

    while (true) {
        std::cout << "Currency: ";
        std::getline(std::cin, currency);
        if (!currency.empty()) break;
        std::cout << "Currency is mandatory.\n";
    }

    json jEmployees = json::array();
    std::ifstream empFile("employees.json");
    if (empFile.is_open()) {
        try { empFile >> jEmployees; }
        catch (...) { jEmployees = json::array(); }
        empFile.close();
    }

    if (jEmployees.empty()) {
        std::cout << "No employees found. Please create employees first.\n";
        return false;
    }

    size_t choice;
    Employee emp(0, "", "", "");
    while (true) {
        std::cout << "\nSelect an employee to assign:\n";
        for (size_t i = 0; i < jEmployees.size(); ++i) {
            const auto& e = jEmployees[i];
            std::cout << i + 1 << ". [" << e["id"] << "] "
                << e["firstname"].get<std::string>() << " "
                << e["lastname"].get<std::string>()
                << " (" << e["email"].get<std::string>() << ")\n";
        }

        std::cout << "Enter number: ";
        std::cin >> choice;
        std::cin.ignore();

        if (choice >= 1 && choice <= jEmployees.size()) {
            auto selected = jEmployees[choice - 1];
            emp = Employee(
                selected["id"].get<long>(),
                selected["firstname"].get<std::string>(),
                selected["lastname"].get<std::string>(),
                selected["email"].get<std::string>()
            );
            break;
        }
        std::cout << "Invalid employee selection. Try again.\n";
    }

    long newId = 1;
    int version = 1;
    json accounts = json::array();
    std::ifstream inFile("accounts.json");
    if (inFile.is_open()) {
        try { inFile >> accounts; }
        catch (...) { accounts = json::array(); }
        inFile.close();
        if (!accounts.empty())
            newId = accounts.back()["id"].get<long>() + 1;
    }

    Account account(newId, accType, balance, currency, version, emp);
    accounts.push_back(account.to_json());

    std::ofstream outFile("accounts.json");
    outFile << accounts.dump(4);
    outFile.close();

    std::cout << "\nAccount saved successfully! ID: " << newId
        << ", Assigned to employee: " << emp.to_json()["firstname"] << " "
        << emp.to_json()["lastname"] << "\n";

    return true;
}