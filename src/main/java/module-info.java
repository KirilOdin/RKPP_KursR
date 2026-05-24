module ru.kafpin124.rkpp_kursr {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires jbcrypt;
    requires spring.security.crypto;


    opens ru.kafpin124.rkpp_kursr to javafx.fxml;
    exports ru.kafpin124.rkpp_kursr;

    opens ru.kafpin124.rkpp_kursr.contoller to javafx.fxml;
    exports ru.kafpin124.rkpp_kursr.contoller;


}