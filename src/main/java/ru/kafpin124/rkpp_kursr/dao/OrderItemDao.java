package ru.kafpin124.rkpp_kursr.dao;



import ru.kafpin124.rkpp_kursr.model.OrderItem;

import java.math.BigDecimal;
import java.util.List;

public interface OrderItemDao {
    // CRUD-операции
    void add(OrderItem orderItem);
    OrderItem findById(Long id);
    List<OrderItem> getAll();
    void update(OrderItem orderItem);
    void delete(OrderItem orderItem);

    //TODO: Реализовать дополнительные операции для результата (или order_list'а)
    // Поиск по сотрудникам (лаборантам, врачам) -> возвращать List
    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findBySpecimenId(Long specimenId);
    List<OrderItem> findByEnteredBy(Long employeeId); // кто ввёл результаты
    void updateResult(Long itemId, BigDecimal value, String text, boolean abnormal);
    void updateStatus(Long itemId, String status);

}
