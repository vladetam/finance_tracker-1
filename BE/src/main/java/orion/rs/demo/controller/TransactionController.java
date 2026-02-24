package orion.rs.demo.controller;

import org.springframework.data.jpa.domain.Specification;
import orion.rs.demo.domain.Status;
import orion.rs.demo.domain.Transaction;
import orion.rs.demo.repository.TransactionRepository;
import orion.rs.demo.repository.EmployeeRepository;
import orion.rs.demo.repository.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import orion.rs.demo.transaction_specification.TransactionSpecification;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;

    public TransactionController(
            TransactionRepository transactionRepository,
            EmployeeRepository employeeRepository,
            AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction) {


        if (transaction.getReporter() == null ||
                transaction.getReporter().getId() == null ||
                !employeeRepository.existsById(transaction.getReporter().getId())) {
            return ResponseEntity.status(400).body("Employee does not exist");
        }

        if (transaction.getAccount() == null ||
                transaction.getAccount().getId() == null ||
                !accountRepository.existsById(transaction.getAccount().getId())) {
            return ResponseEntity.status(400).body("Account does not exist");
        }

        // 3. Provera obaveznih polja
        if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
            return ResponseEntity.status(400).body("Description is mandatory");
        }
        if (transaction.getDate() == null) {
            return ResponseEntity.status(400).body("Date is mandatory");
        }
        if (transaction.getAmount() == null) {
            return ResponseEntity.status(400).body("Amount is mandatory");
        }
        if (transaction.getCategory() == null || transaction.getCategory().trim().isEmpty()) {
            return ResponseEntity.status(400).body("Category is mandatory");
        }
        if (transaction.getStatus() == null) {
            return ResponseEntity.status(400).body("Status is mandatory");
        }


        try {
            Transaction saved = transactionRepository.save(transaction);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Database error");
        }
    }

    @GetMapping
    public List<Transaction> getTransactions(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false)Status status,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) Date endDate
    ) {
        Specification<Transaction> spec = Specification
                .where(TransactionSpecification.hasEmployeeID(employeeId))
                .and(TransactionSpecification.hasAccountId(accountId))
                //.and(TransactionSpecification.hashStatus(status))
                .and(TransactionSpecification.hasDateBetween(startDate, endDate))
                .and(TransactionSpecification.orderByDateDesc());

        return transactionRepository.findAll(spec);
    }


}
