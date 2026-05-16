package ru.kafpin124.rkpp_kursr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
// public.orders
public class Order {
    private Long idOrder;
    private OrderStatus status;
    private Patient patient;
    private Organization organization;
    private Employee registeredBy;
    private Employee acceptedBy;
    private Employee verifiedBy;
    private LocalDateTime registrationDatetime;
    private LocalDateTime acceptanceDatetime;
    private LocalDateTime verificationDatetime;

    private List<OrderItem> orderItems = new ArrayList<>();
    private List<Specimen> specimens = new ArrayList<>();


//    public Order() {
//        this.orderItems = new ArrayList<>();
//        this.specimens = new ArrayList<>();
//    }


}