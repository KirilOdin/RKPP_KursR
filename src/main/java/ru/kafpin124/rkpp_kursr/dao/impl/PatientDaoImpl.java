package ru.kafpin124.rkpp_kursr.dao.impl;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.util.DBHelper;
import ru.kafpin124.rkpp_kursr.dao.PatientDao;
import ru.kafpin124.rkpp_kursr.model.Patient;
import ru.kafpin124.rkpp_kursr.util.SqlStatements;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDaoImpl implements PatientDao {

    public static final Logger logger = LoggerFactory.getLogger(PatientDaoImpl.class);

//    1. Вставка нового пациента

//    private static final String ADD = "INSERT INTO public.patients(policy_number, last_name, " +
//            "first_name, middle_name, gender, birth_date, phone) " +
//            "VALUES (?, ?, ?, ?, ?, ?, ?);";

    public static final String ADD = SqlStatements.get("sql.Patient.ADD");

    @Override
    public void add(Patient patient) {
        logger.debug("Добавление нового пациента: {} {}", patient.getLastName(), patient.getFirstName());
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
                    logger.info("Пациент добавлен с ID: {} ({} {})", patient.getIdPatient(), patient.getLastName(), patient.getFirstName());
                } else {
                    throw new SQLException("Creating patient failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении пациента: {}", e.getMessage());
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
        logger.debug("Поиск пациента по ID: {}", id);
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Поиск пациента по ID '{}' завершён", id);
                    return mapSingle(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске пациента с ID {}: {}", id, e.getMessage());
            throw new RuntimeException(e);
        }
        logger.warn("Пациент с ID {} не найден", id);
        return null;
    }

    //    3. Вывод всех пациентов, находящихся в базе данных

//    private static final String GET_ALL = "SELECT id_patient, policy_number, last_name, " +
//            "first_name, middle_name, gender, birth_date, phone " +
//            "FROM public.patients;";

    public static final String GET_ALL = SqlStatements.get("sql.Patient.GET_ALL");

    @Override
    public List<Patient> getAll() {
        logger.debug("Получение всех пациентов, находящихся в базе данных...");
        List<Patient> patients = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL)) {
            patients = mapper(rs);
            logger.debug("Загружено {} пациентов", patients.size());
        } catch (SQLException e) {
            logger.error("ООшибка при получении всех пациентов, хранящихся в базе данных: {}", e.getMessage());
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
        logger.debug("Обновление данных пациента с ID: {}", patient.getIdPatient());
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
            logger.info("Пациент с ID {} обновлён", patient.getIdPatient());
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении пациента с ID {}: {}", patient.getIdPatient(), e.getMessage());
            throw new RuntimeException(e);
        }
    }



//    5. Удаление пациента из базы данных

//    private static final String DELETE_BY_ID = "DELETE FROM public.patients WHERE id_patient = ?;";

    public static final String DELETE_BY_ID = SqlStatements.get("sql.Patient.DELETE_BY_ID");

    @Override
    public void delete(Patient patient) {
        logger.debug("Удаление пациента с ID: {}", patient.getIdPatient());
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BY_ID)) {
            ps.setLong(1, patient.getIdPatient());
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Deleting patient failed, no rows deleted.");
            logger.info("Пациент с ID {} удалён", patient.getIdPatient());
        } catch (SQLException e) {
            logger.error("Ошибка при удалении пациента с ID {}: {}", patient.getIdPatient(), e.getMessage());
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
        logger.debug("Поиск пациента по номеру полиса: {}", policyNumber);
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_POLICY)) {
            ps.setString(1, policyNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Поиск пациента по номеру полиса '{}' завершён", policyNumber);
                    return mapSingle(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске пациента по полису {}: {}", policyNumber, e.getMessage());
            throw new RuntimeException(e);
        }
        logger.warn("Пациент с полисом {} не найден", policyNumber);
        return null;
    }

//    7. Поиск пациентов по ФИО

//    private static final String SEARCH_BY_FULLNAME = "SELECT id_patient, " +
//            "policy_number, last_name, first_name, middle_name, gender, birth_date, phone " +
//            "FROM public.patients WHERE CONCAT_WS(' ', last_name, first_name, middle_name) ILIKE ?;";

    public static final String SEARCH_BY_FULLNAME = SqlStatements.get("sql.Patient.SEARCH_BY_FULLNAME");

    @Override
    public List<Patient> searchByFullName(String pattern) {
        logger.debug("Поиск пациентов по ФИО: {}", pattern);
        List<Patient> patients = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(SEARCH_BY_FULLNAME)) {
            ps.setString(1, "%" + pattern + "%");
            try (ResultSet rs = ps.executeQuery()) {
                patients = mapper(rs);
                logger.debug("Найдено {} пациентов по запросу '{}'", patients.size(), pattern);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске пациентов по ФИО '{}': {}", pattern, e.getMessage());
            throw new RuntimeException(e);
        }
        return patients;
    }

    private Patient mapSingle(ResultSet rs){
        Patient patient = new Patient();
        try {
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
        } catch (SQLException e) {
            logger.error("Ошибка при работе single-маппера: '{}'", e.getMessage());
            throw new RuntimeException(e);
        }
        return patient;
    }

    private List<Patient> mapper(ResultSet rs) {
        List<Patient> patients = new ArrayList<>();
        try {
            while (rs.next()) {
                patients.add(mapSingle(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при работе маппера: '{}'", e.getMessage());
            throw new RuntimeException(e);
        }
        return patients;
    }
}
