package ru.kafpin124.rkpp_kursr.dao.impl;


import ru.kafpin124.rkpp_kursr.dao.OrderStatusDao;
import ru.kafpin124.rkpp_kursr.model.OrderStatus;

import java.util.List;

public class OrderStatusDaoImpl implements OrderStatusDao {

    //TODO: Сделать мапперы + реализовать методы!





    private static final String ADD = "INSERT INTO public.order_statuses(status_name) VALUES (?);";


    private static final String GET_ALL = "SELECT id_status, status_name FROM public.order_statuses;";


    private static final String FIND_BY_ID = "SELECT id_status, status_name FROM public.order_statuses " +
            "WHERE id_status = ?;";


    private static final String DELETE_BY_ID = "DELETE FROM public.order_statuses WHERE id_status = ?;";


    private static final String UPDATE = "UPDATE public.order_statuses SET status_name=? WHERE id_status = ?;";





    @Override
    public void add(OrderStatus orderStatus) {

    }

    @Override
    public OrderStatus getById(Long id) {
        return null;
    }

    @Override
    public List<OrderStatus> getAll() {
        return List.of();
    }

    @Override
    public void update(OrderStatus orderStatus) {

    }

    @Override
    public void delete(OrderStatus orderStatus) {

    }
}
