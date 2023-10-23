package com.unison.ratemaster.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContainerType {
    STANDARD_DRY("Standard/Dry"),
    REEFER("Reefer"),
    HARDTOP_OPEN_TOP("Hardtop/Open Top"),
    FLAT_RACK("Flat Rack"),
    PLATFORM("Platform "),
    INSULATED("Insulated"),
    TRANSPORTABLE_TANK("Tanker");

    private final String containerSize;
}
