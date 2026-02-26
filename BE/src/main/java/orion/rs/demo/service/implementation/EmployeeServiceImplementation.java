package orion.rs.demo.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import orion.rs.demo.domain.Employee;
import orion.rs.demo.dto.BulkEmployeeDTO;
import orion.rs.demo.dto.EmployeeDto;
import orion.rs.demo.mapper.EmployeeMapper;
import orion.rs.demo.exceptionHandling.EmployeeNotFoundException;
import orion.rs.demo.repository.EmployeeRepository;
import orion.rs.demo.service.EmployeeService;
import orion.rs.demo.validationObj.FailedEmployee;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImplementation implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private static final List<FailedEmployee> failedEmployees = new ArrayList<>();
    /**
     * BULK Insert Employees
     * Save into DataBase if valid or skip
     * */

    public void saveOrSkipEmployee(List<BulkEmployeeDTO> bulkEmployeeDTOS){
        for(int i = 0; i < bulkEmployeeDTOS.size(); i++){
            if(checkValid(bulkEmployeeDTOS.get(i))){
                Employee employee = EmployeeMapper.toEntity(bulkEmployeeDTOS.get(i));
                employeeRepository.save(employee);
            }else{
                failedEmployees.add(new FailedEmployee(bulkEmployeeDTOS.get(i), "Validation Failed"));
            }
        }
    }

    public List<FailedEmployee> getFailedEmployees(){
        return failedEmployees;
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