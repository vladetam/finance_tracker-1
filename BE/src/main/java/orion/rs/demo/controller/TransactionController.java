package orion.rs.demo.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import orion.rs.demo.domain.Status;
import orion.rs.demo.domain.Transaction;
import orion.rs.demo.dto.BulkInsertTransactionDTO;
import orion.rs.demo.repository.TransactionRepository;
import orion.rs.demo.repository.EmployeeRepository;
import orion.rs.demo.repository.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import orion.rs.demo.service.TransactionService;
import orion.rs.demo.transaction_specification.TransactionSpecification;

import java.io.IOException;
import java.io.PrintWriter;
import  java.util.Map;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    public TransactionController(
            TransactionRepository transactionRepository,
            EmployeeRepository employeeRepository,
            AccountRepository accountRepository, TransactionService transactionService) {
        this.transactionRepository = transactionRepository;
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    @GetMapping(value = "/getAllForCSV", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllTransactionCSV(){
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getAllTransaction());
    }

    @GetMapping(value = "/exportCSVtransactions")
    public void exportToCSV(HttpServletResponse servletResponse) throws IOException{
        servletResponse.setContentType("text/csv");
        servletResponse.setHeader("Content-Disposition", "attachment; filename=transakcije.csv");
        List<Transaction> transactionList = transactionService.getAllTransaction();
        PrintWriter printWriter = servletResponse.getWriter();

        printWriter.println("Amount, status, description and date od transactions");

        for(Transaction t : transactionList){
            printWriter.println(t.getAmount() + "\n" + t.getStatus() + "\n"+ t.getDescription() + "\n" + t.getDate()
            );

            printWriter.println("\n\n\n");
        }

        printWriter.flush();
        printWriter.close();
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

    // filter po id
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(
            @PathVariable Long id,
            @RequestBody Transaction transaction) {

        try {
            transactionService.updateTrans(id,transaction);
            return ResponseEntity.status(HttpStatus.OK).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping
    public List<Transaction> getTransactions(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false)Status status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            Date date
    ) {
        Specification<Transaction> spec = Specification
                .where(TransactionSpecification.hasEmployeeID(employeeId))
                .and(TransactionSpecification.hasAccountId(accountId))
                .and(TransactionSpecification.hashStatus(status))
                .and(TransactionSpecification.hasDate(date))
                .and(TransactionSpecification.orderByDateDesc());

        return transactionRepository.findAll(spec);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {

        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {

            //ako transakcija ne postoji
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(e.getMessage());
            }
            //sve ostale greške
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    @PostMapping("/bulk")
    public ResponseEntity<?> bulkInsert(
            @RequestBody List<@Valid BulkInsertTransactionDTO> dtos) {

        Map<String, Object> result = transactionService.bulkInsertTransactions(dtos);
        return ResponseEntity.ok(result);
    }
}
