package ru.kafpin124.rkpp_kursr.dao;


import ru.kafpin124.rkpp_kursr.model.Organization;

import java.util.List;

public interface OrganizationDao {
    // CRUD-операции
    void add(Organization organization);
    Organization getById(Long id);
    List<Organization> getAll();
    void update(Organization organization);
    void delete(Organization organization);

    //TODO: Реализовать дополнительные операции для организации (поиск по названию?)
    List<Organization> searchByName(String pattern); //попытка дополнить запрос?
}
