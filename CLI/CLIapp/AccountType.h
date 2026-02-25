#pragma once
#include <string>
#include <iostream>
enum class AccountType {
    GOLD,
    SILVER,
    PLATINUM
};

std::string accTypeToString(AccountType at);
AccountType stringToAccType(const std::string& str);