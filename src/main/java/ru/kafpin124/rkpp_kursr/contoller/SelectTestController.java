package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import ru.kafpin124.rkpp_kursr.dao.impl.AnalysisTestDaoImpl;
import ru.kafpin124.rkpp_kursr.model.AnalysisTest;

import java.util.ArrayList;
import java.util.List;

public class SelectTestController {

    //TODO: Реализовать контроллер!


    @FXML private ListView<AnalysisTest> testListView;
    private List<AnalysisTest> selectedTests = new ArrayList<>();


    @FXML
    private ComboBox<?> filterCombo;


    @FXML
    void initialize() {
        testListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        AnalysisTestDaoImpl dao = new AnalysisTestDaoImpl();
        testListView.setItems(FXCollections.observableArrayList(dao.getAll()));
    }

    @FXML
    void onAddSelected() {
        selectedTests = testListView.getSelectionModel().getSelectedItems();
        ((Stage) testListView.getScene().getWindow()).close();
    }

    public List<AnalysisTest> getSelectedTests() {
        return selectedTests;
    }

    @FXML
    void onCancel(ActionEvent event) {

    }

    @FXML
    void onFilter(ActionEvent event) {

    }

}
