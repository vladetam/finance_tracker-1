#pragma once
#include <string>
enum class AccountType {
    GOLD,
    SILVER,
    PLATINUM
};

std::string accTypeToString(AccountType at);