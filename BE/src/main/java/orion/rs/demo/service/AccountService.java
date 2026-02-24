package orion.rs.demo.service;

import orion.rs.demo.dto.AccountCreateDTO;
import orion.rs.demo.dto.AccountDTO;

public interface AccountService {

    /**
     * Kreira novi account u bazi.
     * @param dto DTO sa podacima za kreiranje accounta
     * @return DTO kreiranog accounta
     */
    AccountDTO createAccount(AccountCreateDTO dto);
    public AccountDTO updateAccount(Long id, AccountCreateDTO dto);
}