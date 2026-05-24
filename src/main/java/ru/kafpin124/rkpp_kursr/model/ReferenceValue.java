package ru.kafpin124.rkpp_kursr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceValue {
    private Long idReference;
    private Character genderApplicable;
    private Integer ageMin;
    private Integer ageMax;
    private BigDecimal refValueMin;
    private BigDecimal refValueMax;
    private String refText;

    private AnalysisTest analysisTest;


//    public ReferenceValue() {}


    public AnalysisTest getTest() { return analysisTest; }
    public void setTest(AnalysisTest analysisTest) { this.analysisTest = analysisTest; }

}