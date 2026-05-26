package ru.kafpin124.rkpp_kursr.dao.impl;



import ru.kafpin124.rkpp_kursr.util.DBHelper;
import ru.kafpin124.rkpp_kursr.dao.PatientDao;
import ru.kafpin124.rkpp_kursr.model.Patient;
import ru.kafpin124.rkpp_kursr.util.SqlStatements;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDaoImpl implements PatientDao {


//    1. Вставка нового пациента

//    private static final String ADD = "INSERT INTO public.patients(policy_number, last_name, " +
//            "first_name, middle_name, gender, birth_date, phone) " +
//            "VALUES (?, ?, ?, ?, ?, ?, ?);";

    public static final String ADD = SqlStatements.get("sql.Patient.ADD");

    @Override
    public void add(Patient patient) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(ADD, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, patient.getPolicyNumber());
            ps.setString(2, patient.getLastName());
            ps.setString(3, patient.getFirstName());
            ps.setString(4, patient.getMiddleName());
            ps.setString(5, String.valueOf(patient.getGender()));
            ps.setDate(6, Date.valueOf(patient.getBirthDate()));
            ps.setString(7, patient.getPhone());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Creating patient failed, no rows affected.");
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    patient.setIdPatient(keys.getLong(1));
                } else {
                    throw new SQLException("Creating patient failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }



//    2. Поиск пациента по идентификатору

//    private static final String FIND_BY_ID = "SELECT id_patient, policy_number, last_name, " +
//            "first_name, middle_name, gender, birth_date, phone " +
//            "FROM public.patients WHERE id_patient = ?;";

    public static final String FIND_BY_ID = SqlStatements.get("sql.Patient.FIND_BY_ID");

    @Override
    public Patient findById(Long id) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSingle(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    //    3. Вывод всех пациентов, находящихся в базе данных

//    private static final String GET_ALL = "SELECT id_patient, policy_number, last_name, " +
//            "first_name, middle_name, gender, birth_date, phone " +
//            "FROM public.patients;";

    public static final String GET_ALL = SqlStatements.get("sql.Patient.GET_ALL");

    @Override
    public List<Patient> getAll() {
        List<Patient> patients = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL)) {
            patients = mapper(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return patients;
    }

//    4. Изменение значений у существующего пациента

//    private static final String UPDATE = "UPDATE public.patients " +
//            "SET policy_number=?, last_name=?, first_name=?, middle_name=?, " +
//            "gender=?, birth_date=?, phone=? WHERE id_patient = ?;";

    public static final String UPDATE = SqlStatements.get("sql.Patient.UPDATE");

    @Override
    public void update(Patient patient) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, patient.getPolicyNumber());
            ps.setString(2, patient.getLastName());
            ps.setString(3, patient.getFirstName());
            ps.setString(4, patient.getMiddleName());
            ps.setString(5, String.valueOf(patient.getGender()));
            ps.setDate(6, Date.valueOf(patient.getBirthDate()));
            ps.setString(7, patient.getPhone());
            ps.setLong(8, patient.getIdPatient());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Updating patient failed, no rows affected.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }



//    5. Удаление пациента из базы данных

//    private static final String DELETE_BY_ID = "DELETE FROM public.patients WHERE id_patient = ?;";

    public static final String DELETE_BY_ID = SqlStatements.get("sql.Patient.DELETE_BY_ID");

    @Override
    public void delete(Patient patient) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BY_ID)) {
            ps.setLong(1, patient.getIdPatient());
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Deleting patient failed, no rows deleted.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

//    6. Поиск пациента по номеру медицинского полиса

//    private static final String FIND_BY_POLICY = "SELECT id_patient, policy_number, " +
//            "last_name, first_name, middle_name, gender, birth_date, phone " +
//            "FROM public.patients WHERE policy_number = ?;";

    public static final String FIND_BY_POLICY = SqlStatements.get("sql.Patient.FIND_BY_POLICY");

    @Override
    public Patient findByPolicyNumber(String policyNumber) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_POLICY)) {
            ps.setString(1, policyNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSingle(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

//    7. Поиск пациентов по ФИО

//    private static final String SEARCH_BY_FULLNAME = "SELECT id_patient, " +
//            "policy_number, last_name, first_name, middle_name, gender, birth_date, phone " +
//            "FROM public.patients WHERE CONCAT_WS(' ', last_name, first_name, middle_name) ILIKE ?;";

    public static final String SEARCH_BY_FULLNAME = SqlStatements.get("sql.Patient.SEARCH_BY_FULLNAME");

    @Override
    public List<Patient> searchByFullName(String pattern) {
        List<Patient> patients = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(SEARCH_BY_FULLNAME)) {
            ps.setString(1, "%" + pattern + "%");
            try (ResultSet rs = ps.executeQuery()) {
                patients = mapper(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return patients;
    }

    private Patient mapSingle(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setIdPatient(rs.getLong("id_patient"));
        patient.setPolicyNumber(rs.getString("policy_number"));
        patient.setLastName(rs.getString("last_name"));
        patient.setFirstName(rs.getString("first_name"));
        patient.setMiddleName(rs.getString("middle_name"));
        patient.setGender(rs.getString("gender").charAt(0));
        Date date = rs.getDate("birth_date");
        if (date != null) {
            patient.setBirthDate(date.toLocalDate());
        }
        patient.setPhone(rs.getString("phone"));
        return patient;
    }

    private List<Patient> mapper(ResultSet rs) throws SQLException {
        List<Patient> patients = new ArrayList<>();
        while (rs.next()) {
            patients.add(mapSingle(rs));
        }
        return patients;
    }
}
