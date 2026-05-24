package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import ru.kafpin124.rkpp_kursr.dao.impl.OrderDaoImpl;
import ru.kafpin124.rkpp_kursr.dao.impl.OrderItemDaoImpl;
import ru.kafpin124.rkpp_kursr.model.Order;
import ru.kafpin124.rkpp_kursr.model.OrderItem;

import java.util.List;

public class VerificationController {
    @FXML private TableView<Order> pendingOrdersTable;
    @FXML private TableView<OrderItem> resultsTable;
    @FXML private TextField commentField;

    private OrderDaoImpl orderDao = new OrderDaoImpl();
    private OrderItemDaoImpl itemDao = new OrderItemDaoImpl();

    @FXML
    void initialize() {
        List<Order> pending = orderDao.findByStatusId(3L); // выполненные, ожидающие верификации
        pendingOrdersTable.setItems(FXCollections.observableArrayList(pending));
        pendingOrdersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                resultsTable.setItems(FXCollections.observableArrayList(itemDao.findByOrderId(newVal.getIdOrder())));
            }
        });
    }

    @FXML
    void onApproveOrder() {
        Order order = pendingOrdersTable.getSelectionModel().getSelectedItem();
        if (order != null) {
            orderDao.updateStatus(order.getIdOrder(), 4L); // утверждён
            pendingOrdersTable.getItems().remove(order);
        }
    }

    public void onRejectOrder(ActionEvent actionEvent) {

    }
}