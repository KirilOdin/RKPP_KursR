package ru.kafpin124.rkpp_kursr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private Long idItem;
    private Order order;
    private AnalysisTest test;
    private Specimen specimen;
    private String status; // "назначен", "в работе", "выполнен"
    private BigDecimal resultValue;
    private String resultText;
    private Boolean isAbnormal;
    private Employee enteredBy;
    private LocalDateTime entryDatetime;


    // Метод для получения строкового представления результата
    public String getResultAsString() {
        if (resultValue != null) return resultValue.stripTrailingZeros().toPlainString();
        if (resultText != null) return resultText;
        return "";
    }

}