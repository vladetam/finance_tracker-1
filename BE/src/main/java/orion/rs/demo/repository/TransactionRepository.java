package orion.rs.demo.repository;
import orion.rs.demo.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TransactionRepository extends JpaRepository<Transaction, Long>{

}
