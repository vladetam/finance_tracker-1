package orion.rs.demo.dto;

import lombok.Getter;
import lombok.Setter;
import orion.rs.demo.domain.Status;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class TransactionDTO {
    private Long id;
    private Long reporterId;
    private String reporterFirstName;
    private String reporterLastName;
    private Long accountId;
    private String accountType;
    private String description;
    private Integer version;
    private Date date;
    private BigDecimal amount;
    private String category;
    private Status status;
}

