#include "Transaction.h"

bool createTransaction()
{
    // Employee Check
    json jEmployees = json::array();
    std::ifstream empFile("employees.json");
    if (empFile.is_open()) {
        try { empFile >> jEmployees; }
        catch (...) { jEmployees = json::array(); }
        empFile.close();
    }

    if (jEmployees.empty()) {// TODO: dodati api call i proveriti da li postoje podaci na njemu
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
                << e["firstname"].get<std::string>() << " "
                << e["lastname"].get<std::string>()
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
                    selected["firstname"].get<std::string>(),
                    selected["lastname"].get<std::string>(),
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


    // Account Check
    json jAccounts = json::array();
    std::ifstream accFile("accounts.json");
    if (accFile.is_open()) {
        try {
            accFile >> jAccounts;
        }
        catch (...) {
            jAccounts = json::array();
        }
        accFile.close();
    }

    if (jAccounts.empty()) {// TODO: dodati api call i proveriti da li postoje podaci na njemu
        std::cout << "No accounts found. Please create accounts first.\n";
        return false;
    }

    Account acc(0, AccountType::GOLD, 0.0, "", 0, Employee(0, "", "", ""));

    while (true) {
        std::cout << "\nSelect an account:\n";
        for (size_t i = 0; i < jAccounts.size(); ++i) {
            const auto& a = jAccounts[i];
            std::cout << i + 1 << ". [ID: " << a["id"] << "] "
                << "Type: " << a["type"] << ", "
                << "Balance: " << a["balance"] << " "
                << a["currency"] << "\n";
        }

        std::cout << "Enter number: ";
        std::cin >> input;
        std::cin.ignore();

        try {
            choice = std::stoi(input);
            if (choice >= 1 && choice <= jAccounts.size()) {

                auto selected = jAccounts[choice - 1];

                // Extract Employee from nested JSON
                auto empJson = selected["employee"];
                Employee empAcc(
                    empJson["id"].get<long>(),
                    empJson["firstname"].get<std::string>(),
                    empJson["lastname"].get<std::string>(),
                    empJson["email"].get<std::string>()
                );
                
                // Create Account object
                acc = Account(
                    selected["id"].get<long>(),
                    stringToAccType(selected["type"].get<std::string>()),
                    selected["balance"].get<double>(),
                    selected["currency"].get<std::string>(),
                    selected["version"].get<int>(),
                    empAcc
                );

                break;
            }

            std::cout << "Invalid account selection. Try again.\n";
        }
        catch (std::invalid_argument&) {
            std::cout << "Invalid number\n";
        }
        catch (std::out_of_range&) {
            std::cout << "Number out of range\n";
        }
    }

    // description check
    std::string description;
    std::cout << "Please provide a description" << std::endl;   
    std::regex descRegex("^[A-Za-z0-9 .,\\-_]{3,100}$");
    while (true) {
        std::cin >> description;
       
        if (std::regex_match(description, descRegex)) break;
        std::cout << "Bad description, please try again." << std::endl;
    }

    // date check

    

    std::string datePart, timePart, fullDate;

    std::regex pattern(
        R"(^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])\s([01]\d|2[0-3]):[0-5]\d:[0-5]\d$)"
    );

    while (true) {
        std::cout << "Please enter the date: (format: %Y-%m-%d)\n";
        std::cin >> datePart;
        std::cout << "Please enter the time: (format: %H:%M:%S)\n";
        std::cin >> timePart;  // read two tokens
        fullDate = datePart +" " + timePart;

        if (std::regex_match(fullDate, pattern))
            break;

        std::cout << "Invalid format. Try again:\n";
    }
    //conversion into chrono date
    std::chrono::system_clock::time_point date = stringToTimePoint(fullDate);


    // amount check
    long long amount;
    while (true)
    {
        std::cout << "Enter amount: ";

        if (std::cin >> amount)
        {
            if (amount < 0)
            {
                std::cout << "Amount cannot be negative.\n";
                continue;
            }

            break; // valid input
        }
        else
        {
            std::cout << "Invalid number. Please enter digits only.\n";

            std::cin.clear(); // clear fail state
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n'); // discard bad input
        }
    }

    // category check
    std::regex categoryRegex("^[A-Za-z ]+$");
    std::string category;
    while (true) {
        std::cout << "Enter category:" << std::endl;
        std::cin >> category;
        if (std::regex_match(category, categoryRegex)) break;
        std::cout << "Wrong input for category, please try again." << std::endl;
    }

    // status check
    std::string statusStr;
    int num;
    Status status;
    while (true) {
        std::cout << "\nChoose status (type number):\n";
        std::cout << " 1. COMPLETED\n 2. PENDING\n 3. RECONCILED\n";
        std::cout << "Enter number: ";
        std::cin >> statusStr;
        try {
            num = std::stoi(statusStr);
            if (num == 1) { status = Status::COMPLETED; break; }
            else if (num == 2) { status = Status::PENDING; break; }
            else if (num == 3) { status = Status::RECONCILED; break; }
            else std::cout << "Invalid choice. Please enter 1, 2, or 3.\n";
        }
        catch (std::invalid_argument&) {
            std::cout << "Invalid number\n";
        }
        catch (std::out_of_range&) {
            std::cout << "Number out of range\n";
        }

    }
    // cuvanje u json
    Transaction transaction(0, emp, acc, description, 0, date, amount, category, status);

    json transactions = json::array();
    std::ifstream inFile("transactions.json");

    if (inFile.is_open()) {
        try {
            inFile >> transactions;
        }
        catch (...) {
            transactions = json::array();
        }
        inFile.close();
    }
    transactions.push_back(transaction.to_json());
    std::ofstream outFile("transactions.json");
    outFile << transactions.dump(4);
    outFile.close();

    std::cout << "\nTransaction saved successfully!\n";
    std::cout << "=====================================" << std::endl;
    return false;
}

std::string timePointToString(std::chrono::system_clock::time_point tp)
{
    std::time_t t = std::chrono::system_clock::to_time_t(tp);

    std::tm tm{};
    gmtime_s(&tm, &t);

    std::stringstream ss;
    ss << std::put_time(&tm, "%Y-%m-%d %H:%M:%SZ");
    return ss.str();
}
std::chrono::system_clock::time_point stringToTimePoint(const std::string& str)
{
    std::istringstream iss(str);
    std::chrono::system_clock::time_point tp;

    iss >> std::chrono::parse("%Y-%m-%d %H:%M:%SZ", tp);//"%Y-%m-%dT%H:%M:%SZ"

    return tp;
}

json Transaction::to_json() const {
    return {
        {"id", id},
        {"reporter", reporter.to_json()},
        {"account", account.to_json()},
        {"description", description},
        {"version", version},
        {"date", timePointToString(now)},
        {"amount", amount},
        {"category", category},
        {"status", statusToString(status)}
    };
}