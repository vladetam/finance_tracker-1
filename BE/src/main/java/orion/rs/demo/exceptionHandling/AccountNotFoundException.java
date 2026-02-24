package orion.rs.demo.exceptionHandling;

import jakarta.persistence.EntityNotFoundException;

public class AccountNotFoundException extends EntityNotFoundException {

    public AccountNotFoundException(Long id) {
        super("Account with ID " + id + " does not exist!");
    }
}
