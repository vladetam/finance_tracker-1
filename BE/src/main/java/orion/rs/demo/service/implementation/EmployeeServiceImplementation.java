package orion.rs.demo.service.implementation;

import jakarta.persistence.SecondaryTable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import orion.rs.demo.domain.Employee;
import orion.rs.demo.dto.BulkEmployeeDTO;
import orion.rs.demo.dto.BulkResultDTO;
import orion.rs.demo.dto.EmployeeDto;
import orion.rs.demo.mapper.EmployeeMapper;
import orion.rs.demo.exceptionHandling.EmployeeNotFoundException;
import orion.rs.demo.repository.EmployeeRepository;
import orion.rs.demo.service.EmployeeService;
import orion.rs.demo.validationObj.FailedEmployee;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImplementation implements EmployeeService {


    private final EmployeeRepository employeeRepository;
    private static final List<FailedEmployee> failedEmployees = new ArrayList<>();
    static List<BulkEmployeeDTO> VALID_EMPLOYEE_DTOS = new ArrayList<>();
    /**
     * BULK Insert Employees
     * Save into DataBase if valid or skip
     * */

    public BulkResultDTO saveOrSkipEmployeeService(List<BulkEmployeeDTO> bulkEmployeeDTOS){

        List<BulkEmployeeDTO> invalidEmployees = new ArrayList<>();
        List<Employee> validEmployees = new ArrayList<>();

        List<String> emails = bulkEmployeeDTOS.stream().map(BulkEmployeeDTO::getEmail).
                filter(Objects::nonNull).toList();

        Set<String> existingEmails = employeeRepository.findByEmailIn(emails)
                .stream()
                .map(Employee::getEmail)
                .collect(Collectors.toSet());

        for (BulkEmployeeDTO dto : bulkEmployeeDTOS) {

            if(dto.getFirst_name() == null || dto.getFirst_name().isBlank()
                    || dto.getLast_name() == null || dto.getLast_name().isBlank()
                    || dto.getEmail() == null || dto.getEmail().isBlank()
                    || existingEmails.contains(dto.getEmail())) {

                invalidEmployees.add(dto);

            } else {

                validEmployees.add(EmployeeMapper.toEntity(dto));
            }
        }

        List<Employee> saved = employeeRepository.saveAll(validEmployees);

        List<Long> savedIds = saved.stream()
                .map(Employee::getId)
                .toList();

        return new BulkResultDTO(savedIds, invalidEmployees);

    }


    public boolean checkValid(BulkEmployeeDTO bulkEmployeeDTO){
        return !bulkEmployeeDTO.getEmail().isBlank() && !bulkEmployeeDTO.getLast_name().isBlank()
                && !bulkEmployeeDTO.getFirst_name().isBlank();

    }

    @Override
    public EmployeeDto create(EmployeeDto dto) {

        Employee employee = new Employee();
        employee.setFirstname(dto.getFirstname());
        employee.setLastname(dto.getLastname());
        employee.setEmail(dto.getEmail());

        Employee saved = employeeRepository.save(employee);

        return mapToDto(saved);
    }

    @Override
    public EmployeeDto getById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

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
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        employee.setFirstname(dto.getFirstname());
        employee.setLastname(dto.getLastname());
        employee.setEmail(dto.getEmail());

        Employee updated = employeeRepository.save(employee);

        return mapToDto(updated);
    }

    @Override
    public void delete(Long id) {

        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }

        employeeRepository.deleteById(id);
    }

    private EmployeeDto mapToDto(Employee employee) {

        EmployeeDto dto = new EmployeeDto();
        dto.setId(employee.getId());
        dto.setFirstname(employee.getFirstname());
        dto.setLastname(employee.getLastname());
        dto.setEmail(employee.getEmail());

        return dto;
    }

    @Override
    public byte[] exportEmployeesToCsv() {

        List<Employee> employees = employeeRepository.findAll();

        StringBuilder sb = new StringBuilder();

        sb.append("ID,First Name,Last Name,Email\n");

        for (Employee employee : employees) {
            sb.append(employee.getId()).append(",");
            sb.append(employee.getFirstname()).append(",");
            sb.append(employee.getLastname()).append(",");
            sb.append(employee.getEmail()).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

}