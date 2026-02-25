package orion.rs.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import orion.rs.demo.annotations.CheckValidEmail;
import orion.rs.demo.dto.BulkEmployeeDTO;
import orion.rs.demo.dto.EmployeeDto;
import orion.rs.demo.service.EmployeeService;
import orion.rs.demo.service.implementation.EmployeeServiceImplementation;

import javax.print.attribute.standard.Media;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeServiceImplementation employeeServiceImpl;
    @PostMapping
    public ResponseEntity<EmployeeDto> create(@Valid @RequestBody EmployeeDto dto) {
        EmployeeDto created = employeeService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeDto>> getAll(Pageable pageable) {
        Page<EmployeeDto> page = employeeService.getAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getById(@PathVariable Long id) {
        EmployeeDto dto = employeeService.getById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> update(@PathVariable Long id,
                                              @Valid @RequestBody EmployeeDto dto) {
        EmployeeDto updated = employeeService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @CheckValidEmail
    @PostMapping(value = "bulkSaveAll",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveOrSkipEmployee(@RequestBody List<BulkEmployeeDTO> bulkEmployeeDTOS){

            employeeServiceImpl.saveOrSkipEmployee(bulkEmployeeDTOS);

            return ResponseEntity.status(HttpStatus.OK).body("Successfully added employees!" +
                    "Employees that are invalid and failed to add are: " + employeeServiceImpl.getFailedEmployees());
    }


    @GetMapping("/export")
    public ResponseEntity<byte[]> exportEmployees() {

        byte[] csvData = employeeService.exportEmployeesToCsv();

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String filename = "employees_" + date + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + filename)
                .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                .body(csvData);
    }

    /**
     * Get invalid bulk insert Employees
     * */

    /*
    @GetMapping(value = "/getInvalidBulkEmployees", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FailedEmployee>> getInvalidEmployee(){


    }

    */



}