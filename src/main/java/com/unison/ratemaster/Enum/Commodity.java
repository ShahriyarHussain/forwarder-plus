package com.unison.ratemaster.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Commodity {
    FAK("Freight All Kinds"),
    JUTE("JUTE YARN"),
    RMG("READY MADE GARMENTS");

    private final String description;
}
