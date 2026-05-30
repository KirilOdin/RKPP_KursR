package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.print.PrinterJob;
import javafx.stage.FileChooser;
import java.io.*;
import java.nio.charset.StandardCharsets;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.dao.ReportDao;
import ru.kafpin124.rkpp_kursr.dao.impl.ReportDaoImpl;
import ru.kafpin124.rkpp_kursr.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

//@NoArgsConstructor(force = true)
public class ReportsController {

    @FXML private DatePicker reportDateFrom;
    @FXML private DatePicker reportDateTo;
    @FXML private TableView  reportTable;
    @FXML private ComboBox<String> reportTypeCombo;

    private final ReportDao reportDao;


    public static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    public ReportsController(ReportDao reportDao) {
        this.reportDao = reportDao;
        logger.debug("ReportsController создан");
    }

    @FXML
    void initialize() {
        logger.info("Инициализация ReportsController");
        reportTypeCombo.setItems(FXCollections.observableArrayList(
                "Количество анализов по видам",
                "Нагрузка сотрудников",
                "Выручка по организациям"
        ));
        reportTypeCombo.setValue("Количество анализов по видам");
        logger.debug("Типы отчётов загружены, выбран тип по умолчанию");
    }

    @FXML
    void onGenerateReport() {
        String type = reportTypeCombo.getValue();
        LocalDate from = reportDateFrom.getValue();
        LocalDate to = reportDateTo.getValue();

        if (from == null || to == null) {
            logger.warn("Попытка генерации отчёта без выбранного периода");
            showAlert("Выберите период");
            return;
        }

        logger.info("Генерация отчёта типа '{}' за период с {} по {}", type, from, to);
        reportTable.getColumns().clear();
        reportTable.getItems().clear();

        switch (type) {
            case "Количество анализов по видам":
                showTestCountByType(from, to);
                break;
            case "Нагрузка сотрудников":
                showEmployeeWorkload(from, to);
                break;
            case "Выручка по организациям":
                showRevenueByOrganization(from, to);
                break;
            default:
                logger.warn("Неизвестный тип отчёта: {}", type);
        }
    }

    private void showTestCountByType(LocalDate from, LocalDate to) {
        logger.debug("Загрузка данных о количестве анализов по видам");
        List<TestCountByType> data = reportDao.getTestCountByType(from, to);
        TableColumn nameCol = new TableColumn("Название теста");
        nameCol.setCellValueFactory(new PropertyValueFactory<TestCountByType, String>("testName"));
        TableColumn countCol = new TableColumn("Количество");
        countCol.setCellValueFactory(new PropertyValueFactory<TestCountByType, Long>("count"));


        reportTable.getColumns().clear();
        reportTable.getColumns().addAll(nameCol, countCol);
        reportTable.setItems(FXCollections.observableArrayList(data));
        logger.info("Отчёт 'Количество анализов по видам' загружен, записей: {}", data.size());
    }

    private void showEmployeeWorkload(LocalDate from, LocalDate to) {
        logger.debug("Загрузка данных о нагрузке сотрудников");
        List<EmployeeStatistic> data = reportDao.getWorkloadByEmployee(from, to);
        TableColumn nameCol = new TableColumn("Сотрудник");
        nameCol.setCellValueFactory(new PropertyValueFactory<EmployeeStatistic, String>("employeeFullName"));
        TableColumn countCol = new TableColumn("Выполнено тестов");
        countCol.setCellValueFactory(new PropertyValueFactory<EmployeeStatistic, Long>("testsCount"));

        reportTable.getColumns().clear();
        reportTable.getColumns().addAll(nameCol, countCol);
        reportTable.setItems(FXCollections.observableArrayList(data));
        logger.info("Отчёт 'Нагрузка сотрудников' загружен, записей: {}", data.size());
    }

    private void showRevenueByOrganization(LocalDate from, LocalDate to) {
        logger.debug("Загрузка данных о выручке по организациям");
        List<OrganizationStatistic> data = reportDao.getRevenueByOrganization(from, to);
        TableColumn nameCol = new TableColumn("Организация");
        nameCol.setCellValueFactory(new PropertyValueFactory<OrganizationStatistic, String>("orgName"));
        TableColumn revCol = new TableColumn("Выручка");
        revCol.setCellValueFactory(new PropertyValueFactory<OrganizationStatistic, BigDecimal>("totalRevenue"));

        reportTable.getColumns().clear();
        reportTable.getColumns().addAll(nameCol, revCol);
        reportTable.setItems(FXCollections.observableArrayList(data));
        logger.info("Отчёт 'Выручка по организациям' загружен, записей: {}", data.size());
    }

    @FXML
    void onPrint() {
        if (reportTable.getItems().isEmpty()) {
            logger.warn("Попытка печати пустого отчёта");
            showAlert("Нет данных для печати");
            return;
        }
        logger.info("Запрос на печать текущего отчёта");
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) {
            logger.debug("Диалог печати подтверждён");
            boolean success = job.printPage(reportTable);
            if (success) {
                job.endJob();
                logger.info("Отчёт успешно отправлен на печать");
            } else {
                logger.warn("Не удалось напечатать отчёт");
            }
        } else {
            logger.warn("Печать отменена пользователем или принтер не доступен");
        }

    }

    @FXML
    void onExport() {
        if (reportTable.getItems().isEmpty()) {
            logger.warn("Попытка экспорта пустого отчёта");
            showAlert("Нет данных для экспорта");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить отчёт");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV файлы (*.csv)", "*.csv"));
        File file = fileChooser.showSaveDialog(reportTable.getScene().getWindow());

        if (file == null) {
            logger.info("Экспорт отменён пользователем");
            return;
        }

        logger.info("Экспорт отчёта в файл: {}", file.getAbsolutePath());

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // Запись заголовков
            StringBuilder headerLine = new StringBuilder();
            for (Object colObj : reportTable.getColumns()) {
                if (headerLine.length() > 0) headerLine.append(",");
                TableColumn<?, ?> col = (TableColumn<?, ?>) colObj;
                headerLine.append(col.getText());
            }
            writer.println(headerLine.toString());

            // Запись строк данных
            for (Object row : reportTable.getItems()) {
                StringBuilder dataLine = new StringBuilder();
                for (Object colObj : reportTable.getColumns()) {
                    TableColumn<Object, ?> col = (TableColumn<Object, ?>) colObj;
                    if (dataLine.length() > 0) dataLine.append(",");
                    Object cellData = col.getCellObservableValue(row).getValue();
                    String cellValue = (cellData != null) ? cellData.toString() : "";
                    if (cellValue.contains(",") || cellValue.contains("\"")) {
                        cellValue = "\"" + cellValue.replace("\"", "\"\"") + "\"";
                    }
                    dataLine.append(cellValue);
                }
                writer.println(dataLine.toString());
            }

            logger.info("Экспорт успешно завершён, записей: {}", reportTable.getItems().size());
            showAlert("Экспорт завершён");

        } catch (IOException e) {
            logger.error("Ошибка при экспорте в CSV: {}", e.getMessage());
            showAlert("Ошибка при экспорте: " + e.getMessage());
        }
    }

    @FXML
    void onCancel() {
        logger.info("Очистка отчёта пользователем");
        reportTable.getColumns().clear();
        reportTable.getItems().clear();
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}