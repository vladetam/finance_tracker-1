package orion.rs.demo.bootStrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import orion.rs.demo.domain.Account;
import orion.rs.demo.domain.AccountType;
import orion.rs.demo.domain.Employee;
import orion.rs.demo.repository.AccountRepository;
import orion.rs.demo.repository.EmployeeRepository;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class BootStrapClass implements CommandLineRunner {

    // klasa se koristi
    // da baza bude inicijalno napunjena kada se pokrene projekat za isprobavanje funkcionalnosti

    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;

    @Override
    public void run(String... args) throws Exception {

        Employee employee = new Employee();
        employee.setFirstname("john.doe");
        employee.setLastname("Doe");
        employee.setEmail("john.doe@example.com");

        Employee employee2 = new Employee();
        employee2.setFirstname("Marko");
        employee2.setLastname("Markovic");
        employee2.setEmail("marko@example.com");

        Account account1 = new Account();
        account1.setType(AccountType.GOLD);
        account1.setBalance(1000.0);
        account1.setCurrency("USD");
        account1.setEmployee(employee);

        Account account2 = new Account();
        account2.setType(AccountType.PLATINUM);
        account2.setBalance(500.0);
        account2.setCurrency("EUR");
        account2.setEmployee(employee);

        Account account3 = new Account();
        account3.setType(AccountType.SILVER);
        account3.setBalance(500.0);
        account3.setCurrency("AUD");
        account3.setEmployee(employee);


        employee.getEmployeeAcounts().add(account1);
        employee.getEmployeeAcounts().add(account2);
        employee2.getEmployeeAcounts().add(account3);


        employeeRepository.save(employee);
        employeeRepository.save(employee2);




        }




}
