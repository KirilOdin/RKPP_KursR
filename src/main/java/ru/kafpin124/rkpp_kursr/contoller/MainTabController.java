package ru.kafpin124.rkpp_kursr.contoller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kafpin124.rkpp_kursr.dao.*;
import ru.kafpin124.rkpp_kursr.dao.impl.*;
import ru.kafpin124.rkpp_kursr.model.Employee;
import ru.kafpin124.rkpp_kursr.model.Order;
import ru.kafpin124.rkpp_kursr.util.LocalizationService;

import java.io.IOException;
import java.util.ResourceBundle;

public class MainTabController {
    @FXML private TabPane mainTabPane;
    @FXML private Tab ordersTab, newOrderTab, resultsTab, verificationTab, reportsTab, employeesTab, testsTab;

    private OrdersListController ordersListController;
    private NewOrderController newOrderController;
    private NewResultController newResultController;
    private VerificationController verificationController;
    private ReportsController reportsController;
    private ManageEmployeesController manageEmployeesController;
    private ManageTestsController manageTestsController;

    private Employee currentUser;
    private Stage primaryStage;   // ссылка на главное окно
    ResourceBundle bundle = LocalizationService.getBundle();
    private OrderDao orderDao;


    public static final Logger logger = LoggerFactory.getLogger(MainTabController.class);

    public MainTabController(OrderDao orderDao) {
        this.orderDao = orderDao;
        logger.debug("MainTabController создан с переданным OrderDao");
    }

    public MainTabController() {
        logger.debug("MainTabController создан без параметров");
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
        logger.debug("PrimaryStage установлен");
    }


//    public void setCurrentUser(Employee user) {
//        this.currentUser = user;
//        configureTabsByRole();
//
//        newOrderController.setCurrentUser(user);
//        newResultController.setCurrentUser(user);
//    }

    private void configureTabsByRole() {
        String role = currentUser.getRole();
        logger.info("Настройка вкладок для роли: {}", role);
        if (!role.equals("admin")) {
            mainTabPane.getTabs().remove(employeesTab);
            mainTabPane.getTabs().remove(testsTab);
            logger.debug("Удалены вкладки 'Сотрудники' и 'Справочник анализов' (не admin)");
        }
        if (!role.equals("lab_doctor")) {
            mainTabPane.getTabs().remove(verificationTab);
            logger.debug("Удалена вкладка 'Верификация' (не lab_doctor)");
        }
    }


    @FXML
    void initialize() throws IOException {
        logger.info("Инициализация MainTabController");
        OrderDao orderDao = new OrderDaoImpl();
        PatientDao patientDao = new PatientDaoImpl();
        AnalysisTestDao testDao = new AnalysisTestDaoImpl();
        SpecimenDao specimenDao = new SpecimenDaoImpl();
        OrderItemDao itemDao = new OrderItemDaoImpl();
        ReferenceValueDao refDao = new ReferenceValueDaoImpl();
        EmployeeDao employeeDao = new EmployeeDaoImpl();
        ReportDao reportDao = new ReportDaoImpl();

        Callback<Class<?>, Object> factory = clazz -> {
            if (clazz == OrdersListController.class) {
                return new OrdersListController(orderDao);
            } else if (clazz == NewOrderController.class) {
                return new NewOrderController(patientDao, testDao, orderDao, specimenDao, itemDao);
            } else if (clazz == NewResultController.class) {
                return new NewResultController(orderDao, itemDao, refDao);
            } else if (clazz == VerificationController.class) {
                return new VerificationController(orderDao, itemDao);
            } else if (clazz == ReportsController.class) {
                return new ReportsController(reportDao);
            } else if (clazz == ManageEmployeesController.class) {
                return new ManageEmployeesController(employeeDao);
            } else if (clazz == ManageTestsController.class) {
                return new ManageTestsController(testDao, refDao);
            }
            return null;
        };
        // Загружаем содержимое каждой вкладки и получаем контроллеры
        ordersListController = loadTab(ordersTab, "/ru/kafpin124/rkpp_kursr/orders_list.fxml", factory, bundle);
        newOrderController = loadTab(newOrderTab, "/ru/kafpin124/rkpp_kursr/new_order.fxml", factory, bundle);
        newResultController = loadTab(resultsTab, "/ru/kafpin124/rkpp_kursr/new_result.fxml", factory, bundle);
        verificationController = loadTab(verificationTab, "/ru/kafpin124/rkpp_kursr/verification.fxml", factory, bundle);
        reportsController = loadTab(reportsTab, "/ru/kafpin124/rkpp_kursr/reports.fxml", factory, bundle);
        manageEmployeesController = loadTab(employeesTab, "/ru/kafpin124/rkpp_kursr/manage_employees.fxml", factory, bundle);
        manageTestsController = loadTab(testsTab, "/ru/kafpin124/rkpp_kursr/manage_tests.fxml", factory, bundle);

        logger.info("Все вкладки успешно загружены");
    }


//    private <T> T loadTab(Tab tab, String fxmlPath) throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
//        tab.setContent(loader.load());
//        return loader.getController();
//    }


    private <T> T loadTab(Tab tab, String fxmlPath, Callback<Class<?>, Object> controllerFactory, ResourceBundle bundle) throws IOException {
        logger.debug("Загрузка вкладки: {}", fxmlPath);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), bundle);
        loader.setControllerFactory(controllerFactory);
        tab.setContent(loader.load());
        T controller = loader.getController();
        logger.debug("Вкладка {} загружена, контроллер: {}", fxmlPath, controller.getClass().getSimpleName());
        return controller;
    }

    public void setCurrentUser(Employee user) {
        this.currentUser = user;
        logger.info("Установлен текущий пользователь: {} (роль: {})", user.getLogin(), user.getRole());
        configureTabsByRole();          // убрать ненужные вкладки

        // Теперь можно безопасно передавать пользователя (контроллеры уже не null)
        if (newOrderController != null) newOrderController.setCurrentUser(user);
        if (newResultController != null) newResultController.setCurrentUser(user);
        if (verificationController != null) verificationController.setCurrentUser(user);
        logger.debug("Пользователь передан в дочерние контроллеры");
    }



//    private void configureTabsByRole() {
//        String role = currentUser.getRole();
//        if (!role.equals("admin")) {
//            mainTabPane.getTabs().remove(employeesTab);
//            mainTabPane.getTabs().remove(testsTab);
//        }
//        if (!role.equals("lab_doctor")) {
//            mainTabPane.getTabs().remove(verificationTab);
//        }
//        // Можно также скрыть "Новый заказ" для врача? Пока оставим.
//    }

    @FXML
    void onLogout() {
        logger.info("Пользователь {} вышел из системы", currentUser != null ? currentUser.getLogin() : "unknown");
        ((Stage) mainTabPane.getScene().getWindow()).close();
    }

    @FXML
    void onEnterResults() {
        logger.debug("Переключение на вкладку 'Ввод результатов'");
        mainTabPane.getSelectionModel().select(resultsTab);
    }

//    @FXML
//    void onPrintReport() {
//        // Определяем активную вкладку и текущий выбранный заказ
//        Tab selectedTab = mainTabPane.getSelectionModel().getSelectedItem();
//        if (selectedTab == ordersTab) {
//            Order selectedOrder = ordersListController.getSelectedOrder();
//            if (selectedOrder != null && selectedOrder.getStatus().getStatusName().equals("утверждён")) {
//                // открыть форму печати с этим заказом
//            }
//        }
//    }

    @FXML
    void onRefresh() {
        Tab selected = mainTabPane.getSelectionModel().getSelectedItem();
        logger.info("Обновление активной вкладки: {}", selected.getText());
        if (selected == ordersTab) {
            ordersListController.loadOrders();
        } else if (selected == resultsTab) {
            newResultController.refreshOrderList();
        } else if (selected == verificationTab) {
            verificationController.refreshPendingOrders();
        } else {
            logger.debug("Обновление для вкладки {} не предусмотрено", selected.getText());
        }
    }

    @FXML
    void onSetInProgress() {
        Tab selected = mainTabPane.getSelectionModel().getSelectedItem();
        if (selected == ordersTab) {
            Order selectedOrder = ordersListController.getSelectedOrder();
            if (selectedOrder != null && selectedOrder.getStatus().getStatusName().equals("зарегистрирован")) {
                logger.info("Перевод заказа ID={} в статус 'в работе'", selectedOrder.getIdOrder());
                new OrderDaoImpl().updateStatus(selectedOrder.getIdOrder(), 2L); // статус "в работе"
                // также можно обновить accepted_by и accepted_datetime, но это потом
                ordersListController.loadOrders();
            } else {
                logger.warn("Не выбран заказ или его статус не 'зарегистрирован'");
                new Alert(Alert.AlertType.WARNING, "Выберите зарегистрированный заказ").show();
            }
        } else {
            logger.debug("Кнопка 'Взять в работу' нажата не на вкладке заказов");
        }
    }

    @FXML
    void onSwitchToEmployees() {
        logger.info("Переключение на вкладку 'Управление сотрудниками'");
        mainTabPane.getSelectionModel().select(employeesTab);
    }

    @FXML
    void onSwitchToTests() {
        logger.info("Переключение на вкладку 'Справочник анализов'");
        mainTabPane.getSelectionModel().select(testsTab);
    }


    @FXML
    void onPrintReport() {
        Order selectedOrder = ordersListController.getSelectedOrder();
        if (selectedOrder != null && selectedOrder.getStatus().getStatusName().equals("утверждён")) {
            logger.info("Печать бланка для заказа ID={}", selectedOrder.getIdOrder());
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/report_form.fxml"));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(loader.load()));
                ReportFormController reportCtrl = loader.getController();
                reportCtrl.setOrder(selectedOrder);
                stage.showAndWait();
                logger.debug("Окно печати закрыто");
            } catch (IOException e) {
                logger.error("Ошибка загрузки report_form.fxml", e);
                e.printStackTrace();
            }
        } else {
            logger.warn("Попытка печати без выбранного утверждённого заказа");
            new Alert(Alert.AlertType.WARNING, "Выберите утверждённый заказ для печати").show();
        }
    }
}