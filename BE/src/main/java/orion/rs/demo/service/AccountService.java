package orion.rs.demo.service;

import orion.rs.demo.dto.AccountCreateDTO;
import orion.rs.demo.dto.AccountDTO;
import orion.rs.demo.dto.BulkInsertAccDTO;

import java.util.List;

public interface AccountService {

    /**
     * Kreira novi account u bazi.
     * @param dto DTO sa podacima za kreiranje accounta
     * @return DTO kreiranog accounta
     */
    AccountDTO createAccount(AccountCreateDTO dto);
    AccountDTO updateAccount(Long id, AccountCreateDTO dto);
    BulkInsertAccDTO bulkInsert(List<AccountCreateDTO> dtos);
}