package orion.rs.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import orion.rs.demo.domain.Employee;
import orion.rs.demo.service.implementation.EmployeeServiceImplementation;

import java.util.List;

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

    /**
     * Get all employes from DB
     * API: http://localhost:8080/api/employee/getAll
     * */


    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllEmployes(){

        List<Employee> employeeList = null;

        try {
            employeeList = employeeService.getAllEmployes();
        }catch (Exception e){
            e.printStackTrace(); /*** some error occurred*/
        }
        return ResponseEntity.status(HttpStatus.OK).body(employeeList);
    }
}
