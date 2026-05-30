package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.dao.impl.AnalysisTestDaoImpl;
import ru.kafpin124.rkpp_kursr.model.AnalysisTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectTestController {

    @FXML private Label lbFilter;
    @FXML private ListView<AnalysisTest> testListView;
    @FXML private ComboBox<String> filterCombo;
    @FXML private Button btAddSelected, btCancel, btFilter;

    private AnalysisTestDaoImpl testDao = new AnalysisTestDaoImpl();
    private List<AnalysisTest> allTests;
    private List<AnalysisTest> selectedTests = new ArrayList<>();

    public static final Logger logger = LoggerFactory.getLogger(SelectTestController.class);

    @FXML
    void initialize() {
        logger.info("Инициализация диалога выбора тестов");
        testListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        allTests = testDao.getAll();
        testListView.setItems(FXCollections.observableArrayList(allTests));
        logger.info("Загружено {} доступных тестов", allTests.size());

        // Заполняем ComboBox уникальными биоматериалами
        List<String> biomaterials = allTests.stream()
                .map(AnalysisTest::getBiomaterial)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        filterCombo.setItems(FXCollections.observableArrayList(biomaterials));
        filterCombo.setValue(null); // или "Все"
        logger.debug("Доступные биоматериалы для фильтрации: {}", biomaterials);
    }

    @FXML
    void onFilter() {
        String selectedBiomaterial = filterCombo.getValue();
        if (selectedBiomaterial == null || selectedBiomaterial.isEmpty()) {
            testListView.setItems(FXCollections.observableArrayList(allTests));
            logger.debug("Фильтр сброшен, показаны все тесты");
        } else {
            List<AnalysisTest> filtered = allTests.stream()
                    .filter(t -> t.getBiomaterial().equals(selectedBiomaterial))
                    .collect(Collectors.toList());
            testListView.setItems(FXCollections.observableArrayList(filtered));
            logger.info("Фильтр по биоматериалу '{}': показано {} тестов из {}", selectedBiomaterial, filtered.size(), allTests.size());
        }
    }

    @FXML
    void onAddSelected() {
        selectedTests.clear();
        selectedTests.addAll(testListView.getSelectionModel().getSelectedItems());
        if (selectedTests.isEmpty()) {
            logger.warn("Попытка добавить тесты, но ничего не выбрано");
        } else {
            logger.info("Выбрано {} тестов для добавления в заказ", selectedTests.size());
            for (AnalysisTest t : selectedTests) {
                logger.debug("Выбран тест: {} (ID={})", t.getTestName(), t.getIdTest());
            }
        }
        ((Stage) btAddSelected.getScene().getWindow()).close();
    }

    @FXML
    void onCancel() {
        logger.debug("Выбор тестов отменён пользователем");
        selectedTests.clear();
        ((Stage) btCancel.getScene().getWindow()).close();
    }

    public List<AnalysisTest> getSelectedTests() {
        String a = "Чтобы не ругался Lombok";
        return selectedTests;
    }
}