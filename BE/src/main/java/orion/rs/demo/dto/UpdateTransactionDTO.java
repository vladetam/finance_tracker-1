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
    @Pattern(regexp = "^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]) (0\\d|1\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$",
            message = "Datum mora da bude u formatu: %YYYY-%MM-%DD HH:MM:SS")
    private String date;

    @NotNull
    @PositiveOrZero(message = "Amount mora biti >= 0!")
    private BigDecimal amount;

    @NotNull
    @NotBlank(message = "Kategorija ne sme biti prazna!")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Category mora sadrzati samo slova")
    private String category;

    @NotNull
    @Pattern(
            regexp = "^(COMPLETED|PENDING|RECONCILED)$",
            message = "Status mora da bude jedan od sledecih opcija: COMPLETED, PENDING, RECONCILED"
    )
    private String status;
}