package orion.rs.demo.validationObj;

import lombok.Data;
import orion.rs.demo.dto.BulkEmployeeDTO;
import orion.rs.demo.dto.EmployeeDto;

@Data
public class FailedEmployee {

    private EmployeeDto employeeDto;
    private String message;


    public FailedEmployee(BulkEmployeeDTO bulkEmployeeDTO, String validationFailed) {
    }
}
