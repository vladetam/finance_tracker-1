package orion.rs.demo.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import orion.rs.demo.domain.Account;
import orion.rs.demo.domain.Account;
import orion.rs.demo.dto.AccountCreateDTO;
import orion.rs.demo.dto.AccountDTO;
import orion.rs.demo.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import orion.rs.demo.service.implementation.AccountServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountServiceImpl accountServiceImpl;
    public AccountController(AccountService accountService, AccountServiceImpl accountServiceImpl) {
        this.accountService = accountService;
        this.accountServiceImpl = accountServiceImpl;
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

    /**
     * Get all accounts from dataBase
     * */
    // moze da se doda paginacija
    @GetMapping(value = "getAccounts",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Account>> getAllAcc(){
        return ResponseEntity.status(HttpStatus.OK).body(accountServiceImpl.getAllAcc());
    }
}