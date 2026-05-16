package ru.kafpin124.rkpp_kursr.dao;


import ru.kafpin124.rkpp_kursr.model.Patient;

import java.util.List;

public interface PatientDao {
    // CRUD-операции
    void add(Patient patient);
    Patient findById(Long id);
    List<Patient> getAll();
    void update(Patient patient);
    void delete(Patient patient);

    //TODO: Реализовать дополнительные операции для пациента (найти по полису ОМС)
    Patient findByPolicyNumber(String policyNumber);
    List<Patient> searchByFullName(String pattern); // "Иванов", "Иван", "Иванов Иван"

//    List<Patient> searchByFullName(String lastName, String firstName, String middleName);

//    List<Patient> searchByBirthDateRange(LocalDateTime startDate, LocalDateTime endDate); //для составления статистики?

}
