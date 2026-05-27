package ru.kafpin124.rkpp_kursr.contoller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.kafpin124.rkpp_kursr.util.LocalizationService;
import ru.kafpin124.rkpp_kursr.util.DBHelper;
import ru.kafpin124.rkpp_kursr.MainApplication;
import ru.kafpin124.rkpp_kursr.dao.EmployeeDao;
import ru.kafpin124.rkpp_kursr.dao.impl.*;
import ru.kafpin124.rkpp_kursr.model.Employee;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController {
    @FXML private ComboBox<Locale> languageCombo;
    @FXML private RadioButton rbDB;
    @FXML private ToggleGroup authModeGroup;
    @FXML private RadioButton rbBCrypt;
    @FXML private Label lbPassword;
    @FXML private Label lbLogin;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Button btnLogin, btnClose;


    private final EmployeeDao employeeDao;
    private final BCryptPasswordEncoder encoder;

    //TODO: Добавить логирование!

    public static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    public LoginController(EmployeeDao employeeDao, BCryptPasswordEncoder encoder) {
        this.employeeDao = employeeDao;
        this.encoder = encoder;
    }

    public LoginController() {
        this.employeeDao = new EmployeeDaoImpl();
        this.encoder = new BCryptPasswordEncoder();
    }

    @FXML
    void initialize() {
        languageCombo.getItems().addAll(
                new Locale("en"),
                new Locale("ru", "RU"),
                new Locale("de", "DE")
        );
        languageCombo.setValue(LocalizationService.getCurrentLocale());

        languageCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                LocalizationService.changeLocale(newVal);
                // Перезагружаем окно входа с новым языком
                reloadLoginWindow();
            }
        });


        btnLogin.setOnAction(e -> login());
        btnClose.setOnAction(e -> ((Stage) btnClose.getScene().getWindow()).close());

    }

    private void login() {
        String login = loginField.getText().trim();
        String password = passwordField.getText();
        if (login.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Заполните логин и пароль");
            return;
        }

        boolean useBCrypt = rbBCrypt.isSelected();
        Employee emp = null;

        if (useBCrypt) {
            emp = employeeDao.findByLogin(login);
            if (emp == null || !encoder.matches(password, emp.getPasswordHash())) {
                messageLabel.setText("Неверный логин или пароль");
                return;
            }
        } else {
                try {
                    DBHelper.initConnection(login, password);
                } catch (SQLException e) {
                    messageLabel.setText("Неверный логин или пароль (СУБД)");
                    return;
                }

                emp = employeeDao.findByLogin(login);
                if (emp == null) {
                    messageLabel.setText("Сотрудник не найден в справочнике");
                    return;
                }
            }

//        System.out.println(getClass().getResource("ru/kafpin124/rkpp_kursr/main_tab.fxml"));

        // Открытие основного окна АРМ
        try {
            EmployeeDaoImpl employeeDao = new EmployeeDaoImpl();
            OrderDaoImpl orderDao = new OrderDaoImpl();
            PatientDaoImpl patientDao = new PatientDaoImpl();
            AnalysisTestDaoImpl testDao = new AnalysisTestDaoImpl();
            OrderItemDaoImpl itemDao = new OrderItemDaoImpl();
            SpecimenDaoImpl specimenDao = new SpecimenDaoImpl();
            ReportDaoImpl reportDao = new ReportDaoImpl();
            ReferenceValueDaoImpl refDao = new ReferenceValueDaoImpl();

            ResourceBundle bundle = ResourceBundle.getBundle("text", LocalizationService.getCurrentLocale());
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("main_tab.fxml"), bundle);

            loader.setControllerFactory(clazz -> {
                if (clazz == MainTabController.class) {
                    return new MainTabController(orderDao);
                } else if (clazz == OrdersListController.class) {
                    return new OrdersListController(orderDao);
                } else if (clazz == NewOrderController.class) {
                    return new NewOrderController(patientDao, testDao, orderDao, specimenDao, itemDao);
                } else if (clazz == NewResultController.class) {
                    return new NewResultController(orderDao, itemDao, refDao);
                } else if (clazz == VerificationController.class) {
                    return new VerificationController(orderDao, itemDao);
                } else if (clazz == ReportsController.class) {
                    return new ReportsController(reportDao);
                } else if (clazz == ManageEmployeesController.class) {
                    return new ManageEmployeesController(employeeDao);
                } else if (clazz == ManageTestsController.class) {
                    return new ManageTestsController(testDao, refDao);
                }
                // Для остальных (SelectPersonController и т.п.) можно возвращать null,
                // тогда будет использован стандартный конструктор без параметров.
                return null;
            });


            if (emp == null) {
                messageLabel.setText("Ошибка: данные сотрудника не получены");
                return;
            }

            Parent root = loader.load();
            MainTabController mainCtrl = loader.getController();
            mainCtrl.setCurrentUser(emp);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("АРМ лаборанта");
            stage.show();

            // Закрытие окна входа
            ((Stage) btnLogin.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Ошибка загрузки главного окна");
        }
    }

    private void reloadLoginWindow() {
        try {
            Stage currentStage = (Stage) languageCombo.getScene().getWindow();
            currentStage.close();

            ResourceBundle bundle = LocalizationService.getBundle();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ru/kafpin124/rkpp_kursr/login.fxml"),
                    bundle
            );
            Stage newStage = new Stage();
            newStage.setScene(new Scene(loader.load()));
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}