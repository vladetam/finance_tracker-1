package orion.rs.demo.controller;

import jakarta.validation.Valid;
import orion.rs.demo.dto.AccountCreateDTO;
import orion.rs.demo.dto.AccountDTO;
import orion.rs.demo.dto.BulkInsertAccDTO;
import orion.rs.demo.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountCreateDTO accountCreateDTO) {
        AccountDTO savedAccount = accountService.createAccount(accountCreateDTO);
        return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountCreateDTO accountUpdateDTO) {

        AccountDTO updatedAccount = accountService.updateAccount(id, accountUpdateDTO);
        return ResponseEntity.ok(updatedAccount);
    }

    @PostMapping("/bulk")
    public ResponseEntity<BulkInsertAccDTO> bulkInsertAccounts(
            @RequestBody List<AccountCreateDTO> dtos) {

        BulkInsertAccDTO result = accountService.bulkInsert(dtos);
        return ResponseEntity.ok(result);
    }
}