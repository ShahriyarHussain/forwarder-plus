package com.unison.ratemaster.Util;

import com.unison.ratemaster.Entity.Shipment;
import com.vaadin.flow.component.grid.dataview.GridListDataView;

public class ShipmentFilter {
    private final GridListDataView<Shipment> dataView;

    private String blNo;
    private String bookingNo;
    private String shipperInvoiceNo;
    private String invoiceNo;

    public ShipmentFilter(GridListDataView<Shipment> dataView) {
        this.dataView = dataView;
        this.dataView.addFilter(this::filterData);
    }

    public void setBlNo(String blNo) {
        this.blNo = blNo;
        this.dataView.refreshAll();
    }

    public void setBookingNo(String bookingNo) {
        this.bookingNo = bookingNo;
        this.dataView.refreshAll();
    }

    public void setShipperInvoiceNo(String shipperInvoiceNo) {
        this.shipperInvoiceNo = shipperInvoiceNo;
        this.dataView.refreshAll();
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
        this.dataView.refreshAll();
    }

    public boolean filterData(Shipment shipment) {
        boolean matchesBLNo = matches(shipment.getBlNo(), blNo);
        boolean matchesBookingNo = matches(shipment.getBooking().getBookingNo(), bookingNo);
        boolean matchesShipperInvoice = matches(shipment.getInvoiceNo(), shipperInvoiceNo);
        boolean matchesInvoice;
        if (shipment.getInvoice() == null) {
            matchesInvoice = false;
        } else {
            matchesInvoice = matches(shipment.getInvoice().getInvoiceNo(), invoiceNo);
        }
        return matchesBLNo && matchesBookingNo && matchesShipperInvoice && matchesInvoice;
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty()
                || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
