package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.kafpin124.rkpp_kursr.dao.impl.OrderDaoImpl;
import ru.kafpin124.rkpp_kursr.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrdersListController {
    @FXML private TableView<Order> ordersTable;
    @FXML private ComboBox<String> filterStatus;
    @FXML private TextField searchField;
    @FXML private DatePicker dateFrom, dateTo;

    private OrderDaoImpl orderDao = new OrderDaoImpl();

    @FXML
    void initialize() {

        TableColumn<Order, Long> idCol = (TableColumn<Order, Long>) ordersTable.getColumns().get(0);
        idCol.setCellValueFactory(new PropertyValueFactory<>("idOrder"));

        TableColumn<Order, String> patientCol = (TableColumn<Order, String>) ordersTable.getColumns().get(1);
        patientCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getPatient().getLastName() + " " +
                                cellData.getValue().getPatient().getFirstName().charAt(0) + "."
                ));

        TableColumn<Order, String> barcodeCol = (TableColumn<Order, String>) ordersTable.getColumns().get(2);
        barcodeCol.setCellValueFactory(cellData -> {
            // Берём штрих-код первой пробы, если есть
            if (!cellData.getValue().getSpecimens().isEmpty()) {
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getSpecimens().get(0).getBarcode());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        TableColumn<Order, String> statusCol = (TableColumn<Order, String>) ordersTable.getColumns().get(3);
        statusCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getStatus().getStatusName()));

        TableColumn<Order, LocalDateTime> dateCol = (TableColumn<Order, LocalDateTime>) ordersTable.getColumns().get(4);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("registrationDatetime"));

        filterStatus.setItems(FXCollections.observableArrayList(
                "Все", "зарегистрирован", "в работе", "выполнен", "утверждён"));
        filterStatus.setValue("Все");
        loadOrders();
    }

    @FXML
    void onSearch() {
        loadOrders();
    }

    public void loadOrders() {
        String status = filterStatus.getValue();
        String searchText = searchField.getText().toLowerCase();
        LocalDateTime from = dateFrom.getValue() != null ? dateFrom.getValue().atStartOfDay() : null;
        LocalDateTime to = dateTo.getValue() != null ? dateTo.getValue().atTime(23, 59, 59) : null;

        List<Order> orders = orderDao.getAll();
        // Фильтрация с помощью Stream
        List<Order> filtered = orders.stream()
                .filter(o -> status.equals("Все") || o.getStatus().getStatusName().equals(status))
                .filter(o -> from == null || !o.getRegistrationDatetime().isBefore(from))
                .filter(o -> to == null || !o.getRegistrationDatetime().isAfter(to))
                .filter(o -> searchText.isEmpty() ||
                        o.getPatient().getLastName().toLowerCase().contains(searchText) ||
                        o.getPatient().getFirstName().toLowerCase().contains(searchText) ||
                        o.getSpecimens().stream().anyMatch(s -> s.getBarcode().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());

        ordersTable.setItems(FXCollections.observableArrayList(filtered));
    }

    /** Возвращает выбранный заказ */
    public Order getSelectedOrder() {
        return ordersTable.getSelectionModel().getSelectedItem();
    }
}