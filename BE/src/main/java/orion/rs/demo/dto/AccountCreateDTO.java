package orion.rs.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateDTO {

    @NotNull(message = "Tip naloga je obavezan")
    private String type;

    @PositiveOrZero(message = "Balans mora da je >= 0")
    private double balance;

    @NotBlank(message = "Valuta je obavezna")
    private String currency;

    @NotNull(message = "Zaposleni je obavezan")
    private Long employeeId;
}