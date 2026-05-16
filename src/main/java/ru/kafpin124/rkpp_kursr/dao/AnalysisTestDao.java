package ru.kafpin124.rkpp_kursr.dao;


import ru.kafpin124.rkpp_kursr.model.AnalysisTest;

import java.util.List;

public interface AnalysisTestDao {
    // CRUD-операции
    void add(AnalysisTest analysisTest);
    AnalysisTest findById(Long id);
    List<AnalysisTest> getAll();
    void update(AnalysisTest analysisTest);
    void delete(AnalysisTest analysisTest);
    void deleteById(Long id);
    //TODO: Дополнительные методы в интерфейсе "Анализы"
    List<AnalysisTest> findByBiomaterial(String biomaterial);
    List<AnalysisTest> searchByName(String testName);
}
