package ru.kafpin124.rkpp_kursr.dao;


import ru.kafpin124.rkpp_kursr.model.ReferenceValue;

import java.util.List;

/**
 * The interface Reference value dao to work with the reference_values table (reference values of analyses).
 * Provides CRUD operations and search methods.
 */
public interface ReferenceValueDao {
    /**
     * Add a new reference value.
     *
     * @param referenceValue the reference value (id will be assigned automatically)
     */
    void add(ReferenceValue referenceValue);

    /**
     * Gets a reference value by ID.
     *
     * @param id the reference value's id (primary key)
     * @return the reference value object or null, if not found
     */
    ReferenceValue getById(Long id);

    /**
     * Gets a list of all reference values.
     *
     * @return the list of all reference values
     */
    List<ReferenceValue> getAll();

    /**
     * Update the reference value data (all fields).
     *
     * @param referenceValue the reference value with a filled-in id and new values
     */
    void update(ReferenceValue referenceValue);

    /**
     * Delete the reference value by object.
     *
     * @param referenceValue the reference value object
     */
    void delete(ReferenceValue referenceValue);

    /**
     * Find all the reference values for a given test.
     *
     * @param testId the test id
     * @return the list of reference values
     */
    List<ReferenceValue> findByTestId(Long testId);

    /**
     * Find a suitable reference value for the test, gender, and age.
     *
     * @param testId the test id
     * @param gender the patient's gender ('м' or 'ж')
     * @param age    the patient's age (full years)
     * @return the most appropriate reference value or null
     */
    ReferenceValue findByTestAndGenderAndAge(Long testId, char gender, int age);
    //Для поиска в справочнике в фоне?

}
