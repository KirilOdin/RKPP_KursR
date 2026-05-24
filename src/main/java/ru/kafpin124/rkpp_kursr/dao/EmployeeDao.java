package ru.kafpin124.rkpp_kursr.dao;

import ru.kafpin124.rkpp_kursr.model.Employee;

import java.util.List;

/**
 * The interface Employee dao to work with the employees table (laboratory staff).
 * Provides CRUD operations and search methods.
 */
public interface EmployeeDao {
    /**
     * Add a new employee.
     *
     * @param employee the employee object (id will be assigned automatically)
     */
    void add(Employee employee);

    /**
     * Find an employee by ID.
     *
     * @param id the employee's id (primary key)
     * @return the employee object or null, if not found
     */
    Employee findById(Long id);

    /**
     * Gets a list of all employees.
     *
     * @return the list of all employees
     */
    List<Employee> getAll();

    /**
     * Update the employee data (all fields)
     *
     * @param employee the employee object with a filled-in id and new values
     */
    void update(Employee employee);

    /**
     * Delete the employee by object.
     *
     * @param employee the employee object
     */
    void delete(Employee employee);

    /**
     * Delete the employee by id.
     *
     * @param id the employee id
     */
    void deleteById(Long id);

    /**
     * Find the employee by login (unique field). Used for authentication.
     *
     * @param login the employee login
     * @return the employee object or null, if not found
     */
    Employee findByLogin(String login);

    /**
     * Find all employees with a given role.
     *
     * @param role the employee role (admin, lab_assistant, lab_doctor)
     * @return the list of employees with this role
     */
    List<Employee> findByRole(String role);

    /**
     * Search for employees by substring in full name.
     *
     * @param pattern the search string (may contain part of the last name, first name, or middle name)
     * @return the list of eligible employees
     */
    List<Employee> searchByFullName(String pattern); // "Иванов", "Иван", "Иванов Иван"
//    List<Employee> searchByFullName(String lastname, String firstName, String middleName);

}
