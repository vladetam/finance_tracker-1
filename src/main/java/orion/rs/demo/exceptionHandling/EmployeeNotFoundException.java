package orion.rs.demo.exceptionHandling;


public class EmployeeNotFoundException extends RuntimeException{

    public EmployeeNotFoundException(Long id){
        super("Employee with ID " + id + " does not exist!");
    }

}
