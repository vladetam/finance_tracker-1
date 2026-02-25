package orion.rs.demo.transaction_specification;

import orion.rs.demo.domain.Status;
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


    // filter po datumu
    public static Specification<Transaction> hasDate(Date date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }

            Date startOfDay = new Date(date.getYear(), date.getMonth(), date.getDate(), 0, 0, 0);

            Date endOfDay = new Date(date.getYear(), date.getMonth(), date.getDate(), 23, 59, 59);

            return criteriaBuilder.between(root.get("date"), startOfDay, endOfDay);
        };
    }

    // Sortiranje po datumu, prvo poslednji
    public static Specification<Transaction> orderByDateDesc() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("date")));
            return criteriaBuilder.conjunction();
        };
    }

    // po statusu
    public static Specification<Transaction> hashStatus(Status status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

}
