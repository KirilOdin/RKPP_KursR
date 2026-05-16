package ru.kafpin124.rkpp_kursr.dao;


import ru.kafpin124.rkpp_kursr.model.Specimen;

import java.util.List;

public interface SpecimenDao {
    // CRUD-операции
    void add(Specimen specimen);
    Specimen getById(Long id);
    List<Specimen> getAll();
    void update(Specimen specimen);
    void delete(Specimen specimen);

    //TODO: Реализовать дополнительные операции для Биологической пробы
    // Поиск по штрих-коду -> в Specimen
    Specimen findByBarcode(String barcode);
    List<Specimen> findByOrderId(Long orderId);
}
