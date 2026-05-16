package ru.kafpin124.rkpp_kursr.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Specimen {
    private Long idSpecimen;
    private String specimenType;
    private String containerType;
    private LocalDateTime collectionDatetime;
    private String barcode;

    private Order order;

    public Specimen() {}

}