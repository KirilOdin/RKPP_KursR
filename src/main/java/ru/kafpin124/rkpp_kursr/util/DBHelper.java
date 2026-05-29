package ru.kafpin124.rkpp_kursr.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/RKPP_KR";
    private static final String APP_USER = "postgres";
    private static final String APP_PASSWORD = "postgres";

    private static Connection connection;
    private static String currentDbUser;
    private static String currentDbPassword;

    public static final Logger logger = LoggerFactory.getLogger(DBHelper.class);

    public static void initConnection(String user, String password) throws SQLException {
        logger.info("Попытка проверки подключения пользователем {}", user);
        try (Connection testConn = DriverManager.getConnection(DB_URL, user, password)) {
            currentDbUser = user;
            currentDbPassword = password;
            logger.info("Успешная проверка подключения для {}", user);
        } catch (SQLException e) {
            logger.error("Ошибка проверки подключения для {}: {}", user, e.getMessage());
            throw e;
        }

    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            logger.info("Установка нового подключения к БД как {}", APP_USER);
            connection = DriverManager.getConnection(DB_URL, APP_USER, APP_PASSWORD);
            logger.debug("Соединение установлено");
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Соединение с БД закрыто");
            } catch (SQLException e) {
                logger.warn("Ошибка при закрытии соединения: {}", e.getMessage());
            }
        }
    }
}