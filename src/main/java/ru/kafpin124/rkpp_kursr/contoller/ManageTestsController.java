package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import lombok.NoArgsConstructor;
import ru.kafpin124.rkpp_kursr.dao.impl.*;
import ru.kafpin124.rkpp_kursr.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(force = true)
public class ManageTestsController {

    @FXML private TableView<AnalysisTest> testsTable;
    @FXML private TableColumn<AnalysisTest, Long> colId;
    @FXML private TableColumn<AnalysisTest, String> colName, colBiomaterial;
    @FXML private TableColumn<AnalysisTest, Integer> colTime;
    @FXML private TableColumn<AnalysisTest, BigDecimal> colPrice;

    private final AnalysisTestDaoImpl testDao;
    private final ReferenceValueDaoImpl refDao;

    private ObservableList<AnalysisTest> testList = FXCollections.observableArrayList();


    public ManageTestsController(AnalysisTestDaoImpl testDao, ReferenceValueDaoImpl refDao) {
        this.testDao = testDao;
        this.refDao = refDao;
    }

    @FXML
    void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idTest"));
        colName.setCellValueFactory(new PropertyValueFactory<>("testName"));
        colBiomaterial.setCellValueFactory(new PropertyValueFactory<>("biomaterial"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("executionTimeHours"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        loadTests();
    }

    private void loadTests() {
        List<AnalysisTest> list = testDao.getAll();
        testList.setAll(list);
        testsTable.setItems(testList);
    }

    @FXML
    void onAddTest(ActionEvent event) {
        AnalysisTest newTest = showTestDialog(null);
        if (newTest != null) {
            testDao.add(newTest);
            loadTests();
        }
    }

    @FXML
    void onEditTest(ActionEvent event) {
        AnalysisTest selected = testsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Выберите анализ для редактирования");
            return;
        }
        AnalysisTest edited = showTestDialog(selected);
        if (edited != null) {
            testDao.update(edited);
            loadTests();
        }
    }

    @FXML
    void onDeleteTest(ActionEvent event) {
        AnalysisTest selected = testsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Удалить анализ " + selected.getTestName() + "?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    testDao.delete(selected);
                    loadTests();
                }
            });
        }
    }

    @FXML
    void onManageReferences(ActionEvent event) {
        AnalysisTest selected = testsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Сначала выберите анализ");
            return;
        }
        showReferenceDialog(selected);
    }

    // Диалог для теста
    private AnalysisTest showTestDialog(AnalysisTest existing) {
        Dialog<AnalysisTest> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Новый анализ" : "Редактирование");
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        TextField nameField = new TextField();
        TextField biomaterialField = new TextField();
        TextField timeField = new TextField();
        TextField priceField = new TextField();
        TextField unitField = new TextField();

        if (existing != null) {
            nameField.setText(existing.getTestName());
            biomaterialField.setText(existing.getBiomaterial());
            timeField.setText(String.valueOf(existing.getExecutionTimeHours()));
            priceField.setText(existing.getPrice().toPlainString());
            unitField.setText(existing.getUnit());
        }

        grid.add(new Label("Название*:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Биоматериал*:"), 0, 1);
        grid.add(biomaterialField, 1, 1);
        grid.add(new Label("Срок (ч)*:"), 0, 2);
        grid.add(timeField, 1, 2);
        grid.add(new Label("Цена*:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Ед.изм.*:"), 0, 4);
        grid.add(unitField, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                if (nameField.getText().isEmpty() || biomaterialField.getText().isEmpty() ||
                        timeField.getText().isEmpty() || priceField.getText().isEmpty() || unitField.getText().isEmpty()) {
                    showAlert("Заполните все поля");
                    return null;
                }
                AnalysisTest test = existing != null ? existing : new AnalysisTest();
                test.setTestName(nameField.getText());
                test.setBiomaterial(biomaterialField.getText());
                test.setExecutionTimeHours(Integer.parseInt(timeField.getText()));
                test.setPrice(new BigDecimal(priceField.getText()));
                test.setUnit(unitField.getText());
                return test;
            }
            return null;
        });

        Optional<AnalysisTest> result = dialog.showAndWait();
        return result.orElse(null);
    }

    // Диалог для референсных значений
    private void showReferenceDialog(AnalysisTest test) {
        List<ReferenceValue> refs = refDao.findByTestId(test.getIdTest());
        // Можно отобразить в TableView внутри диалога и дать кнопки добавить/удалить/редактировать
        // Для простоты покажем сообщение с количеством
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Референсные значения для " + test.getTestName());
        alert.setHeaderText("Найдено записей: " + refs.size());
        alert.showAndWait();
        // В реальном проекте здесь нужно открыть окно управления нормами.
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }
}