package ru.kafpin124.rkpp_kursr.dao.impl;

import ru.kafpin124.rkpp_kursr.DBHelper;
import ru.kafpin124.rkpp_kursr.dao.OrderItemDao;
import ru.kafpin124.rkpp_kursr.model.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDaoImpl implements OrderItemDao {


//    1. Вставка новой позиции заказа

    private static final String ADD = "INSERT INTO public.order_items(order_id, test_id, specimen_id, " +
        "status, result_value, result_text, is_abnormal, entered_by, entry_datetime) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    @Override
    public void add(OrderItem orderItem) {
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
                } else {
                    throw new SQLException("Creating order_item failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }


//    2. Поиск позиции заказа по идентификатору

    private static final String FIND_BY_ID = "SELECT id_item, order_id, test_id, specimen_id, " +
            "status, result_value, result_text, is_abnormal, entered_by, entry_datetime " +
            "FROM public.order_items WHERE id_item = ?;";

    @Override
    public OrderItem findById(Long id) {
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


//    3. Вывод всех позиций заказов, находящихся в базе данных

    private static final String GET_ALL = "SELECT id_item, order_id, test_id, specimen_id, " +
            "status, result_value, result_text, is_abnormal, entered_by, entry_datetime " +
            "FROM public.order_items;";

    @Override
    public List<OrderItem> getAll() {
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL)) {
            items = mapper(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return items;
    }


//    4. Изменение значений у существующей позиции заказа

    private static final String UPDATE = "UPDATE public.order_items SET order_id=?, test_id=?, specimen_id=?, " +
            "status=?, result_value=?, result_text=?, is_abnormal=?, entered_by=?, entry_datetime=? " +
            "WHERE id_item = ?;";

    @Override
    public void update(OrderItem orderItem) {
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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

//    5. Удаление позиции заказа из базы данных (как несостоявшаяся?)

    private static final String DELETE = "DELETE FROM public.order_items WHERE id_item = ?;";

    @Override
    public void delete(OrderItem orderItem) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setLong(1, orderItem.getIdItem());
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Deleting order_item failed, no rows deleted.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

//    6. Поиск позиций заказов по идентификатору заказа

    private static final String FIND_BY_ORDER_ID = "SELECT id_item, order_id, test_id, specimen_id, " +
            "status, result_value, result_text, is_abnormal, entered_by, entry_datetime " +
            "FROM public.order_items WHERE order_id = ?;";

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ORDER_ID)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                items = mapper(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return items;
    }


//    7. Поиск позиций заказов по идентификатору биологической пробы

    private static final String FIND_BY_SPECIMEN_ID = "SELECT id_item, order_id, test_id, specimen_id, " +
            "status, result_value, result_text, is_abnormal, entered_by, entry_datetime " +
            "FROM public.order_items WHERE specimen_id = ?;";

    @Override
    public List<OrderItem> findBySpecimenId(Long specimenId) {
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_SPECIMEN_ID)) {
            ps.setLong(1, specimenId);
            try (ResultSet rs = ps.executeQuery()) {
                items = mapper(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return items;
    }

//    8. Поиск позиций заказов по идентификатору работника, обрабатывавшего значения

    private static final String FIND_BY_ENTERED_BY = "SELECT id_item, order_id, test_id, specimen_id, " +
        "status, result_value, result_text, is_abnormal, entered_by, entry_datetime " +
        "FROM public.order_items WHERE entered_by = ?;";

    @Override
    public List<OrderItem> findByEnteredBy(Long employeeId) {
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ENTERED_BY)) {
            ps.setLong(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                items = mapper(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return items;
    }


//    9. Обновление результатов в позиции заказа

    private static final String UPDATE_RESULT = "UPDATE public.order_items " +
            "SET result_value = ?, result_text = ?, is_abnormal = ?, entered_by = ?, entry_datetime = ?, " +
            "status = 'выполнен' WHERE id_item = ?;";

    @Override
    public void updateResult(Long itemId, BigDecimal value, String text, boolean abnormal) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_RESULT)) {
            ps.setBigDecimal(1, value);
            ps.setString(2, text);
            ps.setBoolean(3, abnormal);
            // entered_by и entry_datetime обычно должны передаваться, но у нас метод без них.
            // Можно либо доработать интерфейс, либо временно оставить так.
            ps.setNull(4, Types.BIGINT);     // entered_by – позже добавишь
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(6, itemId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Updating result failed, no rows affected.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }


//    10. Обновление статуса позиции заказа

    private static final String UPDATE_STATUS = "UPDATE public.order_items SET status = ? WHERE id_item = ?;";

    @Override
    public void updateStatus(Long itemId, String status) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STATUS)) {
            ps.setString(1, status);
            ps.setLong(2, itemId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Updating status failed, no rows affected.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }



    private OrderItem mapSingle(ResultSet rs) throws SQLException {
        OrderItem item = new OrderItem();

        item.setIdItem(rs.getLong("id_item"));

        Order order = new Order();
        order.setIdOrder(rs.getLong("order_id"));
        item.setOrder(order);

        AnalysisTest test = new AnalysisTest();
        test.setIdTest(rs.getLong("test_id"));
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

        return item;
    }

    private List<OrderItem> mapper(ResultSet rs) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        while (rs.next()) {
            items.add(mapSingle(rs));
        }
        return items;
    }
}

