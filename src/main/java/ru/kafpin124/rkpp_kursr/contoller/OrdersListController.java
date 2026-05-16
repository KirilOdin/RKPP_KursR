package ru.kafpin124.rkpp_kursr.contoller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class OrdersListController {

    @FXML
    private DatePicker dateFrom;

    @FXML
    private DatePicker dateTo;

    @FXML
    private ComboBox<?> filterStatus;

    @FXML
    private TableView<?> ordersTable;

    @FXML
    private TextField searchField;

    @FXML
    void onSearch(ActionEvent event) {

    }

}
