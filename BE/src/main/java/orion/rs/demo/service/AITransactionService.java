package orion.rs.demo.service;

import orion.rs.demo.domain.Account;
import orion.rs.demo.domain.Employee;
import orion.rs.demo.domain.Transaction;
import orion.rs.demo.domain.Status;
import orion.rs.demo.repository.AccountRepository;
import orion.rs.demo.repository.EmployeeRepository;
import orion.rs.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AITransactionService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private final Set<String> usedCombinations = new HashSet<>();

    private final String[] CATEGORIES = {
            "Hrana", "Gorivo", "Kancelarija", "Putovanje",
            "Obuka", "Računi", "Marketing", "IT Oprema"
    };

    private final String[] DESCRIPTIONS = {
            "Kupovina u marketu", "Račun za struju", "Gorivo za službeno vozilo",
            "Kancelarijski materijal", "Ručak sa klijentom", "Putni troškovi"
    };

    @Transactional
    public List<Transaction> generateAndSaveTransactions(int count, Date date) {
        List<Employee> employees = employeeRepository.findAll();
        List<Account> accounts = accountRepository.findAll();

        if (employees.isEmpty() || accounts.isEmpty()) {
            throw new RuntimeException("Nema zaposlenih ili računa u bazi!");
        }

        System.out.println("Generišem " + count + " transakcija za datum: " + date);

        List<Transaction> transactions = generateTransactions(count, date, employees, accounts);

        if (transactions.isEmpty()) {
            return new ArrayList<>();
        }

        return transactionRepository.saveAll(transactions);
    }

    private List<Transaction> generateTransactions(int count, Date date,
                                                   List<Employee> employees,
                                                   List<Account> accounts) {
        List<Transaction> transactions = new ArrayList<>();
        usedCombinations.clear();

        // Postavi datum na početak i kraj dana za proveru
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startOfDay = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);
        Date endOfDay = cal.getTime();

        // Pronađi sve transakcije za ovaj dan
        List<Transaction> dailyTransactions = transactionRepository.findByDateBetween(startOfDay, endOfDay);

        // Dodaj postojeće kombinacije u set
        for (Transaction t : dailyTransactions) {
            if (t.getReporter() != null && t.getAccount() != null) {
                String key = t.getReporter().getId() + "-" + t.getAccount().getId() + "-" + date;
                usedCombinations.add(key);
            }
        }

        Random random = new Random();
        int attempts = 0;
        int maxAttempts = count * 20;

        while (transactions.size() < count && attempts < maxAttempts) {
            attempts++;

            Employee employee = employees.get(random.nextInt(employees.size()));

            // Filtriranje računa koji pripadaju ovom employee-u
            // KORISTIMO getEmployee() jer tako se zove u Account klasi
            List<Account> employeeAccounts = accounts.stream()
                    .filter(acc -> {
                        Employee assignedEmp = acc.getEmployee(); // ← OVO JE ISPRAVNO
                        return assignedEmp != null && assignedEmp.getId().equals(employee.getId());
                    })
                    .collect(Collectors.toList());

            if (employeeAccounts.isEmpty()) {
                continue;
            }

            Account account = employeeAccounts.get(random.nextInt(employeeAccounts.size()));

            String key = employee.getId() + "-" + account.getId() + "-" + date;

            if (usedCombinations.contains(key)) {
                continue;
            }

            usedCombinations.add(key);

            Transaction transaction = new Transaction();
            transaction.setReporter(employee);
            transaction.setAccount(account);
            transaction.setDate(date);

            double amount = 100 + (random.nextDouble() * 99900);
            transaction.setAmount(BigDecimal.valueOf(Math.round(amount * 100.0) / 100.0));

            transaction.setCategory(CATEGORIES[random.nextInt(CATEGORIES.length)]);
            transaction.setDescription(DESCRIPTIONS[random.nextInt(DESCRIPTIONS.length)]);

            Status[] statuses = Status.values();
            transaction.setStatus(statuses[random.nextInt(statuses.length)]);

            transactions.add(transaction);
        }

        return transactions;
    }

    @Transactional
    public List<Transaction> generateHistoricalData(int daysBack, int transactionsPerDay) {
        List<Transaction> allTransactions = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        for (int i = 0; i < daysBack; i++) {
            Date date = cal.getTime();
            try {
                List<Transaction> dailyTransactions = generateAndSaveTransactions(
                        transactionsPerDay, date
                );
                allTransactions.addAll(dailyTransactions);
            } catch (Exception e) {
                System.out.println("Greška za datum " + date + ": " + e.getMessage());
            }
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }

        return allTransactions;
    }

    @Transactional
    public List<Transaction> generateForEmployee(Long employeeId, int count, Date date) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new RuntimeException("Employee nije pronađen sa ID: " + employeeId);
        }

        Employee employee = employeeOpt.get();

        // KORISTIMO findByEmployee jer u repository treba da se zove tako
        List<Account> employeeAccounts = accountRepository.findByEmployee(employee);

        if (employeeAccounts.isEmpty()) {
            throw new RuntimeException("Employee nema dodeljene račune");
        }

        List<Transaction> transactions = new ArrayList<>();
        usedCombinations.clear();

        // Postavi datum za proveru
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startOfDay = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);
        Date endOfDay = cal.getTime();

        // Dodaj postojeće transakcije za ovog employee-a na ovaj dan
        List<Transaction> existingTransactions =
                transactionRepository.findByReporterAndDateBetween(employee, startOfDay, endOfDay);

        for (Transaction t : existingTransactions) {
            if (t.getAccount() != null) {
                String key = employee.getId() + "-" + t.getAccount().getId() + "-" + date;
                usedCombinations.add(key);
            }
        }

        Random random = new Random();

        for (int i = 0; i < count; i++) {
            if (usedCombinations.size() >= employeeAccounts.size()) {
                break; // Nema više slobodnih računa za ovog employee-a
            }

            Account account = employeeAccounts.get(random.nextInt(employeeAccounts.size()));

            String key = employee.getId() + "-" + account.getId() + "-" + date;
            if (usedCombinations.contains(key)) {
                i--;
                continue;
            }

            usedCombinations.add(key);

            Transaction transaction = new Transaction();
            transaction.setReporter(employee);
            transaction.setAccount(account);
            transaction.setDate(date);

            double amount = 100 + (random.nextDouble() * 99900);
            transaction.setAmount(BigDecimal.valueOf(Math.round(amount * 100.0) / 100.0));

            transaction.setCategory(CATEGORIES[random.nextInt(CATEGORIES.length)]);
            transaction.setDescription(DESCRIPTIONS[random.nextInt(DESCRIPTIONS.length)]);

            Status[] statuses = Status.values();
            transaction.setStatus(statuses[random.nextInt(statuses.length)]);

            transactions.add(transaction);
        }

        return transactionRepository.saveAll(transactions);
    }
}