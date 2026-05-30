package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.dao.*;
import ru.kafpin124.rkpp_kursr.dao.impl.*;
import ru.kafpin124.rkpp_kursr.model.*;
import ru.kafpin124.rkpp_kursr.model.Employee;
import ru.kafpin124.rkpp_kursr.util.LocalizationService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

//@NoArgsConstructor(force = true)
public class NewOrderController {
    @FXML private TableColumn colCode, colName, colBiomaterial, colTime, colPrice;
    @FXML private TextField patientField;
    @FXML private TextField barcodeField;
    @FXML private DatePicker collectionDatePicker;
    @FXML private ComboBox<String> biomaterialCombo;
    @FXML private TableView<AnalysisTest> selectedTestsTable;


    //TODO: Убрать Alert'ы, заменить на обычный label?

    private final PatientDao patientDao;
    private final AnalysisTestDao testDao;
    private final OrderDao orderDao;
    private final SpecimenDao specimenDao;
    private final OrderItemDao orderItemDao;


//    public NewOrderController() {
//        this.patientDao = new PatientDaoImpl();
//        this.testDao = new AnalysisTestDaoImpl();
//        this.orderDao = new OrderDaoImpl();
//        this.specimenDao = new SpecimenDaoImpl();
//        this.orderItemDao = new OrderItemDaoImpl();
//    }
    private Patient selectedPatient;

    private Employee currentUser;
    private List<AnalysisTest> selectedTests = new ArrayList<>();

    public static final Logger logger = LoggerFactory.getLogger(NewOrderController.class);

    public NewOrderController(PatientDao patientDao, AnalysisTestDao testDao,OrderDao orderDao,
                              SpecimenDao specimenDao, OrderItemDao orderItemDao) {
        this.patientDao = patientDao;
        this.testDao = testDao;
        this.orderDao = orderDao;
        this.specimenDao = specimenDao;
        this.orderItemDao = orderItemDao;
        logger.debug("NewOrderController создан");
    }

    @FXML
    void initialize() {
        logger.info("Инициализация формы создания заказа");
        biomaterialCombo.setItems(FXCollections.observableArrayList("кровь", "моча", "мазок", "кал", "слюна"));
        collectionDatePicker.setValue(LocalDateTime.now().toLocalDate());
        logger.debug("ComboBox биоматериалов заполнен, дата забора установлена на сегодня");
    }

    public void setCurrentUser(Employee user) {
        this.currentUser = user;
        logger.debug("Установлен текущий пользователь для NewOrderController: {}", user.getLogin());
    }

    @FXML
    void onSelectPatient() throws IOException {
        logger.info("Открытие диалога выбора пациента");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin124/rkpp_kursr/select_person.fxml"), LocalizationService.getBundle());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            stage.showAndWait();

            SelectPersonController controller = loader.getController();
            Patient patient = controller.getSelectedPatient();
            if (patient != null) {
                this.selectedPatient = patient;
                patientField.setText(patient.getLastName() + " " + patient.getFirstName());
                logger.info("Выбран пациент: {} {} (ID={})", patient.getLastName(), patient.getFirstName(), patient.getIdPatient());
            } else {
                logger.debug("Выбор пациента отменён");
            }
        } catch (IOException e) {
            logger.error("Ошибка загрузки select_person.fxml", e);
            showAlert("Ошибка", "Не удалось открыть окно выбора пациента");
        }
    }

    @FXML
    void onAddTest() {
        logger.info("Открытие диалога добавления тестов");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/kafpin124/rkpp_kursr/select_test.fxml"), LocalizationService.getBundle());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            stage.showAndWait();

            SelectTestController controller = loader.getController();
            List<AnalysisTest> tests = controller.getSelectedTests();
            if (tests != null && !tests.isEmpty()) {
                selectedTests.addAll(tests);
                selectedTestsTable.setItems(FXCollections.observableArrayList(selectedTests));
                logger.info("Добавлено {} тестов в заказ", tests.size());
                for (AnalysisTest t : tests) {
                    logger.debug("Добавлен тест: {} (ID={})", t.getTestName(), t.getIdTest());
                }
            } else {
                logger.debug("Тесты не выбраны");
            }
        } catch (IOException e) {
            logger.error("Ошибка загрузки select_test.fxml", e);
            showAlert("Ошибка", "Не удалось открыть окно выбора тестов");
        }
    }

    @FXML
    void onGenerateBarcode() {
        String barcode = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
        barcodeField.setText(barcode);
        logger.debug("Сгенерирован штрих-код: {}", barcode);;
    }

    @FXML
    void onCreateOrder() {
        logger.info("Попытка создания заказа");
        if (selectedPatient == null) {
            logger.warn("Создание заказа отменено: не выбран пациент");
            showAlert("Ошибка", "Выберите пациента");
            return;
        }
        if (selectedTests.isEmpty()) {
            logger.warn("Создание заказа отменено: не добавлено ни одного теста");
            showAlert("Ошибка", "Добавьте хотя бы один анализ");
            return;
        }
        String barcode = barcodeField.getText();
        if (barcode.isEmpty()) {
            logger.warn("Создание заказа отменено: не сгенерирован штрих-код");
            showAlert("Ошибка", "Сгенерируйте штрих-код");
            return;
        }
        String biomaterial = biomaterialCombo.getValue();
        if (biomaterial == null) {
            logger.warn("Создание заказа отменено: не выбран тип биоматериала");
            showAlert("Ошибка", "Выберите тип биоматериала");
            return;
        }

        Order order = new Order();
        order.setPatient(selectedPatient);
        order.setStatus(new OrderStatus(1L, "зарегистрирован"));
        order.setRegisteredBy(currentUser);
        order.setRegistrationDatetime(LocalDateTime.now());
        // Организация – null, если не выбрана (если пациент не от организации)
        order.setOrganization(null);

        OrderDaoImpl orderDao = new OrderDaoImpl();
        orderDao.add(order);   // после вызова в order присвоится id
        logger.info("Создан заказ ID={} для пациента {} {}", order.getIdOrder(), selectedPatient.getLastName(), selectedPatient.getFirstName());

        // Создаём пробу
        Specimen specimen = new Specimen();
        specimen.setOrder(order);
        specimen.setSpecimenType(biomaterial);
        specimen.setContainerType("стандартный");
        specimen.setCollectionDatetime(collectionDatePicker.getValue().atStartOfDay());
        specimen.setBarcode(barcode);

        SpecimenDaoImpl specimenDao = new SpecimenDaoImpl();
        specimenDao.add(specimen);
        logger.debug("Создана проба с штрих-кодом {} для заказа {}", barcode, order.getIdOrder());


        // Создаём позиции заказа (order_items)
        OrderItemDaoImpl itemDao = new OrderItemDaoImpl();
        for (AnalysisTest test : selectedTests) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setTest(test);
            item.setSpecimen(specimen);
            item.setStatus("назначен");
            // result_value и т.д. пока null
            itemDao.add(item);
            logger.debug("Добавлена позиция заказа для теста {} (ID={})", test.getTestName(), test.getIdTest());
        }

        showAlert("Готово", "Заказ создан");
        logger.info("Заказ {} успешно создан, содержит {} тестов", order.getIdOrder(), selectedTests.size());
        // Очистка полей
        patientField.clear();
        selectedPatient = null;
        selectedTests.clear();
        selectedTestsTable.getItems().clear();
        barcodeField.clear();
    }

    @FXML
    void onRemoveTest() {
        AnalysisTest selected = selectedTestsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedTests.remove(selected);
            selectedTestsTable.getItems().remove(selected);
            logger.info("Удалён тест из списка: {}", selected.getTestName());
        } else {
            logger.debug("Попытка удалить тест без выбора");
        }
    }

    @FXML
    void onCancel() {
        logger.info("Отмена создания заказа, очистка формы");
        // Очистить форму
        patientField.clear();
        selectedTests.clear();
        selectedTestsTable.getItems().clear();
        barcodeField.clear();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}