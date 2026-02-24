#pragma once
#include "AccountType.h"
#include "Employee.h"
class Account
{
private:
	long id;
	AccountType type;
	double balance;
	std::string currency;
	int version;
	Employee employee;
};

