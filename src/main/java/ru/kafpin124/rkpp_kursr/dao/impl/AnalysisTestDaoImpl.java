package ru.kafpin124.rkpp_kursr.dao.impl;



import ru.kafpin124.rkpp_kursr.util.DBHelper;
import ru.kafpin124.rkpp_kursr.dao.AnalysisTestDao;
import ru.kafpin124.rkpp_kursr.model.AnalysisTest;
import ru.kafpin124.rkpp_kursr.util.SqlStatements;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnalysisTestDaoImpl implements AnalysisTestDao {
//
//    URL url = this.getClass().getResource("ru/kafpin/rkpp_lb_7/statements.properties");
//    this.property = new Properties();
//    FileInputStream fis =
//            new FileInputStream(url.getFile());
//        property.load(fis);


//    1. Вставка нового теста

//    private static final String ADD = "INSERT INTO public.tests(test_name, biomaterial, " +
//            "execution_time_hours, price, unit) " +
//            "VALUES (?, ?, ?, ?, ?);";

    private static final String ADD = SqlStatements.get("sql.AnalysisTest.ADD");
    @Override
    public void add(AnalysisTest analysisTest) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ADD, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, analysisTest.getTestName());
            pstmt.setString(2, analysisTest.getBiomaterial());
            pstmt.setInt(3, analysisTest.getExecutionTimeHours());
            pstmt.setBigDecimal(4, analysisTest.getPrice());
            pstmt.setString(5, analysisTest.getUnit());

            int addedRows = pstmt.executeUpdate();
            if (addedRows == 0) {
                throw new SQLException("Creating analysis_test failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    analysisTest.setIdTest(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating analysis_test failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }


//    2. Поиск теста по идентификатору

//    private static final String FIND_BY_ID = "SELECT id_test, test_name, biomaterial, " +
//            "execution_time_hours, price, unit FROM public.tests WHERE id_test = ?;";
    public static final String FIND_BY_ID = SqlStatements.get("sql.AnalysisTest.FIND_BY_ID");

    @Override
    public AnalysisTest findById(Long id) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_ID)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapSingle(rs);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }


//    3. Вывод всех тестов, находящихся в базе данных

//    private static final String GET_ALL = "SELECT id_test, test_name, biomaterial, " +
//            "execution_time_hours, price, unit FROM public.tests;";

    public static final String GET_ALL = SqlStatements.get("sql.AnalysisTest.GET_ALL");

    @Override
    public List<AnalysisTest> getAll() {
        List<AnalysisTest> analysisTestList = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL)) {
            analysisTestList = mapper(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return analysisTestList;
    }


//    4. Изменение значений у существующего теста

//    private static final String UPDATE = "UPDATE public.tests SET test_name=?, biomaterial=?, " +
//            "execution_time_hours=?, price=?, unit=? WHERE id_test = ?;";

    public static final String UPDATE = SqlStatements.get("sql.AnalysisTest.UPDATE");

    @Override
    public void update(AnalysisTest analysisTest) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE)) {
            pstmt.setString(1, analysisTest.getTestName());
            pstmt.setString(2, analysisTest.getBiomaterial());
            pstmt.setInt(3, analysisTest.getExecutionTimeHours());
            pstmt.setBigDecimal(4, analysisTest.getPrice());
            pstmt.setString(5, analysisTest.getUnit());
            pstmt.setLong(6, analysisTest.getIdTest());

            int addedRows = pstmt.executeUpdate();

            if (addedRows == 0) {
                throw new SQLException("Updating analysis_test failed, no rows added.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }


//    5. Удаление теста из базы данных (как побочный?)

//    private static final String DELETE_BY_ID = "DELETE FROM public.tests WHERE id_test = ?;";

    public static final String DELETE_BY_ID = SqlStatements.get("sql.AnalysisTest.DELETE_BY_ID");

    @Override
    public void delete(AnalysisTest analysisTest) {
        deleteById(analysisTest.getIdTest());
    }

    @Override
    public void deleteById(Long id) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_BY_ID)) {
            pstmt.setLong(1, id);
            int deletedRows = pstmt.executeUpdate();

            if (deletedRows == 0) {
                throw new SQLException("Deleting analysis_test failed, no rows deleted.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }


//    6. Поиск тестов по необходимому биоматериалу

//    private static final String FIND_BY_MATERIAL = "SELECT id_test, test_name, biomaterial, " +
//            "execution_time_hours, price, unit FROM public.tests WHERE biomaterial =?;";

    public static final String FIND_BY_MATERIAL = SqlStatements.get("sql.AnalysisTest.FIND_BY_MATERIAL");

    @Override
    public List<AnalysisTest> findByBiomaterial(String biomaterial) {
        List<AnalysisTest> analysisTestList = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_MATERIAL)) {
            pstmt.setString(1, biomaterial);
            try (ResultSet rs = pstmt.executeQuery()) {
                analysisTestList = mapper(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return analysisTestList;
    }


//    7. Поиск тестов по их названию

//    private static final String SEARCH_BY_NAME = "SELECT id_test, test_name, biomaterial, " +
//            "execution_time_hours, price, unit FROM public.tests WHERE test_name ILIKE ?;";

    public static final String SEARCH_BY_NAME = SqlStatements.get("sql.SEARCH_BY_NAME");

    @Override
    public List<AnalysisTest> searchByName(String testName) {
        List<AnalysisTest> analysisTestList = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SEARCH_BY_NAME)) {
            pstmt.setString(1, "%" + testName + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                analysisTestList = mapper(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return analysisTestList;
    }



    protected List<AnalysisTest> mapper(ResultSet rs) {
        List<AnalysisTest> analysisTestList = new ArrayList<>();
        try {
            while (rs.next()) {
                analysisTestList.add(mapSingle(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return analysisTestList;
    }

    private AnalysisTest mapSingle(ResultSet rs) throws SQLException {
        AnalysisTest analysisTest = new AnalysisTest();
        analysisTest.setIdTest(rs.getLong("id_test"));
        analysisTest.setTestName(rs.getString("test_name"));
        analysisTest.setBiomaterial(rs.getString("biomaterial"));
        analysisTest.setExecutionTimeHours(rs.getInt("execution_time_hours"));
        analysisTest.setPrice(rs.getBigDecimal("price"));
        analysisTest.setUnit(rs.getString("unit"));

        return analysisTest;
    }
}
