package orion.rs.demo.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import orion.rs.demo.domain.Employee;
import orion.rs.demo.dto.EmployeeDto;
import orion.rs.demo.repository.EmployeeRepository;
import orion.rs.demo.service.EmployeeService;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImplementation implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeDto create(EmployeeDto dto) {

        Employee employee = new Employee();
        employee.setFirstname(dto.getFirstName());
        employee.setLastname(dto.getLastName());
        employee.setEmail(dto.getEmail());

        Employee saved = employeeRepository.save(employee);

        return mapToDto(saved);
    }

    @Override
    public EmployeeDto getById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return mapToDto(employee);
    }

    @Override
    public Page<EmployeeDto> getAll(Pageable pageable) {

        return employeeRepository.findAll(pageable)
                .map(this::mapToDto);
    }

    @Override
    public EmployeeDto update(Long id, EmployeeDto dto) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setFirstname(dto.getFirstName());
        employee.setLastname(dto.getLastName());
        employee.setEmail(dto.getEmail());

        Employee updated = employeeRepository.save(employee);

        return mapToDto(updated);
    }

    @Override
    public void delete(Long id) {

        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found");
        }

        employeeRepository.deleteById(id);
    }

    private EmployeeDto mapToDto(Employee employee) {

        EmployeeDto dto = new EmployeeDto();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstname());
        dto.setLastName(employee.getLastname());
        dto.setEmail(employee.getEmail());

        return dto;
    }

}