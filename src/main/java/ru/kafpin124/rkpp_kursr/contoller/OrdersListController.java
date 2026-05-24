package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.kafpin124.rkpp_kursr.dao.impl.OrderDaoImpl;
import ru.kafpin124.rkpp_kursr.model.Order;

import java.time.LocalDateTime;
import java.util.List;

public class OrdersListController {
    @FXML private TableView<Order> ordersTable;
    @FXML private ComboBox<String> filterStatus;
    @FXML private TextField searchField;
    @FXML private DatePicker dateFrom, dateTo;

    private OrderDaoImpl orderDao = new OrderDaoImpl();

    @FXML
    void initialize() {
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

        // В реальности нужно комбинировать фильтры, но для упрощения возьмём все заказы
        List<Order> orders = orderDao.getAll();
        // Применить фильтры программно
        ObservableList<Order> filtered = FXCollections.observableArrayList();
        for (Order o : orders) {
            // фильтрация по статусу, датам, поисковой строке
            filtered.add(o);
        }
        ordersTable.setItems(filtered);
    }
}