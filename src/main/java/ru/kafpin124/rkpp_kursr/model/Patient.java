package ru.kafpin124.rkpp_kursr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Patient {
    private Long idPatient;
    private String policyNumber;
    private String lastName;
    private String firstName;
    private String middleName;
    private Character gender;          // 'м' или 'ж'
    private LocalDate birthDate;
    private String phone;

    private List<Order> orders;

//    public Patient() {
//        this.orders = new ArrayList<>();
//    }


}