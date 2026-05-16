package ru.kafpin124.rkpp_kursr.dao;


import ru.kafpin124.rkpp_kursr.dto.EmployeeStatistic;
import ru.kafpin124.rkpp_kursr.dto.OrganizationStatistic;
import ru.kafpin124.rkpp_kursr.dto.TestCountByType;

import java.time.LocalDate;
import java.util.List;

public interface ReportDao {

    List<TestCountByType> getTestCountByType(LocalDate from, LocalDate to);
    List<EmployeeStatistic> getWorkloadByEmployee(LocalDate from, LocalDate to);
    List<OrganizationStatistic> getRevenueByOrganization(LocalDate from, LocalDate to);

//     Отчёты – это выборки с агрегацией. Нужны методы типа getTestCountByType(...), getEmployeeWorkload(...)

}
