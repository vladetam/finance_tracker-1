package orion.rs.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import orion.rs.demo.domain.Account;

public interface AccountRepository extends JpaRepository<Account,Long> {
}
