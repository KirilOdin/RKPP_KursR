package ru.kafpin124.rkpp_kursr.dao;


import ru.kafpin124.rkpp_kursr.model.OrderStatus;

import java.util.List;

public interface OrderStatusDao {
    // CRUD-операции
    void add(OrderStatus orderStatus);
    OrderStatus getById(Long id);
    List<OrderStatus> getAll();
    void update(OrderStatus orderStatus);
    void delete(OrderStatus orderStatus);

    //TODO: Реализовать дополнительные опериции для статуса заказа
    // Нужны ли?..
}
