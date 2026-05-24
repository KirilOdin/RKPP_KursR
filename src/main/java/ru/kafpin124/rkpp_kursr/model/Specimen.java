package ru.kafpin124.rkpp_kursr.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Specimen {
    private Long idSpecimen;
    private String specimenType;
    private String containerType;
    private LocalDateTime collectionDatetime;
    private String barcode;

    private Order order;

//    public Specimen() {}

}