package ru.kafpin124.rkpp_kursr.util;

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

    public static void initConnection(String user, String password) throws SQLException {
        try (Connection testConn = DriverManager.getConnection(DB_URL, user, password)) {
            currentDbUser = user;
            currentDbPassword = password;
        }

    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, APP_USER, APP_PASSWORD);
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}