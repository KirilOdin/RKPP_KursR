package ru.kafpin124.rkpp_kursr.contoller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;

public class ReportsController {

    @FXML
    private DatePicker reportDateFrom;

    @FXML
    private DatePicker reportDateTo;

    @FXML
    private TableView<?> reportTable;

    @FXML
    private ComboBox<?> reportTypeCombo;

    @FXML
    void onCancel(ActionEvent event) {

    }

    @FXML
    void onExport(ActionEvent event) {

    }

    @FXML
    void onGenerateReport(ActionEvent event) {

    }

    @FXML
    void onPrint(ActionEvent event) {

    }

}
