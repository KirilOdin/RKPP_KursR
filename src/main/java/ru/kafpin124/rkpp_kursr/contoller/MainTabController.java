package ru.kafpin124.rkpp_kursr.contoller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.kafpin124.rkpp_kursr.dao.impl.OrderDaoImpl;
import ru.kafpin124.rkpp_kursr.model.Employee;
import ru.kafpin124.rkpp_kursr.model.Order;

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

    public void setCurrentUser(Employee user) {
        this.currentUser = user;
        configureTabsByRole();

        newOrderController.setCurrentUser(user);
        newResultController.setCurrentUser(user);
    }

    private void configureTabsByRole() {
        if (!currentUser.getRole().equals("admin")) {
            mainTabPane.getTabs().remove(employeesTab);
            mainTabPane.getTabs().remove(testsTab);
        }
        if (!currentUser.getRole().equals("lab_doctor")) {
            mainTabPane.getTabs().remove(verificationTab);
        }
    }

    @FXML
    void onLogout() {
        ((Stage) mainTabPane.getScene().getWindow()).close();
    }

    @FXML
    void onEnterResults() {
        mainTabPane.getSelectionModel().select(resultsTab);
    }

    @FXML
    void onPrintReport() {
        // Определяем активную вкладку и текущий выбранный заказ
        Tab selectedTab = mainTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == ordersTab) {
            Order selectedOrder = ordersListController.getSelectedOrder();
            if (selectedOrder != null && selectedOrder.getStatus().getStatusName().equals("утверждён")) {
                // открыть форму печати с этим заказом
            }
        }
    }

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
}