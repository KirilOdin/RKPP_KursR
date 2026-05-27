package ru.kafpin124.rkpp_kursr.dao.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.util.DBHelper;
import ru.kafpin124.rkpp_kursr.dao.SpecimenDao;
import ru.kafpin124.rkpp_kursr.model.Order;
import ru.kafpin124.rkpp_kursr.model.Specimen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecimenDaoImpl implements SpecimenDao {

    //TODO: Добавить логирование!

    public static final Logger logger = LoggerFactory.getLogger(SpecimenDaoImpl.class);

//    1. Вставка новой биологической пробы

    private static final String ADD =
            "INSERT INTO public.specimens(order_id, specimen_type, container_type, collection_datetime, barcode) " +
                    "VALUES (?, ?, ?, ?, ?);";
    @Override
    public void add(Specimen specimen) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(ADD, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, specimen.getOrder().getIdOrder());
            ps.setString(2, specimen.getSpecimenType());
            ps.setString(3, specimen.getContainerType());
            ps.setTimestamp(4, Timestamp.valueOf(specimen.getCollectionDatetime()));
            ps.setString(5, specimen.getBarcode());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Creating specimen failed, no rows affected.");
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    specimen.setIdSpecimen(keys.getLong(1));
                } else {
                    throw new SQLException("Creating specimen failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }


//    2. Поиск биологической пробы ао идентификатору

    private static final String FIND_BY_ID = "SELECT id_specimen, order_id, specimen_type, container_type, " +
            "collection_datetime, barcode FROM public.specimens WHERE id_specimen = ?;";

    @Override
    public Specimen getById(Long id) {
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



//    3. Вывод всех биологических проб, находящихся в базе данных

    private static final String GET_ALL = "SELECT id_specimen, order_id, specimen_type, container_type, " +
            "collection_datetime, barcode FROM public.specimens;";

    @Override
    public List<Specimen> getAll() {
        List<Specimen> list = new ArrayList<>();
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




//    4. Изменение значений у существующей биологической пробы

    private static final String UPDATE = "UPDATE public.specimens SET order_id=?, " +
            "specimen_type=?, container_type=?, collection_datetime=?, barcode=? WHERE id_specimen = ?;";

    @Override
    public void update(Specimen specimen) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setLong(1, specimen.getOrder().getIdOrder());
            ps.setString(2, specimen.getSpecimenType());
            ps.setString(3, specimen.getContainerType());
            ps.setTimestamp(4, Timestamp.valueOf(specimen.getCollectionDatetime()));
            ps.setString(5, specimen.getBarcode());
            ps.setLong(6, specimen.getIdSpecimen());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Updating specimen failed, no rows affected.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }


//    5. Удаление биологической пробы из базы данных

    private static final String DELETE = "DELETE FROM public.specimens WHERE id_specimen = ?;";

    @Override
    public void delete(Specimen specimen) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setLong(1, specimen.getIdSpecimen());
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Deleting specimen failed, no rows deleted.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }


//    6. Поиск биологической пробы по штрих-коду

    private static final String FIND_BY_BARCODE = "SELECT id_specimen, order_id, specimen_type, container_type, " +
            "collection_datetime, barcode FROM public.specimens WHERE barcode = ?;";

    @Override
    public Specimen findByBarcode(String barcode) {
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_BARCODE)) {
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


//    7. Поиск биологической пробы по идентификатору заказа

    private static final String FIND_BY_ORDER_ID = "SELECT id_specimen, order_id, specimen_type, container_type, " +
            "collection_datetime, barcode FROM public.specimens WHERE order_id = ?;";

    @Override
    public List<Specimen> findByOrderId(Long orderId) {
        List<Specimen> list = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ORDER_ID)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                list = mapper(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return list;
    }


    private Specimen mapSingle(ResultSet rs) throws SQLException {
        Specimen specimen = new Specimen();
        specimen.setIdSpecimen(rs.getLong("id_specimen"));

        Order order = new Order();
        order.setIdOrder(rs.getLong("order_id"));
        specimen.setOrder(order);

        specimen.setSpecimenType(rs.getString("specimen_type"));
        specimen.setContainerType(rs.getString("container_type"));

        Timestamp ts = rs.getTimestamp("collection_datetime");
        if (ts != null) {
            specimen.setCollectionDatetime(ts.toLocalDateTime());
        }
        specimen.setBarcode(rs.getString("barcode"));
        return specimen;
    }

    private List<Specimen> mapper(ResultSet rs) throws SQLException {
        List<Specimen> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapSingle(rs));
        }
        return list;
    }
}
