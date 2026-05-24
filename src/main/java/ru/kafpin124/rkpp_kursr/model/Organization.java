package ru.kafpin124.rkpp_kursr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Organization {
    private Long idOrg;
    private String orgName;
    private String contractNumber;
    private String contactLastName;
    private String contactFirstName;
    private String contactMiddleName;
    private String contactPersonPhone;

//    public Organization() {
//    }

}
