package ru.kafpin124.rkpp_kursr.contoller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import lombok.NoArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.dao.EmployeeDao;
import ru.kafpin124.rkpp_kursr.dao.impl.EmployeeDaoImpl;
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

    //TODO: Добавить логирование!

    public static final Logger logger = LoggerFactory.getLogger(ManageEmployeesController.class);

    public ManageEmployeesController(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    @FXML
    void initialize() {
        // Привязываем колонки к свойствам модели
        colId.setCellValueFactory(new PropertyValueFactory<>("idEmployee"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colMiddleName.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        loadEmployees();
    }

    private void loadEmployees() {
        List<Employee> list = employeeDao.getAll();
        employeeList.setAll(list);
        employeesTable.setItems(employeeList);
    }

    @FXML
    void onAddEmployee(ActionEvent event) {
        Employee newEmp = showEmployeeDialog(null);
        if (newEmp != null) {
            // хэшируем пароль перед сохранением
            String rawPassword = newEmp.getPasswordHash(); // временно храним сырой пароль
            String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
            newEmp.setPasswordHash(hash);
            employeeDao.add(newEmp, rawPassword);
            loadEmployees();
        }
    }

    @FXML
    void onEditEmployee(ActionEvent event) {
        Employee selected = employeesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Выберите сотрудника для редактирования");
            return;
        }
        Employee edited = showEmployeeDialog(selected);
        if (edited != null) {
            // если пароль изменился (пользователь ввёл новый) – хэшируем
            if (edited.getPasswordHash() != null && !edited.getPasswordHash().isEmpty()) {
                String hash = BCrypt.hashpw(edited.getPasswordHash(), BCrypt.gensalt(12));
                edited.setPasswordHash(hash);
            } else {
                // оставляем старый хэш
                edited.setPasswordHash(selected.getPasswordHash());
            }
            employeeDao.update(edited);
            loadEmployees();
        }
    }

    @FXML
    void onDeleteEmployee(ActionEvent event) {
        Employee selected = employeesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Удалить сотрудника " + selected.getLastName() + "?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    employeeDao.delete(selected);
                    loadEmployees();
                }
            });
        }
    }

    // Диалог для ввода/редактирования сотрудника
    private Employee showEmployeeDialog(Employee existing) {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Новый сотрудник" : "Редактирование");
        dialog.setHeaderText("Введите данные сотрудника");

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

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
                return emp;
            }
            return null;
        });

        Optional<Employee> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }
}