package orion.rs.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BulkEmployeeDTO {

    @NotBlank
    private String first_name;
    @NotBlank
    private String last_name;
    @NotBlank
    @Email
    private String email;
}
