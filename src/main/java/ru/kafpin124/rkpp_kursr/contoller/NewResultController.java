package ru.kafpin124.rkpp_kursr.contoller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import ru.kafpin124.rkpp_kursr.dao.impl.*;
import ru.kafpin124.rkpp_kursr.model.*;
import ru.kafpin124.rkpp_kursr.model.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

public class NewResultController {
    @FXML private TextField orderField;
    @FXML private TextField searchBarcodeField;
    @FXML private TableView<OrderItem> resultTable;
    @FXML private Label statusLabel;

    private Order currentOrder;
    private Employee currentUser;
    private final OrderDaoImpl orderDao = new OrderDaoImpl();
    private final OrderItemDaoImpl itemDao = new OrderItemDaoImpl();
    private final ReferenceValueDaoImpl refDao = new ReferenceValueDaoImpl();

    @FXML
    void initialize() {
        // Настройка колонок таблицы
        TableColumn<OrderItem, String> testCol = (TableColumn<OrderItem, String>) resultTable.getColumns().get(0);
        testCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTest().getTestName()));

        // Колонка нормы
        TableColumn<OrderItem, String> normCol = (TableColumn<OrderItem, String>) resultTable.getColumns().get(1);
        normCol.setCellValueFactory(cellData -> {

            ReferenceValue rv = refDao.findByTestAndGenderAndAge(
                    cellData.getValue().getTest().getIdTest(),

                    'м',  //TODO: !Достать пол из заказа!
                    30    //TODO: !Достать возраст из пациента!
            );
            if (rv != null) {
                String norm = (rv.getRefValueMin() != null ? rv.getRefValueMin().stripTrailingZeros().toPlainString() : "")
                        + " – " + (rv.getRefValueMax() != null ? rv.getRefValueMax().stripTrailingZeros().toPlainString() : "");
                return new javafx.beans.property.SimpleStringProperty(norm);
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        // Колонка "Результат" – редактируемая ячейка
        TableColumn<OrderItem, String> resultCol = (TableColumn<OrderItem, String>) resultTable.getColumns().get(2);
        resultCol.setCellValueFactory(cellData -> {
            OrderItem item = cellData.getValue();
            String val = item.getResultValue() != null ? item.getResultValue().stripTrailingZeros().toPlainString() : item.getResultText();
            return new javafx.beans.property.SimpleStringProperty(val);
        });
        resultCol.setCellFactory(TextFieldTableCell.forTableColumn());
        resultCol.setOnEditCommit(event -> {
            OrderItem item = event.getRowValue();
            String newValue = event.getNewValue();
            try {
                BigDecimal bd = new BigDecimal(newValue);
                item.setResultValue(bd);
                item.setResultText(null);
            } catch (NumberFormatException e) {
                item.setResultValue(null);
                item.setResultText(newValue);
            }
            // Проверка на отклонение (упрощённая – можно вызывать checkAbnormal)
        });
        resultTable.setEditable(true);
    }

    public void setCurrentUser(Employee user) {
        this.currentUser = user;
    }

    @FXML
    void onSelectOrder() {
        String barcode = searchBarcodeField.getText().trim();
        if (barcode.isEmpty()) {
            showAlert("Введите штрих-код пробы");
            return;
        }
        Order order = orderDao.findBySpecimenBarcode(barcode);
        if (order != null) {
            currentOrder = order;
            orderField.setText("Заказ #" + order.getIdOrder());
            loadOrderItems(order.getIdOrder());
        } else {
            orderField.setText("Заказ не найден");
        }
    }

    private void loadOrderItems(Long orderId) {
        List<OrderItem> items = itemDao.findByOrderId(orderId);
        resultTable.setItems(FXCollections.observableArrayList(items));
        statusLabel.setText("Статус заказа: " + currentOrder.getStatus().getStatusName());
    }

    @FXML
    void onSaveResults() {
        if (currentOrder == null) return;
        for (OrderItem item : resultTable.getItems()) {
            if (item.getResultValue() != null || item.getResultText() != null) {
                // Определить отклонение
                boolean abnormal = checkAbnormal(item);
                item.setIsAbnormal(abnormal);
                item.setEnteredBy(currentUser);
                item.setEntryDatetime(LocalDateTime.now());
                itemDao.updateResult(item.getIdItem(), item.getResultValue(), item.getResultText(), abnormal, currentUser.getIdEmployee());
            }
        }
        orderDao.updateStatus(currentOrder.getIdOrder(), 3L); // статус "выполнен"
        statusLabel.setText("Результаты сохранены, статус: выполнен");
    }

    private boolean checkAbnormal(OrderItem item) {
        // Получение референсных значений для пациента (пол и возраст из заказа)
        Patient patient = currentOrder.getPatient();
        char gender = patient.getGender();
        int age = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
        ReferenceValue rv = refDao.findByTestAndGenderAndAge(item.getTest().getIdTest(), gender, age);
        if (rv == null || item.getResultValue() == null) return false;
        BigDecimal min = rv.getRefValueMin();
        BigDecimal max = rv.getRefValueMax();
        if (min != null && max != null) {
            BigDecimal val = item.getResultValue();
            return val.compareTo(min) < 0 || val.compareTo(max) > 0;
        }
        return false;
    }

    @FXML
    void onCancel() {
        currentOrder = null;
        orderField.clear();
        resultTable.getItems().clear();
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    // Метод для обновления списка
    public void refreshOrderList() {
        if (currentOrder != null) {
            loadOrderItems(currentOrder.getIdOrder());
        }
    }
}