package orion.rs.demo.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateDTO {

    @NotNull(message = "Tip naloga je obavezan")
    @Pattern(
            regexp = "^(GOLD|SILVER|PLATINUM)$",
            message = "Mora da bude jedan od sledecih opcija: GOLD, SILVER, PLATINUM"
    )
    private String type;

    @PositiveOrZero(message = "Balans mora da je >= 0")
    private double balance;

    @NotBlank(message = "Valuta je obavezna")
    @Pattern(regexp = "^[A-Za-z]+$",
            message = "Samo slova su dozvoljena u currency")
    private String currency;

    @NotNull(message = "Zaposleni je obavezan")
    private Long employeeId;
}