package orion.rs.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Employee reporter;

    @ManyToOne
    private Account account;

    private String description;

    @Version
    private Integer version;

    private Date date;

    private BigDecimal amount;

    private String category;
    @Enumerated(EnumType.STRING)
    @NotNull
    private Status status;

}
