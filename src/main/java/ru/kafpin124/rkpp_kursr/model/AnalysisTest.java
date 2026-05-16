package ru.kafpin124.rkpp_kursr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
// public.tests
public class AnalysisTest {
    private Long idTest;
    private String testName;
    private String biomaterial;
    private Integer executionTimeHours;
    private BigDecimal price;
    private String unit;
    private List<ReferenceValue> referenceValues = new ArrayList<>();
    private List<OrderItem> orderItems = new ArrayList<>();


//    public AnalysisTest() {
//        this.referenceValues = new ArrayList<>();
//        this.orderItems = new ArrayList<>();}
}