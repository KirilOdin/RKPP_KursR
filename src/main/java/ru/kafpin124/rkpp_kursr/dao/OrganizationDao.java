package ru.kafpin124.rkpp_kursr.dao;


import ru.kafpin124.rkpp_kursr.model.Organization;

import java.util.List;

/**
 * The interface Organization dao to work with the organizations table.
 * Provides CRUD operations and search methods.
 *
 */
public interface OrganizationDao {
    /**
     * Add a new organization.
     *
     * @param organization the organization object (id will be assigned automatically)
     */
    void add(Organization organization);

    /**
     * Gets an organization by ID.
     *
     * @param id the organization's id (primary key)
     * @return the organization object of null, if not found
     */
    Organization getById(Long id);

    /**
     * Gets a list of all organizations.
     *
     * @return the list of all organizations
     */
    List<Organization> getAll();

    /**
     * Update the organization data (all fields).
     *
     * @param organization the organization with a filled-in id and new values
     */
    void update(Organization organization);

    /**
     * Delete the organization by object.
     *
     * @param organization the organization object
     */
    void delete(Organization organization);

    /**
     * Search for organizations by substring in the name.
     *
     * @param pattern the part of the name
     * @return the list of suitable organizations
     */
    List<Organization> searchByName(String pattern);

}
