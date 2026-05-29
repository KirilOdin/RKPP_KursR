package ru.kafpin124.rkpp_kursr.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static final Logger logger = LoggerFactory.getLogger(SqlStatements.class);

    static {
        try (InputStream inputStream = SqlStatements.class.getResourceAsStream("/sql/statements.properties")) {
            if (inputStream == null) {
                logger.error("Файл /sql/statements.properties не найден в classpath");
                throw new RuntimeException("Ресурс /statements.properties не найден в classpath");
            }
            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                props.load(reader);
                logger.info("SQL-запросы успешно загружены из /sql/statements.properties");
            }
        } catch (IOException e) {
            logger.error("Ошибка загрузки SQL-запросов: {}", e.getMessage());
            throw new RuntimeException("Ошибка загрузки SQL-запросов: " + e.getMessage(), e);
        }
    }

    public static String get(String key) {
        String value = props.getProperty(key);
        if (value == null) {
            logger.warn("SQL-запрос с ключом '{}' не найден", key);
        }
        return value;
    }

}
