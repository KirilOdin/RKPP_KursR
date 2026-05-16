package ru.kafpin124.rkpp_kursr.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Patient {
    private Long idPatient;
    private String policyNumber;
    private String lastName;
    private String firstName;
    private String middleName;
    private Character gender;          // 'М' или 'Ж'
    private LocalDate birthDate;
    private String phone;

    private List<Order> orders;

    public Patient() {
        this.orders = new ArrayList<>();
    }


}