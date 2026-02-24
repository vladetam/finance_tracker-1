#pragma once
#include <string>
enum class Status {
    PENDING,
    COMPLETED,
    RECONCILED
};

std::string statusToString(Status s);