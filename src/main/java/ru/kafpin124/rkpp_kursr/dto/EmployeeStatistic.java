package ru.kafpin124.rkpp_kursr.dto;

public class EmployeeStatistic {
    private final String employeeFullName;
    private final long testsCount;

    public EmployeeStatistic(String employeeFullName, long testsCount) {
        this.employeeFullName = employeeFullName;
        this.testsCount = testsCount;
    }

    public String getEmployeeFullName() { return employeeFullName; }
    public long getTestsCount() { return testsCount; }
}
