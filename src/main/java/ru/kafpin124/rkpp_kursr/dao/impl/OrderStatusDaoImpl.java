package ru.kafpin124.rkpp_kursr.dao.impl;


import ru.kafpin124.rkpp_kursr.util.DBHelper;
import ru.kafpin124.rkpp_kursr.dao.OrderStatusDao;
import ru.kafpin124.rkpp_kursr.model.OrderStatus;
import ru.kafpin124.rkpp_kursr.util.SqlStatements;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderStatusDaoImpl implements OrderStatusDao {


//    1. Вставка нового статуса заказа

//    private static final String ADD = "INSERT INTO public.order_statuses(status_name) VALUES (?);";

    public static final String ADD = SqlStatements.get("sql.OrderStatus.ADD");

    @Override
    public void add(OrderStatus orderStatus) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(ADD, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, orderStatus.getStatusName());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Creating order status failed, no rows affected.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    orderStatus.setIdStatus(keys.getLong(1));
                } else {
                    throw new SQLException("Creating order status failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

//    2. Поиск статуса заказа по идентификатору

//    private static final String FIND_BY_ID = "SELECT id_status, status_name FROM public.order_statuses " +
//            "WHERE id_status = ?;";

    public static final String FIND_BY_ID = SqlStatements.get("sql.OrderStatus.FIND_BY_ID");

    @Override
    public OrderStatus getById(Long id) {
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

//    3. Вывод всех статусов заказов, находящихся в базе данных

//    private static final String GET_ALL = "SELECT id_status, status_name FROM public.order_statuses;";

    public static final String GET_ALL = SqlStatements.get("sql.OrderStatus.GET_ALL");

    @Override
    public List<OrderStatus> getAll() {
        List<OrderStatus> list = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL)) {

            list = mapper(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return list;
    }

//    4. Изменение значений у существующего статуса заказа

//    private static final String UPDATE = "UPDATE public.order_statuses SET status_name=? WHERE id_status = ?;";

    public static final String UPDATE = SqlStatements.get("sql.OrderStatus.UPDATE");

    @Override
    public void update(OrderStatus orderStatus) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, orderStatus.getStatusName());
            ps.setLong(2, orderStatus.getIdStatus());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Updating order status failed, no rows affected.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

//    5. Удаление статуса заказа из базы данных

//    private static final String DELETE_BY_ID = "DELETE FROM public.order_statuses WHERE id_status = ?;";

    public static final String DELETE_BY_ID = SqlStatements.get("sql.OrderStatus.DELETE_BY_ID");
    @Override
    public void delete(OrderStatus orderStatus) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BY_ID)) {

            ps.setLong(1, orderStatus.getIdStatus());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Deleting order status failed, no rows deleted.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }


    private OrderStatus mapSingle(ResultSet rs) throws SQLException {
        OrderStatus status = new OrderStatus();
        status.setIdStatus(rs.getLong("id_status"));
        status.setStatusName(rs.getString("status_name"));
        return status;
    }

    private List<OrderStatus> mapper(ResultSet rs) throws SQLException {
        List<OrderStatus> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapSingle(rs));
        }
        return list;
    }
}
