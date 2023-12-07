package com.unison.ratemaster.Dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private BigDecimal ratePerContainer;
    private BigDecimal totalFreight;
    private BigDecimal conversionRate;
    private String currency;
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
