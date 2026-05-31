package ru.kafpin124.rkpp_kursr.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.dao.OrderDao;
import ru.kafpin124.rkpp_kursr.dao.OrderItemDao;
import ru.kafpin124.rkpp_kursr.dao.ReferenceValueDao;
import ru.kafpin124.rkpp_kursr.model.*;
import ru.kafpin124.rkpp_kursr.model.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

//@NoArgsConstructor(force = true)
public class NewResultController {
    @FXML private Button btCancel;
    @FXML private Button btSaveResults;
    @FXML private Label lbResult;
    @FXML private Button btSelectOrder;
    @FXML private Label lbOrder;
    @FXML private TextField orderField;
    @FXML private TextField searchBarcodeField;
    @FXML private TableView<OrderItem> resultTable;
    @FXML private TableColumn<OrderItem, String> colNorm, colResult, colDeviation;
    @FXML private TableColumn<OrderItem, String> colTest;
    @FXML private Label statusLabel;

    private Order currentOrder;
    private Employee currentUser;
    private final OrderDao orderDao;
    private final OrderItemDao itemDao;
    private final ReferenceValueDao refDao;


    public static final Logger logger = LoggerFactory.getLogger(NewResultController.class);

    public NewResultController(OrderDao orderDao, OrderItemDao itemDao, ReferenceValueDao refDao) {
        this.orderDao = orderDao;
        this.itemDao = itemDao;
        this.refDao = refDao;
        logger.debug("NewResultController создан");
    }


    @FXML
    void initialize() {
        logger.info("Инициализация формы ввода результатов");
        // Настройка колонок таблицы
        TableColumn<OrderItem, String> testCol = (TableColumn<OrderItem, String>) resultTable.getColumns().get(0);
        testCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTest().getTestName()));

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
                logger.debug("Для позиции заказа ID={} установлено числовое значение результата: {}", item.getIdItem(), bd);
            } catch (NumberFormatException e) {
                item.setResultValue(null);
                item.setResultText(newValue);
                logger.debug("Для позиции заказа ID={} установлен текстовый результат: {}", item.getIdItem(), newValue);
            }
        });
        resultTable.setEditable(true);
        logger.debug("Таблица результатов настроена, редактирование разрешено");
    }





    public void setCurrentUser(Employee user) {
        this.currentUser = user;
        logger.debug("Установлен текущий пользователь для NewResultController: {}", user.getLogin());
    }

    @FXML
    void onSelectOrder() {
        String barcode = searchBarcodeField.getText().trim();
        if (barcode.isEmpty()) {
            logger.warn("Попытка поиска заказа с пустым штрих-кодом");
            showAlert("Введите штрих-код пробы");
            return;
        }
        logger.info("Поиск заказа по штрих-коду пробы: {}", barcode);
        Order order = orderDao.findBySpecimenBarcode(barcode);
        if (order != null) {
            currentOrder = order;
            orderField.setText("Заказ #" + order.getIdOrder());
            logger.info("Найден заказ ID={}, статус: {}", order.getIdOrder(), order.getStatus().getStatusName());
            loadOrderItems(order.getIdOrder());
        } else {
            orderField.setText("Заказ не найден");
            logger.warn("Заказ со штрих-кодом {} не найден", barcode);
        }
    }

    private void loadOrderItems(Long orderId) {
        logger.debug("Загрузка позиций заказа ID={}", orderId);
        List<OrderItem> items = itemDao.findByOrderId(orderId);
        resultTable.setItems(FXCollections.observableArrayList(items));
        statusLabel.setText("Статус заказа: " + currentOrder.getStatus().getStatusName());

        TableColumn<OrderItem, String> normCol = (TableColumn<OrderItem, String>) resultTable.getColumns().get(1);
        normCol.setCellValueFactory(cellData -> {
            // Получаем заказ из поля currentOrder
            if (currentOrder == null || currentOrder.getPatient() == null) {
                return new javafx.beans.property.SimpleStringProperty("");
            }
            Patient patient = currentOrder.getPatient();
            char gender = patient.getGender();
            int age = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();

            ReferenceValue rv = refDao.findByTestAndGenderAndAge(
                    cellData.getValue().getTest().getIdTest(), gender, age);
            if (rv != null) {
                String norm = (rv.getRefValueMin() != null ? rv.getRefValueMin().stripTrailingZeros().toPlainString() : "")
                        + " – " + (rv.getRefValueMax() != null ? rv.getRefValueMax().stripTrailingZeros().toPlainString() : "");
                return new javafx.beans.property.SimpleStringProperty(norm);
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        logger.info("Загружено {} позиций для заказа {}", items.size(), orderId);
    }

    @FXML
    void onSaveResults() {
        if (currentOrder == null) {
            logger.warn("Попытка сохранить результаты без выбранного заказа");
            return;
        }
        logger.info("Сохранение результатов для заказа ID={}", currentOrder.getIdOrder());

        int updatedCount = 0;
        for (OrderItem item : resultTable.getItems()) {
            if (item.getResultValue() != null || item.getResultText() != null) {
                // Определить отклонение
                boolean abnormal = checkAbnormal(item);
                item.setIsAbnormal(abnormal);
                item.setEnteredBy(currentUser);
                item.setEntryDatetime(LocalDateTime.now());
                itemDao.updateResult(item.getIdItem(), item.getResultValue(), item.getResultText(), abnormal, currentUser.getIdEmployee());
                updatedCount++;
                logger.debug("Обновлена позиция ID={}, результат {}, отклонение: {}", item.getIdItem(),
                        item.getResultValue() != null ? item.getResultValue() : item.getResultText(),
                        abnormal);
            }
        }
        orderDao.updateStatus(currentOrder.getIdOrder(), 3L); // статус "выполнен"
        statusLabel.setText("Результаты сохранены, статус: выполнен");
        logger.info("Сохранено {} результатов для заказа {}, статус обновлён на 'выполнен'", updatedCount, currentOrder.getIdOrder());
    }

    private boolean checkAbnormal(OrderItem item) {
        // Получение референсных значений для пациента (пол и возраст из заказа)
        Patient patient = currentOrder.getPatient();
        char gender = patient.getGender();
        int age = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
        ReferenceValue rv = refDao.findByTestAndGenderAndAge(item.getTest().getIdTest(), gender, age);
        if (rv == null || item.getResultValue() == null) {
            logger.debug("Для позиции ID={} нет референсных значений или результат не числовой", item.getIdItem());
            return false;
        }
        BigDecimal min = rv.getRefValueMin();
        BigDecimal max = rv.getRefValueMax();
        if (min != null && max != null) {
            BigDecimal val = item.getResultValue();
            boolean abnormal =  val.compareTo(min) < 0 || val.compareTo(max) > 0;
            if (abnormal) {
                logger.debug("Позиция ID={} имеет отклонение: значение {} не входит в норму [{}, {}]",
                        item.getIdItem(), val, min, max);
            }
            return abnormal;
        }
        return false;
    }

    @FXML
    void onCancel() {
        logger.info("Отмена ввода результатов, очистка формы");
        currentOrder = null;
        orderField.clear();
        resultTable.getItems().clear();
        statusLabel.setText("");
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    // Метод для обновления списка
    public void refreshOrderList() {
        if (currentOrder != null) {
            logger.debug("Обновление списка позиций для текущего заказа {}", currentOrder.getIdOrder());
            loadOrderItems(currentOrder.getIdOrder());
        } else {
            logger.debug("refreshOrderList вызван, но текущий заказ не выбран");
        }
    }
}