package ru.kafpin124.rkpp_kursr.dao;


import ru.kafpin124.rkpp_kursr.model.AnalysisTest;

import java.util.List;

/**
 * The interface Analysis test dao to work with the tests table (analysis reference).
 * Provides CRUD operations and search methods.
 */
public interface AnalysisTestDao {
    /**
     * Add a new analysis to the database.
     *
     * @param analysisTest the analysisTest object (without id, id will be generated)
     */
    void add(AnalysisTest analysisTest);

    /**
     * Find analysis by ID.
     *
     * @param id the analysis id (primary key)
     * @return the analysis or null, if not found
     */
    AnalysisTest findById(Long id);

    /**
     * Gets a list of all analyses.
     *
     * @return the list of all analyses
     */
    List<AnalysisTest> getAll();

    /**
     * Update the analysis data (all fields, including name, biomaterial, price, etc.).
     *
     * @param analysisTest the analysisTest object with filled-in id and new values
     */
    void update(AnalysisTest analysisTest);

    /**
     * Delete the analysis by object (uses the id).
     *
     * @param analysisTest the analysisTest object
     */
    void delete(AnalysisTest analysisTest);

    /**
     * Delete the analysis by ID.
     *
     * @param id the analysis id
     */
    void deleteById(Long id);

    /**
     * Find the analyses corresponding to a given biomaterial.
     *
     * @param biomaterial the biomaterial string, for example, "blood", "urine"
     * @return the list of analyses
     */
    List<AnalysisTest> findByBiomaterial(String biomaterial);

    /**
     * Search for analyses by substring in the name (ILIKE).
     *
     * @param testName the part of the name of the analysis
     * @return the list of suitable analyses
     */
    List<AnalysisTest> searchByName(String testName);
}
