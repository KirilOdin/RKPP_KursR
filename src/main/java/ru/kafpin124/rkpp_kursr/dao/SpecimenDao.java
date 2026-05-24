package ru.kafpin124.rkpp_kursr.dao;


import ru.kafpin124.rkpp_kursr.model.Specimen;

import java.util.List;

/**
 * The interface Specimen dao to work with the specimens table.
 * Provides CRUD operations and search methods.
 */
public interface SpecimenDao {
    /**
     * Add a new specimen.
     *
     * @param specimen the specimen (id will be assigned automatically)
     */
    void add(Specimen specimen);

    /**
     * Gets a specimen by ID.
     *
     * @param id the specimen's ID (primary key)
     * @return the specimen object or null, if not found
     */
    Specimen getById(Long id);

    /**
     * Gets a list of all specimens.
     *
     * @return the list of all specimens
     */
    List<Specimen> getAll();

    /**
     * Update the specimen data (all fields).
     *
     * @param specimen the specimen with a filled-in id and new values
     */
    void update(Specimen specimen);

    /**
     * Delete the specimen by object
     *
     * @param specimen the specimen object
     */
    void delete(Specimen specimen);

    /**
     * Find the specimen by unique barcode.
     *
     * @param barcode the barcode
     * @return the specimen or null, if not found
     */
    Specimen findByBarcode(String barcode);

    /**
     * Find all specimens related to the specified order by order id .
     *
     * @param orderId the order id
     * @return the list or specimens
     */
    List<Specimen> findByOrderId(Long orderId);
}
