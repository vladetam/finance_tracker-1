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
};

