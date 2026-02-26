package orion.rs.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkResultDTO {

    private List<Long> savedIds;
    private List<BulkEmployeeDTO> failedEmployees;

    public BulkResultDTO(List<Long> savedIds, List<BulkEmployeeDTO> failedEmployees) {
        this.savedIds = savedIds;
        this.failedEmployees = failedEmployees;
    }
}
