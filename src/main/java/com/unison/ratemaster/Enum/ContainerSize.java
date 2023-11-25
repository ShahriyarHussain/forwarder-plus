package com.unison.ratemaster.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContainerSize {
    TWENTY_FEET("20' "),
    FORTY_FEET("40' "),
    FORTY_FEET_HIGH_CUBE("40' HQ ");

    private final String containerSize;

}
