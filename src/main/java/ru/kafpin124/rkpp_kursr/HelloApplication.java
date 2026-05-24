package ru.kafpin124.rkpp_kursr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Locale locale = new Locale("de", "DE");
        Locale.setDefault(locale);
        ResourceBundle bundle = ResourceBundle.getBundle("text", Locale.getDefault());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"), bundle);
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

//        System.out.println("admin1: " + passwordEncoder.encode("admin"));
//        System.out.println("admin: " + BCrypt.hashpw("admin", BCrypt.gensalt(12)));
    }
}
