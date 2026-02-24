package orion.rs.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class FailedRecord {
    private int index;
    private String reason;
}
