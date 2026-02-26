package orion.rs.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionDTO {
    @NotNull(message = "Obavezan reported ID")
    private Long reporterId;
    @NotNull(message = "Obavezan account ID")
    private Long accountId;
    @NotNull(message = "Opis ne sme biti null")
    private String description;
    @NotNull(message = "Datum ne sme biti null")
    @Pattern(regexp = "^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]) (0\\d|1\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$",
            message = "Datum mora da bude u formatu: %YYYY-%MM-%DD HH:MM:SS")
    private String date;
    @PositiveOrZero(message = "Amount mora biti >= 0")
    private BigDecimal amount;
    @NotNull(message = "Kategorija ne sme biti null")
    @NotBlank(message = "Kategorija ne sme biti prazna!")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Category mora sadrzati samo slova")
    private String category;
    @NotNull(message = "Status ne sme biti null")
    @Pattern(
            regexp = "^(COMPLETED|PENDING|RECONCILED)$",
            message = "Status mora da bude jedan od sledecih opcija: COMPLETED, PENDING, RECONCILED"
    )
    private String status;
}
