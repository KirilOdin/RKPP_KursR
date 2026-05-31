package ru.kafpin124.rkpp_kursr.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.dao.AnalysisTestDao;
import ru.kafpin124.rkpp_kursr.dao.ReferenceValueDao;
import ru.kafpin124.rkpp_kursr.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

//@NoArgsConstructor(force = true)
public class ManageTestsController {

    @FXML private TableView<AnalysisTest> testsTable;
    @FXML private TableColumn<AnalysisTest, Long> colId;
    @FXML private TableColumn<AnalysisTest, String> colName, colBiomaterial;
    @FXML private TableColumn<AnalysisTest, Integer> colTime;
    @FXML private TableColumn<AnalysisTest, BigDecimal> colPrice;

    private final AnalysisTestDao testDao;
    private final ReferenceValueDao refDao;

    private ObservableList<AnalysisTest> testList = FXCollections.observableArrayList();

    public static final Logger logger = LoggerFactory.getLogger(ManageTestsController.class);


    public ManageTestsController(AnalysisTestDao testDao, ReferenceValueDao refDao) {
        this.testDao = testDao;
        this.refDao = refDao;
        logger.debug("ManageTestsController создан");
    }

    @FXML
    void initialize() {
        logger.info("Инициализация ManageTestsController");
        colId.setCellValueFactory(new PropertyValueFactory<>("idTest"));
        colName.setCellValueFactory(new PropertyValueFactory<>("testName"));
        colBiomaterial.setCellValueFactory(new PropertyValueFactory<>("biomaterial"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("executionTimeHours"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        logger.debug("Колонки таблицы настроены");
        loadTests();
    }

    private void loadTests() {
        logger.debug("Загрузка списка анализов");
        List<AnalysisTest> list = testDao.getAll();
        testList.setAll(list);
        testsTable.setItems(testList);
        logger.info("Загружено {} анализов", list.size());
    }

    @FXML
    void onAddTest(ActionEvent event) {
        logger.info("Открытие диалога добавления нового анализа");
        AnalysisTest newTest = showTestDialog(null);
        if (newTest != null) {
            testDao.add(newTest);
            logger.info("Добавлен новый анализ: {} (ID={})", newTest.getTestName(), newTest.getIdTest());
            loadTests();
        } else {
            logger.debug("Добавление анализа отменено пользователем");
        }
    }

    @FXML
    void onEditTest(ActionEvent event) {
        AnalysisTest selected = testsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            logger.warn("Попытка редактирования без выбора анализа");
            showAlert("Выберите анализ для редактирования");
            return;
        }
        logger.info("Редактирование анализа: {} (ID={})", selected.getTestName(), selected.getIdTest());
        AnalysisTest edited = showTestDialog(selected);
        if (edited != null) {
            testDao.update(edited);
            logger.info("Анализ {} обновлён", edited.getTestName());
            loadTests();
        } else {
            logger.debug("Редактирование анализа отменено пользователем");
        }
    }

    @FXML
    void onDeleteTest(ActionEvent event) {
        AnalysisTest selected = testsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            logger.warn("Попытка удаления без выбора анализа");
            showAlert("Выберите анализ для удаления");
            return;
        }
        logger.info("Запрос на удаление анализа: {} (ID={})", selected.getTestName(), selected.getIdTest());
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Удалить анализ " + selected.getTestName() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                testDao.delete(selected);
                logger.info("Анализ {} удалён", selected.getTestName());
                loadTests();
            } else {
                logger.debug("Удаление анализа отменено пользователем");
            }
        });
    }

    @FXML
    void onManageReferences(ActionEvent event) {
        AnalysisTest selected = testsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            logger.warn("Попытка управления нормами без выбора анализа");
            showAlert("Сначала выберите анализ");
            return;
        }
        logger.info("Управление референсными значениями для анализа: {} (ID={})", selected.getTestName(), selected.getIdTest());
        showReferenceDialog(selected);
    }

    // Диалог для теста
    private AnalysisTest showTestDialog(AnalysisTest existing) {
        Dialog<AnalysisTest> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Новый анализ" : "Редактирование");
        logger.debug("Отображение диалога {} анализа", existing == null ? "создания" : "редактирования");

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
                    logger.warn("Валидация не пройдена: не все поля заполнены");
                    showAlert("Заполните все поля");
                    return null;
                }
                AnalysisTest test = existing != null ? existing : new AnalysisTest();
                test.setTestName(nameField.getText());
                test.setBiomaterial(biomaterialField.getText());
                test.setExecutionTimeHours(Integer.parseInt(timeField.getText()));
                test.setPrice(new BigDecimal(priceField.getText()));
                test.setUnit(unitField.getText());
                logger.debug("Данные из диалога собраны успешно");
                return test;
            }
            return null;
        });

        Optional<AnalysisTest> result = dialog.showAndWait();
        return result.orElse(null);
    }

    // Диалог для референсных значений
    private void showReferenceDialog(AnalysisTest test) {
        logger.debug("Загрузка референсных значений для анализа ID={}", test.getIdTest());
        List<ReferenceValue> refs = refDao.findByTestId(test.getIdTest());
        logger.info("Найдено {} референсных значений для анализа {}", refs.size(), test.getTestName());

        List<ReferenceValue> refList = refDao.findByTestId(test.getIdTest());
        ObservableList<ReferenceValue> items = FXCollections.observableArrayList(refList);

        TableView<ReferenceValue> refTable = new TableView<>();
        refTable.setEditable(false);

        TableColumn<ReferenceValue, String> genderCol = new TableColumn<>("Пол");
        genderCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getGenderApplicable() == 'м' ? "Мужской" : "Женский"));


        TableColumn<ReferenceValue, String> ageCol = new TableColumn<>("Возраст");
        ageCol.setCellValueFactory(cellData -> {
            Integer min = cellData.getValue().getAgeMin();
            Integer max = cellData.getValue().getAgeMax();
            String text = (min == null ? "0" : min) + " – " + (max == null ? "∞" : max);
            return new javafx.beans.property.SimpleStringProperty(text);
        });


        TableColumn<ReferenceValue, String> valueCol = new TableColumn<>("Значения");
        valueCol.setCellValueFactory(cellData -> {
            ReferenceValue rv = cellData.getValue();
            if (rv.getRefValueMin() != null && rv.getRefValueMax() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        rv.getRefValueMin().stripTrailingZeros().toPlainString() + " – " +
                                rv.getRefValueMax().stripTrailingZeros().toPlainString());
            } else {
                return new javafx.beans.property.SimpleStringProperty(rv.getRefText() != null ? rv.getRefText() : "");
            }
        });

        refTable.getColumns().addAll(genderCol, ageCol, valueCol);
        refTable.setItems(items);


        Button btnAdd = new Button("Добавить");
        Button btnEdit = new Button("Редактировать");
        Button btnDelete = new Button("Удалить");


        btnAdd.setOnAction(e -> {
            ReferenceValue newRef = showReferenceEditDialog(test, null);
            if (newRef != null) {
                refDao.add(newRef);
                items.setAll(refDao.findByTestId(test.getIdTest()));
            }
        });

        btnEdit.setOnAction(e -> {
            ReferenceValue selected = refTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Выберите запись для редактирования");
                return;
            }
            ReferenceValue edited = showReferenceEditDialog(test, selected);
            if (edited != null) {
                refDao.update(edited);
                items.setAll(refDao.findByTestId(test.getIdTest()));
            }
        });

        btnDelete.setOnAction(e -> {
            ReferenceValue selected = refTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Выберите запись для удаления");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Удалить выбранное значение?");
            confirm.showAndWait().ifPresent(res -> {
                if (res == ButtonType.OK) {
                    refDao.delete(selected);
                    items.setAll(refDao.findByTestId(test.getIdTest()));
                }
            });
        });


        HBox buttonBox = new HBox(10, btnAdd, btnEdit, btnDelete);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        VBox root = new VBox(10, refTable, buttonBox);
        root.setPadding(new javafx.geometry.Insets(10));

        Stage dialogStage = new Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Референсные значения: " + test.getTestName());
        dialogStage.setScene(new javafx.scene.Scene(root, 500, 400));
        dialogStage.showAndWait();
    }
        //Нужно открыть окно управления нормами (с TableView, добавлением/удалением)!
//
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Референсные значения для " + test.getTestName());
//        alert.setHeaderText("Найдено записей: " + refs.size());
//        alert.showAndWait();
//        logger.debug("Диалог референсных значений закрыт");

    private ReferenceValue showReferenceEditDialog(AnalysisTest test, ReferenceValue existing) {
        Dialog<ReferenceValue> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Добавить норму" : "Редактировать норму");
        dialog.setHeaderText("Анализ: " + test.getTestName());

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        ComboBox<String> genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("Мужской", "Женский");
        genderCombo.setValue(existing != null ? (existing.getGenderApplicable() == 'м' ? "Мужской" : "Женский") : "Мужской");

        TextField ageMinField = new TextField();
        TextField ageMaxField = new TextField();
        TextField valueMinField = new TextField();
        TextField valueMaxField = new TextField();
        TextField textField = new TextField(); // для текстовых норм (необязательно)

        if (existing != null) {
            if (existing.getAgeMin() != null) ageMinField.setText(String.valueOf(existing.getAgeMin()));
            if (existing.getAgeMax() != null) ageMaxField.setText(String.valueOf(existing.getAgeMax()));
            if (existing.getRefValueMin() != null) valueMinField.setText(existing.getRefValueMin().toPlainString());
            if (existing.getRefValueMax() != null) valueMaxField.setText(existing.getRefValueMax().toPlainString());
            if (existing.getRefText() != null) textField.setText(existing.getRefText());
        }

        grid.add(new Label("Пол:"), 0, 0);
        grid.add(genderCombo, 1, 0);
        grid.add(new Label("Мин. возраст:"), 0, 1);
        grid.add(ageMinField, 1, 1);
        grid.add(new Label("Макс. возраст:"), 0, 2);
        grid.add(ageMaxField, 1, 2);
        grid.add(new Label("Мин. значение:"), 0, 3);
        grid.add(valueMinField, 1, 3);
        grid.add(new Label("Макс. значение:"), 0, 4);
        grid.add(valueMaxField, 1, 4);
        grid.add(new Label("Текстовое значение (если не числовое):"), 0, 5);
        grid.add(textField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    ReferenceValue rv = existing != null ? existing : new ReferenceValue();
                    rv.setTest(test);
                    rv.setGenderApplicable(genderCombo.getValue().equals("Мужской") ? 'м' : 'ж');

                    // Возраст
                    String ageMin = ageMinField.getText().trim();
                    if (!ageMin.isEmpty()) rv.setAgeMin(Integer.parseInt(ageMin));
                    else rv.setAgeMin(null);
                    String ageMax = ageMaxField.getText().trim();
                    if (!ageMax.isEmpty()) rv.setAgeMax(Integer.parseInt(ageMax));
                    else rv.setAgeMax(null);

                    // Значения
                    String valMin = valueMinField.getText().trim();
                    String valMax = valueMaxField.getText().trim();
                    String text = textField.getText().trim();

                    if (!valMin.isEmpty() && !valMax.isEmpty()) {
                        rv.setRefValueMin(new BigDecimal(valMin));
                        rv.setRefValueMax(new BigDecimal(valMax));
                        rv.setRefText(null);
                    } else if (!text.isEmpty()) {
                        rv.setRefValueMin(null);
                        rv.setRefValueMax(null);
                        rv.setRefText(text);
                    } else {
                        showAlert("Заполните либо числовые мин/макс, либо текстовое значение");
                        return null;
                    }
                    return rv;
                } catch (NumberFormatException e) {
                    showAlert("Ошибка в числовых полях");
                    return null;
                }
            }
            return null;
        });

        Optional<ReferenceValue> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void showAlert(String msg) {
        logger.debug("Показ предупреждения: {}", msg);
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }
}
