package com.unison.ratemaster.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne
    private Invoice invoice;
    private String description;
    private long quantity;
    private String itemUnit;
    private String currency;
    private boolean isForeignCurr;
    private BigDecimal rate;
    private BigDecimal totalInForeignCurr;
    private BigDecimal totalInLocalCurr;
}
