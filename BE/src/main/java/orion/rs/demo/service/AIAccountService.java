package orion.rs.demo.service;

import lombok.RequiredArgsConstructor;
import orion.rs.demo.domain.Account;
import orion.rs.demo.domain.AccountType;
import orion.rs.demo.domain.Employee;
import orion.rs.demo.dto.AccountCreateDTO;
import orion.rs.demo.dto.BulkInsertAccDTO;
import orion.rs.demo.dto.FailedRecord;
import orion.rs.demo.repository.AccountRepository;
import orion.rs.demo.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AIAccountService {

    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;
    private final Random random = new Random();

    // Generise i cuva broj random naloga
    public List<Account> generateAndSaveAccounts(int count) {

        List<Employee> employees = employeeRepository.findAll();
        if (employees.isEmpty()) {
            throw new RuntimeException("Nema zaposlenih u bazi");
        }

        List<Account> savedAccounts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Employee randomEmployee = employees.get(random.nextInt(employees.size()));
            Account account = buildRandomAccount(randomEmployee);
            savedAccounts.add(accountRepository.save(account));
        }
        return savedAccounts;
    }

    // Generise i cuva naloge za konkretnog zaposlenog
    public List<Account> generateForEmployee(Long employeeId, int count) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Nema tog zaposlenog"));

        List<Account> savedAccounts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Account account = buildRandomAccount(employee);
            savedAccounts.add(accountRepository.save(account));
        }
        return savedAccounts;
    }

    // Helper za random account
    private Account buildRandomAccount(Employee employee) {
        Account account = new Account();
        account.setType(randomAccountType());
        account.setBalance(random.nextInt(10000));
        account.setCurrency(randomCurrency());
        account.setEmployee(employee);
        return account;
    }

    private AccountType randomAccountType() {
        AccountType[] values = AccountType.values();
        return values[random.nextInt(values.length)];
    }

    private String randomCurrency() {
        String[] currencies = {"EUR", "USD", "RSD", "CHF", "GBP"};
        return currencies[random.nextInt(currencies.length)];
    }
}