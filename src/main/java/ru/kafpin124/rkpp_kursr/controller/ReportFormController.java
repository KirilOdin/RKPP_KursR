package ru.kafpin124.rkpp_kursr.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.print.PrinterJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.model.*;

import java.time.format.DateTimeFormatter;

public class ReportFormController {

    @FXML private TextArea reportArea;

    private Order order;


    public static final Logger logger = LoggerFactory.getLogger(ReportFormController.class);

    public void setOrder(Order order) {
        this.order = order;
        logger.info("Установлен заказ ID={} для формирования бланка", order != null ? order.getIdOrder() : null);
        generateReport();
    }

    private void generateReport() {
        if (order == null) {
            logger.warn("Попытка генерации бланка без заказа");
            reportArea.setText("Нет данных для отображения");
            return;
        }
        logger.debug("Генерация бланка для заказа ID={}", order.getIdOrder());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("   МЕДИЦИНСКАЯ ЛАБОРАТОРИЯ\n");
        sb.append("========================================\n");
        sb.append("Пациент: ").append(order.getPatient().getLastName()).append(" ")
                .append(order.getPatient().getFirstName()).append(" ")
                .append(order.getPatient().getMiddleName() != null ? order.getPatient().getMiddleName() : "").append("\n");
        sb.append("Дата рождения: ").append(order.getPatient().getBirthDate().format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n");
        sb.append("Пол: ").append(order.getPatient().getGender()).append("\n");
        if (order.getOrganization() != null) {
            sb.append("Организация: ").append(order.getOrganization().getOrgName()).append("\n");
        }
        sb.append("----------------------------------------\n");
        sb.append("Результаты анализов (заказ №").append(order.getIdOrder()).append("):\n");
        sb.append("Статус: ").append(order.getStatus().getStatusName()).append("\n");
        sb.append("Дата регистрации: ").append(order.getRegistrationDatetime().format(dtf)).append("\n\n");

        sb.append(String.format("%-5s %-25s %-10s %-10s %-10s\n", "№", "Наименование", "Результат", "Норма", "Откл."));
        sb.append("-------------------------------------------------------------\n");

        int i = 1;
        int itemCount = 0;
        if (order.getOrderItems() != null) {
            itemCount = order.getOrderItems().size();
            for (OrderItem item : order.getOrderItems()) {
                String testName = item.getTest().getTestName();
                String result = item.getResultValue() != null ? item.getResultValue().stripTrailingZeros().toPlainString()
                        : (item.getResultText() != null ? item.getResultText() : "");
                // Формирование строки нормы
                String norm = "";
                norm = "--"; // упрощённо, без ReferenceValue
                String abnormal = item.getIsAbnormal() != null && item.getIsAbnormal() ? "*" : "";
                sb.append(String.format("%-5d %-25s %-10s %-10s %-10s\n", i++, testName, result, norm, abnormal));
            }
        }

        sb.append("-------------------------------------------------------------\n");
        if (order.getVerifiedBy() != null) {
            sb.append("Врач, утвердивший результаты: ").append(order.getVerifiedBy().getLastName())
                    .append(" ").append(order.getVerifiedBy().getFirstName()).append("\n");
        }
        sb.append("Дата утверждения: ").append(order.getVerificationDatetime() != null
                ? order.getVerificationDatetime().format(dtf) : "не утверждено").append("\n");
        sb.append("========================================\n");

        reportArea.setText(sb.toString());
        logger.info("Бланк для заказа ID={} сгенерирован, количество позиций: {}", order.getIdOrder(), itemCount);
    }

    @FXML
    void onPrint() {
        logger.info("Запрос на печать бланка для заказа ID={}", order != null ? order.getIdOrder() : null);
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) {
            logger.debug("Диалог печати подтверждён, отправка на печать");
            boolean success = job.printPage(reportArea.lookup("TextArea"));
            if (success) {
                job.endJob();
                logger.info("Бланк успешно отправлен на печать");
            } else {
                logger.warn("Не удалось напечатать бланк");
            }
        } else {
            logger.warn("Печать отменена пользователем или принтер не доступен");
        }
    }

    @FXML
    void onCancel() {
        logger.debug("Закрытие окна печати");
        reportArea.getScene().getWindow().hide();
    }
}