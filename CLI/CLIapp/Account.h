#pragma once
#include "AccountType.h"
#include "Employee.h"
#include <string>
#include <nlohmann/json.hpp>
#include <iostream>
#include <fstream>

using json = nlohmann::json;

class Account
{
private:
	long id;
	AccountType type;
	double balance;
	std::string currency;
	int version;
	Employee employee;

public:
    Account(long id, AccountType type,double balance,const std::string& currency,int version,const Employee& employee);

    json to_json() const;

    static bool createAccount(); 
};

