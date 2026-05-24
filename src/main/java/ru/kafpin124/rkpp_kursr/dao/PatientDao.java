package ru.kafpin124.rkpp_kursr.dao;


import ru.kafpin124.rkpp_kursr.model.Patient;

import java.util.List;

/**
 * The interface Patient dao.
 * Provides CRUD operations and search methods.
 */
public interface PatientDao {
    /**
     * Add a new patient.
     *
     * @param patient the patient object (id will be assigned automatically)
     */
    void add(Patient patient);

    /**
     * Find a patient by ID.
     *
     * @param id the patient's id (primary key)
     * @return the patient object or null, if not found
     */
    Patient findById(Long id);

    /**
     * Gets a list of all patients.
     *
     * @return the list of all patients
     */
    List<Patient> getAll();

    /**
     * Update the patient data (all fields).
     *
     * @param patient the patient with a filled-in id and new values
     */
    void update(Patient patient);

    /**
     * Delete the patient by object.
     *
     * @param patient the patient object
     */
    void delete(Patient patient);

    /**
     * Find the patient by policy number (unique field).
     *
     * @param policyNumber the policy number
     * @return the patient object or null, if not found
     */
    Patient findByPolicyNumber(String policyNumber);

    /**
     * Search for patients by substring in full name.
     *
     * @param pattern the search string (may contain part of the last name, first name, or middle name)
     * @return the list of eligible patients
     */
    List<Patient> searchByFullName(String pattern); // "Иванов", "Иван", "Иванов Иван"

//    List<Patient> searchByFullName(String lastName, String firstName, String middleName);

//    List<Patient> searchByBirthDateRange(LocalDateTime startDate, LocalDateTime endDate); //для составления статистики?

}
