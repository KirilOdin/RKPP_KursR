package ru.kafpin124.rkpp_kursr.contoller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.kafpin124.rkpp_kursr.dao.impl.*;
import ru.kafpin124.rkpp_kursr.model.Employee;

import java.io.IOException;

public class MainTabController {
    @FXML private TabPane mainTabPane;
    @FXML private Tab ordersTab, newOrderTab, resultsTab, verificationTab, reportsTab, employeesTab, testsTab;
    private Employee currentUser;

    public void setCurrentUser(Employee user) {
        this.currentUser = user;
        configureTabsByRole();
    }

    private void configureTabsByRole() {
        if (!currentUser.getRole().equals("admin")) {
            mainTabPane.getTabs().remove(employeesTab);
            mainTabPane.getTabs().remove(testsTab);
        }
        if (!currentUser.getRole().equals("lab_doctor")) {
            mainTabPane.getTabs().remove(verificationTab);
        }
        // Можно также скрыть "Новый заказ" или "Ввод результатов" для врача?
    }

    @FXML void onLogout() {
        ((Stage) mainTabPane.getScene().getWindow()).close();
        // Можно заново открыть логин, если нужно
    }

    @FXML void onEnterResults() {
        mainTabPane.getSelectionModel().select(resultsTab);
    }

    @FXML void onPrintReport() { /* открыть ReportFormController для выделенного заказа */ }

    @FXML void onRefresh() {
        // Обновить данные в активной вкладке
        Tab selected = mainTabPane.getSelectionModel().getSelectedItem();
        if (selected == ordersTab) {
            // Вызвать метод загрузки списка в OrdersListController
        }
    }

    @FXML void onSetInProgress() {
        // Перевести выделенный заказ в статус "в работе"
    }

    public void onSwitchToEmployees(ActionEvent actionEvent) {
    }

    public void onSwitchToTests(ActionEvent actionEvent) {
    }
}