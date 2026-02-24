package orion.rs.demo.transaction_specification;

import orion.rs.demo.domain.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;


public class TransactionSpecification {

    //filter po employee
    public static Specification<Transaction> hasEmployeeID(Long employeeId) {
        return (root, query, criteriaBuilder) -> {
            if (employeeId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("reporter").get("id"), employeeId);
        };
    }

    // filter po acc
    public static Specification<Transaction> hasAccountId(Long accountId) {
        return (root, query, cb) -> {
            if (accountId == null) return cb.conjunction();
            return cb.equal(root.get("account").get("id"), accountId);
        };
    }

    // Filter po category (transaction type)
    public static Specification<Transaction> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null || category.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category"), category);
        };
    }

    // filter po datumu
    public static Specification<Transaction> hasDateBetween(Date startDate, Date endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            } else if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("date"), startDate, endDate);
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate);
            }
        };
    }

    // Sortiranje po datumu, prvo poslednji
    public static Specification<Transaction> orderByDateDesc() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("date")));
            return criteriaBuilder.conjunction();
        };
    }

}
