package ru.kafpin124.rkpp_kursr.dao;



import ru.kafpin124.rkpp_kursr.model.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderDao {
    // CRUD-операции
    void add(Order order);
    Order findById(Long id);
    List<Order> getAll();
    void update(Order order);
    void delete(Order order);

    //TODO: Реализация дополнительных методов для заказов (Поиск по времени, айди пациента?)
    List<Order> findByPatientId(Long patientId);
    List<Order> findByStatusId(Long statusId);
    List<Order> findByDateRange(LocalDateTime from, LocalDateTime to);
    List<Order> findByRegisteredBy(Long employeeId);
    Order findBySpecimenBarcode(String barcode);
    void updateStatus(Long orderId, Long statusId);

}
