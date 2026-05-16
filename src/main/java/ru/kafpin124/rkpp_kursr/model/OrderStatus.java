package ru.kafpin124.rkpp_kursr.model;

import lombok.Data;

@Data
public class OrderStatus {
    private Long idStatus;
    private String statusName;

    public OrderStatus() {}

}