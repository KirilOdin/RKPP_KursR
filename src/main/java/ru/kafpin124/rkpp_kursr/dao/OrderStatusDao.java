package ru.kafpin124.rkpp_kursr.dao;


import ru.kafpin124.rkpp_kursr.model.OrderStatus;

import java.util.List;

/**
 * The interface Order status dao to work with the directory of order statuses (order_statuses).
 * Provides CRUD operations and search methods.
 */
public interface OrderStatusDao {
    /**
     * Add a new order status.
     *
     * @param orderStatus the order status object (id will be assigned automatically)
     */
    void add(OrderStatus orderStatus);

    /**
     * Gets an order status by ID.
     *
     * @param id the order status' ID (primary key)
     * @return the order status object or null, if not found
     */
    OrderStatus getById(Long id);

    /**
     * Gets a list of all order statuses.
     *
     * @return the list of all order statuses
     */
    List<OrderStatus> getAll();

    /**
     * Update the order status data (all fields).
     *
     * @param orderStatus the order status with a filled-in id and new values
     */
    void update(OrderStatus orderStatus);

    /**
     * Delete the order status by object.
     *
     * @param orderStatus the order status object
     */
    void delete(OrderStatus orderStatus);

}
