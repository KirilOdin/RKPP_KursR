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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//@NoArgsConstructor(force = true)
public class NewOrderController {
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

    //TODO: Добавить логирование!

    public static final Logger logger = LoggerFactory.getLogger(NewOrderController.class);

    @FXML
    void initialize() {
        biomaterialCombo.setItems(FXCollections.observableArrayList("кровь", "моча", "мазок", "кал", "слюна"));
        collectionDatePicker.setValue(LocalDateTime.now().toLocalDate());
    }

    public NewOrderController(PatientDao patientDao, AnalysisTestDao testDao,OrderDao orderDao,
                              SpecimenDao specimenDao, OrderItemDao orderItemDao) {
        this.patientDao = patientDao;
        this.testDao = testDao;
        this.orderDao = orderDao;
        this.specimenDao = specimenDao;
        this.orderItemDao = orderItemDao;
    }

    public void setCurrentUser(Employee user) {
        this.currentUser = user;
    }

    @FXML
    void onSelectPatient() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/select_person.fxml"));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(loader.load()));
        stage.showAndWait();

        SelectPersonController controller = loader.getController();
        Patient patient = controller.getSelectedPatient();
        if (patient != null) {
            this.selectedPatient = patient;
            patientField.setText(patient.getLastName() + " " + patient.getFirstName());
        }
    }

    @FXML
    void onAddTest() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/select_test_demo.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(loader.load()));
            stage.showAndWait();

            SelectTestController controller = loader.getController();
            List<AnalysisTest> tests = controller.getSelectedTests();
            if (tests != null && !tests.isEmpty()) {
                selectedTests.addAll(tests);
                selectedTestsTable.setItems(FXCollections.observableArrayList(selectedTests));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onGenerateBarcode() {
        barcodeField.setText(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12));
    }

    @FXML
    void onCreateOrder() {
        if (selectedPatient == null) {
            showAlert("Ошибка", "Выберите пациента");
            return;
        }
        if (selectedTests.isEmpty()) {
            showAlert("Ошибка", "Добавьте хотя бы один анализ");
            return;
        }
        String barcode = barcodeField.getText();
        if (barcode.isEmpty()) {
            showAlert("Ошибка", "Сгенерируйте штрих-код");
            return;
        }
        String biomaterial = biomaterialCombo.getValue();
        if (biomaterial == null) {
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

        // Создаём пробу
        Specimen specimen = new Specimen();
        specimen.setOrder(order);
        specimen.setSpecimenType(biomaterial);
        specimen.setContainerType("стандартный");
        specimen.setCollectionDatetime(collectionDatePicker.getValue().atStartOfDay());
        specimen.setBarcode(barcode);

        SpecimenDaoImpl specimenDao = new SpecimenDaoImpl();
        specimenDao.add(specimen);

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
        }

        showAlert("Готово", "Заказ создан");
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
        }
    }

    @FXML
    void onCancel() {
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