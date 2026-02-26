#pragma once
#include <string>
#include <nlohmann/json.hpp>
#include <iostream>
#include <fstream>
#include <regex>
#include <ctime>
#include <iomanip>
#include <sstream>
#include "http.h"

using json = nlohmann::json;

class Employee
{
private:
	long id;
	std::string firstname;
	std::string lastname;
	std::string email;

public:
    // Constructor to be used in create
	Employee(std::string firstname, std::string lastname, std::string email) : firstname(firstname), lastname(lastname), email(email) {}

    // Constructor to be used when retrieving from db
    Employee(long id ,std::string firstname, std::string lastname, std::string email) : id(id), firstname(firstname), lastname(lastname), email(email) {}
    
    // Convert to JSON
    json to_json() const {
        return json{
          
            {"first_name", firstname},
            {"last_name", lastname},
            {"email", email}
        };
    }

    void setId(long newId) { id = newId; }
    long getId() { return id; }
};

bool createEmployee();

void syncEmployees();