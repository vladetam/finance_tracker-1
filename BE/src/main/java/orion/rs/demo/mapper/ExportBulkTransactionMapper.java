package orion.rs.demo.mapper;

import orion.rs.demo.domain.Transaction;
import orion.rs.demo.dto.BulkExportTransactionDTO;

public class ExportBulkTransactionMapper {

    public static BulkExportTransactionDTO toBulkDto(Transaction t) {
        if (t == null) return null;

        BulkExportTransactionDTO dto = new BulkExportTransactionDTO();
        dto.setId(t.getId());
        dto.setReporterId(t.getReporter() != null ? t.getReporter().getId() : null);
        dto.setReporterFirstName(t.getReporter() != null ? t.getReporter().getFirstname() : null);
        dto.setReporterLastName(t.getReporter() != null ? t.getReporter().getLastname() : null);
        dto.setAccountId(t.getAccount() != null ? t.getAccount().getId() : null);
        dto.setAccountType(t.getAccount() != null ? String.valueOf(t.getAccount().getType()) : null);
        dto.setDescription(t.getDescription());
        dto.setDateTimeUtc(t.getDate() != null ?
                java.time.format.DateTimeFormatter.ISO_INSTANT.format(t.getDate().toInstant()) : null);
        dto.setAmount(t.getAmount() != null
                ? t.getAmount().toPlainString()
                : null);
        dto.setCategory(t.getCategory());
        dto.setStatus(t.getStatus() != null ? t.getStatus().name() : null);

        return dto;
    }
}
