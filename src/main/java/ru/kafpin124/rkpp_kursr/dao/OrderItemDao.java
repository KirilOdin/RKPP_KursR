package ru.kafpin124.rkpp_kursr.dao;



import ru.kafpin124.rkpp_kursr.model.OrderItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * The interface Order item dao to work with the order_items table (order items).
 * Provides CRUD operations and search methods.
 */
public interface OrderItemDao {
    /**
     * Add a new order item.
     *
     * @param orderItem the order item object (id will be assigned automatically)
     */
    void add(OrderItem orderItem);

    /**
     * Find an order item by ID.
     *
     * @param id the order item's id (primary key)
     * @return the order item or null, if not found
     */
    OrderItem findById(Long id);

    /**
     * Gets a list of all order items.
     *
     * @return the list of all order items
     */
    List<OrderItem> getAll();

    /**
     * Update the order item data (all fields).
     *
     * @param orderItem the order item with a filled-in id and new values
     */
    void update(OrderItem orderItem);

    /**
     * Delete the order item by object.
     *
     * @param orderItem the order item object
     */
    void delete(OrderItem orderItem);

    /**
     * Find the order items by order id.
     *
     * @param orderId the order id
     * @return the list of order items
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * Find the order items by specimen id.
     *
     * @param specimenId the specimen id
     * @return the list of order items
     */
    List<OrderItem> findBySpecimenId(Long specimenId);

    /**
     * Find the order items by entered by.
     *
     * @param employeeId the employee id
     * @return the list of order items
     */
    List<OrderItem> findByEnteredBy(Long employeeId); // кто ввёл результаты

    /**
     * Update the test result.
     *
     * @param itemId     the item id
     * @param value      the numeric value (can be null)
     * @param text       the text result (can be null)
     * @param abnormal   the sign of deviation from the norm
     * @param idEmployee
     */
    void updateResult(Long itemId, BigDecimal value, String text, boolean abnormal, Long idEmployee);

    /**
     * Update the status of a particular position (assigned, in progress, completed).
     *
     * @param itemId the item id
     * @param status the new status
     */
    void updateStatus(Long itemId, String status);

}
