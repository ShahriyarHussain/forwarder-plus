package com.unison.ratemaster.Entity;

import com.unison.ratemaster.Enum.AmountCurrency;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
public class Invoice {

    @Id
    private String invoiceNo;
    private String expNo;
    private LocalDate expDate;
    private BigDecimal conversionRate;
    private BigDecimal grandTotal;
    private AmountCurrency foreignCurrency;
    private AmountCurrency localCurrency;
    @OneToMany(fetch = FetchType.EAGER)
    private Set<InvoiceItem> invoiceItems;
    @OneToOne
    private BankDetails bankDetails;
    @OneToOne
    private ContactDetails contactDetails;
}
