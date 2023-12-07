package com.unison.ratemaster.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AmountCurrency {
    USD("US Dollar"),
    BDT("Bangladeshi Taka"),
    JPY("Japanese Yen"),
    AUD("Australian Dollar"),
    CAD("Canadian Dollar"),
    HKD("Hong Kong Dollar"),
    CNY("Chinese Yen"),
    EUR("EURO");

    private final String currencyName;
}
