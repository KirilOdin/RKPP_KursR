package ru.kafpin124.rkpp_kursr.dao.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.util.DBHelper;
import ru.kafpin124.rkpp_kursr.dao.SpecimenDao;
import ru.kafpin124.rkpp_kursr.model.Order;
import ru.kafpin124.rkpp_kursr.model.Specimen;
import ru.kafpin124.rkpp_kursr.util.SqlStatements;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecimenDaoImpl implements SpecimenDao {

    public static final Logger logger = LoggerFactory.getLogger(SpecimenDaoImpl.class);

//    1. Вставка новой биологической пробы

//    private static final String ADD =
//            "INSERT INTO public.specimens(order_id, specimen_type, container_type, collection_datetime, barcode) " +
//                    "VALUES (?, ?, ?, ?, ?);";

    private static final String ADD = SqlStatements.get("sql.Specimen.ADD");

    @Override
    public void add(Specimen specimen) {
        logger.debug("Добавление новой пробы с штрих-кодом: {}", specimen.getBarcode());
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
                    logger.info("Проба добавлена с ID: {} (штрих-код: {})", specimen.getIdSpecimen(), specimen.getBarcode());
                } else {
                    throw new SQLException("Creating specimen failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении пробы (штрих-код: {}): {}", specimen.getBarcode(), e.getMessage());
            throw new RuntimeException(e);
        }
    }


//    2. Поиск биологической пробы ао идентификатору

//    private static final String FIND_BY_ID = "SELECT id_specimen, order_id, specimen_type, container_type, " +
//            "collection_datetime, barcode FROM public.specimens WHERE id_specimen = ?;";

    private static final String FIND_BY_ID = SqlStatements.get("sql.Specimen.FIND_BY_ID");

    @Override
    public Specimen getById(Long id) {
        logger.debug("Поиск пробы по ID: {}", id);
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.info("Поиск пробы по ID '{}' завершён", id);
                    return mapSingle(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске пробы с ID {}: {}", id, e.getMessage());
            throw new RuntimeException(e);
        }
        logger.warn("Проба с ID {} не найдена", id);
        return null;
    }



//    3. Вывод всех биологических проб, находящихся в базе данных

//    private static final String GET_ALL = "SELECT id_specimen, order_id, specimen_type, container_type, " +
//            "collection_datetime, barcode FROM public.specimens;";

    private static final String GET_ALL = SqlStatements.get("sql.Specimen.GET_ALL");

    @Override
    public List<Specimen> getAll() {
        logger.debug("Получение всех проб, находящихся в базе данных...");
        List<Specimen> list = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL)) {
            list = mapper(rs);
            logger.info("Все пробы из базы данных получены");
        } catch (SQLException e) {
            logger.error("Ошибка при получении всех проб, хранящихся в базе данных: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return list;
    }




//    4. Изменение значений у существующей биологической пробы

//    private static final String UPDATE = "UPDATE public.specimens SET order_id=?, " +
//            "specimen_type=?, container_type=?, collection_datetime=?, barcode=? WHERE id_specimen = ?;";

    private static final String UPDATE = SqlStatements.get("sql.Specimen.UPDATE");

    @Override
    public void update(Specimen specimen) {
        logger.debug("Обновление пробы с ID: {}", specimen.getIdSpecimen());
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
            logger.info("Проба с ID {} обновлена", specimen.getIdSpecimen());
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении пробы с ID {}: {}", specimen.getIdSpecimen(), e.getMessage());
            throw new RuntimeException(e);
        }
    }


//    5. Удаление биологической пробы из базы данных

//    private static final String DELETE = "DELETE FROM public.specimens WHERE id_specimen = ?;";

    private static final String DELETE = SqlStatements.get("sql.Specimen.DELETE");

    @Override
    public void delete(Specimen specimen) {
        logger.debug("Удаление пробы с ID: {}", specimen.getIdSpecimen());
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setLong(1, specimen.getIdSpecimen());
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Deleting specimen failed, no rows deleted.");
            logger.info("Проба с ID {} удалена", specimen.getIdSpecimen());
        } catch (SQLException e) {
            logger.error("Ошибка при удалении пробы с ID {}: {}", specimen.getIdSpecimen(), e.getMessage());
            throw new RuntimeException(e);
        }
    }


//    6. Поиск биологической пробы по штрих-коду

//    private static final String FIND_BY_BARCODE = "SELECT id_specimen, order_id, specimen_type, container_type, " +
//            "collection_datetime, barcode FROM public.specimens WHERE barcode = ?;";

    private static final String FIND_BY_BARCODE = SqlStatements.get("sql.Specimen.FIND_BY_BARCODE");

    @Override
    public Specimen findByBarcode(String barcode) {
        logger.debug("Поиск пробы по штрих-коду: {}", barcode);
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_BARCODE)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.info("Поиск пробы по штрих-коду '{}' завершён", barcode);
                    return mapSingle(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске пробы по штрих-коду {}: {}", barcode, e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }


//    7. Поиск биологической пробы по идентификатору заказа

//    private static final String FIND_BY_ORDER_ID = "SELECT id_specimen, order_id, specimen_type, container_type, " +
//            "collection_datetime, barcode FROM public.specimens WHERE order_id = ?;";

    private static final String FIND_BY_ORDER_ID = SqlStatements.get("sql.Specimen.FIND_BY_ORDER_ID");

    @Override
    public List<Specimen> findByOrderId(Long orderId) {
        logger.debug("Поиск проб по ID заказа: {}", orderId);
        List<Specimen> list = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ORDER_ID)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                list = mapper(rs);
                logger.debug("Найдено {} проб для заказа с ID {}", list.size(), orderId);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске проб для заказа с ID {}: {}", orderId, e.getMessage());
            throw new RuntimeException(e);
        }
        return list;
    }


    private Specimen mapSingle(ResultSet rs){
        Specimen specimen = new Specimen();
        try {
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
        } catch (SQLException e) {
            logger.error("Ошибка при работе single-маппера: '{}'", e.getMessage());
            throw new RuntimeException(e);
        }
        return specimen;
    }

    private List<Specimen> mapper(ResultSet rs){
        List<Specimen> list = new ArrayList<>();
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
