package ru.kafpin124.rkpp_kursr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Locale locale = new Locale("de", "DE");
        Locale.setDefault(locale);
        ResourceBundle bundle = ResourceBundle.getBundle("text", Locale.getDefault());
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main_tab.fxml"), bundle);
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
