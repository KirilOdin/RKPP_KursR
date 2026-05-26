package ru.kafpin124.rkpp_kursr.dao.impl;



import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.kafpin124.rkpp_kursr.util.DBHelper;
import ru.kafpin124.rkpp_kursr.dao.EmployeeDao;
import ru.kafpin124.rkpp_kursr.model.Employee;
import ru.kafpin124.rkpp_kursr.util.SqlStatements;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDaoImpl implements EmployeeDao {


//    1. Вставка нового сотрудника
//    private static final String ADD = "INSERT INTO public.employees(role, \"position\", last_name, first_name, " +
//            "middle_name, login, password_hash) " +
//            "VALUES (?, ?, ?, ?, ?, ?, ?);";

    public static final String ADD = SqlStatements.get("sql.Employee.ADD");

    @Override
    public void add(Employee employee, String rawPassword) {
        try (Connection conn = DBHelper.getConnection(); PreparedStatement pstmt = conn.prepareStatement(ADD, Statement.RETURN_GENERATED_KEYS)){
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
            String hash = passwordEncoder.encode(rawPassword);
            employee.setPasswordHash(hash);
            pstmt.setString(1, employee.getRole());
            pstmt.setString(2, employee.getPosition());
            pstmt.setString(3, employee.getLastName());
            pstmt.setString(4, employee.getFirstName());
            pstmt.setString(5, employee.getMiddleName());
            pstmt.setString(6, employee.getLogin());
            pstmt.setString(7, employee.getPasswordHash());
            int addedRows = pstmt.executeUpdate();
            if (addedRows == 0){
                throw new SQLException("Creating employee failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()){
                if (generatedKeys.next()){
                    employee.setIdEmployee(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating employee failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage() );
            throw new RuntimeException(e);
        }
    }


//    2. Поиск сотрудника по идентификатору

//    private static final String FIND_BY_ID = "SELECT id_employee, role, \"position\", last_name, first_name, " +
//        "middle_name, login, password_hash FROM public.employees WHERE id_employee = ?;";

    public static final String FIND_BY_ID = SqlStatements.get("sql.Employee.FIND_BY_ID");

    @Override
    public Employee findById(Long id) {
        try (Connection conn = DBHelper.getConnection(); PreparedStatement pstmt = conn.prepareStatement(FIND_BY_ID)){
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return mapSingle(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }


//    3. Вывод всех сотрудников, находящихся в базе данных

//    private static final String GET_ALL =  "SELECT id_employee, role, \"position\", last_name, first_name, " +
//            "middle_name, login, password_hash FROM public.employees;";

    public static final String GET_ALL = SqlStatements.get("sql.Employee.GET_ALL");

    @Override
    public List<Employee> getAll() {
        List<Employee> employeeList = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL)){
            employeeList = mapper(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return employeeList;
    }


//    4. Изменение значений у существующего сотрудника

//    private static final String UPDATE = "UPDATE public.employees SET role=?, \"position\"=?, last_name=?, " +
//        "first_name=?, middle_name=?, login=?, password_hash=? WHERE id_employee = ?;";

    public static final String UPDATE = SqlStatements.get("sql.Employee.UPDATE");

    @Override
    public void update(Employee employee) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE)){
            pstmt.setString(1, employee.getRole());
            pstmt.setString(2, employee.getPosition());
            pstmt.setString(3, employee.getLastName());
            pstmt.setString(4, employee.getFirstName());
            pstmt.setString(5, employee.getMiddleName());
            pstmt.setString(6, employee.getLogin());
            pstmt.setString(7, employee.getPasswordHash());
            pstmt.setLong(8, employee.getIdEmployee());

            int addedRows = pstmt.executeUpdate();

            if (addedRows == 0){
                throw new SQLException("Updating employee failed, no rows added.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }


//    5. Удаление сотрудника из базы данных

//    private static final String DELETE_BY_ID = "DELETE FROM public.employees WHERE id_employee = ?;";

    public static final String DELETE_BY_ID = SqlStatements.get("sql.Employee.DELETE_BY_ID");

    @Override
    public void delete(Employee employee) {
        deleteById(employee.getIdEmployee());
    }

    @Override
    public void deleteById(Long id) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_BY_ID)) {
            pstmt.setLong(1, id);

            int deletedRows = pstmt.executeUpdate();

            if (deletedRows == 0){
                throw new SQLException("Deleting employee failed, no rows deleted.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }



//    6. Поиск сотрудника по логину

//    private static final String FIND_BY_LOGIN = "SELECT id_employee, role, \"position\", last_name, first_name, " +
//            "middle_name, login, password_hash FROM public.employees WHERE login = ?;";
//
    public static final String FIND_BY_LOGIN = SqlStatements.get("sql.Employee.FIND_BY_LOGIN");

    @Override
    public Employee findByLogin(String login) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_LOGIN)){
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return mapSingle(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }


//    7. Поиск сотрудников по их роли (лаборант, админ...)

//    private static final String FIND_BY_ROLE = "SELECT id_employee, role, \"position\", last_name, first_name, " +
//        "middle_name, login, password_hash FROM public.employees WHERE role = ?;";

    public static final String FIND_BY_ROLE = SqlStatements.get("sql.Employee.FIND_BY_ROLE");

    @Override
    public List<Employee> findByRole(String role) {
        List<Employee> employeeList = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_ROLE)){
            pstmt.setString(1, role);
            try (ResultSet rs = pstmt.executeQuery()){
                employeeList = mapper(rs);
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return employeeList;
    }


//    8. Поиск сотрудников по ФИО

//    private static final String SEARCH_BY_FULLNAME = "SELECT id_employee, role, \"position\", last_name, " +
//        "first_name, middle_name, login, password_hash FROM public.employees " +
//        "WHERE CONCAT_WS(' ', last_name, first_name, middle_name) ILIKE ?;";

    public static final String SEARCH_BY_FULLNAME = SqlStatements.get("sql.Employee.SEARCH_BY_FULLNAME");

    @Override
    public List<Employee> searchByFullName(String pattern) {
        List<Employee> employeeList = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SEARCH_BY_FULLNAME)) {
            pstmt.setString(1, "%" + pattern + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                employeeList = mapper(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return employeeList;
    }


    protected List<Employee> mapper(ResultSet rs) {
        List<Employee> employeeList = new ArrayList<>();
        try {
            while (rs.next()) {
                employeeList.add(mapSingle(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return employeeList;
    }

    private Employee mapSingle(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setIdEmployee(rs.getLong("id_employee"));
        employee.setRole(rs.getString("role"));
        employee.setPosition(rs.getString("position"));
        employee.setLastName(rs.getString("last_name"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setMiddleName(rs.getString("middle_name"));
        employee.setLogin(rs.getString("login"));
        employee.setPasswordHash(rs.getString("password_hash"));

        return employee;
    }

}
