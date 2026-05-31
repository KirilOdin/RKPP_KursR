package ru.kafpin124.rkpp_kursr.dao.impl;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.dao.OrderStatusDao;
import ru.kafpin124.rkpp_kursr.dao.SpecimenDao;
import ru.kafpin124.rkpp_kursr.util.DBHelper;
import ru.kafpin124.rkpp_kursr.dao.OrderDao;
import ru.kafpin124.rkpp_kursr.model.*;
import ru.kafpin124.rkpp_kursr.util.SqlStatements;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao {

    public static final Logger logger = LoggerFactory.getLogger(OrderDaoImpl.class);
    private PatientDaoImpl patientDao = new PatientDaoImpl();
    private final OrderStatusDao orderStatusDao = new OrderStatusDaoImpl();
    private final SpecimenDao specimenDao = new SpecimenDaoImpl();
//    1. Вставка нового заказа

//    private static final String ADD = "INSERT INTO public.orders(status_id, patient_id, " +
//            "organization_id, registered_by, accepted_by, verified_by, " +
//            "registration_datetime, acceptance_datetime, verification_datetime) " +
//            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    public static final String ADD = SqlStatements.get("sql.Order.ADD");

    @Override
    public void add(Order order) {
        Long patientId = order.getPatient() != null ? order.getPatient().getIdPatient() : null;
        logger.debug("Попытка создания заказа для пациента ID: {}", patientId);
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
                    logger.info("Заказ успешно создан. ID заказа: {}, Пациент ID: {}", order.getIdOrder(), patientId);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при создании заказа для пациента ID {}: {}", patientId, e.getMessage());
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
        logger.debug("Поиск заказа по ID: {}", id);
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.info("Поиск заказа по ID '{}' завершён", id);
                    return mapSingle(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Заказ с ID {} не найден", id);
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
        logger.debug("Получение всех заказов, находящихся в базе данных...");
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL)) {
            orders = mapper(rs);
            logger.info("Все заказы из базы данных получены");
        } catch (SQLException e) {
            logger.error("Ошибка при получении всех заказов, хранящихся в базе данных: {}", e.getMessage());
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
        logger.info("Обновление заказа с ID: {}", order.getIdOrder());
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
            logger.info("Обновлены данные заказа с ID {}", order.getIdOrder());
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении заказа с идентификатором '{}': {}", order.getIdOrder(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

//    5. Удаление заказа из базы данных (как несостоявшийся?)

//    private static final String DELETE_BY_ID = "DELETE FROM public.orders WHERE id_order = ?;";

    public static final String DELETE_BY_ID = SqlStatements.get("sql.Order.DELETE_BY_ID");

    @Override
    public void delete(Order order) {
        logger.info("Удаление заказа с ID: {}", order.getIdOrder());
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BY_ID)) {
            ps.setLong(1, order.getIdOrder());
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Deleting order failed, no rows deleted.");
            logger.info("Удалён заказ с ID {}", order.getIdOrder());
        } catch (SQLException e) {
            logger.error("Ошибка при удалении заказа с идентификатором '{}': {}", order.getIdOrder(), e.getMessage());
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
        logger.debug("Поиск заказа по ID пациента: {}", patientId);
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_PATIENT_ID)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                orders = mapper(rs);
                logger.info("Поиск заказа по ID пациента '{}' завершён", patientId);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске заказа по ID пациента '{}': {}", patientId, e.getMessage());
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
        logger.debug("Поиск заказа по ID Статуса Заказа: {}", statusId);
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_STATUS_ID)) {
            ps.setLong(1, statusId);
            try (ResultSet rs = ps.executeQuery()) {
                orders = mapper(rs);
                logger.debug("Поиск заказа по ID Статуса Заказа '{}' завершён", statusId);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске заказа по ID Статуса Заказа '{}': {}", statusId, e.getMessage());
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
        logger.debug("Поиск заказов в диапазоне дат: {} - {}", from, to);
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_DATE_RANGE)) {
            ps.setTimestamp(1, Timestamp.valueOf(from));
            ps.setTimestamp(2, Timestamp.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                orders = mapper(rs);
                logger.debug("Найдено {} заказов в заданном диапазоне", orders.size());
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске заказов по датам ({} – {}): {}", from, to, e.getMessage());
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
        logger.debug("Поиск заказов, зарегистрированных сотрудником с ID: {}", employeeId);
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_REGISTERED_BY)) {
            ps.setLong(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                orders = mapper(rs);
                logger.debug("Найдено {} заказов для сотрудника ID: {}", orders.size(), employeeId);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске заказов по сотруднику (ID: {}): {}", employeeId, e.getMessage());
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
        logger.debug("Поиск заказа по штрих-коду пробы: {}", barcode);
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_SPECIMEN_BARCODE)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapSingle(rs);
                    logger.debug("Заказ с ID {} найден по штрих-коду {}", order.getIdOrder(), barcode);
                    return order;
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске заказа по штрих-коду {}: {}", barcode, e.getMessage());
            throw new RuntimeException(e);
        }
        logger.warn("Заказ по штрих-коду {} не найден", barcode);
        return null;
    }


//    11. Обновление статуса заказа

//    private static final String UPDATE_STATUS = "UPDATE public.orders SET status_id = ? WHERE id_order = ?;";

    public static final String UPDATE_STATUS = SqlStatements.get("sql.Order.UPDATE_STATUS");

    @Override
    public void updateStatus(Long orderId, Long statusId) {
        logger.info("Изменение статуса заказа с идентификатором {} на статус ID {}", orderId, statusId);
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS)) {
            ps.setLong(1, statusId);
            ps.setLong(2, orderId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Updating status failed, no rows affected.");
            logger.info("Изменение статуса заказа с идентификатором {} на статус ID {}", orderId, statusId);
        } catch (SQLException e) {
            logger.info("Ошибка при изменении статуса заказа с идентификатором {} на статус ID {}: {}", orderId, statusId, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected List<Order> mapper(ResultSet rs){
        List<Order> orders = new ArrayList<>();
        try {
            while (rs.next()) {
                orders.add(mapSingle(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при работе маппера: '{}'", e.getMessage());
            throw new RuntimeException(e);
        }
        return orders;
    }

    private Order mapSingle(ResultSet rs){
        Order order = new Order();

        try {
            order.setIdOrder(rs.getLong("id_order"));

            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setIdStatus(rs.getLong("status_id"));
            order.setStatus(orderStatus);

            long statusId = rs.getLong("status_id");
            OrderStatus status = orderStatusDao.getById(statusId);
            if (status == null) {
                status = new OrderStatus();
                status.setIdStatus(statusId);
                status.setStatusName("неизвестно");
            }
            order.setStatus(status);

            long patientId = rs.getLong("patient_id");
            Patient patient = patientDao.findById(patientId);
            if (patient == null) {
                patient = new Patient();
                patient.setIdPatient(patientId);
            }
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

            List<Specimen> specimens = specimenDao.findByOrderId(order.getIdOrder());
            order.setSpecimens(specimens);


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
        } catch (SQLException e) {
            logger.error("Ошибка при работе single-маппера: '{}'", e.getMessage());
            throw new RuntimeException(e);
        }

        return order;
    }
}
