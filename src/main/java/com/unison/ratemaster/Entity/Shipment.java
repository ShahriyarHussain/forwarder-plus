package com.unison.ratemaster.Entity;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "SHIPMENT")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Length(max = 150)
    private String name;
    private String description;
    private String blNo;
    private String bookingNo;
    private String goodsDescription;
    @Lob
    private byte[] masterBl;
    @OneToOne
    private Client shipper;
    @OneToOne
    private Client consignee;
    @OneToOne
    private Client notifyParty;
    @OneToOne
    private Rate rate;
    @OneToOne
    private Schedule schedule;
    @OneToOne
    private Commodity commodity;
}
