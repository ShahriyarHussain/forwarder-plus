package com.unison.ratemaster.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContainerSize {
    TWENTY_FEET_DRY("20 Feet "),
    FORTY_FEET_DRY("40 Feet "),
    FORTY_FEET_DRY_HC("40 Feet High Cube ");

    private final String containerSize;

}
