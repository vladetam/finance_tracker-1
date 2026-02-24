#include "status.h"


std::string statusToString(Status s) {
    switch (s) {
    case Status::PENDING: return "PENDING";
    case Status::COMPLETED: return "COMPLETED";
    case Status::RECONCILED: return "RECONCILED";
    }
    return "";
}