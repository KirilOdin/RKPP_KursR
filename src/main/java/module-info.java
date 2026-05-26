module ru.kafpin124.rkpp_kursr {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires jbcrypt;
    requires spring.security.crypto;
    requires java.prefs;


    opens ru.kafpin124.rkpp_kursr to javafx.fxml;
    exports ru.kafpin124.rkpp_kursr;

    opens ru.kafpin124.rkpp_kursr.contoller to javafx.fxml;
    exports ru.kafpin124.rkpp_kursr.contoller;
    exports ru.kafpin124.rkpp_kursr.util;
    opens ru.kafpin124.rkpp_kursr.util to javafx.fxml;


}