package orion.rs.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import orion.rs.demo.service.implementation.EmployeeServiceImplementation;

@RestController
@RequestMapping("api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeServiceImplementation employeeService;


    /***
     * Deleting employee by ID if ID is valid , ID must be entered!
     * API: http://localhost:8080/api/employee/deleteByID
     */

    @DeleteMapping(value = "/deleteByID")
    public ResponseEntity<?> deleteEmployee(@RequestParam Long employee_id){
        try {

            employeeService.deleteEmployee(employee_id);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted employee!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
