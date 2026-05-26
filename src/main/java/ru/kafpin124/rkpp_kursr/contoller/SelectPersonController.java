package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ru.kafpin124.rkpp_kursr.dao.impl.PatientDaoImpl;
import ru.kafpin124.rkpp_kursr.model.Patient;

import java.util.List;
import java.util.stream.Collectors;

public class SelectPersonController {

    @FXML private TableView<Patient> tableViewPerson;
    @FXML private TableColumn<Patient, String> colLastName;
    @FXML private TableColumn<Patient, String> colFirstName;
    @FXML private TableColumn<Patient, String> colMiddleName;
    @FXML private TextArea searchField;   // заменим TextArea на TextField
    @FXML private Button btSelect, btCancel;

    private PatientDaoImpl patientDao = new PatientDaoImpl();
    private List<Patient> allPatients;
    private Patient selectedPatient;

    @FXML
    void initialize() {
        // Настройка колонок
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colMiddleName.setCellValueFactory(new PropertyValueFactory<>("middleName"));

        // Загрузка данных
        allPatients = patientDao.getAll();
        tableViewPerson.setItems(FXCollections.observableArrayList(allPatients));

        // Фильтрация при изменении текста
        searchField.textProperty().addListener((obs, oldText, newText) -> filterPatients(newText));

        // Кнопки
        btSelect.setOnAction(e -> {
            selectedPatient = tableViewPerson.getSelectionModel().getSelectedItem();
            ((Stage) btSelect.getScene().getWindow()).close();
        });
        btCancel.setOnAction(e -> {
            selectedPatient = null;
            ((Stage) btCancel.getScene().getWindow()).close();
        });
    }

    private void filterPatients(String search) {
        if (search == null || search.trim().isEmpty()) {
            tableViewPerson.setItems(FXCollections.observableArrayList(allPatients));
        } else {
            String lower = search.toLowerCase();
            List<Patient> filtered = allPatients.stream()
                    .filter(p -> (p.getLastName() != null && p.getLastName().toLowerCase().contains(lower)) ||
                            (p.getFirstName() != null && p.getFirstName().toLowerCase().contains(lower)) ||
                            (p.getMiddleName() != null && p.getMiddleName().toLowerCase().contains(lower)))
                    .collect(Collectors.toList());
            tableViewPerson.setItems(FXCollections.observableArrayList(filtered));
        }
    }

    public Patient getSelectedPatient() {
        return selectedPatient;
    }
}