package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import lombok.Getter;
import ru.kafpin124.rkpp_kursr.dao.impl.PatientDaoImpl;
import ru.kafpin124.rkpp_kursr.model.Patient;

public class SelectPersonController {

    //TODO: Реализовать контроллер!

    @FXML private TableView<Patient> tableViewPerson;
    @FXML private Button btSelect, btCancel;
    @Getter
    private Patient selectedPatient;

    @FXML
    void initialize() {
        PatientDaoImpl dao = new PatientDaoImpl();
        tableViewPerson.setItems(FXCollections.observableArrayList(dao.getAll()));
        btSelect.setOnAction(e -> {
            selectedPatient = tableViewPerson.getSelectionModel().getSelectedItem();
            ((Stage) btSelect.getScene().getWindow()).close();
        });
        btCancel.setOnAction(e -> ((Stage) btCancel.getScene().getWindow()).close());
    }

}
