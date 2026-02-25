#include "AccountType.h"

std::string accTypeToString(AccountType at)
{
    switch (at) {
    case AccountType::GOLD: return "GOLD";
    case AccountType::SILVER: return "SILVER";
    case AccountType::PLATINUM: return "PLATINUM";
    }
    return "";
}

AccountType stringToAccType(const std::string& str)
{
    if (str == "GOLD") return AccountType::GOLD;
    if (str == "SILVER") return AccountType::SILVER;
    if (str == "PLATINUM") return AccountType::PLATINUM;

    throw std::invalid_argument("Invalid account type string");
}
