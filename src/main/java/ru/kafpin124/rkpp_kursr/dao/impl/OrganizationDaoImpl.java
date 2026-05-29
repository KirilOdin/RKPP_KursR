package ru.kafpin124.rkpp_kursr.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.dao.OrganizationDao;
import ru.kafpin124.rkpp_kursr.model.Organization;
import ru.kafpin124.rkpp_kursr.util.DBHelper;
import ru.kafpin124.rkpp_kursr.util.SqlStatements;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrganizationDaoImpl implements OrganizationDao {

    public static final Logger logger = LoggerFactory.getLogger(OrganizationDaoImpl.class);

//    1. Вставка новой организации

//    private static final String ADD =
//            "INSERT INTO public.organizations(org_name, contract_number, " +
//                    "contact_last_name, contact_first_name, contact_middle_name, contact_person_phone) " +
//                    "VALUES (?, ?, ?, ?, ?, ?);";

    public static final String ADD = SqlStatements.get("sql.Organization.ADD");

    @Override
    public void add(Organization organization) {
        logger.debug("Добавление новой организации: {}", organization.getOrgName());
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(ADD, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, organization.getOrgName());
            ps.setString(2, organization.getContractNumber());
            ps.setString(3, organization.getContactLastName());
            ps.setString(4, organization.getContactFirstName());
            ps.setString(5, organization.getContactMiddleName());
            ps.setString(6, organization.getContactPersonPhone());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Creating organization failed, no rows affected.");
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    organization.setIdOrg(keys.getLong(1));
                    logger.info("Организация '{}' добавлена с ID {}", organization.getOrgName(), organization.getIdOrg());
                } else {
                    throw new SQLException("Creating organization failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении организации '{}': {}", organization.getOrgName(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

//    2. Поиск организации по идентификатору

//    private static final String FIND_BY_ID =
//            "SELECT id_org, org_name, contract_number, contact_last_name, " +
//                    "contact_first_name, contact_middle_name, contact_person_phone " +
//                    "FROM public.organizations WHERE id_org = ?;";

    public static final String FIND_BY_ID = SqlStatements.get("sql.Organization.FIND_BY_ID");

    @Override
    public Organization getById(Long id) {
        logger.debug("Поиск организации по ID: {}", id);
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.info("Поиск организации по ID '{}' завершён", id);
                    return mapSingle(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске организации с ID {}: {}", id, e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

//    3. Вывод всех организаций, находящихся в базе данных

//    private static final String GET_ALL =
//            "SELECT id_org, org_name, contract_number, contact_last_name, " +
//                    "contact_first_name, contact_middle_name, contact_person_phone " +
//                    "FROM public.organizations;";

    public static final String GET_ALL = SqlStatements.get("sql.Organization.GET_ALL");

    @Override
    public List<Organization> getAll() {
        logger.debug("Получение всех организаций, находящихся в базе данных...");
        List<Organization> list = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL)) {
            list = mapper(rs);
            logger.info("Все организации из базы данных получены");
        } catch (SQLException e) {
            logger.error("Ошибка при получении всех организаций, хранящихся в базе данных: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return list;
    }

//    4. Изменение значений у существующей компании

//    private static final String UPDATE =
//            "UPDATE public.organizations SET org_name=?, contract_number=?, " +
//                    "contact_last_name=?, contact_first_name=?, contact_middle_name=?, " +
//                    "contact_person_phone=? WHERE id_org = ?;";

    public static final String UPDATE = SqlStatements.get("sql.Organization.UPDATE");

    @Override
    public void update(Organization organization) {
        logger.debug("Обновление организации с ID {}", organization.getIdOrg());
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, organization.getOrgName());
            ps.setString(2, organization.getContractNumber());
            ps.setString(3, organization.getContactLastName());
            ps.setString(4, organization.getContactFirstName());
            ps.setString(5, organization.getContactMiddleName());
            ps.setString(6, organization.getContactPersonPhone());
            ps.setLong(7, organization.getIdOrg());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Updating organization failed, no rows affected.");
            logger.info("Организация с ID {} обновлена", organization.getIdOrg());
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении организации с ID {}: {}", organization.getIdOrg(), e.getMessage());
            throw new RuntimeException(e);
        }
    }


//    5. Удаление организации из базы данных

//    private static final String DELETE_BY_ID =
//            "DELETE FROM public.organizations WHERE id_org = ?;";

    public static final String DELETE_BY_ID = SqlStatements.get("sql.Organization.DELETE_BY_ID");

    @Override
    public void delete(Organization organization) {
        logger.debug("Удаление организации с ID {}", organization.getIdOrg());
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BY_ID)) {
            ps.setLong(1, organization.getIdOrg());
            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Deleting organization failed, no rows deleted.");
            logger.info("Организация с ID {} удалена", organization.getIdOrg());
        } catch (SQLException e) {
            logger.error("Ошибка при удалении организации с ID {}: {}", organization.getIdOrg(), e.getMessage());
            throw new RuntimeException(e);
        }
    }


//    6. Поиск организации по названию (ILIKE)

//    private static final String SEARCH_BY_NAME =
//            "SELECT id_org, org_name, contract_number, contact_last_name, " +
//                    "contact_first_name, contact_middle_name, contact_person_phone " +
//                    "FROM public.organizations WHERE org_name ILIKE ?;";

    public static final String SEARCH_BY_NAME = SqlStatements.get("sql.Organization.SEARCH_BY_NAME");

    @Override
    public List<Organization> searchByName(String pattern) {
        logger.debug("Поиск организаций по названию: {}", pattern);
        List<Organization> list = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(SEARCH_BY_NAME)) {
            ps.setString(1, "%" + pattern + "%");
            try (ResultSet rs = ps.executeQuery()) {
                list = mapper(rs);
                logger.debug("Найдено {} организаций по запросу '{}'", list.size(), pattern);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при поиске организаций по названию '{}': {}", pattern, e.getMessage());
            throw new RuntimeException(e);
        }
        return list;
    }


    private Organization mapSingle(ResultSet rs){
        Organization org = new Organization();
        try {
            org.setIdOrg(rs.getLong("id_org"));
            org.setOrgName(rs.getString("org_name"));
            org.setContractNumber(rs.getString("contract_number"));
            org.setContactLastName(rs.getString("contact_last_name"));
            org.setContactFirstName(rs.getString("contact_first_name"));
            org.setContactMiddleName(rs.getString("contact_middle_name"));
            org.setContactPersonPhone(rs.getString("contact_person_phone"));
        } catch (SQLException e) {
            logger.error("Ошибка при работе single-маппера: '{}'", e.getMessage());
            throw new RuntimeException(e);
        }
        return org;
    }

    private List<Organization> mapper(ResultSet rs){
        List<Organization> list = new ArrayList<>();
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