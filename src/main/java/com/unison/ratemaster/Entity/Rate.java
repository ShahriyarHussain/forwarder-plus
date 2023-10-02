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
    @OneToOne
    private Port portOfLoading;
    @OneToOne
    private Port portOfDestination;
    private String commodity;
    private ShippingTerm term;
    private BigDecimal twentyFtRate;
    private BigDecimal fortyFtRate;
    private BigDecimal fortyFtHQRate;
    private LocalDate validity;
    private String carrier;
    private String remarks;
    private LocalDate entryDate;
    private String entryBy;
    private LocalDate editDate;
    private String editBy;
}
