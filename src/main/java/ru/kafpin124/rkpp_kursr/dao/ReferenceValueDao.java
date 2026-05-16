package ru.kafpin124.rkpp_kursr.dao;


import ru.kafpin124.rkpp_kursr.model.ReferenceValue;

import java.util.List;

public interface ReferenceValueDao {
    // CRUD-операции
    void add(ReferenceValue referenceValue);
    ReferenceValue getById(Long id);
    List<ReferenceValue> getAll();
    void update(ReferenceValue referenceValue);
    void delete(ReferenceValue referenceValue);

    //TODO: Реализовать дополнительные операции для референсных значений
    // Нужно ли?..

    List<ReferenceValue> findByTestId(Long testId);
    ReferenceValue findByTestAndGenderAndAge(Long testId, char gender, int age);
    //Для поиска в справочнике в фоне?

}
