package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.kafpin124.rkpp_kursr.dao.impl.AnalysisTestDaoImpl;
import ru.kafpin124.rkpp_kursr.model.AnalysisTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectTestController {

    @FXML private ListView<AnalysisTest> testListView;
    @FXML private ComboBox<String> filterCombo;
    @FXML private Button btAddSelected, btCancel, btFilter;

    private AnalysisTestDaoImpl testDao = new AnalysisTestDaoImpl();
    private List<AnalysisTest> allTests;
    private List<AnalysisTest> selectedTests = new ArrayList<>();

    @FXML
    void initialize() {
        testListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        allTests = testDao.getAll();
        testListView.setItems(FXCollections.observableArrayList(allTests));

        // Заполняем ComboBox уникальными биоматериалами
        List<String> biomaterials = allTests.stream()
                .map(AnalysisTest::getBiomaterial)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        filterCombo.setItems(FXCollections.observableArrayList(biomaterials));
        filterCombo.setValue(null); // или "Все"
    }

    @FXML
    void onFilter() {
        String selectedBiomaterial = filterCombo.getValue();
        if (selectedBiomaterial == null || selectedBiomaterial.isEmpty()) {
            testListView.setItems(FXCollections.observableArrayList(allTests));
        } else {
            List<AnalysisTest> filtered = allTests.stream()
                    .filter(t -> t.getBiomaterial().equals(selectedBiomaterial))
                    .collect(Collectors.toList());
            testListView.setItems(FXCollections.observableArrayList(filtered));
        }
    }

    @FXML
    void onAddSelected() {
        selectedTests = testListView.getSelectionModel().getSelectedItems();
        ((Stage) btAddSelected.getScene().getWindow()).close();
    }

    @FXML
    void onCancel() {
        selectedTests.clear();
        ((Stage) btCancel.getScene().getWindow()).close();
    }

    public List<AnalysisTest> getSelectedTests() {
        return selectedTests;
    }
}