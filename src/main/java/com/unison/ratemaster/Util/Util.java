package com.unison.ratemaster.Util;

import com.unison.ratemaster.Entity.Port;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Util {

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
}
