package com.unison.ratemaster.Entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class FreightDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne
    private Invoice invoice;
    private String description;
    private long quantity;
    private boolean isForeignCurr;
    private BigDecimal rate;
    private BigDecimal totalInForeignCurr;
    private BigDecimal totalInLocalCurr;
}
