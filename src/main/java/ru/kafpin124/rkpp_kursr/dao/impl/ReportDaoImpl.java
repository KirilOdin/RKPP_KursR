package ru.kafpin124.rkpp_kursr.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.dao.ReportDao;
import ru.kafpin124.rkpp_kursr.dto.TestCountByType;
import ru.kafpin124.rkpp_kursr.dto.EmployeeStatistic;
import ru.kafpin124.rkpp_kursr.dto.OrganizationStatistic;
import ru.kafpin124.rkpp_kursr.util.DBHelper;
import ru.kafpin124.rkpp_kursr.util.SqlStatements;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportDaoImpl implements ReportDao {

    //TODO: Добавить логирование!

    public static final Logger logger = LoggerFactory.getLogger(ReportDaoImpl.class);

//    public static final String GET_TEST_COUNT_BY_TYPE = "SELECT t.test_name, COUNT(oi.id_item) AS cnt " +
//            "FROM public.order_items oi " +
//            "JOIN public.tests t ON oi.test_id = t.id_test " +
//            "JOIN public.orders o ON oi.order_id = o.id_order " +
//            "WHERE o.registration_datetime >= ? AND o.registration_datetime <= ? " +
//            "AND oi.status = 'выполнен' " +
//            "GROUP BY t.test_name ORDER BY cnt DESC";

    public static final String GET_TEST_COUNT_BY_TYPE = SqlStatements.get("sql.Report.GET_TEST_COUNT_BY_TYPE");

    @Override
    public List<TestCountByType> getTestCountByType(LocalDate from, LocalDate to) {
        List<TestCountByType> result = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_TEST_COUNT_BY_TYPE)) {
            ps.setTimestamp(1, Timestamp.valueOf(from.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(to.atTime(23, 59, 59)));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new TestCountByType(rs.getString("test_name"), rs.getLong("cnt")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return result;
    }

//    public static final String GET_WORKLOAD_BY_EMPLOYEE = "SELECT e.last_name || ' ' || e.first_name || ' ' || e.middle_name AS fullname, " +
//            "COUNT(oi.id_item) AS cnt " +
//            "FROM public.order_items oi " +
//            "JOIN public.employees e ON oi.entered_by = e.id_employee " +
//            "JOIN public.orders o ON oi.order_id = o.id_order " +
//            "WHERE o.registration_datetime >= ? AND o.registration_datetime <= ? " +
//            "AND oi.status = 'выполнен' " +
//            "GROUP BY fullname ORDER BY cnt DESC";

    public static final String GET_WORKLOAD_BY_EMPLOYEE = SqlStatements.get("sql.Report.GET_WORKLOAD_BY_EMPLOYEE");

    @Override
    public List<EmployeeStatistic> getWorkloadByEmployee(LocalDate from, LocalDate to) {
        List<EmployeeStatistic> result = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_WORKLOAD_BY_EMPLOYEE)) {
            ps.setTimestamp(1, Timestamp.valueOf(from.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(to.atTime(23, 59, 59)));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new EmployeeStatistic(rs.getString("fullname"), rs.getLong("cnt")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return result;
    }

//    public static final String GET_REVENUE_BY_ORGANIZATION = "SELECT org.org_name, SUM(t.price) AS total_revenue " +
//            "FROM public.order_items oi " +
//            "JOIN public.tests t ON oi.test_id = t.id_test " +
//            "JOIN public.orders o ON oi.order_id = o.id_order " +
//            "JOIN public.organizations org ON o.organization_id = org.id_org " +
//            "WHERE o.registration_datetime >= ? AND o.registration_datetime <= ? " +
//            "AND oi.status = 'выполнен' " +
//            "GROUP BY org.org_name ORDER BY total_revenue DESC";

    public static final String GET_REVENUE_BY_ORGANIZATION = SqlStatements.get("sql.Report.GET_REVENUE_BY_ORGANIZATION");

    @Override
    public List<OrganizationStatistic> getRevenueByOrganization(LocalDate from, LocalDate to) {
        List<OrganizationStatistic> result = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_REVENUE_BY_ORGANIZATION)) {
            ps.setTimestamp(1, Timestamp.valueOf(from.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(to.atTime(23, 59, 59)));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new OrganizationStatistic(rs.getString("org_name"), rs.getBigDecimal("total_revenue")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return result;
    }
}