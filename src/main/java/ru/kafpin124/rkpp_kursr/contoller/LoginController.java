package ru.kafpin124.rkpp_kursr.contoller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.kafpin124.rkpp_kursr.HelloApplication;
import ru.kafpin124.rkpp_kursr.dao.EmployeeDao;
import ru.kafpin124.rkpp_kursr.dao.impl.EmployeeDaoImpl;
import ru.kafpin124.rkpp_kursr.model.Employee;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController {
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Button btnLogin, btnClose;


    private final EmployeeDao employeeDao;
    private final BCryptPasswordEncoder encoder;

    // Внедряем зависимости через конструктор
    public LoginController(EmployeeDao employeeDao, BCryptPasswordEncoder encoder) {
        this.employeeDao = employeeDao;
        this.encoder = encoder;
    }

    @FXML
    void initialize() {
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

        Employee emp = employeeDao.findByLogin(login);

        if (emp == null || !encoder.matches(password, emp.getPasswordHash())) {
            messageLabel.setText("Неверный логин или пароль");
            return;
        }

//        System.out.println(getClass().getResource("ru/kafpin124/rkpp_kursr/main_tab.fxml"));

        // Открытие основного окна АРМ
        try {
            Locale locale = Locale.getDefault(); // или new Locale("de", "DE")
            ResourceBundle bundle = ResourceBundle.getBundle("text", locale);
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("main_tab.fxml"), bundle);
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
}