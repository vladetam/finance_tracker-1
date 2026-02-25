package orion.rs.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import orion.rs.demo.domain.Status;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class UpdateTransactionDTO {

    @NotNull
    private Long reporterId;

    @NotNull
    private Long accountId;

    @NotBlank
    private String description;

    @NotNull
    private Date date;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String category;

    @NotNull
    private Status status;
}