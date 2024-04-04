package com.unison.ratemaster.Entity;

import com.unison.ratemaster.Enum.AmountCurrency;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
public class Invoice {

    @Id
    private String invoiceNo;
    private String expNo;
    private LocalDate expDate;
    private BigDecimal totalFreight;
    private BigDecimal conversionRate;
    private BigDecimal subTotal;
    private AmountCurrency freightCurrency;
    private AmountCurrency localCurrency;
    @OneToMany
    private List<FreightDetails> freightDetails;
    @OneToOne
    private BankDetails bankDetails;
    @OneToOne
    private ContactDetails contactDetails;
}
