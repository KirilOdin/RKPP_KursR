package ru.kafpin124.rkpp_kursr.contoller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class NewOrderController {

    @FXML
    private TextField barcodeField;

    @FXML
    private ComboBox<?> biomaterialCombo;

    @FXML
    private DatePicker collectionDatePicker;

    @FXML
    private TextField patientField;

    @FXML
    private TableView<?> selectedTestsTable;

    @FXML
    void onAddTest(ActionEvent event) {

    }

    @FXML
    void onCancel(ActionEvent event) {

    }

    @FXML
    void onCreateOrder(ActionEvent event) {

    }

    @FXML
    void onGenerateBarcode(ActionEvent event) {

    }

    @FXML
    void onRemoveTest(ActionEvent event) {

    }

    @FXML
    void onSelectPatient(ActionEvent event) {

    }

}
