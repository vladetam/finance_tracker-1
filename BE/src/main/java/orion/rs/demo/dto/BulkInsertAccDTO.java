package orion.rs.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BulkInsertAccDTO {
    private List<Long> savedIds;
    private List<FailedRecord> failedRecords;
}


