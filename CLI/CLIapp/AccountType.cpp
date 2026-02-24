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
