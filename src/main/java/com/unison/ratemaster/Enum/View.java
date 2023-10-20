package com.unison.ratemaster.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum View {
    CREATE_RATE("Create Rate"),
    VIEW_RATE("View Rate"),
    CREATE_SCHEDULE("Create Schedule"),
    VIEW_SCHEDULE("View Schedule"),
    CREATE_SHIPMENT("Create Shipment"),
    VIEW_SHIPMENT("View Shipment"),
    CREATE_BILL_OF_LADING("Create B/L");

    private final String viewName;
}
