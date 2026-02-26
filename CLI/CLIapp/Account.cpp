#include "Account.h"

Account::Account(long id,
    AccountType type,
    double balance,
    const std::string& currency,
    int version,
    long employee)
    : id(id), type(type), balance(balance), currency(currency), version(version), employee(employee) {
}

json Account::to_json() const {
    return json{
       
        {"type", accTypeToString(type)},
        {"balance", balance},
        {"currency", currency},
      
        {"employeeId", employee}
    };
}

bool Account::createAccount() {
    std::string balanceStr, currency;
    std::string acctyp;
    int num;
    AccountType accType;

    std::cout << "\n=== Create Account ===\n";

    while (true) {
        std::cout << "\nChoose account type (type number):\n";
        std::cout << " 1. GOLD\n 2. SILVER\n 3. PLATINUM\n";
        std::cout << "Enter number: ";
        std::cin >> acctyp;
        try {
            num = std::stoi(acctyp);
            if (num == 1) { accType = AccountType::GOLD; break; }
            else if (num == 2) { accType = AccountType::SILVER; break; }
            else if (num == 3) { accType = AccountType::PLATINUM; break; }
            else std::cout << "Invalid choice. Please enter 1, 2, or 3.\n";
        }
        catch (std::invalid_argument&) {
            std::cout << "Invalid number\n";
        }
        catch (std::out_of_range&) {
            std::cout << "Number out of range\n";
        }
        
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
        std::regex pattern("^[A-Za-z]+$");
        if (!std::regex_match(currency, pattern)) {
            std::cout << "Invalid input (letters only)\n";
            continue;
        }
        if (!currency.empty()) break;
        std::cout << "Currency is mandatory.\n";
    }

    json jEmployees = json::array();

    std::string response = httpGET("http://localhost:8080/api/employees");

    try {
        json j = json::parse(response);

        // Check if "content" exists and is an array
        if (j.contains("content") && j["content"].is_array()) {
            jEmployees = j["content"];
        }
        else {
            std::cout << "Invalid API response format.\n";
            return false;
        }
    }
    catch (const json::parse_error& e) {
        std::cout << "Invalid JSON: " << e.what() << "\n";
        return false;
    }

    // If no employees returned
    if (jEmployees.empty()) {
        std::cout << "No employees found. Please create employees first.\n";
        return false;
    }

    size_t choice;
    std::string input;
    Employee emp(0, "", "", "");

    while (true) {
        std::cout << "\nSelect an employee to assign:\n";

        for (size_t i = 0; i < jEmployees.size(); ++i) {
            const auto& e = jEmployees[i];
            std::cout << i + 1 << ". [" << e["id"] << "] "
                << e["firstName"].get<std::string>() << " "
                << e["lastName"].get<std::string>()
                << " (" << e["email"].get<std::string>() << ")\n";
        }

        std::cout << "Enter number: ";
        std::cin >> input;
        std::cin.ignore();

        try {
            choice = std::stoi(input);

            if (choice >= 1 && choice <= jEmployees.size()) {
                auto selected = jEmployees[choice - 1];

                emp = Employee(
                    selected["id"].get<long>(),
                    selected["firstName"].get<std::string>(),
                    selected["lastName"].get<std::string>(),
                    selected["email"].get<std::string>()
                );

                break;
            }

            std::cout << "Invalid employee selection. Try again.\n";
        }
        catch (std::invalid_argument&) {
            std::cout << "Invalid number\n";
        }
        catch (std::out_of_range&) {
            std::cout << "Number out of range\n";
        }
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

    Account account(newId, accType, balance, currency, version, emp.getId());
    accounts.push_back(account.to_json());

    std::ofstream outFile("accounts.json");
    outFile << accounts.dump(4);
    outFile.close();

    std::cout << "\nAccount saved successfully! ID: " << newId
        << ", Assigned to employee: " << emp.to_json()["firstname"] << " "
        << emp.to_json()["lastname"] << "\n";

    return true;
}