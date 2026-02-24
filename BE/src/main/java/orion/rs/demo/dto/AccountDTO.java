package orion.rs.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    /*
    * Ovu klasu koristimo kao ResponseDTO
    * Ne trebaju nam svi atributi za svaku funkcionalnost
    * pa je lakse samo da odvojimo jedan ResponseDTO i
    * DTO za funkcionalnosti
    * */
    private Long id;
    private String type;        // enum kao String
    private double balance;
    private String currency;
    private Long employeeId;    // samo ID reference
}