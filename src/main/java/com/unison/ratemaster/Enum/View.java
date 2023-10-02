package com.unison.ratemaster.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum View {
    CREATE_RATE("Create Rate"),
    VIEW_RATE("View Rate"),
    CREATE_BILL_OF_LADING("Create B/L"),
    CREATE_SCHEDULE("Create Schedule"),
    VIEW_SCHEDULE("View Schedule");

    private final String viewName;
}
