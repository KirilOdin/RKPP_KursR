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

    public static final Logger logger = LoggerFactory.getLogger(VerificationController.class);


    public VerificationController(OrderDao orderDao, OrderItemDao itemDao) {
        this.orderDao = orderDao;
        this.itemDao = itemDao;
        logger.debug("VerificationController создан");
    }

    @FXML
    void initialize() {
        logger.info("Инициализация контроллера верификации");
        bindSelection();
    }

    public void setCurrentUser(Employee user) {
        this.currentUser = user;
        logger.debug("Установлен текущий пользователь для верификации: {}", user.getLogin());
    }

    public void refreshPendingOrders() {
        logger.info("Обновление списка заказов, ожидающих верификации (статус 'выполнен')");
        List<Order> pending = orderDao.findByStatusId(3L); // "выполнен"
        pendingOrdersTable.setItems(FXCollections.observableArrayList(pending));
        logger.debug("Загружено {} заказов со статусом 'выполнен'", pending.size());
    }

    @FXML
    void onApproveOrder() {
        Order order = pendingOrdersTable.getSelectionModel().getSelectedItem();
        if (order == null) {
            logger.warn("Попытка утвердить заказ без выбора");
            showAlert("Выберите заказ для утверждения");
            return;
        }
        if (currentUser == null) {
            logger.error("Не установлен текущий пользователь (currentUser = null)");
            showAlert("Ошибка: пользователь не авторизован");
            return;
        }

        logger.info("Утверждение заказа ID={} пользователем {}", order.getIdOrder(), currentUser.getLogin());
        order.setStatus(new OrderStatus(4L, "утверждён"));
        order.setVerifiedBy(currentUser);
        order.setVerificationDatetime(LocalDateTime.now());
        orderDao.update(order);


        String comment = commentField.getText();
        if (comment != null && !comment.trim().isEmpty()) {
            logger.debug("Комментарий к утверждению: {}", comment);
        }

        refreshPendingOrders();
        resultsTable.getItems().clear();
        commentField.clear();
        showAlert("Заказ #" + order.getIdOrder() + " утверждён");
        logger.info("Заказ {} успешно утверждён", order.getIdOrder());
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
                logger.debug("Выбран заказ ID={} для просмотра результатов", newVal.getIdOrder());
                List<OrderItem> items = itemDao.findByOrderId(newVal.getIdOrder());
                resultsTable.setItems(FXCollections.observableArrayList(items));
                logger.info("Загружено {} позиций для заказа {}", items.size(), newVal.getIdOrder());
            } else {
                resultsTable.getItems().clear();
                logger.debug("Выбор заказа сброшен");
            }
        });
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Верификация");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}