package ru.kafpin124.rkpp_kursr.contoller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.kafpin124.rkpp_kursr.model.AnalysisTest;
import ru.kafpin124.rkpp_kursr.model.Order;
import ru.kafpin124.rkpp_kursr.model.OrderStatus;
import ru.kafpin124.rkpp_kursr.model.Patient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NewOrderController {
    @FXML private TextField patientField;
    @FXML private TextField barcodeField;
    @FXML private DatePicker collectionDatePicker;
    @FXML private ComboBox<String> biomaterialCombo;
    @FXML private TableView<AnalysisTest> selectedTestsTable;

    private Patient selectedPatient;
    private List<AnalysisTest> selectedTests = new ArrayList<>();

    @FXML void onSelectPatient() throws IOException {
        // Открыть SelectPersonController
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

    @FXML void onAddTest() {
        // Открыть SelectTestController
        // аналогично, получить выбранные тесты, добавить в selectedTests и обновить таблицу
    }

    @FXML void onGenerateBarcode() {
        barcodeField.setText(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12));
    }

    @FXML void onCreateOrder() {
        // Проверки, затем сохранение
        Order order = new Order();
        order.setPatient(selectedPatient);
        order.setStatus(new OrderStatus(1L, "зарегистрирован")); // id=1
//        order.setRegisteredBy(currentUser); // нужен доступ к текущему пользователю
        order.setRegistrationDatetime(LocalDateTime.now());
        // ... сохранить заказ, потом пробу, потом позиции
    }

    public void onRemoveTest(ActionEvent actionEvent) {
    }

    public void onCancel(ActionEvent actionEvent) {

    }
}