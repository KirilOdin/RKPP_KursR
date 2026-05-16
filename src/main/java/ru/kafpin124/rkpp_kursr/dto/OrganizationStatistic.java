package ru.kafpin124.rkpp_kursr.dto;

import java.math.BigDecimal;

public class OrganizationStatistic {
    private final String orgName;
    private final BigDecimal totalRevenue;

    public OrganizationStatistic(String orgName, BigDecimal totalRevenue) {
        this.orgName = orgName;
        this.totalRevenue = totalRevenue;
    }

    public String getOrgName() { return orgName; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
}
