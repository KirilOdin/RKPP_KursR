package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.print.PrinterJob;
import lombok.NoArgsConstructor;
import ru.kafpin124.rkpp_kursr.dao.impl.ReportDaoImpl;
import ru.kafpin124.rkpp_kursr.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(force = true)
public class ReportsController {

    @FXML private DatePicker reportDateFrom;
    @FXML private DatePicker reportDateTo;
    @FXML private TableView  reportTable;
    @FXML private ComboBox<String> reportTypeCombo;

    private final ReportDaoImpl reportDao;

    public ReportsController(ReportDaoImpl reportDao) {
        this.reportDao = reportDao;
    }

    @FXML
    void initialize() {
        reportTypeCombo.setItems(FXCollections.observableArrayList(
                "Количество анализов по видам",
                "Нагрузка сотрудников",
                "Выручка по организациям"
        ));
        reportTypeCombo.setValue("Количество анализов по видам");
    }

    @FXML
    void onGenerateReport() {
        String type = reportTypeCombo.getValue();
        LocalDate from = reportDateFrom.getValue();
        LocalDate to = reportDateTo.getValue();

        if (from == null || to == null) {
            showAlert("Выберите период");
            return;
        }

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
        }
    }

    private void showTestCountByType(LocalDate from, LocalDate to) {
        List<TestCountByType> data = reportDao.getTestCountByType(from, to);
        TableColumn<TestCountByType, String> nameCol = new TableColumn<>("Название теста");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("testName"));
        TableColumn<TestCountByType, Long> countCol = new TableColumn<>("Количество");
        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));

        reportTable.getColumns().clear();
        reportTable.getColumns().add(nameCol);
        reportTable.getColumns().add(countCol);
        reportTable.setItems(FXCollections.observableArrayList(data));
    }

    private void showEmployeeWorkload(LocalDate from, LocalDate to) {
        List<EmployeeStatistic> data = reportDao.getWorkloadByEmployee(from, to);
        TableColumn<EmployeeStatistic, String> nameCol = new TableColumn<>("Сотрудник");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("employeeFullName"));
        TableColumn<EmployeeStatistic, Long> countCol = new TableColumn<>("Выполнено тестов");
        countCol.setCellValueFactory(new PropertyValueFactory<>("testsCount"));

        reportTable.getColumns().addAll(nameCol, countCol);
        reportTable.setItems(FXCollections.observableArrayList(data));
    }

    private void showRevenueByOrganization(LocalDate from, LocalDate to) {
        List<OrganizationStatistic> data = reportDao.getRevenueByOrganization(from, to);
        TableColumn<OrganizationStatistic, String> nameCol = new TableColumn<>("Организация");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("orgName"));
        TableColumn<OrganizationStatistic, BigDecimal> revCol = new TableColumn<>("Выручка");
        revCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));

        reportTable.getColumns().addAll(nameCol, revCol);
        reportTable.setItems(FXCollections.observableArrayList(data));
    }

    @FXML
    void onPrint() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) {
            boolean success = job.printPage(reportTable);
            if (success) {
                job.endJob();
            }
        }
    }

    @FXML
    void onExport() {
        // Заглушка: в реальности можно сохранить в CSV или Excel
        showAlert("Экспорт ещё не реализован");
    }

    @FXML
    void onCancel() {
        // Очищаем отчёт
        reportTable.getColumns().clear();
        reportTable.getItems().clear();
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}