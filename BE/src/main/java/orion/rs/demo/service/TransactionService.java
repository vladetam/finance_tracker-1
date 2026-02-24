package orion.rs.demo.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import orion.rs.demo.domain.*;
import orion.rs.demo.dto.TransactionDTO;
import orion.rs.demo.dto.UpdateTransactionDTO;
import orion.rs.demo.repository.AccountRepository;
import orion.rs.demo.repository.EmployeeRepository;
import orion.rs.demo.repository.TransactionRepository;
import java.util.Date;  // VAŽNO: koristi java.util.Date, NE LocalDate!

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

    public Transaction updateTrans(Long id, Transaction transaction) throws Exception {

        // 1. Provera da li transakcija postoji u bazi
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new Exception("Transaction not found with id: " + id));

        // 2. Provera da li Employee postoji (ako je poslat novi)
        if (transaction.getReporter() != null &&
                transaction.getReporter().getId() != null) {
            if (!employeeRepository.existsById(transaction.getReporter().getId())) {
                throw new Exception("Employee does not exist");
            }
            existingTransaction.setReporter(transaction.getReporter());
        }

        // 3. Provera da li Account postoji (ako je poslat novi)
        if (transaction.getAccount() != null &&
                transaction.getAccount().getId() != null) {
            if (!accountRepository.existsById(transaction.getAccount().getId())) {
                throw new Exception("Account does not exist");
            }
            existingTransaction.setAccount(transaction.getAccount());
        }

        // Provera da li je bilo sta upisano u polja
        if (transaction.getDescription() != null) {
            existingTransaction.setDescription(transaction.getDescription());
        }

        if (transaction.getDate() != null) {
            existingTransaction.setDate(transaction.getDate());
        }


        if (transaction.getAmount() != null) {
            existingTransaction.setAmount(transaction.getAmount());
        }


        if (transaction.getCategory() != null) {
            existingTransaction.setCategory(transaction.getCategory());
        }


        if (transaction.getStatus() != null) {
            existingTransaction.setStatus(transaction.getStatus());
        }


        try {
            // 6. Sačuvaj izmene
            Transaction updated = transactionRepository.save(existingTransaction);
            return updated;

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
}