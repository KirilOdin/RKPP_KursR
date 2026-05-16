package ru.kafpin124.rkpp_kursr.dao;

import ru.kafpin124.rkpp_kursr.model.Employee;

import java.util.List;

public interface EmployeeDao {
    // CRUD-операции
    void add(Employee employee);
    Employee findById(Long id);
    List<Employee> getAll();
    void update(Employee employee);
    void delete(Employee employee);
    void deleteById(Long id);

    //TODO: Дополнительные методы для поиска сотрудников (по имени?)
    Employee findByLogin(String login);
    List<Employee> findByRole(String role);
    List<Employee> searchByFullName(String pattern); // "Иванов", "Иван", "Иванов Иван"
//    List<Employee> searchByFullName(String lastname, String firstName, String middleName);

}
