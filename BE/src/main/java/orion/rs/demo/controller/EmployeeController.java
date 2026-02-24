package orion.rs.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import orion.rs.demo.dto.BulkEmployeeDTO;
import orion.rs.demo.dto.EmployeeDto;
import orion.rs.demo.service.EmployeeService;
import orion.rs.demo.service.implementation.EmployeeServiceImplementation;

import javax.print.attribute.standard.Media;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeServiceImplementation employeeServiceImpl;
    @PostMapping
    public ResponseEntity<EmployeeDto> create(@RequestBody EmployeeDto dto) {
        EmployeeDto created = employeeService.create(dto);
        return ResponseEntity.ok(created);
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
                                              @RequestBody EmployeeDto dto) {
        EmployeeDto updated = employeeService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping(value = "bulkSaveAll",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveOrSkipEmployee(@RequestBody List<BulkEmployeeDTO> bulkEmployeeDTOS){

            employeeServiceImpl.saveOrSkipEmployee(bulkEmployeeDTOS);
            return ResponseEntity.status(HttpStatus.OK).build();
    }

}