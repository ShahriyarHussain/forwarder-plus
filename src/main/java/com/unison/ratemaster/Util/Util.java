package com.unison.ratemaster.Util;

import com.unison.ratemaster.Entity.Port;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class Util {
    public static String getPortLabel(Port port) {
        return port.getPortShortCode() + " - " + port.getPortCity() + ", " + port.getPortCountry();
    }

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
}
