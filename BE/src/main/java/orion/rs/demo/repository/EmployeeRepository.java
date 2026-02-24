package orion.rs.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import orion.rs.demo.domain.Employee;

@Repository
public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {
}