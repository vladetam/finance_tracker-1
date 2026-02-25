package orion.rs.demo.controller;

import orion.rs.demo.domain.Transaction;
import orion.rs.demo.service.AITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-transactions")
@CrossOrigin(origins = "*")
public class AITransactionController {

    @Autowired
    private AITransactionService aiTransactionService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateTransactions(
            @RequestParam int count,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

        try {
            List<Transaction> transactions = aiTransactionService.generateAndSaveTransactions(count, date);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", transactions.size());
            response.put("transactions", transactions);
            response.put("message", "Uspešno generisano " + transactions.size() + " transakcija");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/generate-historical")
    public ResponseEntity<?> generateHistoricalData(
            @RequestParam int daysBack,
            @RequestParam int transactionsPerDay) {

        try {
            List<Transaction> transactions = aiTransactionService.generateHistoricalData(
                    daysBack, transactionsPerDay
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", transactions.size());
            response.put("transactions", transactions);
            response.put("message", "Uspešno generisano " + transactions.size() + " istorijskih transakcija");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/generate-for-employee/{employeeId}")
    public ResponseEntity<?> generateForEmployee(
            @PathVariable Long employeeId,
            @RequestParam int count,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

        try {
            List<Transaction> transactions = aiTransactionService.generateForEmployee(employeeId, count, date);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", transactions.size());
            response.put("transactions", transactions);
            response.put("message", "Uspešno generisano " + transactions.size() + " transakcija za employee");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}