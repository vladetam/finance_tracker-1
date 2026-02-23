package orion.rs.demo.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import orion.rs.demo.domain.Employee;
import orion.rs.demo.exceptionHandling.EmployeeNotFoundException;
import orion.rs.demo.repository.EmployeeRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImplementation {

    private final EmployeeRepository employeeRepository;


    /**
     *
    * Deleting employee by ID
     *
    * */

    public void deleteEmployee(Long id_employee) throws Exception {

        Employee employee = employeeRepository.findById(id_employee).orElseThrow(() ->
                new EmployeeNotFoundException(id_employee));

        employeeRepository.delete(employee);
    }

}
