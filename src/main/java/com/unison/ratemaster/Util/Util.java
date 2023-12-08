package com.unison.ratemaster.Util;

import com.unison.ratemaster.Entity.Port;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {

    private static final Map<String, String> map = new HashMap<>();
    private static final BigDecimal hundred = BigDecimal.valueOf(100);
    private static final BigDecimal thousand = BigDecimal.valueOf(1000);
    private static final BigDecimal lakh = BigDecimal.valueOf(100000);
    private static final BigDecimal crore = BigDecimal.valueOf(10000000);
    public static final String imagePath = "logo_best.png";

    public static Notification getNotificationForError(String errorMessage) {
        Notification notification = new Notification();
        notification.setDuration(2000);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setText(errorMessage);
        return notification;
    }

    public static Notification getNotificationForSuccess(String successMessage) {
        Notification notification = new Notification();
        notification.setDuration(1500);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        notification.setText(successMessage);
        return notification;
    }

    public static ComboBox<Port> getPortComboBoxByItemListAndTitle(List<Port> portList, String title) {
        ComboBox<Port> portOfLoading = new ComboBox<>(title);
        portOfLoading.setItems(portList);
        portOfLoading.setAllowCustomValue(true);
        portOfLoading.setItemLabelGenerator(Port::getPortLabel);
        portOfLoading.setRequired(true);
        portOfLoading.setRequiredIndicatorVisible(true);
        return portOfLoading;
    }

    public static String formatDateTime(String pattern, LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getAmountInWords(BigDecimal amount) {
        if (amount == null) {
            return "Zero";
        }
        setMap();
        StringBuilder amountInWords = new StringBuilder();
        amount = amount.setScale(0, RoundingMode.DOWN);

        BigDecimal remainder = amount.divide(crore, 0, RoundingMode.DOWN);
        if (isNotZero(remainder)) {
            amountInWords.append(getTwoDigitInWords(remainder)).append(" Crore ");
        }
        amount = amount.remainder(crore);

        remainder = amount.divide(lakh, 0, RoundingMode.DOWN);
        if (isNotZero(remainder)) {
            amountInWords.append(getTwoDigitInWords(remainder)).append(" Lakh ");
        }
        amount = amount.remainder(lakh);

        remainder = amount.divide(thousand, 0, RoundingMode.DOWN);
        if (isNotZero(remainder)) {
            amountInWords.append(getTwoDigitInWords(remainder)).append(" Thousand ");
        }
        amount = amount.remainder(thousand);

        remainder = amount.divide(hundred, 0, RoundingMode.DOWN);
        if (isNotZero(remainder)) {
            amountInWords.append(getTwoDigitInWords(remainder)).append(" Hundred ");
        }
        amount = amount.remainder(hundred);
        amountInWords.append(getTwoDigitInWords(amount));
        return "In Words: " + amountInWords + " Taka Only";
    }

    private static boolean isNotZero(BigDecimal bigDecimal) {
        return bigDecimal.compareTo(BigDecimal.ZERO) != 0;
    }

    private static String getTwoDigitInWords(BigDecimal amount) {
        BigDecimal remainder = amount.divide(BigDecimal.TEN, 0, RoundingMode.DOWN);
        if (remainder.equals(BigDecimal.ONE)) {
            return map.get(amount.toPlainString());
        } else if (remainder.compareTo(BigDecimal.ZERO) == 0) {
            return map.get(amount.remainder(BigDecimal.TEN).toPlainString());
        } else if (amount.remainder(BigDecimal.TEN).compareTo(BigDecimal.ZERO) == 0) {
            return map.get(remainder.multiply(BigDecimal.TEN).toPlainString());
        } else {
            return map.get(remainder.multiply(BigDecimal.TEN).toPlainString()) + " "
                    + map.get(amount.remainder(BigDecimal.TEN).toPlainString());
        }
    }

    public static void setMap() {
        map.put("0", "");
        map.put("1", "One");
        map.put("2", "Two");
        map.put("3", "Three");
        map.put("4", "Four");
        map.put("5", "Five");
        map.put("6", "Six");
        map.put("7", "Seven");
        map.put("8", "Eight");
        map.put("9", "Nine");
        map.put("10", "Ten");
        map.put("11", "Eleven");
        map.put("12", "Twelve");
        map.put("13", "Thirteen");
        map.put("14", "Fourteen");
        map.put("15", "Fifteen");
        map.put("16", "Sixteen");
        map.put("17", "Seventeen");
        map.put("18", "Eighteen");
        map.put("19", "Nineteen");
        map.put("20", "Twenty");
        map.put("30", "Thirty");
        map.put("40", "Forty");
        map.put("50", "Fifty");
        map.put("60", "Sixty");
        map.put("70", "Seventy");
        map.put("80", "Eighty");
        map.put("90", "Ninety");
    }


}
