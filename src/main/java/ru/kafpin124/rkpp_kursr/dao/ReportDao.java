package ru.kafpin124.rkpp_kursr.dao;


import ru.kafpin124.rkpp_kursr.dto.EmployeeStatistic;
import ru.kafpin124.rkpp_kursr.dto.OrganizationStatistic;
import ru.kafpin124.rkpp_kursr.dto.TestCountByType;

import java.time.LocalDate;
import java.util.List;

/**
 * The interface Report dao to receive statistical reports.
 * Uses DTO to transfer aggregated data.
 */
public interface ReportDao {

    /**
     * Gets the number of analyses performed for each type (test) during the period.
     *
     * @param from the start date
     * @param to   the end date
     * @return the list of DTOs with the test name and quantity
     */
    List<TestCountByType> getTestCountByType(LocalDate from, LocalDate to);

    /**
     * Gets employee workload (number of entered results) for the period.
     *
     * @param from the start date
     * @param to   the end date
     * @return the list of DTOs with the employee's full name and the number of tests
     */
    List<EmployeeStatistic> getWorkloadByEmployee(LocalDate from, LocalDate to);

    /**
     * Gets revenue for organizations for the period (the sum of the prices of the performed analyses).
     *
     * @param from the start date
     * @param to   the end date
     * @return the list of DTOs with the name of the organization and the amount
     */
    List<OrganizationStatistic> getRevenueByOrganization(LocalDate from, LocalDate to);


}
