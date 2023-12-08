package com.unison.ratemaster.Entity;

import com.unison.ratemaster.Enum.AmountCurrency;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Invoice {

    @Id
    private String invoiceNo;
    private String expNo;
    private LocalDate expDate;
    private BigDecimal ratePerContainer;
    private BigDecimal totalFreight;
    private BigDecimal conversionRate;
    private BigDecimal freightTotalInLocalCurr;
    private BigDecimal subTotal;
    private AmountCurrency currency;
    private String goodsDescription;
    private String otherDesc1;
    private BigDecimal other1Amt;
    private String otherDesc2;
    private BigDecimal other2Amt;
    private String bankName;
    private String acName;
    private String acNo;
    private String routingNo;
    private String branch;
    private String preparedBy;
    private String preparedByEmail;
    private String preparedByContact;
}
