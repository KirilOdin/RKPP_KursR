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

        EmployeeDaoImpl dao = new EmployeeDaoImpl();
        Employee emp = dao.findByLogin(login);
//        System.out.println(emp.getIdEmployee());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

        if (emp == null || !encoder.matches(password, emp.getPasswordHash())) {
            messageLabel.setText("Неверный логин или пароль");
            return;
        }

//        System.out.println(getClass().getResource("ru/kafpin124/rkpp_kursr/main_tab.fxml"));

        System.out.println(getClass().getResource("/main_tab.fxml"));
        System.out.println(getClass().getResource("main_tab.fxml"));
        System.out.println(getClass().getClassLoader().getResource("main_tab.fxml"));
        // Открываем главное окно
        try {
            Locale locale = Locale.getDefault(); // или new Locale("de", "DE")
            ResourceBundle bundle = ResourceBundle.getBundle("text", locale);
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("main_tab.fxml"), bundle);
            Parent root = loader.load();
            MainTabController mainCtrl = loader.getController();
            mainCtrl.setCurrentUser(emp);  // передаём пользователя
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            // Закрываем окно логина
            ((Stage) btnLogin.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}