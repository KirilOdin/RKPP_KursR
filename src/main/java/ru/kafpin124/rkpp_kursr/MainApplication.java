package ru.kafpin124.rkpp_kursr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.kafpin124.rkpp_kursr.controller.LoginController;
import ru.kafpin124.rkpp_kursr.dao.EmployeeDao;
import ru.kafpin124.rkpp_kursr.dao.impl.EmployeeDaoImpl;
import ru.kafpin124.rkpp_kursr.util.LocalizationService;

import java.io.IOException;
import java.util.ResourceBundle;

public class MainApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);
    @Override
    public void start(Stage stage) throws IOException {
        LocalizationService.initFromPreferences();

        ResourceBundle bundle = ResourceBundle.getBundle("text", LocalizationService.getCurrentLocale());

        EmployeeDao employeeDao = new EmployeeDaoImpl();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"), bundle);

        fxmlLoader.setControllerFactory(c -> {
            if (c == LoginController.class) {
                return new LoginController(employeeDao, encoder);
            }
            return null;
        });

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(LocalizationService.get("loginCont.sceneTitle"));
        stage.setScene(scene);
        stage.show();
        logger.info("Приложение запущено");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

//        System.out.println("admin1: " + passwordEncoder.encode("admin"));
//        System.out.println("admin: " + BCrypt.hashpw("admin", BCrypt.gensalt(12)));
    }



}
