package com.unison.ratemaster.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum View {
    CREATE_RATE("Create Rate"),
    VIEW_RATE("Search Rate"),
    SCHEDULE_PANEL("Schedule Panel"),
    CREATE_SHIPMENT("Create Shipment"),
    VIEW_SHIPMENT("View Shipments"),
    CREATE_BILL_OF_LADING("Create B/L"),
    ADD_BOOKING("Add Booking"),
    MANAGE_PARTIES("Add Parties"),
    ADD_OTHERS("Add Others"),
    CREATE_BOOKING("Create Booking"),
    MISC_MANAGEMENT("Misc Management");
    //Add others includes Add Carrier, Add Commodity and Add Ports

    private final String viewName;
}
