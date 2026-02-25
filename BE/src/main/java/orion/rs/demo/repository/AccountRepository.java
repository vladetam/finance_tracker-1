package orion.rs.demo.repository;

import orion.rs.demo.domain.Account;
import orion.rs.demo.domain.AccountType;
import orion.rs.demo.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Ovo radi jer se u Account klasi polje zove "employee"
    List<Account> findByEmployee(Employee employee);

    // Opciono - ako ti zatreba
    List<Account> findByCurrency(String currency);

    List<Account> findByType(AccountType type);
}