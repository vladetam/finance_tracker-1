package orion.rs.demo.annotationsImplementation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import orion.rs.demo.dto.BulkEmployeeDTO;
import orion.rs.demo.repository.EmployeeRepository;
import orion.rs.demo.service.implementation.EmployeeServiceImplementation;

import java.util.List;

@Aspect
@Configuration
public class CheckValidEmployeeImpl {
/**
 * Class for using aspect oriented programing and
 * reflection so we can use same functionalities without repeating code
 * And it will be used on BUlK insert employee
 * */

    @Autowired
    EmployeeServiceImplementation employeeServiceImplementation;
    @Autowired
    EmployeeRepository employeeRepository;

    @Around("@annotation(orion.rs.demo.annotations.CheckValidEmail)")
    public Object uniqueEmailCheck(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object obj[] = proceedingJoinPoint.getArgs();
        List<BulkEmployeeDTO> bulkEmployeeDTOS = null;
        for (Object o : obj) {
            if (o instanceof List<?>) {
                bulkEmployeeDTOS = (List<BulkEmployeeDTO>) o;
                break;
            }
        }
        for (BulkEmployeeDTO b : bulkEmployeeDTOS) {
            if (employeeRepository.existsByEmail(b.getEmail()))
                return new ResponseEntity<>("User with that email address already exist!", HttpStatus.CONFLICT);
        }

        return proceedingJoinPoint.proceed();
    }
}
