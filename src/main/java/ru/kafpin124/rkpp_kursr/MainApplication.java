package ru.kafpin124.rkpp_kursr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.kafpin124.rkpp_kursr.contoller.LoginController;
import ru.kafpin124.rkpp_kursr.dao.EmployeeDao;
import ru.kafpin124.rkpp_kursr.dao.impl.EmployeeDaoImpl;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Locale locale = new Locale("de", "DE");
        Locale.setDefault(locale);
        ResourceBundle bundle = ResourceBundle.getBundle("text", Locale.getDefault());

        EmployeeDao employeeDao = new EmployeeDaoImpl();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"), bundle);

        fxmlLoader.setControllerFactory(c -> {
            if (c == LoginController.class) {
                return new LoginController(employeeDao, encoder);
            }
            return null;
        });

        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Вход в АРМ лаборанта");
        stage.setScene(scene);
        stage.show();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

//        System.out.println("admin1: " + passwordEncoder.encode("admin"));
//        System.out.println("admin: " + BCrypt.hashpw("admin", BCrypt.gensalt(12)));
    }
}
