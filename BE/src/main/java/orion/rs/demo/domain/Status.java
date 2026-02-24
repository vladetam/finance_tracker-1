package orion.rs.demo.domain;

import jakarta.persistence.Entity;
import lombok.Getter;

@Getter
public enum Status {

    PENDING,
    COMPLETED,
    RECONCILED
}
