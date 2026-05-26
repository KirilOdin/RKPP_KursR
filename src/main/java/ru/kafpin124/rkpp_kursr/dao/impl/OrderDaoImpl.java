package ru.kafpin124.rkpp_kursr.dao.impl;



import ru.kafpin124.rkpp_kursr.util.DBHelper;
import ru.kafpin124.rkpp_kursr.dao.OrderDao;
import ru.kafpin124.rkpp_kursr.model.*;
import ru.kafpin124.rkpp_kursr.util.SqlStatements;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao {

//    1. Вставка нового заказа

//    private static final String ADD = "INSERT INTO public.orders(status_id, patient_id, " +
//            "organization_id, registered_by, accepted_by, verified_by, " +
//            "registration_datetime, acceptance_datetime, verification_datetime) " +
//            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    public static final String ADD = SqlStatements.get("sql.Order.ADD");

    @Override
    public void add(Order order) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(ADD, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, order.getStatus().getIdStatus());
            ps.setLong(2, order.getPatient().getIdPatient());

            if (order.getOrganization() != null) {
                ps.setLong(3, order.getOrganization().getIdOrg());
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            ps.setLong(4, order.getRegisteredBy().getIdEmployee());

            if (order.getAcceptedBy() != null) {
                ps.setLong(5, order.getAcceptedBy().getIdEmployee());
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            if (order.getVerifiedBy() != null) {
                ps.setLong(6, order.getVerifiedBy().getIdEmployee());
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            ps.setTimestamp(7, Timestamp.valueOf(order.getRegistrationDatetime()));
            ps.setTimestamp(8, order.getAcceptanceDatetime() != null ?
                    Timestamp.valueOf(order.getAcceptanceDatetime()) : null);
            ps.setTimestamp(9, order.getVerificationDatetime() != null ?
                    Timestamp.valueOf(order.getVerificationDatetime()) : null);

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Creating order failed, no rows affected.");
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    order.setIdOrder(keys.getLong(1));
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

//    2. Поиск заказа по идентификатору

//    private static final String FIND_BY_ID = "SELECT id_order, status_id, patient_id, " +
//            "organization_id, registered_by, accepted_by, verified_by, " +
//            "registration_datetime, acceptance_datetime, verification_datetime " +
//            "FROM public.orders WHERE id_order = ?;";

    public static final String FIND_BY_ID = SqlStatements.get("sql.Order.FIND_BY_ID");

    @Override
    public Order findById(Long id) {
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

//    3. Вывод всех заказов, находящихся в базе данных

//    private static final String GET_ALL = "SELECT id_order, status_id, patient_id, " +
//            "organization_id, registered_by, accepted_by, verified_by, " +
//            "registration_datetime, acceptance_datetime, verification_datetime FROM public.orders;";

    public static final String GET_ALL = SqlStatements.get("sql.Order.GET_ALL");

    @Override
    public List<Order> getAll() {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL)) {
            orders = mapper(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return orders;
    }

//    4. Изменение значений у существующего заказа

//    private static final String UPDATE = "UPDATE public.orders SET status_id=?, patient_id=?, " +
//            "organization_id=?, registered_by=?, accepted_by=?, verified_by=?, " +
//            "registration_datetime=?, acceptance_datetime=?, verification_datetime=? WHERE id_order = ?;";

    public static final String UPDATE = SqlStatements.get("sql.Order.UPDATE");

    @Override
    public void update(Order order) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setLong(1, order.getStatus().getIdStatus());
            ps.setLong(2, order.getPatient().getIdPatient());

            if (order.getOrganization() != null) {
                ps.setLong(3, order.getOrganization().getIdOrg());
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            ps.setLong(4, order.getRegisteredBy().getIdEmployee());

            if (order.getAcceptedBy() != null) {
                ps.setLong(5, order.getAcceptedBy().getIdEmployee());
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            if (order.getVerifiedBy() != null) {
                ps.setLong(6, order.getVerifiedBy().getIdEmployee());
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            ps.setTimestamp(7, Timestamp.valueOf(order.getRegistrationDatetime()));
            ps.setTimestamp(8, order.getAcceptanceDatetime() != null ?
                    Timestamp.valueOf(order.getAcceptanceDatetime()) : null);
            ps.setTimestamp(9, order.getVerificationDatetime() != null ?
                    Timestamp.valueOf(order.getVerificationDatetime()) : null);
            ps.setLong(10, order.getIdOrder());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Updating order failed, no rows affected.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

//    5. Удаление заказа из базы данных (как несостоявшийся?)

//    private static final String DELETE_BY_ID = "DELETE FROM public.orders WHERE id_order = ?;";

    public static final String DELETE_BY_ID = SqlStatements.get("sql.Order.DELETE_BY_ID");

    @Override
    public void delete(Order order) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BY_ID)) {
            ps.setLong(1, order.getIdOrder());
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Deleting order failed, no rows deleted.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

//    6. Поиск заказов по идентификатору пациента

//    private static final String FIND_BY_PATIENT_ID = "SELECT id_order, status_id, patient_id, " +
//            "organization_id, registered_by, accepted_by, verified_by, " +
//            "registration_datetime, acceptance_datetime, verification_datetime " +
//            "FROM public.orders WHERE patient_id = ?;";

    public static final String FIND_BY_PATIENT_ID = SqlStatements.get("sql.Order.FIND_BY_PATIENT_ID");

    @Override
    public List<Order> findByPatientId(Long patientId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_PATIENT_ID)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                orders = mapper(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return orders;
    }


//    7. Поиск заказов по идентификатору статуса заказа

//    private static final String FIND_BY_STATUS_ID = "SELECT id_order, status_id, patient_id, " +
//            "organization_id, registered_by, accepted_by, verified_by, " +
//            "registration_datetime, acceptance_datetime, verification_datetime " +
//            "FROM public.orders WHERE status_id = ?;";

    public static final String FIND_BY_STATUS_ID = SqlStatements.get("sql.Order.FIND_BY_STATUS_ID");

    @Override
    public List<Order> findByStatusId(Long statusId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_STATUS_ID)) {
            ps.setLong(1, statusId);
            try (ResultSet rs = ps.executeQuery()) {
                orders = mapper(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return orders;
    }


//  8. Поиск заказов по дате регистрации за данный промежуток времени

//    private static final String FIND_BY_DATE_RANGE = "SELECT id_order, status_id, patient_id, " +
//            "organization_id, registered_by, accepted_by, verified_by, " +
//            "registration_datetime, acceptance_datetime, verification_datetime " +
//            "FROM public.orders " +
//            "WHERE registration_datetime >= ? AND registration_datetime <= ? " +
//            "ORDER BY registration_datetime DESC;";

    public static final String FIND_BY_DATE_RANGE = SqlStatements.get("sql.Order.FIND_BY_DATE_RANGE");

    @Override
    public List<Order> findByDateRange(LocalDateTime from, LocalDateTime to) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_DATE_RANGE)) {
            ps.setTimestamp(1, Timestamp.valueOf(from));
            ps.setTimestamp(2, Timestamp.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                orders = mapper(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return orders;
    }


//    9. Поиск заказа по идентификатору сотрудника, зарегистрировавшего заказ

//    private static final String FIND_BY_REGISTERED_BY = "SELECT id_order, status_id, patient_id, " +
//            "organization_id, registered_by, accepted_by, verified_by, " +
//            "registration_datetime, acceptance_datetime, verification_datetime " +
//            "FROM public.orders WHERE registered_by = ?;";

    public static final String FIND_BY_REGISTERED_BY = SqlStatements.get("sql.Order.FIND_BY_REGISTERED_BY");

    @Override
    public List<Order> findByRegisteredBy(Long employeeId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_REGISTERED_BY)) {
            ps.setLong(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                orders = mapper(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return orders;
    }


//    10. Поиск заказа по штрих-коду с биологической пробы

//    private static final String FIND_BY_SPECIMEN_BARCODE = "SELECT o.id_order, o.status_id, o.patient_id, " +
//            "o.organization_id, o.registered_by, o.accepted_by, o.verified_by, " +
//            "o.registration_datetime, o.acceptance_datetime, o.verification_datetime " +
//            "FROM public.orders o " +
//            "JOIN public.specimens s ON o.id_order = s.order_id " +
//            "WHERE s.barcode = ?;";

    public static final String FIND_BY_SPECIMEN_BARCODE = SqlStatements.get("sql.Order.FIND_BY_SPECIMEN_BARCODE");

    @Override
    public Order findBySpecimenBarcode(String barcode) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_SPECIMEN_BARCODE)) {
            ps.setString(1, barcode);
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


//    11. Обновление статуса заказа

//    private static final String UPDATE_STATUS = "UPDATE public.orders SET status_id = ? WHERE id_order = ?;";

    public static final String UPDATE_STATUS = SqlStatements.get("sql.Order.UPDATE_STATUS");

    @Override
    public void updateStatus(Long orderId, Long statusId) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS)) {
            ps.setLong(1, statusId);
            ps.setLong(2, orderId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Updating status failed, no rows affected.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected List<Order> mapper(ResultSet rs) throws SQLException {
        List<Order> orders = new ArrayList<>();
        while (rs.next()) {
            orders.add(mapSingle(rs));
        }
        return orders;
    }

    private Order mapSingle(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setIdOrder(rs.getLong("id_order"));

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setIdStatus(rs.getLong("status_id"));
        order.setStatus(orderStatus);

        Patient patient = new Patient();
        patient.setIdPatient(rs.getLong("patient_id"));
        order.setPatient(patient);

        long orgId = rs.getLong("organization_id");
        if (!rs.wasNull()) {
            Organization org = new Organization();
            org.setIdOrg(orgId);
            order.setOrganization(org);
        }

        Employee registeredBy = new Employee();
        registeredBy.setIdEmployee(rs.getLong("registered_by"));
        order.setRegisteredBy(registeredBy);

        long acceptedById = rs.getLong("accepted_by");
        if (!rs.wasNull()) {
            Employee acceptedBy = new Employee();
            acceptedBy.setIdEmployee(acceptedById);
            order.setAcceptedBy(acceptedBy);
        }


        long verifiedById = rs.getLong("verified_by");
        if (!rs.wasNull()) {
            Employee verifiedBy = new Employee();
            verifiedBy.setIdEmployee(verifiedById);
            order.setVerifiedBy(verifiedBy);
        }

        Timestamp regTs = rs.getTimestamp("registration_datetime");
        if (regTs != null) {
            order.setRegistrationDatetime(regTs.toLocalDateTime());
        }

        Timestamp accTs = rs.getTimestamp("acceptance_datetime");
        if (accTs != null) {
            order.setAcceptanceDatetime(accTs.toLocalDateTime());
        }

        Timestamp verTs = rs.getTimestamp("verification_datetime");
        if (verTs != null) {
            order.setVerificationDatetime(verTs.toLocalDateTime());
        }

        return order;
    }
}
