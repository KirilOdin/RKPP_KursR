package ru.kafpin124.rkpp_kursr.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.dao.ReferenceValueDao;
import ru.kafpin124.rkpp_kursr.model.AnalysisTest;
import ru.kafpin124.rkpp_kursr.model.ReferenceValue;
import ru.kafpin124.rkpp_kursr.util.DBHelper;
import ru.kafpin124.rkpp_kursr.util.SqlStatements;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ReferenceValueDaoImpl implements ReferenceValueDao {


    public static final Logger logger = LoggerFactory.getLogger(ReferenceValueDaoImpl.class);

    // 1. Вставка нового референсного значения

//    private static final String ADD =
//            "INSERT INTO public.reference_values(test_id, gender_applicable, age_min, age_max, ref_value_min, ref_value_max, ref_text) " +
//                    "VALUES (?, ?, ?, ?, ?, ?, ?);";

    public static final String ADD = SqlStatements.get("sql.ReferenceValue.ADD");

    @Override
    public void add(ReferenceValue referenceValue) {
        logger.debug("Добавление референсного значения для теста с ID: {}", referenceValue.getTest().getIdTest());
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(ADD, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, referenceValue.getTest().getIdTest());
            ps.setString(2, String.valueOf(referenceValue.getGenderApplicable()));

            if (referenceValue.getAgeMin() != null) {
                ps.setInt(3, referenceValue.getAgeMin());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            if (referenceValue.getAgeMax() != null) {
                ps.setInt(4, referenceValue.getAgeMax());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            if (referenceValue.getRefValueMin() != null) {
                ps.setBigDecimal(5, referenceValue.getRefValueMin());
            } else {
                ps.setNull(5, Types.NUMERIC);
            }
            if (referenceValue.getRefValueMax() != null) {
                ps.setBigDecimal(6, referenceValue.getRefValueMax());
            } else {
                ps.setNull(6, Types.NUMERIC);
            }

            ps.setString(7, referenceValue.getRefText());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Creating reference value failed, no rows affected.");
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    referenceValue.setIdReference(keys.getLong(1));
                    logger.info("Референсное значение добавлено с ID: {}", referenceValue.getIdReference());
                } else {
                    throw new SQLException("Creating reference value failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении референсного значения: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    // 2. Поиск по идентификатору

//    private static final String GET_BY_ID =
//            "SELECT id_reference, test_id, gender_applicable, age_min, age_max, ref_value_min, ref_value_max, ref_text " +
//                    "FROM public.reference_values WHERE id_reference = ?;";

    public static final String GET_BY_ID = SqlStatements.get("sql.ReferenceValue.GET_BY_ID");

    @Override
    public ReferenceValue getById(Long id) {
        logger.debug("Поиск референсного значения по ID: {}", id);
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.info("Поиск референсного значения по ID '{}' завершён", id);
                    return mapSingle(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске референсного значения с ID {}: {}", id, e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }


    // 3. Получение всех референсных значений, находящихся в базе данных

//    private static final String GET_ALL =
//            "SELECT id_reference, test_id, gender_applicable, age_min, age_max, ref_value_min, ref_value_max, ref_text " +
//                    "FROM public.reference_values;";

    public static final String GET_ALL = SqlStatements.get("sql.ReferenceValue.GET_ALL");

    @Override
    public List<ReferenceValue> getAll() {
        logger.debug("Получение всех референсных значений, находящихся в базе данных...");
        List<ReferenceValue> list = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL)) {
            list = mapper(rs);
            logger.debug("Загружено {} референсных значений", list.size());
        } catch (SQLException e) {
            logger.error("Ошибка при получении всех референсных значений, хранящихся в базе данных: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return list;
    }


    // 4. Изменение значений у существующего референсного значения

//    private static final String UPDATE =
//            "UPDATE public.reference_values SET test_id=?, gender_applicable=?, age_min=?, age_max=?, " +
//                    "ref_value_min=?, ref_value_max=?, ref_text=? WHERE id_reference = ?;";

    public static final String UPDATE = SqlStatements.get("sql.ReferenceValue.UPDATE");

    @Override
    public void update(ReferenceValue referenceValue) {
        logger.debug("Обновление референсного значения с ID: {}", referenceValue.getIdReference());
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setLong(1, referenceValue.getTest().getIdTest());
            ps.setString(2, String.valueOf(referenceValue.getGenderApplicable()));

            if (referenceValue.getAgeMin() != null) {
                ps.setInt(3, referenceValue.getAgeMin());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            if (referenceValue.getAgeMax() != null) {
                ps.setInt(4, referenceValue.getAgeMax());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            if (referenceValue.getRefValueMin() != null) {
                ps.setBigDecimal(5, referenceValue.getRefValueMin());
            } else {
                ps.setNull(5, Types.NUMERIC);
            }
            if (referenceValue.getRefValueMax() != null) {
                ps.setBigDecimal(6, referenceValue.getRefValueMax());
            } else {
                ps.setNull(6, Types.NUMERIC);
            }

            ps.setString(7, referenceValue.getRefText());
            ps.setLong(8, referenceValue.getIdReference());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Updating reference value failed, no rows affected.");
            logger.info("Референсное значение с ID {} обновлено", referenceValue.getIdReference());
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении референсного значения с ID {}: {}", referenceValue.getIdReference(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // 5. Удаление референсного значения из базы данных
//    private static final String DELETE =
//            "DELETE FROM public.reference_values WHERE id_reference = ?;";

    public static final String DELETE = SqlStatements.get("sql.ReferenceValue.DELETE");

    @Override
    public void delete(ReferenceValue referenceValue) {
        logger.debug("Удаление референсного значения с ID: {}", referenceValue.getIdReference());
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setLong(1, referenceValue.getIdReference());
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Deleting reference value failed, no rows deleted.");
            logger.info("Референсное значение с ID {} удалено", referenceValue.getIdReference());
        } catch (SQLException e) {
            logger.error("Ошибка при удалении референсного значения с ID {}: {}", referenceValue.getIdReference(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // 6. Поиск референсного значения по идентификатору теста
//    private static final String FIND_BY_TEST_ID =
//            "SELECT id_reference, test_id, gender_applicable, age_min, age_max, ref_value_min, ref_value_max, ref_text " +
//                    "FROM public.reference_values WHERE test_id = ?;";

    public static final String FIND_BY_TEST_ID = SqlStatements.get("sql.ReferenceValue.FIND_BY_TEST_ID");

    @Override
    public List<ReferenceValue> findByTestId(Long testId) {
        logger.debug("Поиск референсных значений для теста с ID: {}", testId);
        List<ReferenceValue> list = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_TEST_ID)) {
            ps.setLong(1, testId);
            try (ResultSet rs = ps.executeQuery()) {
                list = mapper(rs);
                logger.debug("Найдено {} референсных значений для теста с ID {}", list.size(), testId);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске референсных значений для теста с ID {}: {}", testId, e.getMessage());
            throw new RuntimeException(e);
        }
        return list;
    }

    // 7. Поиск референсного значения по тесту, полу и возрасту
//    private static final String FIND_BY_TEST_AND_GENDER_AND_AGE =
//            "SELECT id_reference, test_id, gender_applicable, age_min, age_max, ref_value_min, ref_value_max, ref_text " +
//                    "FROM public.reference_values WHERE test_id = ? " +
//                    "AND gender_applicable = ? " +
//                    "AND (age_min IS NULL OR age_min <= ?) " +
//                    "AND (age_max IS NULL OR age_max >= ?);";

    public static final String FIND_BY_TEST_AND_GENDER_AND_AGE = SqlStatements.get("sql.ReferenceValue.FIND_BY_TEST_AND_GENDER_AND_AGE");

    @Override
    public ReferenceValue findByTestAndGenderAndAge(Long testId, char gender, int age) {
        logger.debug("Поиск референсного значения: тест={}, пол={}, возраст={}", testId, gender, age);
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_TEST_AND_GENDER_AND_AGE)) {
            ps.setLong(1, testId);
            ps.setString(2, String.valueOf(gender));
            ps.setInt(3, age);
            ps.setInt(4, age);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.info("Найдено референсное значение для теста={}, пола={}, возраста={}", testId, gender, age);
                    return mapSingle(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске референсного значения по критериям (тест={}, пол={}, возраст={}): {}", testId, gender, age, e.getMessage());
            throw new RuntimeException(e);
        }
        logger.warn("Референсное значение не найдено для теста={}, пола={}, возраста={}", testId, gender, age);
        return null;
    }


    private ReferenceValue mapSingle(ResultSet rs){
        ReferenceValue rv = new ReferenceValue();
        try {
            rv.setIdReference(rs.getLong("id_reference"));

            // связь с тестом
            AnalysisTest test = new AnalysisTest();
            test.setIdTest(rs.getLong("test_id"));
            rv.setTest(test);

            rv.setGenderApplicable(rs.getString("gender_applicable").charAt(0));

            int ageMin = rs.getInt("age_min");
            if (!rs.wasNull()) {
                rv.setAgeMin(ageMin);
            }
            int ageMax = rs.getInt("age_max");
            if (!rs.wasNull()) {
                rv.setAgeMax(ageMax);
            }

            BigDecimal minVal = rs.getBigDecimal("ref_value_min");
            if (minVal != null) {
                rv.setRefValueMin(minVal);
            }
            BigDecimal maxVal = rs.getBigDecimal("ref_value_max");
            if (maxVal != null) {
                rv.setRefValueMax(maxVal);
            }
            rv.setRefText(rs.getString("ref_text"));
        } catch (SQLException e) {
            logger.error("Ошибка при работе single-маппера: '{}'", e.getMessage());
            throw new RuntimeException(e);
        }
        return rv;
    }

    private List<ReferenceValue> mapper(ResultSet rs){
        List<ReferenceValue> list = new ArrayList<>();
        try {
            while (rs.next()) {
                list.add(mapSingle(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при работе маппера: '{}'", e.getMessage());
            throw new RuntimeException(e);
        }
        return list;
    }
}