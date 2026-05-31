package ru.kafpin124.rkpp_kursr.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.dao.AnalysisTestDao;
import ru.kafpin124.rkpp_kursr.util.DBHelper;
import ru.kafpin124.rkpp_kursr.dao.OrderItemDao;
import ru.kafpin124.rkpp_kursr.model.*;
import ru.kafpin124.rkpp_kursr.util.SqlStatements;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDaoImpl implements OrderItemDao {

    public static final Logger logger = LoggerFactory.getLogger(OrderItemDaoImpl.class);

    AnalysisTestDao testDao = new AnalysisTestDaoImpl();
//    1. Вставка новой позиции заказа

//    private static final String ADD = "INSERT INTO public.order_items(order_id, test_id, specimen_id, " +
//        "status, result_value, result_text, is_abnormal, entered_by, entry_datetime) " +
//        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    public static final String ADD = SqlStatements.get("sql.OrderItem.ADD");

    @Override
    public void add(OrderItem orderItem) {
        Long orderId = orderItem.getOrder() != null ? orderItem.getOrder().getIdOrder() : null;
        logger.debug("Добавление позиции заказа для заказа ID: {}, тест ID: {}", orderId, orderItem.getTest() != null ? orderItem.getTest().getIdTest() : null);
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(ADD, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, orderItem.getOrder().getIdOrder());
            ps.setLong(2, orderItem.getTest().getIdTest());
            ps.setLong(3, orderItem.getSpecimen().getIdSpecimen());
            ps.setString(4, orderItem.getStatus());

            if (orderItem.getResultValue() != null) {
                ps.setBigDecimal(5, orderItem.getResultValue());
            } else {
                ps.setNull(5, Types.NUMERIC);
            }
            ps.setString(6, orderItem.getResultText());
            ps.setBoolean(7, orderItem.getIsAbnormal() != null && orderItem.getIsAbnormal());

            if (orderItem.getEnteredBy() != null) {
                ps.setLong(8, orderItem.getEnteredBy().getIdEmployee());
            } else {
                ps.setNull(8, Types.BIGINT);
            }

            if (orderItem.getEntryDatetime() != null) {
                ps.setTimestamp(9, Timestamp.valueOf(orderItem.getEntryDatetime()));
            } else {
                ps.setNull(9, Types.TIMESTAMP);
            }

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Creating order_item failed, no rows affected.");
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    orderItem.setIdItem(keys.getLong(1));
                    logger.info("Позиция заказа добавлена с ID: {} (заказ ID: {})", orderItem.getIdItem(), orderId);
                } else {
                    throw new SQLException("Creating order_item failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении позиции заказа: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


//    2. Поиск позиции заказа по идентификатору

//    private static final String FIND_BY_ID = "SELECT id_item, order_id, test_id, specimen_id, " +
//            "status, result_value, result_text, is_abnormal, entered_by, entry_datetime " +
//            "FROM public.order_items WHERE id_item = ?;";

    public static final String FIND_BY_ID = SqlStatements.get("sql.OrderItem.FIND_BY_ID");

    @Override
    public OrderItem findById(Long id) {
        logger.debug("Поиск позиции заказа по ID: {}", id);
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.info("Поиск позиции заказа по ID '{}' завершён", id);
                    return mapSingle(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске позиции заказа с ID {}: {}", id, e.getMessage());
            throw new RuntimeException(e);
        }
        logger.warn("Позиция заказа с ID {} не найдена", id);
        return null;
    }


//    3. Вывод всех позиций заказов, находящихся в базе данных

//    private static final String GET_ALL = "SELECT id_item, order_id, test_id, specimen_id, " +
//            "status, result_value, result_text, is_abnormal, entered_by, entry_datetime " +
//            "FROM public.order_items;";

    public static final String GET_ALL = SqlStatements.get("sql.OrderItem.GET_ALL");

    @Override
    public List<OrderItem> getAll() {
        logger.debug("Получение всех позиций заказа, находящихся в базе данных...");
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL)) {
            items = mapper(rs);
            logger.debug("Загружено {} позиций заказов", items.size());
        } catch (SQLException e) {
            logger.error("Ошибка при загрузке всех позиций заказов, хранящихся в базе данных: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return items;
    }


//    4. Изменение значений у существующей позиции заказа

//    private static final String UPDATE = "UPDATE public.order_items SET order_id=?, test_id=?, specimen_id=?, " +
//            "status=?, result_value=?, result_text=?, is_abnormal=?, entered_by=?, entry_datetime=? " +
//            "WHERE id_item = ?;";


    public static final String UPDATE = SqlStatements.get("sql.OrderItem.UPDATE");

    @Override
    public void update(OrderItem orderItem) {
        logger.debug("Обновление позиции заказа с ID: {}", orderItem.getIdItem());
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setLong(1, orderItem.getOrder().getIdOrder());
            ps.setLong(2, orderItem.getTest().getIdTest());
            ps.setLong(3, orderItem.getSpecimen().getIdSpecimen());
            ps.setString(4, orderItem.getStatus());

            if (orderItem.getResultValue() != null) {
                ps.setBigDecimal(5, orderItem.getResultValue());
            } else {
                ps.setNull(5, Types.NUMERIC);
            }
            ps.setString(6, orderItem.getResultText());
            ps.setBoolean(7, orderItem.getIsAbnormal() != null && orderItem.getIsAbnormal());

            if (orderItem.getEnteredBy() != null) {
                ps.setLong(8, orderItem.getEnteredBy().getIdEmployee());
            } else {
                ps.setNull(8, Types.BIGINT);
            }

            if (orderItem.getEntryDatetime() != null) {
                ps.setTimestamp(9, Timestamp.valueOf(orderItem.getEntryDatetime()));
            } else {
                ps.setNull(9, Types.TIMESTAMP);
            }
            ps.setLong(10, orderItem.getIdItem());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Updating order_item failed, no rows affected.");
            logger.info("Позиция заказа с ID {} обновлена", orderItem.getIdItem());
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении позиции заказа с ID {}: {}", orderItem.getIdItem(), e.getMessage());
            throw new RuntimeException(e);
        }

    }

//    5. Удаление позиции заказа из базы данных (как несостоявшаяся?)

//    private static final String DELETE = "DELETE FROM public.order_items WHERE id_item = ?;";

    public static final String DELETE = SqlStatements.get("sql.OrderItem.DELETE");

    @Override
    public void delete(OrderItem orderItem) {
        logger.debug("Удаление позиции заказа с ID: {}", orderItem.getIdItem());
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setLong(1, orderItem.getIdItem());
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Deleting order_item failed, no rows deleted.");
            logger.info("Позиция заказа с ID {} удалена", orderItem.getIdItem());
        } catch (SQLException e) {
            logger.error("Ошибка при удалении позиции заказа с ID {}: {}", orderItem.getIdItem(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

//    6. Поиск позиций заказов по идентификатору заказа

//    private static final String FIND_BY_ORDER_ID = "SELECT id_item, order_id, test_id, specimen_id, " +
//            "status, result_value, result_text, is_abnormal, entered_by, entry_datetime " +
//            "FROM public.order_items WHERE order_id = ?;";

    public static final String FIND_BY_ORDER_ID = SqlStatements.get("sql.OrderItem.FIND_BY_ORDER_ID");

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        logger.debug("Поиск позиций заказа по ID заказа: {}", orderId);
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ORDER_ID)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                items = mapper(rs);
                logger.debug("Найдено {} позиций для заказа ID {}", items.size(), orderId);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            logger.error("Ошибка при поиске позиций заказа по ID заказа {}: {}", orderId, e.getMessage());
        }
        return items;
    }


//    7. Поиск позиций заказов по идентификатору биологической пробы

//    private static final String FIND_BY_SPECIMEN_ID = "SELECT id_item, order_id, test_id, specimen_id, " +
//            "status, result_value, result_text, is_abnormal, entered_by, entry_datetime " +
//            "FROM public.order_items WHERE specimen_id = ?;";

    public static final String FIND_BY_SPECIMEN_ID = SqlStatements.get("sql.OrderItem.FIND_BY_SPECIMEN_ID");

    @Override
    public List<OrderItem> findBySpecimenId(Long specimenId) {
        logger.debug("Поиск позиций заказа по ID пробы: {}", specimenId);
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_SPECIMEN_ID)) {
            ps.setLong(1, specimenId);
            try (ResultSet rs = ps.executeQuery()) {
                items = mapper(rs);
                logger.debug("Найдено {} позиций для пробы ID {}", items.size(), specimenId);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске позиций заказа по ID пробы {}: {}", specimenId, e.getMessage());
            throw new RuntimeException(e);
        }
        return items;
    }

//    8. Поиск позиций заказов по идентификатору работника, обрабатывавшего значения

//    private static final String FIND_BY_ENTERED_BY = "SELECT id_item, order_id, test_id, specimen_id, " +
//        "status, result_value, result_text, is_abnormal, entered_by, entry_datetime " +
//        "FROM public.order_items WHERE entered_by = ?;";

    public static final String FIND_BY_ENTERED_BY = SqlStatements.get("sql.OrderItem.FIND_BY_ENTERED_BY");

    @Override
    public List<OrderItem> findByEnteredBy(Long employeeId) {
        logger.debug("Поиск позиций заказа, введённых сотрудником с ID: {}", employeeId);
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ENTERED_BY)) {
            ps.setLong(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                items = mapper(rs);
                logger.debug("Найдено {} позиций, введённых сотрудником ID {}", items.size(), employeeId);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске позиций по сотруднику ID {}: {}", employeeId, e.getMessage());
            throw new RuntimeException(e);
        }
        return items;
    }


//    9. Обновление результатов в позиции заказа

//    private static final String UPDATE_RESULT = "UPDATE public.order_items " +
//            "SET result_value = ?, result_text = ?, is_abnormal = ?, entered_by = ?, entry_datetime = ?, " +
//            "status = 'выполнен' WHERE id_item = ?;";

    public static final String UPDATE_RESULT = SqlStatements.get("sql.OrderItem.UPDATE_RESULT");

    @Override
    public void updateResult(Long itemId, BigDecimal value, String text, boolean abnormal, Long idEmployee) {
        logger.debug("Обновление результата позиции ID: {}, значение: {}, отклонение: {}", itemId, value, abnormal);
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_RESULT)) {
            ps.setBigDecimal(1, value);
            ps.setString(2, text);
            ps.setBoolean(3, abnormal);
            ps.setLong(4, idEmployee);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(6, itemId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Updating result failed, no rows affected.");
            logger.info("Результат позиции ID {} обновлён", itemId);
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении результата позиции ID {}: {}", itemId, e.getMessage());
            throw new RuntimeException(e);
        }
    }


//    10. Обновление статуса позиции заказа

//    private static final String UPDATE_STATUS = "UPDATE public.order_items SET status = ? WHERE id_item = ?;";

    public static final String UPDATE_STATUS = SqlStatements.get("sql.OrderItem.UPDATE_STATUS");

    @Override
    public void updateStatus(Long itemId, String status) {
        logger.debug("Обновление статуса позиции ID {} на '{}'", itemId, status);
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS)) {
            ps.setString(1, status);
            ps.setLong(2, itemId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Updating status failed, no rows affected.");
            logger.info("Статус позиции ID {} изменён на '{}'", itemId, status);
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении статуса позиции ID {}: {}", itemId, e.getMessage());
            throw new RuntimeException(e);
        }
    }



    private OrderItem mapSingle(ResultSet rs) {
        OrderItem item = new OrderItem();

        try {
            item.setIdItem(rs.getLong("id_item"));

            Order order = new Order();
            order.setIdOrder(rs.getLong("order_id"));
            item.setOrder(order);

            long testId = rs.getLong("test_id");
            AnalysisTest test = testDao.findById(testId);
            if (test == null) {
                test = new AnalysisTest();
                test.setIdTest(testId);
            }
            item.setTest(test);

            Specimen specimen = new Specimen();
            specimen.setIdSpecimen(rs.getLong("specimen_id"));
            item.setSpecimen(specimen);

            item.setStatus(rs.getString("status"));

            BigDecimal value = rs.getBigDecimal("result_value");
            if (!rs.wasNull()) {
                item.setResultValue(value);
            }
            item.setResultText(rs.getString("result_text"));

            Boolean abnormal = rs.getBoolean("is_abnormal");
            if (!rs.wasNull()) {
                item.setIsAbnormal(abnormal);
            }

            long enteredById = rs.getLong("entered_by");
            if (!rs.wasNull()) {
                Employee enteredBy = new Employee();
                enteredBy.setIdEmployee(enteredById);
                item.setEnteredBy(enteredBy);
            }

            Timestamp entryTs = rs.getTimestamp("entry_datetime");
            if (entryTs != null) {
                item.setEntryDatetime(entryTs.toLocalDateTime());
            }
        } catch (SQLException e) {
            logger.error("Ошибка при работе single-маппера: '{}'", e.getMessage());
            throw new RuntimeException(e);
        }

        return item;
    }

    private List<OrderItem> mapper(ResultSet rs){
        List<OrderItem> items = new ArrayList<>();
        try {
            while (rs.next()) {
                items.add(mapSingle(rs));
            }
        } catch (SQLException e) {
            logger.error("Ошибка при работе маппера: '{}'", e.getMessage());
            throw new RuntimeException(e);
        }
        return items;
    }
}

