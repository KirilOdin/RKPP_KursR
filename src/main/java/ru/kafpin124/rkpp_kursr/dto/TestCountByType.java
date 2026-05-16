package ru.kafpin124.rkpp_kursr.dto;

public class TestCountByType {
    private final String testName;
    private final long count;

    public TestCountByType(String testName, long count) {
        this.testName = testName;
        this.count = count;
    }

    public String getTestName() { return testName; }
    public long getCount() { return count; }
}


