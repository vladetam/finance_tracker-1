package orion.rs.demo.service.implementation;

import org.springframework.web.bind.annotation.GetMapping;
import orion.rs.demo.domain.Account;
import orion.rs.demo.domain.AccountType;
import orion.rs.demo.domain.Employee;
import orion.rs.demo.dto.AccountCreateDTO;
import orion.rs.demo.dto.AccountDTO;
import orion.rs.demo.repository.AccountRepository;
import orion.rs.demo.repository.EmployeeRepository;
import orion.rs.demo.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

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

    public List<Account> getAllAccounts(){
        return accountRepository.findAll();
    }

    @Override
    @Transactional
    public AccountDTO createAccount(AccountCreateDTO dto) {
        // Validacija zaposlenog
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Zaposleni nije pronadjen."));

        // Kreiranje account objekta
        Account account = new Account();
        account.setType(AccountType.valueOf(dto.getType())); // String -> enum
        account.setBalance(dto.getBalance());
        account.setCurrency(dto.getCurrency());
        account.setEmployee(employee);

        // Sacuvaj racun u bazu
        Account saved = accountRepository.save(account);

        // Vrati kao AccountDTO
        return new AccountDTO(
                saved.getId(),
                saved.getType().name(),
                saved.getBalance(),
                saved.getCurrency(),
                saved.getEmployee().getId()
        );
    }
}