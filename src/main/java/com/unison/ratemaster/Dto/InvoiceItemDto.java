package com.unison.ratemaster.Dto;

import com.unison.ratemaster.Entity.InvoiceItem;
import com.unison.ratemaster.Util.Util;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InvoiceItemDto {
    private long slNo;
    private String description;
    private String quantityWithUnit;
    private String rate;
    private String totalInForeignCurr;
    private String subtotal;

    public InvoiceItemDto(InvoiceItem item) {
        this.description = item.getDescription();
        this.quantityWithUnit = item.getQuantity() + " " + item.getItemUnit();
        this.rate =  item.getCurrency() + " " + Util.getFormattedBigDecimal(item.getRate());
        if (item.isForeignCurr()) {
            this.totalInForeignCurr = item.getCurrency() + " " + Util.getFormattedBigDecimal(item.getTotalInForeignCurr());
        } else {
            this.totalInForeignCurr = null;
        }
        this.subtotal = item.getInvoice().getLocalCurrency().toString() + " " +
                Util.getFormattedBigDecimal(item.getTotalInLocalCurr());
    }

    @Override
    public String toString() {
        return slNo + " - " + description + " - " + quantityWithUnit + " - " + rate + " - " + totalInForeignCurr + " - " + subtotal;
    }
}
