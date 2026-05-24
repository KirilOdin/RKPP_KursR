package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import ru.kafpin124.rkpp_kursr.dao.impl.OrderDaoImpl;
import ru.kafpin124.rkpp_kursr.dao.impl.OrderItemDaoImpl;
import ru.kafpin124.rkpp_kursr.model.Order;
import ru.kafpin124.rkpp_kursr.model.OrderItem;

import java.util.List;

public class NewResultController {
    @FXML private TextField orderField;
    @FXML private TableView<OrderItem> resultTable;
    @FXML private Label statusLabel;

    private Order currentOrder;

    @FXML
    void onSelectOrder() {
        // Можно открыть список заказов в статусе "в работе" или попросить ввести штрих-код
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Поиск заказа");
        dialog.setHeaderText("Введите штрих-код пробы:");
        dialog.showAndWait().ifPresent(barcode -> {
            OrderDaoImpl orderDao = new OrderDaoImpl();
            Order order = orderDao.findBySpecimenBarcode(barcode);
            if (order != null) {
                currentOrder = order;
                orderField.setText("Заказ #" + order.getIdOrder());
                loadOrderItems(order.getIdOrder());
            } else {
                orderField.setText("Заказ не найден");
            }
        });
    }

    private void loadOrderItems(Long orderId) {
        OrderItemDaoImpl itemDao = new OrderItemDaoImpl();
        List<OrderItem> items = itemDao.findByOrderId(orderId);
        // Настройка колонок таблицы для редактирования
        resultTable.setItems(FXCollections.observableArrayList(items));
        statusLabel.setText("Статус заказа: " + currentOrder.getStatus().getStatusName());
    }

    @FXML
    void onSaveResults() {
        // Для каждого элемента, где ввели значение, обновить результат
        OrderItemDaoImpl dao = new OrderItemDaoImpl();
        for (OrderItem item : resultTable.getItems()) {
            if (item.getResultValue() != null || item.getResultText() != null) {
                // Сравниваем с нормой
//                boolean abnormal = checkAbnormal(item);

                boolean abnormal = true;
                dao.updateResult(item.getIdItem(), item.getResultValue(), item.getResultText(), abnormal);
            }
        }
        // Обновить статус заказа на "выполнен"
        new OrderDaoImpl().updateStatus(currentOrder.getIdOrder(), 3L); // id=3 "выполнен"
        statusLabel.setText("Результаты сохранены, статус: выполнен");
    }

    public void onCancel(ActionEvent actionEvent) {

    }
}