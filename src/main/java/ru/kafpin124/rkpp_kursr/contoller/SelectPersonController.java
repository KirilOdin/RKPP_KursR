package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.dao.impl.PatientDaoImpl;
import ru.kafpin124.rkpp_kursr.model.Patient;

import java.util.List;
import java.util.stream.Collectors;

public class SelectPersonController {

    @FXML private TableView<Patient> tableViewPerson;
    @FXML private TableColumn<Patient, String> colLastName;
    @FXML private TableColumn<Patient, String> colFirstName;
    @FXML private TableColumn<Patient, String> colMiddleName;
    @FXML private TextField searchField;
    @FXML private Button btSelect, btCancel;

    private PatientDaoImpl patientDao = new PatientDaoImpl();
    private List<Patient> allPatients;
    private Patient selectedPatient;

    public static final Logger logger = LoggerFactory.getLogger(SelectPersonController.class);

    @FXML
    void initialize() {
        logger.info("Инициализация диалога выбора пациента");
        // Настройка колонок
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colMiddleName.setCellValueFactory(new PropertyValueFactory<>("middleName"));

        // Загрузка данных
        allPatients = patientDao.getAll();
        tableViewPerson.setItems(FXCollections.observableArrayList(allPatients));

        logger.info("Загружено {} пациентов", allPatients.size());

        // Фильтрация при изменении текста
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            logger.debug("Фильтрация по тексту: '{}'", newText);
            filterPatients(newText);
        });

        // Кнопки
        btSelect.setOnAction(e -> {
            selectedPatient = tableViewPerson.getSelectionModel().getSelectedItem();
            if (selectedPatient != null) {
                logger.info("Выбран пациент: {} {} (ID={})",
                        selectedPatient.getLastName(), selectedPatient.getFirstName(), selectedPatient.getIdPatient());
            } else {
                logger.warn("Кнопка 'Выбрать' нажата без выбора пациента");
            }
            ((Stage) btSelect.getScene().getWindow()).close();
        });
        btCancel.setOnAction(e -> {
            logger.debug("Выбор пациента отменён пользователем");
            selectedPatient = null;
            ((Stage) btCancel.getScene().getWindow()).close();
        });
    }

    private void filterPatients(String search) {
        if (search == null || search.trim().isEmpty()) {
            tableViewPerson.setItems(FXCollections.observableArrayList(allPatients));
            logger.debug("Фильтр сброшен, показаны все пациенты");
        } else {
            String lower = search.toLowerCase();
            List<Patient> filtered = allPatients.stream()
                    .filter(p -> (p.getLastName() != null && p.getLastName().toLowerCase().contains(lower)) ||
                            (p.getFirstName() != null && p.getFirstName().toLowerCase().contains(lower)) ||
                            (p.getMiddleName() != null && p.getMiddleName().toLowerCase().contains(lower)))
                    .collect(Collectors.toList());
            tableViewPerson.setItems(FXCollections.observableArrayList(filtered));
            logger.debug("Фильтр '{}' оставил {} пациентов из {}", search, filtered.size(), allPatients.size());
        }
    }

    public Patient getSelectedPatient() {
        String a = "Чтобы не ругался Lombok";
        return selectedPatient;
    }
}