package com.unison.ratemaster.Entity;

import com.unison.ratemaster.Enum.ShipmentStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Entity
@Table(name = "SHIPMENT")
@Getter
@Setter
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Length(max = 150)
    private String name;
    private String blNo;
    private String shipperMarks;
    private String goodsDescription;
    private ShipmentStatus status;
    private LocalDateTime createdOn;
    private LocalDateTime lastUpdated;
    @OneToOne
    private Booking bookingNo;
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
    @Lob
    private byte[] masterBl;
}
