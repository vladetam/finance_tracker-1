package orion.rs.demo.service;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import orion.rs.demo.domain.*;
import orion.rs.demo.dto.BulkInsertTransactionDTO;
import orion.rs.demo.dto.CreateTransactionDTO;
import orion.rs.demo.dto.TransactionDTO;
import orion.rs.demo.dto.UpdateTransactionDTO;
import orion.rs.demo.exceptionHandling.AccountNotFoundException;
import orion.rs.demo.exceptionHandling.EmployeeNotFoundException;
import orion.rs.demo.repository.AccountRepository;
import orion.rs.demo.repository.EmployeeRepository;
import orion.rs.demo.repository.TransactionRepository;
import java.math.BigDecimal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            EmployeeRepository employeeRepository,
            AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
    }

    public List<Transaction> getAllTransaction(){
        return transactionRepository.findAll();
    }




    public Transaction updateTrans(Long id, UpdateTransactionDTO transaction) throws Exception {

        // 1. Provera da li transakcija postoji u bazi
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new Exception("Transaction not found with id: " + id));

        // 2. Provera da li Employee postoji (ako je poslat novi)
        Employee reporter = employeeRepository.findById(transaction.getReporterId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Employee does not exist"
                        )
                );
        existingTransaction.setReporter(reporter);

        // 3. Provera da li Account postoji (ako je poslat novi)
        Account account = accountRepository.findById(transaction.getAccountId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Account does not exist"
                        )
                );
        existingTransaction.setAccount(account);

        // Provera da li je bilo sta upisano u polja
        if (transaction.getDescription() != null) {
            existingTransaction.setDescription(transaction.getDescription());
        }

        try {
            SimpleDateFormat formatter =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date parsedDate = formatter.parse(transaction.getDate());
            existingTransaction.setDate(parsedDate);

        } catch (ParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Neispravan format datuma"
            );
        }


        if (transaction.getAmount() != null) {
            existingTransaction.setAmount(transaction.getAmount());
        }


        if (transaction.getCategory() != null) {
            existingTransaction.setCategory(transaction.getCategory());
        }


        try {
            Status status = Status.valueOf(transaction.getStatus());
            existingTransaction.setStatus(status);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Neispravan status"
            );
        }


        try {
            // 6. Sačuvaj izmene
            return transactionRepository.save(existingTransaction);

        } catch (Exception e) {
            throw new Exception("Database error while updating transaction: " + e.getMessage());
        }
    }

    public void deleteTransaction(Long id) throws Exception {

        // 1. Provera da li transakcija postoji u bazi
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new Exception("Transaction not found with id: " + id));

        // 2. Brisanje transakcije
        try {
            transactionRepository.delete(transaction);
        } catch (Exception e) {
            throw new Exception("Database error while deleting transaction: " + e.getMessage());
        }
    }

    public TransactionDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + id));
        return mapToDTO(transaction);
    }

    private TransactionDTO mapToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());


        if (transaction.getReporter() != null) {
            dto.setReporterId(transaction.getReporter().getId());
            dto.setReporterFirstName(transaction.getReporter().getFirstname());
            dto.setReporterLastName(transaction.getReporter().getLastname());
        }

        // Account podaci
        if (transaction.getAccount() != null) {
            dto.setAccountId(transaction.getAccount().getId());
            // Proveri kako se zove getter za accountType u Account klasi
            // Verovatno: getAccountType() ili getType()
            if (transaction.getAccount().getEmployee() != null) {
                dto.setAccountType(transaction.getAccount().getType().toString());
            }
        }

        dto.setDescription(transaction.getDescription());
        dto.setDate(transaction.getDate());
        dto.setAmount(transaction.getAmount());
        dto.setCategory(transaction.getCategory());
        dto.setStatus(transaction.getStatus());
        dto.setVersion(transaction.getVersion());

        return dto;
    }


    public Map<String, Object> bulkInsertTransactions(List<BulkInsertTransactionDTO> dtos) {
        List<Long> savedIds = new ArrayList<>();
        List<Map<String, Object>> failedRecords = new ArrayList<>();

        for (BulkInsertTransactionDTO dto : dtos) {

            // 1. Validacija employee
            Employee reporter = employeeRepository.findById(dto.getReporterId()).orElse(null);
            if (reporter == null) {
                Map<String, Object> failed = new HashMap<>();
                failed.put("dto", dto);
                failed.put("error", "Employee not found");
                failedRecords.add(failed);
                continue; // preskačemo ovu transakciju
            }

            // 2. Validacija account
            Account account = accountRepository.findById(dto.getAccountId()).orElse(null);
            if (account == null) {
                Map<String, Object> failed = new HashMap<>();
                failed.put("dto", dto);
                failed.put("error", "Account not found");
                failedRecords.add(failed);
                continue; // preskačemo ovu transakciju
            }

            // 3. Validacija ostalih mandatory polja
            if (dto.getDescription() == null || dto.getDescription().isEmpty()) {
                Map<String, Object> failed = new HashMap<>();
                failed.put("dto", dto);
                failed.put("error", "Description is mandatory");
                failedRecords.add(failed);
                continue;
            }
            if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                Map<String, Object> failed = new HashMap<>();
                failed.put("dto", dto);
                failed.put("error", "Amount must be greater than 0");
                failedRecords.add(failed);
                continue;
            }
            if (dto.getCategory() == null || dto.getCategory().isEmpty()) {
                Map<String, Object> failed = new HashMap<>();
                failed.put("dto", dto);
                failed.put("error", "Category is mandatory");
                failedRecords.add(failed);
                continue;
            }
            if (dto.getDate() == null) {
                Map<String, Object> failed = new HashMap<>();
                failed.put("dto", dto);
                failed.put("error", "Date is mandatory");
                failedRecords.add(failed);
                continue;
            }
            if (dto.getStatus() == null) {
                Map<String, Object> failed = new HashMap<>();
                failed.put("dto", dto);
                failed.put("error", "Status is mandatory");
                failedRecords.add(failed);
                continue;
            }

            boolean exists = transactionRepository.existsByReporterAndAccountAndDate(
                    reporter,
                    account,
                    dto.getDate()
            );

            if (exists) {
                Map<String, Object> failed = new HashMap<>();
                failed.put("dto", dto);
                failed.put("error", "Vec postoji transakcija sa identicnim employee-om, account-om i date-om");
                failedRecords.add(failed);
                continue;
            }



            try {
                // 4. Kreiranje Transaction entity
                Transaction transaction = new Transaction();
                transaction.setReporter(reporter);
                transaction.setAccount(account);
                transaction.setDescription(dto.getDescription());
                transaction.setDate(dto.getDate());
                transaction.setAmount(dto.getAmount());
                transaction.setCategory(dto.getCategory());
                transaction.setStatus(dto.getStatus());

                // 5. Save
                Transaction saved = transactionRepository.save(transaction);
                savedIds.add(saved.getId());

            } catch (Exception e) {
                Map<String, Object> failed = new HashMap<>();
                failed.put("dto", dto);
                failed.put("error", "DB save error: " + e.getMessage());
                failedRecords.add(failed);
            }
        }

        // 6. Povratni rezultat
        Map<String, Object> result = new HashMap<>();
        result.put("savedIds", savedIds);
        result.put("failedRecords", failedRecords);
        return result;
    }


    public Transaction createTransaction(@Valid CreateTransactionDTO dto) {

        Employee reporter = employeeRepository.findById(dto.getReporterId())
                .orElseThrow(() -> new EmployeeNotFoundException(dto.getReporterId()));


        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException(dto.getAccountId()));


        Date parsedDate;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setLenient(false); // Vazno
            parsedDate = sdf.parse(dto.getDate());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Datum mora biti u formatu: yyyy-MM-dd HH:mm:ss");
        }

        Status status;
        try {
            status = Status.valueOf(dto.getStatus());
        } catch (Exception e) {
            throw new IllegalArgumentException("Losa vrednost statusa");
        }


        Transaction transaction = new Transaction();
        transaction.setReporter(reporter);
        transaction.setAccount(account);
        transaction.setDescription(dto.getDescription());
        transaction.setDate(parsedDate);
        transaction.setAmount(dto.getAmount());
        transaction.setCategory(dto.getCategory());
        transaction.setStatus(status);

        return transactionRepository.save(transaction);
    }

}