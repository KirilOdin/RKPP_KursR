package ru.kafpin124.rkpp_kursr.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class SqlStatements {
    public static final Properties props = new Properties();
    // URL url = getClass().getResource("/statements.properties");
    // Properties prop = new Properties();
    // try (FileInputStream fis = new FileInputStream(url.getFile())) {
    //     prop.load(fis);
    // }

    static {
        try (InputStream inputStream = SqlStatements.class.getResourceAsStream("/sql/statements.properties")) {
            if (inputStream == null) {
                throw new RuntimeException("Ресурс /statements.properties не найден в classpath");
            }
            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                props.load(reader);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки SQL-запросов: " + e.getMessage(), e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }


}
