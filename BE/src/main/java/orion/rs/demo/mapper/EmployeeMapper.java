package orion.rs.demo.mapper;

import orion.rs.demo.domain.Employee;
import orion.rs.demo.dto.BulkEmployeeDTO;

public class EmployeeMapper {
    public static Employee toEntity(BulkEmployeeDTO dto) {
        if (dto == null) {
            return null;
        }

        Employee employee = new Employee();
        employee.setFirstname(dto.getFirst_name());
        employee.setLastname(dto.getLast_name());
        employee.setEmail(dto.getEmail());

        return employee;
    }

}
