#pragma once
#include "Employee.h"
#include "Account.h"
#include <chrono>
#include "Status.h"
#include <iomanip>
#include <sstream>
class Transaction
{
private:
	long id;
	Employee reporter;
	Account	account;
	std::string description;
	int version;
	std::chrono::system_clock::time_point now;
	long long amount;
	std::string category;
	Status status;
public:
    Transaction(long id,
        const Employee& reporter,
        const Account& account,
        const std::string& description,
        int version,
        std::chrono::system_clock::time_point timestamp,
        long long amount,
        const std::string& category,
        Status status)
        : id(id),
        reporter(reporter),
        account(account),
        description(description),
        version(version),
        now(timestamp),
        amount(amount),
        category(category),
        status(status)
    {
    }
    json to_json() const;
};

bool createTransaction();

std::string timePointToString(std::chrono::system_clock::time_point tp);

std::chrono::system_clock::time_point stringToTimePoint(const std::string& str);