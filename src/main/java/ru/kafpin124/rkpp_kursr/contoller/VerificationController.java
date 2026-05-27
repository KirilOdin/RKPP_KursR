package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.dao.OrderDao;
import ru.kafpin124.rkpp_kursr.dao.OrderItemDao;
import ru.kafpin124.rkpp_kursr.dao.impl.OrderDaoImpl;
import ru.kafpin124.rkpp_kursr.dao.impl.OrderItemDaoImpl;
import ru.kafpin124.rkpp_kursr.model.*;
import ru.kafpin124.rkpp_kursr.model.Employee;

import java.time.LocalDateTime;
import java.util.List;

//@NoArgsConstructor(force = true)
public class VerificationController {
    @FXML private TableView<Order> pendingOrdersTable;
    @FXML private TableView<OrderItem> resultsTable;
    @FXML private TextField commentField;

    private final OrderDao orderDao;
    private final OrderItemDao itemDao;
    private Employee currentUser;

    //TODO: Добавить логирование!

    public static final Logger logger = LoggerFactory.getLogger(VerificationController.class);


    public VerificationController(OrderDao orderDao, OrderItemDao itemDao) {
        this.orderDao = orderDao;
        this.itemDao = itemDao;
    }

    @FXML
    void initialize() {
    }

    public void setCurrentUser(Employee user) {
        this.currentUser = user;
    }

    public void refreshPendingOrders() {
        List<Order> pending = orderDao.findByStatusId(3L); // "выполнен"
        pendingOrdersTable.setItems(FXCollections.observableArrayList(pending));
    }

    @FXML
    void onApproveOrder() {
        Order order = pendingOrdersTable.getSelectionModel().getSelectedItem();
        if (order != null) {
            order.setStatus(new OrderStatus(4L, "утверждён"));
            order.setVerifiedBy(currentUser);
            order.setVerificationDatetime(LocalDateTime.now());
            orderDao.update(order);  // полное обновление

            refreshPendingOrders();
            resultsTable.getItems().clear();
        }
    }

    @FXML
    void onRejectOrder() {
        // ? Вернуть статус "в работе" или добавить особый статус "отклонён" ?
        Order order = pendingOrdersTable.getSelectionModel().getSelectedItem();
        if (order != null) {
            // Возвращаем в работу (или статус "отклонён"?)
            order.setStatus(new OrderStatus(2L, "в работе"));
            orderDao.update(order);
            refreshPendingOrders();
            resultsTable.getItems().clear();
        }
    }

    // Слушатель выбора заказа
    public void bindSelection() {
        pendingOrdersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                List<OrderItem> items = itemDao.findByOrderId(newVal.getIdOrder());
                resultsTable.setItems(FXCollections.observableArrayList(items));
            } else {
                resultsTable.getItems().clear();
            }
        });
    }
}