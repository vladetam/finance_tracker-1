package orion.rs.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import orion.rs.demo.dto.AccountCreateDTO;
import orion.rs.demo.dto.AccountDTO;
import orion.rs.demo.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import orion.rs.demo.service.implementation.AccountServiceImpl;

import javax.print.attribute.standard.Media;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    @Autowired
    private AccountServiceImpl accountServiceImpl;
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountCreateDTO accountCreateDTO) {
        AccountDTO savedAccount = accountService.createAccount(accountCreateDTO);
        return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
    }

    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body(accountServiceImpl.getAllAccounts());
    }
}