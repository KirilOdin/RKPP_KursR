package ru.kafpin124.rkpp_kursr.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * A utility class for working with a PostgreSQL database connection.
 * <p>
 *     Connection parameters (URL, application name and password) are downloaded from the file
 *     {@code /config.properties} in the static block.
 * </p>
 * <p>
 *     The main purpose is to authenticate the user through a DBMS and provide
 *     {@link Connection} for the DAO layer.
 * </p>
 *
 * <p>
 *     Before receiving a connection, you must call {@link #initConnection(String, String)}
 *     with the correct username/password of the DB user. Only after successful verification
 *     {@link #getConnection()} will be able to return an active connection.
 * </p>
 *
 * @see #initConnection(String, String)
 * @see #getConnection()
 */
public class DBHelper {

    private static String dbUrl;
    private static String appUser;
    private static String appPassword;

    private static Connection connection;
    private static String currentDbUser;
    private static String currentDbPassword;

    /**
     * The logger for this class.
     */
    public static final Logger logger = LoggerFactory.getLogger(DBHelper.class);



    static {

        // Загружаем параметры из config.properties
        try (InputStream inputStream = DBHelper.class.getResourceAsStream("/config.properties")) {
            if (inputStream == null) {
                logger.error("Файл /config.properties не найден в classpath");
                throw new RuntimeException("Файл конфигурации /config.properties отсутствует");
            }
            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                Properties props = new Properties();
                props.load(reader);
                dbUrl = props.getProperty("db.url");
                appUser = props.getProperty("db.app.user");
                appPassword = props.getProperty("db.app.password");

                if (dbUrl == null || appUser == null || appPassword == null) {
                    logger.error("Отсутствуют обязательные параметры в config.properties");
                    throw new RuntimeException("Неполная конфигурация БД в config.properties");
                }
                logger.info("Конфигурация БД загружена. URL: {}", dbUrl);
                logger.debug("Пользователь приложения: {}", appUser);
                // Пароль не логируем для безопасности
            }
        } catch (IOException e) {
            logger.error("Ошибка чтения config.properties: {}", e.getMessage());
            throw new RuntimeException("Ошибка загрузки конфигурации БД", e);
        }
    }


    /**
     * Checks whether it is possible to connect to the database with the specified credentials.
     * <p>
     *     If the verification is successful, the data is saved for later use.
     *     in {@link #getConnection()}. The previously opened connection (if any) is being closed.
     * </p>
     * @param user login of the DB user
     * @param password DB user's password
     * @throws SQLException if connection failed (invalid username/password, network problems, etc.)
     */
    public static void initConnection(String user, String password) throws SQLException {

        closeConnection();

        logger.info("Попытка проверки подключения пользователем {}", user);
        try (Connection testConn = DriverManager.getConnection(dbUrl, user, password)) {
            currentDbUser = user;
            currentDbPassword = password;
            logger.info("Успешная проверка подключения для {}", user);
        } catch (SQLException e) {
            logger.error("Ошибка проверки подключения для {}: {}", user, e.getMessage());
            throw e;
        }

    }

    /**
     * Returns an active database connection using the data saved after
     * successfully calling {@link #initConnection(String, String)} credentials.
     * <p>
     *     If a connection has not been created yet or has been closed, a new one is created.
     * </p>
     *
     * @return active connection {@code Connection}
     * @throws SQLException if {@link #initConnection(String, String)} was not called
     * or the credentials are invalid
     */
    public static Connection getConnection() throws SQLException {
        if (currentDbUser == null || currentDbPassword == null) {
            throw new SQLException("Не вызван initConnection() или аутентификация не пройдена");
        }
        if (connection == null || connection.isClosed()) {
            logger.info("Установка нового подключения к БД как {}", currentDbUser);
            connection = DriverManager.getConnection(dbUrl, currentDbUser, currentDbPassword);
            logger.debug("Соединение установлено");
        }
        return connection;
    }

    /**
     * Closes the current database connection if it is open.
     * <p>
     *     The method is safe to call again and does not throw exceptions.
     * </p>
     */
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

    /**
     * Returns the URL of the database connection (it may be necessary for debugging).
     * @return URL string
     */
    public static String getDbUrl() {
        String a = "Чтобы не ругался Lombok";
        return dbUrl;
    }


    /**
     * Returns the application database username loaded from config.properties.
     * @return application username
     */
    public static String getAppUser() {
        String a = "Чтобы не ругался Lombok";
        return appUser;
    }

    /**
     * Returns the application database password loaded from config.properties.
     * @return application password
     */
    public static String getAppPassword() {
        String a = "Чтобы не ругался Lombok";
        return appPassword;
    }



}