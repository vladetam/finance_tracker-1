package orion.rs.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import orion.rs.demo.dto.EmployeeDto;

public interface EmployeeService {

    EmployeeDto create(EmployeeDto dto);

    EmployeeDto getById(Long id);

    Page<EmployeeDto> getAll(Pageable pageable);

    EmployeeDto update(Long id, EmployeeDto dto);

    void delete(Long id);
}