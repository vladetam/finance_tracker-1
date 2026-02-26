package orion.rs.demo.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import orion.rs.demo.domain.Account;
import orion.rs.demo.domain.Employee;
import orion.rs.demo.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    // ostale metode


    // Provera duplikata po datumu
    List<Transaction> findByDateBetween(Date startDate, Date endDate);

    // Provera da li postoji transakcija za reportera, account i period
    boolean existsByReporterAndAccountAndDateBetween(
            Employee reporter,
            Account account,
            Date startDate,
            Date endDate
    );

    // Pronalaženje transakcija za reportera na određeni dan
    List<Transaction> findByReporterAndDateBetween(
            Employee reporter,
            Date startDate,
            Date endDate
    );

    // Statistika
    @Query("SELECT DATE(t.date), COUNT(t) FROM Transaction t GROUP BY DATE(t.date)")
    List<Object[]> countTransactionsPerDay();

    // Za proveru duplikata (ako ih slučajno ima)
    @Query("SELECT t.reporter.id, t.account.id, DATE(t.date), COUNT(t) " +
            "FROM Transaction t " +
            "GROUP BY t.reporter.id, t.account.id, DATE(t.date) " +
            "HAVING COUNT(t) > 1")
    List<Object[]> findDuplicateTransactions();
    boolean existsByReporterAndAccountAndDate(Employee reporter, Account account, Date date);

}