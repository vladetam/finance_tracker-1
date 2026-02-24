package orion.rs.demo.service.implementation;

import orion.rs.demo.domain.Account;
import orion.rs.demo.domain.AccountType;
import orion.rs.demo.domain.Employee;
import orion.rs.demo.dto.AccountCreateDTO;
import orion.rs.demo.dto.AccountDTO;
import orion.rs.demo.dto.BulkInsertAccDTO;
import orion.rs.demo.dto.FailedRecord;
import orion.rs.demo.repository.AccountRepository;
import orion.rs.demo.repository.EmployeeRepository;
import orion.rs.demo.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;

    public AccountServiceImpl(AccountRepository accountRepository,
                              EmployeeRepository employeeRepository) {
        this.accountRepository = accountRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Account> getAllAcc(){
        return accountRepository.findAll();
    }

    @Override
    @Transactional
    public AccountDTO createAccount(AccountCreateDTO dto) {
        // Validacija zaposlenog
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Zaposleni nije pronadjen."));
        if(!dto.getCurrency().matches("[a-zA-Z]+")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valuta mora sadrzati samo slova");
        }
        // Kreiranje account objekta
        Account account = new Account();
        account.setType(AccountType.valueOf(dto.getType())); // String -> enum
        account.setBalance(dto.getBalance());
        account.setCurrency(dto.getCurrency());
        account.setEmployee(employee);

        // Sacuvaj racun u bazu
        Account saved = accountRepository.save(account);

        // Vrati kao AccountDTO
        return mapToDTO(saved);
    }

    @Transactional
    public AccountDTO updateAccount(Long id, AccountCreateDTO dto) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Racun nije pronadjen"));

        // Validacija zaposlenog
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Zaposleni nije pronadjen"));

        if(!dto.getCurrency().matches("[a-zA-Z]+")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valuta mora sadrzati samo slova");
        }
        // Update polja
        account.setType(AccountType.valueOf(dto.getType()));
        account.setBalance(dto.getBalance());
        account.setCurrency(dto.getCurrency());
        account.setEmployee(employee);

        try {
            Account saved = accountRepository.save(account);
            return mapToDTO(saved);

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Greska prilikom cuvanja zaposlenog");
        }
    }

    @Transactional
    public BulkInsertAccDTO bulkInsert(List<AccountCreateDTO> dtos) {
        List<Long> savedIds = new ArrayList<>();
        List<FailedRecord> failedRecords = new ArrayList<>();

        for (int i = 0; i < dtos.size(); i++) {
            AccountCreateDTO dto = dtos.get(i);

            try {
                // Validacija zaposlenog
                Employee employee = employeeRepository.findById(dto.getEmployeeId())
                        .orElseThrow(() -> new IllegalArgumentException("Zaposleni nije pronadjen"));

                // Validacija tipa (enum)
                AccountType type;
                try {
                    type = AccountType.valueOf(dto.getType());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Nevalidan tip naloga");
                }

                // Validacija balansa i valute
                if (dto.getBalance() < 0) {
                    throw new IllegalArgumentException("Balans mora da je pozitivan");
                }
                if (dto.getCurrency() == null || dto.getCurrency().isBlank() || !dto.getCurrency().matches("[a-zA-Z]+")) {
                    throw new IllegalArgumentException("Valuta je obavezna");
                }

                // Kreiranje i cuvanje account-a
                Account account = new Account();
                account.setType(type);
                account.setBalance(dto.getBalance());
                account.setCurrency(dto.getCurrency());
                account.setEmployee(employee);

                Account saved = accountRepository.save(account);
                savedIds.add(saved.getId());

            } catch (Exception e) {
                // Svaki neuspešan zapis se beleži
                failedRecords.add(new FailedRecord(i, e.getMessage()));
            }
        }

        return new BulkInsertAccDTO(savedIds, failedRecords);
    }

    private AccountDTO mapToDTO(Account account) {
        return new AccountDTO(
                account.getId(),
                account.getType().name(),
                account.getBalance(),
                account.getCurrency(),
                account.getEmployee().getId()
        );
    }
}