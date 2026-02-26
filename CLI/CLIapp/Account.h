#pragma once
#include "AccountType.h"
#include "Employee.h"
#include <string>
#include <nlohmann/json.hpp>
#include <iostream>
#include <fstream>
#include "http.h"

using json = nlohmann::json;

class Account
{
private:
	long id;
	AccountType type;
	double balance;
	std::string currency;
	int version;
	long employee;

public:
	Account(long id, AccountType type, double balance, const std::string& currency, int version,long employee);

	json to_json() const;

	static bool createAccount();

	long getId() { return id; }
};

