package com.unison.ratemaster.Entity;

import com.unison.ratemaster.Enum.AmountCurrency;
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

    public static BigDecimal calculateItemTotal(BigDecimal rate, Integer quantity, Boolean isForeignCurr, BigDecimal convRate) {
        if (rate == null || quantity == null || convRate == null) {
            System.out.println("NULL FOUND IN CALCULATION");
            return BigDecimal.ZERO;
        }
        BigDecimal conversionRate = isForeignCurr ? convRate : BigDecimal.ONE;
        return rate.multiply(new BigDecimal(quantity)).multiply(conversionRate);
    }

    public static String addSuffixToWordAmountByCurrency(AmountCurrency currency, String amount) {
        if (currency == null) {
            currency = AmountCurrency.BDT;
        }
        return amount + " " + currency.getCurrencyName() + " Only";
    }

    public static String getTotalColumnName(AmountCurrency currency) {
        if (currency == null) {
            return "Total";
        }
        return "Total in " + currency;
    }
}
