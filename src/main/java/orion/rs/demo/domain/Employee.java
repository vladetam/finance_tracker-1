package orion.rs.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;
    private String lastname;
    @Email
    private String email;

    @JsonIgnore //rekurzivno trazenje instanci dolazi do greske - ovako resavamo
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Account> employeeAcounts = new ArrayList<>();
}
