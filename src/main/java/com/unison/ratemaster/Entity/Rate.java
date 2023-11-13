package com.unison.ratemaster.Entity;

import com.unison.ratemaster.Enum.ShippingTerm;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "RATE")
@Getter
@Setter
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rateId;
    private ShippingTerm term;
    private BigDecimal twentyFtRate;
    private BigDecimal fortyFtRate;
    private BigDecimal fortyFtHQRate;
    private BigDecimal truckingRate;
    private LocalDate validity;
    private String remarks;
    private String factoryLocation;
    private LocalDate entryDate;
    private String entryBy;
    private LocalDate editDate;
    private String editBy;
    private Integer shipmentTime;
    @OneToOne
    private Port portOfLoading;
    @OneToOne
    private Port portOfDestination;
    @OneToOne
    private Commodity commodity;
    @OneToOne
    private Carrier carrier;
}
