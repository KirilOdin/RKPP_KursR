package ru.kafpin124.rkpp_kursr.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
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




//    private LocalDateTime approvalDatetime;
//    private Employee approvedBy;   // врач, утвердивший результат
    /*CREATE TABLE order_items (
    id_item INTEGER PRIMARY KEY DEFAULT nextval('order_items_seq'),
    order_id INT NOT NULL REFERENCES orders(id_order),
    test_id INT NOT NULL REFERENCES tests(id_test),
    specimen_id INT NOT NULL REFERENCES specimens(id_specimen),
    status VARCHAR(20) NOT NULL DEFAULT 'назначен'
        CHECK (status IN ('назначен', 'в работе', 'выполнен')),
    result_value NUMERIC(10,3) NULL,
    result_text TEXT NULL,
    is_abnormal BOOLEAN NULL,
    entered_by INT NULL REFERENCES employees(id_employee),
    entry_datetime TIMESTAMP NULL,
    CONSTRAINT one_result_type CHECK (
        (result_value IS NULL AND result_text IS NULL) OR
        (result_value IS NOT NULL OR result_text IS NOT NULL)
    )
);
*/


}