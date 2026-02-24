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

    // da baza bude inicijalno napunjena kada se pokrene projekat za isprobavanje funkcionalnosti

    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;

    @Override
    public void run(String... args) throws Exception {

        Employee employee = new Employee();
        employee.setFirstname("john.doe");
        employee.setLastname("Doe");
        employee.setEmail("john.doe@example.com");

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

// Dodaj naloge zaposlenom
        employee.getEmployeeAcounts().add(account1);
        employee.getEmployeeAcounts().add(account2);

// Samo snimi zaposlenog, nalozi će se automatski sačuvati
        employeeRepository.save(employee);



        }




}
