#include "Employee.h"



bool createEmployee()
{
	// Collecting of user data
	std::string firstName;
	std::string lastName;
	std::string email;

	std::cout << "Enter first name" << std::endl;
	std::cin >> firstName;
	std::cout << "Enter last name:" << std::endl;
	std::cin >> lastName;
	std::cout << "Enter email:" << std::endl;
	std::cin >> email;
	
	// regex and check
	std::regex namePattern("^[A-Za-z'-]+$");
	std::regex emailPattern(R"(^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$)");

	if (!regex_match(firstName, namePattern)) {
		std::cout << "Invalid first name!" << std::endl;
		std::cout << "=====================================" << std::endl;
		return false;
	}
	if (!regex_match(lastName, namePattern)) {
		std::cout << "Invalid last name!" << std::endl;
		std::cout << "=====================================" << std::endl;
		return false;
	}
	if (!regex_match(email, emailPattern)) {
		std::cout << "Invalid email!" << std::endl;
		std::cout << "=====================================" << std::endl;
		return false;
	}

	// Creating new employee
	Employee emp(firstName, lastName, email);
	emp.setId(0);

	std::string fileName = "employees.json";
	
	// Load existing employees
	json jEmployees = json::array();
	std::ifstream inFile(fileName);
	if (inFile.is_open()) {
		try {
			inFile >> jEmployees;
		}
		catch (...) {
			jEmployees = json::array(); // if file is empty/corrupt
		}
		inFile.close();
	}

	// Add new employee
	jEmployees.push_back(emp.to_json());


	// Save all employees back
	std::ofstream outFile(fileName);
	outFile << jEmployees.dump(4); // pretty print
	outFile.close();

	std::cout << "Employee saved successfully!" << std::endl;

	
	std::cout << "=====================================" << std::endl;
	return false;
}

void syncEmployees() {
	std::string response = httpGET("http://localhost:8080/api/employees/export");

	// Get current date and time for filename
	std::time_t t = std::time(nullptr);
	std::tm tm;
#ifdef _WIN32
	localtime_s(&tm, &t);
#else
	localtime_r(&t, &tm);
#endif

	std::ostringstream oss;
	oss << "employees_"
		<< std::put_time(&tm, "%Y-%m-%d_%H-%M-%S")
		<< ".csv";
	std::string filename = oss.str();

	// Open file for writing
	std::ofstream outFile(filename);
	if (!outFile.is_open()) {
		std::cout << "Failed to open file: " << filename << "\n";
		return;
	}

	// Write CSV data
	outFile << response;
	outFile.close();

	std::cout << "Employees exported successfully to " << filename << "\n";
}