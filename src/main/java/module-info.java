module ru.kafpin124.rkpp_kursr {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires jbcrypt;
    requires spring.security.crypto;
    requires java.prefs;
    requires org.slf4j;


    opens ru.kafpin124.rkpp_kursr to javafx.fxml;
    exports ru.kafpin124.rkpp_kursr;

    opens ru.kafpin124.rkpp_kursr.controller to javafx.fxml;
    exports ru.kafpin124.rkpp_kursr.controller;
    exports ru.kafpin124.rkpp_kursr.util;
    opens ru.kafpin124.rkpp_kursr.util to javafx.fxml;

    opens ru.kafpin124.rkpp_kursr.model to javafx.base;
    opens ru.kafpin124.rkpp_kursr.dto to javafx.base;
}