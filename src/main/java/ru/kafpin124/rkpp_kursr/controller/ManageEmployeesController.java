package ru.kafpin124.rkpp_kursr.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.dao.EmployeeDao;
import ru.kafpin124.rkpp_kursr.model.Employee;

import java.util.List;
import java.util.Optional;

//@NoArgsConstructor(force = true)
public class ManageEmployeesController {

    @FXML private TableView<Employee> employeesTable;
    @FXML private TableColumn<Employee, Long> colId;
    @FXML private TableColumn<Employee, String> colLastName, colFirstName, colMiddleName, colPosition, colRole;

    private final EmployeeDao employeeDao;
    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    public static final Logger logger = LoggerFactory.getLogger(ManageEmployeesController.class);

    public ManageEmployeesController(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
        logger.debug("ManageEmployeesController создан");
    }

    @FXML
    void initialize() {
        logger.info("Инициализация ManageEmployeesController");
        colId.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getIdEmployee()));
        colLastName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLastName()));
        colFirstName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFirstName()));
        colMiddleName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMiddleName()));
        colPosition.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPosition()));
        colRole.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRole()));
        logger.debug("Колонки таблицы настроены");

        loadEmployees();
    }

    private void loadEmployees() {
        logger.debug("Загрузка списка сотрудников");
        List<Employee> list = employeeDao.getAll();
        employeeList.setAll(list);
        employeesTable.setItems(employeeList);
        logger.info("Загружено {} сотрудников", list.size());
    }

    @FXML
    void onAddEmployee(ActionEvent event) {
        logger.info("Открытие диалога добавления нового сотрудника");
        Employee newEmp = showEmployeeDialog(null);
        if (newEmp != null) {
            // хэшируем пароль перед сохранением
            String rawPassword = newEmp.getPasswordHash(); // временно храним сырой пароль
            String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
            newEmp.setPasswordHash(hash);
            employeeDao.add(newEmp, rawPassword);
            logger.info("Добавлен новый сотрудник: {} {} (логин: {})", newEmp.getLastName(), newEmp.getFirstName(), newEmp.getLogin());
            loadEmployees();
        } else {
            logger.debug("Добавление сотрудника отменено пользователем");
        }
    }

    @FXML
    void onEditEmployee(ActionEvent event) {
        Employee selected = employeesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            logger.warn("Попытка редактирования без выбора сотрудника");
            showAlert("Выберите сотрудника для редактирования");
            return;
        }
        logger.info("Редактирование сотрудника: {} {} (ID={})", selected.getLastName(), selected.getFirstName(), selected.getIdEmployee());
        Employee edited = showEmployeeDialog(selected);
        if (edited != null) {
            if (edited.getPasswordHash() != null && !edited.getPasswordHash().isEmpty()) {
                String hash = BCrypt.hashpw(edited.getPasswordHash(), BCrypt.gensalt(12));
                edited.setPasswordHash(hash);
                logger.debug("Пароль изменён, новый хэш установлен");
            } else {
                // оставляем старый хэш
                edited.setPasswordHash(selected.getPasswordHash());
            }
            employeeDao.update(edited);
            logger.info("Сотрудник {} {} обновлён", edited.getLastName(), edited.getFirstName());
            loadEmployees();
        } else {
            logger.debug("Редактирование сотрудника отменено пользователем");
        }
    }

    @FXML
    void onDeleteEmployee(ActionEvent event) {
        Employee selected = employeesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            logger.warn("Попытка удаления без выбора сотрудника");
            showAlert("Выберите сотрудника для удаления");
            return;
        }
        logger.info("Запрос на удаление сотрудника: {} {} (ID={})",
                selected.getLastName(), selected.getFirstName(), selected.getIdEmployee());
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Удалить сотрудника " + selected.getLastName() + " " + selected.getFirstName() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                employeeDao.delete(selected);
                logger.info("Сотрудник {} {} удалён", selected.getLastName(), selected.getFirstName());
                loadEmployees();
            } else {
                logger.debug("Удаление сотрудника отменено пользователем");
            }
        });
    }

    // Диалог для ввода/редактирования сотрудника
    private Employee showEmployeeDialog(Employee existing) {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Новый сотрудник" : "Редактирование");
        dialog.setHeaderText("Введите данные сотрудника");
        logger.debug("Отображение диалога {} сотрудника", existing == null ? "создания" : "редактирования");

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField lastNameField = new TextField();
        TextField firstNameField = new TextField();
        TextField middleNameField = new TextField();
        TextField loginField = new TextField();
        PasswordField passwordField = new PasswordField();
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("admin", "lab_assistant", "lab_doctor");
        TextField positionField = new TextField();

        if (existing != null) {
            lastNameField.setText(existing.getLastName());
            firstNameField.setText(existing.getFirstName());
            middleNameField.setText(existing.getMiddleName() != null ? existing.getMiddleName() : "");
            loginField.setText(existing.getLogin());
            roleCombo.setValue(existing.getRole());
            positionField.setText(existing.getPosition());
            passwordField.setPromptText("Новый пароль (если нужно сменить)");
        }

        grid.add(new Label("Фамилия*:"), 0, 0);
        grid.add(lastNameField, 1, 0);
        grid.add(new Label("Имя*:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Отчество:"), 0, 2);
        grid.add(middleNameField, 1, 2);
        grid.add(new Label("Логин*:"), 0, 3);
        grid.add(loginField, 1, 3);
        grid.add(new Label("Пароль*:"), 0, 4);
        grid.add(passwordField, 1, 4);
        grid.add(new Label("Роль*:"), 0, 5);
        grid.add(roleCombo, 1, 5);
        grid.add(new Label("Должность*:"), 0, 6);
        grid.add(positionField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (lastNameField.getText().isEmpty() || firstNameField.getText().isEmpty() ||
                        loginField.getText().isEmpty() || roleCombo.getValue() == null ||
                        (existing == null && passwordField.getText().isEmpty())) {
                    logger.warn("Валидация не пройдена: не все обязательные поля заполнены");
                    showAlert("Заполните обязательные поля (фамилия, имя, логин, роль, пароль)");
                    return null;
                }
                Employee emp = existing != null ? existing : new Employee();
                emp.setLastName(lastNameField.getText());
                emp.setFirstName(firstNameField.getText());
                emp.setMiddleName(middleNameField.getText());
                emp.setLogin(loginField.getText());
                emp.setRole(roleCombo.getValue());
                emp.setPosition(positionField.getText());
                if (!passwordField.getText().isEmpty()) {
                    emp.setPasswordHash(passwordField.getText()); // сырой пароль, будет хэширован снаружи
                }
                logger.debug("Данные из диалога собраны успешно");
                return emp;
            }
            return null;
        });

        Optional<Employee> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void showAlert(String msg) {
        logger.debug("Показ предупреждения: {}", msg);
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }
}