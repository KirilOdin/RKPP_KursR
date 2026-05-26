package ru.kafpin124.rkpp_kursr.contoller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.kafpin124.rkpp_kursr.dao.*;
import ru.kafpin124.rkpp_kursr.dao.impl.*;
import ru.kafpin124.rkpp_kursr.model.Employee;
import ru.kafpin124.rkpp_kursr.model.Order;

import java.io.IOException;

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

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    private OrderDao orderDao;

    public MainTabController(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public MainTabController() {}


//    public void setCurrentUser(Employee user) {
//        this.currentUser = user;
//        configureTabsByRole();
//
//        newOrderController.setCurrentUser(user);
//        newResultController.setCurrentUser(user);
//    }

    private void configureTabsByRole() {
        String role = currentUser.getRole();
        if (!role.equals("admin")) {
            mainTabPane.getTabs().remove(employeesTab);
            mainTabPane.getTabs().remove(testsTab);
        }
        if (!role.equals("lab_doctor")) {
            mainTabPane.getTabs().remove(verificationTab);
        }
    }


    @FXML
    void initialize() throws IOException {
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
        ordersListController = loadTab(ordersTab, "/ru/kafpin124/rkpp_kursr/orders_list.fxml", factory);
        newOrderController = loadTab(newOrderTab, "/ru/kafpin124/rkpp_kursr/new_order.fxml", factory);
        newResultController = loadTab(resultsTab, "/ru/kafpin124/rkpp_kursr/new_result.fxml", factory);
        verificationController = loadTab(verificationTab, "/ru/kafpin124/rkpp_kursr/verification.fxml", factory);
        reportsController = loadTab(reportsTab, "/ru/kafpin124/rkpp_kursr/reports.fxml", factory);
        manageEmployeesController = loadTab(employeesTab, "/ru/kafpin124/rkpp_kursr/manage_employees.fxml", factory);
        manageTestsController = loadTab(testsTab, "/ru/kafpin124/rkpp_kursr/manage_tests.fxml", factory);
    }


//    private <T> T loadTab(Tab tab, String fxmlPath) throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
//        tab.setContent(loader.load());
//        return loader.getController();
//    }


    private <T> T loadTab(Tab tab, String fxmlPath, Callback<Class<?>, Object> controllerFactory) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(controllerFactory);
        tab.setContent(loader.load());
        return loader.getController();
    }

    public void setCurrentUser(Employee user) {
        this.currentUser = user;
        configureTabsByRole();          // убрать ненужные вкладки

        // Теперь можно безопасно передавать пользователя (контроллеры уже не null)
        if (newOrderController != null) newOrderController.setCurrentUser(user);
        if (newResultController != null) newResultController.setCurrentUser(user);
        if (verificationController != null) verificationController.setCurrentUser(user);
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
        ((Stage) mainTabPane.getScene().getWindow()).close();
    }

    @FXML
    void onEnterResults() {
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
        if (selected == ordersTab) {
            ordersListController.loadOrders();
        } else if (selected == resultsTab) {
            newResultController.refreshOrderList();
        } else if (selected == verificationTab) {
            verificationController.refreshPendingOrders();
        }
    }

    @FXML
    void onSetInProgress() {
        Tab selected = mainTabPane.getSelectionModel().getSelectedItem();
        if (selected == ordersTab) {
            Order selectedOrder = ordersListController.getSelectedOrder();
            if (selectedOrder != null && selectedOrder.getStatus().getStatusName().equals("зарегистрирован")) {
                new OrderDaoImpl().updateStatus(selectedOrder.getIdOrder(), 2L); // статус "в работе"
                // также можно обновить accepted_by и accepted_datetime, но это потом
                ordersListController.loadOrders();
            }
        }
    }

    @FXML
    void onSwitchToEmployees() {
        mainTabPane.getSelectionModel().select(employeesTab);
    }

    @FXML
    void onSwitchToTests() {
        mainTabPane.getSelectionModel().select(testsTab);
    }


    @FXML
    void onPrintReport() {
        Order selectedOrder = ordersListController.getSelectedOrder();
        if (selectedOrder != null && selectedOrder.getStatus().getStatusName().equals("утверждён")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/report_form.fxml"));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(loader.load()));
                ReportFormController reportCtrl = loader.getController();
                reportCtrl.setOrder(selectedOrder);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Выберите утверждённый заказ для печати").show();
        }
    }
}