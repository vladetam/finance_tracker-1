#include "logger.h"

json fetchTransactions(const std::string& url)
{
    try {
        std::string response = httpGET(url);
        if (response.empty()) return json::array();

        json j = json::parse(response);
        if (j.is_array()) return j;
        return json::array();
    }
    catch (...) {
        std::cout << "Error fetching or parsing transactions.\n";
        return json::array();
    }
}

std::string getCurrentTimeString()
{
    auto now = std::chrono::system_clock::now();
    std::time_t t = std::chrono::system_clock::to_time_t(now);
    std::tm tm{};
    localtime_s(&tm, &t);

    char buffer[64];
    std::strftime(buffer, sizeof(buffer), "%Y-%m-%d_%H-%M-%S", &tm);
    return std::string(buffer);
}

// Format transaction output
void logTransaction(std::ostream& os, const json& txnJson)
{
    os << "-----------------------------------\n";
    os << "Transaction ID : " << txnJson["id"] << "\n";
    os << "Reporter ID    : " << txnJson["reporter"]["id"] << "\n";
    os << "Account ID     : " << txnJson["account"]["id"] << "\n";
    os << "Description    : " << txnJson["description"] << "\n";
    os << "Amount         : " << txnJson["amount"] << "\n";
    os << "Category       : " << txnJson["category"] << "\n";
    os << "Date           : " << txnJson["date"] << "\n";
    os << "Status         : " << txnJson["status"] << "\n";
    os << "-----------------------------------\n\n";
}

// Format account output
void logAccount(std::ostream& os, const json& accJson)
{
    os << "-----------------------------------\n";
    os << "Account ID     : " << accJson["id"] << "\n";
    os << "Type           : " << accJson["type"] << "\n";
    os << "Balance        : " << accJson["balance"] << "\n";
    os << "Currency       : " << accJson["currency"] << "\n";

    // Optional: employee attached to this account
    if (accJson.contains("employee") && accJson["employee"].contains("id"))
    {
        os << "Employee ID    : " << accJson["employee"]["id"] << "\n";
    }

    os << "-----------------------------------\n\n";
}

void logEmployee(std::ostream &os, const json &empJson)
{
    os << "-----------------------------------\n";
    os << "Employee ID    : " << empJson["id"] << "\n";
    os << "Firstname      : " << empJson["firstname"] << "\n";
    os << "Lastname       : " << empJson["lastname"] << "\n";
    os << "Email          : " << empJson["email"] << "\n";
    os << "-----------------------------------\n\n";
}

bool monitorTransactions()
{
    std::unordered_set<long> seenTransactions;
    std::unordered_set<long> seenAccounts;
    std::unordered_set<long> seenEmployees;

    std::string txApiUrl = "http://localhost:8080/api/transactions";
    std::string accApiUrl = "http://localhost:8080/api/accounts/getAccounts";
    std::string empApiUrl = "http://localhost:8080/api/employees";

    std::cout << "Monitoring transactions, accounts, and employees...\n";

    //initial fetch to populate seen sets
    {
        for (auto& j : fetchTransactions(txApiUrl))    seenTransactions.insert(j["id"].get<long>());
        for (auto& j : fetchTransactions(accApiUrl))   seenAccounts.insert(j["id"].get<long>());
        for (auto& j : fetchTransactions(empApiUrl))   seenEmployees.insert(j["id"].get<long>());
    }

    while (true)
    {
        std::vector<json> newTx;
        std::vector<json> newAcc;
        std::vector<json> newEmp;

        std::cout << "\n========== FETCHING NEW DATA ==========\n";

        //Fetch check transactions
        for (auto& txn : fetchTransactions(txApiUrl))
        {
            long id = txn["id"].get<long>();
            if (seenTransactions.insert(id).second)
            {
                std::cout << "NEW TRANSACTION:\n";
                logTransaction(std::cout, txn);
                newTx.push_back(txn);
            }
        }

        //Fetch and check accounts
        for (auto& acc : fetchTransactions(accApiUrl))
        {
            long id = acc["id"].get<long>();
            if (seenAccounts.insert(id).second)
            {
                std::cout << "NEW ACCOUNT:\n";
                logAccount(std::cout, acc);
                newAcc.push_back(acc);
            }
        }

        //Fetch and check employees
        for (auto& emp : fetchTransactions(empApiUrl))
        {
            long id = emp["id"].get<long>();
            if (seenEmployees.insert(id).second)
            {
                std::cout << "NEW EMPLOYEE:\n";
                logEmployee(std::cout, emp);
                newEmp.push_back(emp);
            }
        }

        //Only create a log file if something new exists
        if (!newTx.empty() || !newAcc.empty() || !newEmp.empty())
        {
            std::string logFileName = "monitor_log_" + getCurrentTimeString() + ".txt";
            std::ofstream logFile(logFileName, std::ios::app);

            if (!logFile)
            {
                std::cout << "Failed to create log file: " << logFileName << "\n";
            }
            else
            {
                // Write all new items to the same log file
                for (auto& txn : newTx) logTransaction(logFile, txn);
                for (auto& acc : newAcc) logAccount(logFile, acc);
                for (auto& emp : newEmp) logEmployee(logFile, emp);

                logFile.close();
                std::cout << "Saved new data to " << logFileName << "\n";
            }
        }
        else
        {
            std::cout << "No new data in this fetch.\n";
        }

        std::cout << "============================================\n";

        // Wait 5 seconds before next fetch
        std::this_thread::sleep_for(std::chrono::seconds(5));
    }

    return true;
}
