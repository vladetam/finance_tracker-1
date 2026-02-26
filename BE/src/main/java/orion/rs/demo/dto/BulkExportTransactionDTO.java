package orion.rs.demo.dto;

import com.fasterxml.jackson.databind.DatabindException;
import lombok.Getter;
import lombok.Setter;
import orion.rs.demo.domain.Transaction;

import java.math.BigDecimal;
import java.time.*;
import java.util.Date;

@Getter
@Setter
public class BulkExportTransactionDTO {
    Long id;
    Long reporterId;
    String reporterFirstName;
    String reporterLastName;
    Long accountId;
    String accountType;
    String description;
    String dateTimeUtc;
    String amount;
    String category;
    String status;


}
