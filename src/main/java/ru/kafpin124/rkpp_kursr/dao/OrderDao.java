package ru.kafpin124.rkpp_kursr.dao;



import ru.kafpin124.rkpp_kursr.model.Order;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The interface Order dao to work with the orders table (orders for laboratory tests).
 * Provides CRUD operations and search methods.
 */
public interface OrderDao {
    /**
     * Add a new order.
     *
     * @param order the order object (id will be assigned automatically)
     */
    void add(Order order);

    /**
     * Find an order by ID.
     *
     * @param id the order's id (primary key)
     * @return the order or null, if not found
     */
    Order findById(Long id);

    /**
     * Gets a list of all orders.
     *
     * @return the list all orders
     */
    List<Order> getAll();

    /**
     * Update the order data (all fields).
     *
     * @param order the order object with a filled-in id and new values
     */
    void update(Order order);

    /**
     * Delete the order by object.
     *
     * @param order the order object
     */
    void delete(Order order);

    /**
     * Find the orders by patient id.
     *
     * @param patientId the patient id
     * @return the list of orders
     */
    List<Order> findByPatientId(Long patientId);

    /**
     * Find by status id list.
     *
     * @param statusId the status id (1-registered, 2-in operation, ...)
     * @return the list or orders
     */
    List<Order> findByStatusId(Long statusId);

    /**
     * Find the orders registered by specified time range.
     *
     * @param from the beginning of the period
     * @param to   the end of the period
     * @return the list of orders sorted by registration date (new ones first)
     */
    List<Order> findByDateRange(LocalDateTime from, LocalDateTime to);

    /**
     * Find orders registered by a specific employee.
     *
     * @param employeeId the employee id
     * @return the list of orders
     */
    List<Order> findByRegisteredBy(Long employeeId);

    /**
     * Find the order by the specimen barcode (via JOIN with specimens).
     *
     * @param barcode the specimen barcode
     * @return the order or null, if not found
     */
    Order findBySpecimenBarcode(String barcode);

    /**
     * Update the order status.
     *
     * @param orderId  the order id
     * @param statusId the new order status id (id from the directory)
     */
    void updateStatus(Long orderId, Long statusId);

}
