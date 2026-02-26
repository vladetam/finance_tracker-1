package orion.rs.demo.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import orion.rs.demo.domain.Status;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class BulkInsertTransactionDTO {

    @NotNull(message = "Reporter (employee) is mandatory")
    private Long reporterId;

    @NotNull(message = "Account is mandatory")
    private Long accountId;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private Date date;

    @NotNull(message = "Amount is mandatory")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Category is mandatory")
    private String category;

    @NotNull(message = "Status is mandatory")
    private Status status;


}
