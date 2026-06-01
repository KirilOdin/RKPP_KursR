package ru.kafpin124.rkpp_kursr;

import javafx.scene.Node;
import javafx.scene.control.*;
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
    @DisplayName("2. Негативный: попытка создания нового заказа без выбора тестов")
    void testFailedCreatingNewOrder() throws TimeoutException {
        // Вход
        login(ADMIN_LOGIN, ADMIN_PASSWORD);
        waitForMainWindow();
        clickOn("#newOrderTab");
        waitFor(5, TimeUnit.SECONDS, () -> lookup("#patientField").tryQuery().isPresent());

        // Создаём тестового пациента (через DAO)
        Patient testPatient = new Patient();
        testPatient.setPolicyNumber("TEST_POLICY_" + System.currentTimeMillis());
        testPatient.setLastName("Тестов");
        testPatient.setFirstName("Пациент");
        testPatient.setMiddleName("Иванович");
        testPatient.setGender('м');
        testPatient.setBirthDate(LocalDate.of(1990, 5, 15));
        patientDao.add(testPatient);
        createdPatients.add(testPatient);

        // Выбираем пациента
        clickOn("#patientField");
        clickOn("#btSelectPatient");
        waitFor(5, TimeUnit.SECONDS, () -> lookup("#tableViewPerson").tryQuery().isPresent());
        clickOn(testPatient.getLastName());
        clickOn("#btSelect");

        TextField patientField = lookup("#patientField").query();
        assertThat(patientField.getText()).contains("Тестов Пациент");

        // Генерируем штрих-код
        clickOn("#barcodeField");
        clickOn("#btGenerateBarcode");
        TextField barcodeField = lookup("#barcodeField").query();
        assertThat(barcodeField.getText()).isNotEmpty();

        // Добавляем тест
        clickOn("Добавить тест");

        Node firstCell = lookup("#testListView").nth(0).query();
        clickOn(firstCell);

        clickOn("#btAddSelected");
        WaitForAsyncUtils.waitForFxEvents();

        waitFor(5, TimeUnit.SECONDS, () -> !lookup("#testListView").tryQuery().isPresent());


        // Создаём заказ
        clickOn("Создать заказ");
        waitFor(5, TimeUnit.SECONDS, () -> lookup(".dialog-pane").tryQuery().isPresent());

        DialogPane dialogPane = lookup(".dialog-pane").query();
        assertThat(dialogPane.getContentText()).contains("Добавьте хотя бы один анализ");
        clickOn("OK");
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("3. Позитивный: успешное создание нового заказа")
    void testSuccessfulCreatingOrder() throws TimeoutException {
        // Вход
        login(ADMIN_LOGIN, ADMIN_PASSWORD);
        waitForMainWindow();
        clickOn("#newOrderTab");
        waitFor(5, TimeUnit.SECONDS, () -> lookup("#patientField").tryQuery().isPresent());

        // Создаём тестового пациента (через DAO)
        Patient testPatient = new Patient();
        testPatient.setPolicyNumber("TEST_POLICY_" + System.currentTimeMillis());
        testPatient.setLastName("Тестов");
        testPatient.setFirstName("Пациент");
        testPatient.setMiddleName("Иванович");
        testPatient.setGender('м');
        testPatient.setBirthDate(LocalDate.of(1990, 5, 15));
        patientDao.add(testPatient);
        createdPatients.add(testPatient);

        // Выбираем пациента
        clickOn("#patientField");
        clickOn("#btSelectPatient");
        waitFor(5, TimeUnit.SECONDS, () -> lookup("#tableViewPerson").tryQuery().isPresent());
        clickOn(testPatient.getLastName());
        clickOn("#btSelect");

        TextField patientField = lookup("#patientField").query();
        assertThat(patientField.getText()).contains("Тестов Пациент");

        // Генерируем штрих-код
        clickOn("#barcodeField");
        clickOn("#btGenerateBarcode");
        TextField barcodeField = lookup("#barcodeField").query();
        assertThat(barcodeField.getText()).isNotEmpty();

        // Добавляем тест
        clickOn("Добавить тест");

        ListView<AnalysisTest> listView = lookup("#testListView").query();
        ComboBox<String> biomaterialCombo = lookup("#biomaterialCombo").query();
        interact(() -> biomaterialCombo.setValue("кровь"));
        interact(() -> listView.getSelectionModel().select(0));
        interact(() -> listView.getSelectionModel().select(2));


        clickOn("#btAddSelected");
        WaitForAsyncUtils.waitForFxEvents();

        waitFor(5, TimeUnit.SECONDS, () -> !lookup("#testListView").tryQuery().isPresent());

        // Создаём заказ
        clickOn("Создать заказ");
        waitFor(5, TimeUnit.SECONDS, () -> lookup(".dialog-pane").tryQuery().isPresent());
        DialogPane dialogPane = lookup(".dialog-pane").query();

        assertThat(dialogPane.getContentText()).contains("Заказ создан");
        clickOn("OK");
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

        waitFor(5, TimeUnit.SECONDS, () -> lookup(".dialog-pane").tryQuery().isPresent());
        DialogPane dialogPane = lookup(".dialog-pane").query();

        assertThat(dialogPane.getContentText()).contains("Выберите пациента");
        clickOn("OK");
    }
}