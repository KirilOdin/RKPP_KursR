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

    public static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    public LoginController(EmployeeDao employeeDao, BCryptPasswordEncoder encoder) {
        this.employeeDao = employeeDao;
        this.encoder = encoder;
        logger.debug("LoginController создан с переданными DAO и encoder");
    }

    public LoginController() {
        this.employeeDao = new EmployeeDaoImpl();
        this.encoder = new BCryptPasswordEncoder();
        logger.debug("LoginController создан с DAO и encoder по умолчанию");
    }

    @FXML
    void initialize() {
        logger.info("Инициализация окна входа");
        languageCombo.getItems().addAll(
                new Locale("en"),
                new Locale("ru", "RU"),
                new Locale("de", "DE")
        );
        languageCombo.setValue(LocalizationService.getCurrentLocale());
        logger.debug("ComboBox языка заполнен, текущая локаль: {}", LocalizationService.getCurrentLocale());

        languageCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                logger.info("Пользователь изменил язык с {} на {}", oldVal, newVal);
                LocalizationService.changeLocale(newVal);
                // Перезагружаем окно входа с новым языком
                reloadLoginWindow();
            }
        });


        btnLogin.setOnAction(e -> login());
        btnClose.setOnAction(e -> {
            logger.info("Закрытие окна входа по кнопке 'Закрыть'");
            ((Stage) btnClose.getScene().getWindow()).close();
        });

    }

    private void login() {
        String login = loginField.getText().trim();
        String password = passwordField.getText();
        logger.debug("Попытка входа: логин={}, режим аутентификации={}",
                login, rbBCrypt.isSelected() ? "BCrypt" : "DB");
        if (login.isEmpty() || password.isEmpty()) {
            logger.warn("Пустой логин или пароль при попытке входа");
            messageLabel.setText(LocalizationService.get("loginCont.messageLabel.emptyValuesWarning"));
            return;
        }

        boolean useBCrypt = rbBCrypt.isSelected();
        Employee emp = null;

        if (useBCrypt) {
            try {
                DBHelper.initConnection(DBHelper.getAppUser(), DBHelper.getAppPassword());
            } catch (SQLException e) {
                logger.error("Не удалось подключиться к БД под учётной записью приложения", e);
                messageLabel.setText(LocalizationService.get("loginCont.messageLabel.dbConnectionError"));
                return;
            }
            emp = employeeDao.findByLogin(login);
            if (emp == null || !encoder.matches(password, emp.getPasswordHash())) {
                logger.warn("Неудачная попытка входа (BCrypt): логин '{}' не найден или пароль не совпадает", login);
                messageLabel.setText(LocalizationService.get("loginCont.messageLabel.wrongLoginWithHash"));
                return;
            }
            logger.info("Пользователь {} успешно аутентифицирован через BCrypt и данные в системе", login);
        } else {
                try {
                    DBHelper.initConnection(login, password);
                    logger.debug("Соединение с БД установлено под пользователем {}", login);
                } catch (SQLException e) {
                    logger.warn("Неудачная попытка входа (DB): логин '{}' – ошибка подключения", login);
                    messageLabel.setText(LocalizationService.get("loginCont.messageLabel.wrongLoginDB"));
                    return;
                }

                emp = employeeDao.findByLogin(login);
                if (emp == null) {
                    logger.warn("Пользователь {} найден в БД, но отсутствует в таблице employees", login);
                    messageLabel.setText(LocalizationService.get("loginCont.messageLabel.employeeNotFound"));
                    return;
                }
                logger.info("Пользователь {} успешно аутентифицирован через СУБД", login);
            }

//        System.out.println(getClass().getResource("ru/kafpin124/rkpp_kursr/main_tab.fxml"));

        // Открытие основного окна АРМ
        try {
            logger.info("Загрузка главного окна для пользователя {} (роль: {})", emp.getLogin(), emp.getRole());
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
                logger.error("Ошибка: сотрудник не получен, хотя аутентификация пройдена");
                messageLabel.setText(LocalizationService.get("loginCont.messageLabel.employeeNotReceived"));
                return;
            }

            Parent root = loader.load();
            MainTabController mainCtrl = loader.getController();
            mainCtrl.setCurrentUser(emp);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(LocalizationService.get("mainCont.sceneTitle"));
            stage.show();
            logger.info("Главное окно успешно открыто для пользователя {}", emp.getLogin());

            // Закрытие окна входа
            ((Stage) btnLogin.getScene().getWindow()).close();
            logger.debug("Окно входа закрыто");
        } catch (IOException e) {
            logger.error("Ошибка загрузки главного окна", e);
            e.printStackTrace();
            messageLabel.setText(LocalizationService.get("loginCont.messageLabel.loadSceneError"));
        }
    }

    private void reloadLoginWindow() {
        logger.info("Перезагрузка окна входа для применения нового языка");
        try {
            Stage currentStage = (Stage) languageCombo.getScene().getWindow();
            currentStage.close();
            logger.debug("Текущее окно входа закрыто");

            ResourceBundle bundle = LocalizationService.getBundle();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ru/kafpin124/rkpp_kursr/login.fxml"),
                    bundle
            );
            Stage newStage = new Stage();
            newStage.setScene(new Scene(loader.load()));
            newStage.setTitle(LocalizationService.get("loginCont.sceneTitle"));
            newStage.show();
            logger.info("Новое окно входа с языком {} отображено", LocalizationService.getCurrentLocale());
        } catch (IOException e) {
            logger.error("Не удалось перезагрузить окно входа", e);
            e.printStackTrace();
        }
    }
}