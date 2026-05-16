package ru.kafpin124.rkpp_kursr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// public.employees
public class Employee {
    private Long idEmployee;
    private String role;
    private String position;
    private String lastName;
    private String firstName;
    private String middleName;
    private String login;
    private String passwordHash;

}