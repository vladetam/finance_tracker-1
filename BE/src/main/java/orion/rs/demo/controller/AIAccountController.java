package orion.rs.demo.controller;

import lombok.RequiredArgsConstructor;
import orion.rs.demo.domain.Account;
import orion.rs.demo.dto.AccountCreateDTO;
import orion.rs.demo.dto.BulkInsertAccDTO;
import orion.rs.demo.service.AIAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-accounts")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AIAccountController {

    private final AIAccountService aiAccountService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateAccounts(@RequestParam int count) {
        try {
            List<Account> accounts = aiAccountService.generateAndSaveAccounts(count);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", accounts.size());
            response.put("accounts", accounts);
            response.put("message", "Uspesno generisano " + accounts.size() + " naloga");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return errorResponse(e);
        }
    }

    @PostMapping("/generate-for-employee/{employeeId}")
    public ResponseEntity<?> generateForEmployee(
            @PathVariable Long employeeId,
            @RequestParam int count) {
        try {
            List<Account> accounts = aiAccountService.generateForEmployee(employeeId, count);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", accounts.size());
            response.put("accounts", accounts);
            response.put("message", "Uspesno generisano " + accounts.size() + " naloga za zaposlenog");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return errorResponse(e);
        }
    }


    private ResponseEntity<Map<String, Object>> errorResponse(Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}