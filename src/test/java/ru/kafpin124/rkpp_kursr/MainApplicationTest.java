package ru.kafpin124.rkpp_kursr;

import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import ru.kafpin124.rkpp_kursr.dao.impl.*;
import ru.kafpin124.rkpp_kursr.model.*;
import ru.kafpin124.rkpp_kursr.model.Order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MainApplicationTest extends ApplicationTest {

    // Тестовые данные для входа
    private static final String ADMIN_LOGIN = "adminchik";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String WRONG_PASSWORD = "wrongpass";

    // DAO для очистки данных
    private final OrderDaoImpl orderDao = new OrderDaoImpl();
    private final PatientDaoImpl patientDao = new PatientDaoImpl();
    private final SpecimenDaoImpl specimenDao = new SpecimenDaoImpl();
    private final OrderItemDaoImpl orderItemDao = new OrderItemDaoImpl();
    private final AnalysisTestDaoImpl testDao = new AnalysisTestDaoImpl();

    // Списки для удаления созданных записей
    private final List<Order> createdOrders = new ArrayList<>();
    private final List<Patient> createdPatients = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        new MainApplication().start(stage);
        // Ждём появления окна входа
        waitFor(5, TimeUnit.SECONDS, () -> lookup("#loginField").tryQuery().isPresent());
    }

    @AfterEach
    void cleanUp() {
        // Удаляем созданные заказы и связанные данные
        for (Order order : createdOrders) {
            // Удаляем позиции заказа
            List<OrderItem> items = orderItemDao.findByOrderId(order.getIdOrder());
            if (items != null) {
                for (OrderItem item : items) {
                    orderItemDao.delete(item);
                }
            }
            // Удаляем пробы
            List<Specimen> specimens = specimenDao.findByOrderId(order.getIdOrder());
            if (specimens != null) {
                for (Specimen spec : specimens) {
                    specimenDao.delete(spec);
                }
            }
            // Удаляем заказ
            orderDao.delete(order);
        }
        createdOrders.clear();

        // Удаляем созданных пациентов
        for (Patient patient : createdPatients) {
            patientDao.delete(patient);
        }
        createdPatients.clear();
    }

    // ========== Вспомогательные методы ==========

    private void login(String login, String password) throws TimeoutException {
        clickOn("#loginField");
        write(login);
        clickOn("#passwordField");
        write(password);
        clickOn("#btnLogin");
    }

    private void waitForMainWindow() throws TimeoutException {
        waitFor(15, TimeUnit.SECONDS, () -> lookup("#ordersTab").tryQuery().isPresent());
    }

    // ========== Тест-кейсы ==========

    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("1. Позитивный: успешный вход и переход на вкладку 'Новый заказ'")
    void testSuccessfulLoginAndNavigateToNewOrder() throws TimeoutException {
        login(ADMIN_LOGIN, ADMIN_PASSWORD);
        waitForMainWindow();

        // Проверяем, что главное окно открыто (есть вкладка "Заказы")
        assertThat(lookup("#ordersTab").tryQuery()).isPresent();

        // Переходим на вкладку "Новый заказ"
        clickOn("#newOrderTab");
        WaitForAsyncUtils.waitForFxEvents();

        // Проверяем, что поле пациента отображается
        waitFor(5, TimeUnit.SECONDS, () -> lookup("#patientField").tryQuery().isPresent());
        TextField patientField = lookup("#patientField").query();
        assertThat(patientField.isVisible()).isTrue();
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("2. Позитивный: создание нового заказа")
    void testCreateNewOrder() throws TimeoutException {
        // 1. Вход в систему
        login(ADMIN_LOGIN, ADMIN_PASSWORD);
        waitForMainWindow();
        clickOn("#newOrderTab");
        waitFor(5, TimeUnit.SECONDS, () -> lookup("#patientField").tryQuery().isPresent());

        // 2. Создаём тестового пациента (через DAO, чтобы не зависеть от UI)
        Patient testPatient = new Patient();
        testPatient.setPolicyNumber("TEST_POLICY_" + System.currentTimeMillis());
        testPatient.setLastName("Тестов");
        testPatient.setFirstName("Пациент");
        testPatient.setMiddleName("Иванович");
        testPatient.setGender('м');
        testPatient.setBirthDate(LocalDate.of(1990, 5, 15));
        patientDao.add(testPatient);
        createdPatients.add(testPatient);

        // 3. Выбираем пациента через диалог
        clickOn("#patientField"); // кнопка "Выбрать" рядом с полем
        clickOn("#btSelectPatient");
        waitFor(5, TimeUnit.SECONDS, () -> lookup("#tableViewPerson").tryQuery().isPresent());

        // Ищем пациента по фамилии и кликаем на строку
        clickOn(testPatient.getLastName());
        clickOn("#btSelect"); // кнопка "Выбрать" в диалоге

        // Проверяем, что поле пациента заполнилось
        TextField patientField = lookup("#patientField").query();
        assertThat(patientField.getText()).contains("Тестов Пациент");

        // 4. Генерируем штрих-код
        clickOn("#barcodeField"); // кнопка "Сгенерировать"
        clickOn("#btGenerateBarcode");
        TextField barcodeField = lookup("#barcodeField").query();
        assertThat(barcodeField.getText()).isNotEmpty();

        // 5. Добавляем тест (выбираем первый доступный)
        clickOn("Добавить тест");
        waitFor(5, TimeUnit.SECONDS, () -> lookup("#testListView").tryQuery().isPresent());

        // Выбираем первый элемент в списке (предполагаем, что он есть)
        Node firstCell = lookup(".list-cell").queryAll().iterator().next();
        clickOn("#btGenerateBarcode");
        clickOn(firstCell);

        clickOn("#filterCombo");
        clickOn("кровь");

        // Проверяем, что тест появился в таблице
        TableView<?> selectedTestsTable = lookup("#selectedTestsTable").query();
        waitFor(3, TimeUnit.SECONDS, () -> !selectedTestsTable.getItems().isEmpty());
        assertThat(selectedTestsTable.getItems()).isNotEmpty();


        clickOn((MouseButton) selectedTestsTable.getItems().get(0));
        clickOn("%selectedTestsTable");
        // 6. Выбираем тип биоматериала (например, "кровь")
//        clickOn("#biomaterialCombo");
//        clickOn("кровь");

        // 7. Нажимаем "Создать заказ"
        clickOn("Создать заказ");

        // 8. Ждём и закрываем диалог "Готово"
        waitFor(5, TimeUnit.SECONDS, () -> lookup("Готово").tryQuery().isPresent());
        clickOn("OK");

        // 9. Проверяем, что заказ действительно создался в БД
        String barcode = barcodeField.getText();
        Order createdOrder = orderDao.findBySpecimenBarcode(barcode);
        assertThat(createdOrder).isNotNull();
        createdOrders.add(createdOrder);
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("3. Негативный: вход с неверным паролем")
    void testFailedLoginWrongPassword() throws TimeoutException {
        login(ADMIN_LOGIN, WRONG_PASSWORD);

        // Ожидаем появления сообщения об ошибке
        waitFor(5, TimeUnit.SECONDS, () -> {
            String text = lookup("#messageLabel").queryLabeled().getText();
            return text != null && (text.contains("Неверный") || text.contains("Invalid"));
        });
        assertThat(lookup("#messageLabel").queryLabeled().getText()).contains("Неверный");

        // Окно входа должно оставаться открытым
        assertThat(lookup("#loginField").tryQuery()).isPresent();
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("4. Негативный: создание заказа без выбора пациента")
    void testCreateOrderWithoutPatient() throws TimeoutException {
        login(ADMIN_LOGIN, ADMIN_PASSWORD);
        waitForMainWindow();
        clickOn("#newOrderTab");
        waitFor(5, TimeUnit.SECONDS, () -> lookup("#patientField").tryQuery().isPresent());

        // Пытаемся создать заказ, не выбрав пациента
        clickOn("Создать заказ");

        // Ожидаем диалог с ошибкой
        waitFor(5, TimeUnit.SECONDS, () -> lookup("Ошибка").tryQuery().isPresent());
        DialogPane dialogPane = lookup(".dialog-pane").query();
        assertThat(dialogPane.getContentText()).contains("Выберите пациента");
        clickOn("OK");
    }
}