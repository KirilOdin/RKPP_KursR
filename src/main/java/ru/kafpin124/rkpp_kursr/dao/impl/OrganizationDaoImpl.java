package ru.kafpin124.rkpp_kursr.dao.impl;


import ru.kafpin124.rkpp_kursr.dao.OrganizationDao;
import ru.kafpin124.rkpp_kursr.model.Organization;

import java.util.List;

public class OrganizationDaoImpl implements OrganizationDao {

    //TODO: Сделать мапперы + реализовать методы!


    private static final String ADD = "INSERT INTO public.organizations(org_name, contract_number, " +
            "contact_last_name, contact_first_name, contact_middle_name, contact_person_phone) " +
            "VALUES (?, ?, ?, ?, ?, ?);";

    private static final String GET_ALL = "SELECT id_org, org_name, contract_number, " +
            "contact_last_name, contact_first_name, contact_middle_name, contact_person_phone " +
            "FROM public.organizations;";


    private static final String FIND_BY_ID = "SELECT id_org, org_name, contract_number, " +
            "contact_last_name, contact_first_name, contact_middle_name, contact_person_phone " +
            "FROM public.organizations WHERE id_org = ?;";


    private static final String DELETE_BY_ID = "DELETE FROM public.organizations WHERE id_org = ?;";


    private static final String UPDATE = "UPDATE public.organizations SET org_name=?, contract_number=?, " +
            "contact_last_name=?, contact_first_name=?, contact_middle_name=?, contact_person_phone=? " +
            "WHERE id_org = ?;";


    private static final String SEARCH_BY_NAME = "SELECT id_org, org_name, contract_number, " +
            "contact_last_name, contact_first_name, contact_middle_name, contact_person_phone " +
            "FROM public.organizations WHERE org_name ILIKE ?;";


//    1.

    @Override
    public void add(Organization organization) {

    }

//    2.

    @Override
    public Organization getById(Long id) {
        return null;
    }

//    3.

    @Override
    public List<Organization> getAll() {
        return List.of();
    }

//    4.

    @Override
    public void update(Organization organization) {

    }

//    5.

    @Override
    public void delete(Organization organization) {

    }

//    6.

    @Override
    public List<Organization> searchByName(String pattern) {
        return List.of();
    }
}
