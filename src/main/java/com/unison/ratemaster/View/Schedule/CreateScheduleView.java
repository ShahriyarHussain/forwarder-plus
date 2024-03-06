package com.unison.ratemaster.View.Schedule;

import com.unison.ratemaster.Entity.Port;
import com.unison.ratemaster.Entity.Schedule;
import com.unison.ratemaster.Service.PortService;
import com.unison.ratemaster.Service.ScheduleService;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "create-schedule", layout = MainView.class)
public class CreateScheduleView extends VerticalLayout {
    public CreateScheduleView(@Autowired ScheduleService scheduleService,
                              @Autowired PortService portService) {

        H3 pageTitle = new H3("Create Schedule");
        FormLayout formLayout = new FormLayout();
        formLayout.setMaxWidth("50%");

        TextField feederVesselName = new TextField("Feeder Vessel Name");

        List<Port> portList = portService.getPorts();

        ComboBox<Port> portOfLoading = Util.getPortComboBoxByItemListAndTitle(portList, "Loading Port");
        DatePicker polEta = new DatePicker(portOfLoading.getLabel() + " ETA");
        DatePicker polEtd = new DatePicker(portOfLoading.getLabel() + " ETD");
        portOfLoading.setRequired(true);
        polEtd.setRequired(true);
        polEta.setRequired(true);
        portOfLoading.addValueChangeListener(event -> {
            if (event != null && event.getValue() != null) {
                Port vesselPort = event.getValue();
                polEtd.setLabel("ETD " + vesselPort.getPortShortCode());
                polEta.setLabel("ETA " + vesselPort.getPortShortCode());
            }
        });


        TextField motherVesselName = new TextField("Mother Vessel Name");
        motherVesselName.setRequired(true);

        ComboBox<Port> motherVesselPort = Util.getPortComboBoxByItemListAndTitle(portList, "Mother Vessel Port");
        DatePicker motherVesselPortEta = new DatePicker("Mother Vessel Port ETA");
        DatePicker mvPortEta = new DatePicker("Feeder Connect ETA");
        motherVesselPort.setRequired(true);
        motherVesselPortEta.setRequired(true);
        motherVesselPort.addValueChangeListener(event -> {
            if (event != null && event.getValue() != null) {
                Port vesselPort = event.getValue();
                motherVesselPortEta.setLabel("ETA " + vesselPort.getPortShortCode());
                mvPortEta.setLabel("ETA " + vesselPort.getPortShortCode());
            }
        });



        ComboBox<Port> tsPort = Util.getPortComboBoxByItemListAndTitle(portList, "Transshipment Port");
        DatePicker tsPortEta = new DatePicker(tsPort.getLabel() + " ETA");
        tsPort.addValueChangeListener(event -> {
            if (event != null && event.getValue() != null) {
                Port vesselPort = event.getValue();
                tsPortEta.setLabel("ETA " + vesselPort.getPortName());
            }
        });

        ComboBox<Port> destinationPort = Util.getPortComboBoxByItemListAndTitle(portList, "Destination Port");
        DatePicker destinationPortEta = new DatePicker(destinationPort.getLabel() + " ETA");
        destinationPortEta.setRequired(true);
        destinationPort.setRequired(true);
        destinationPort.addValueChangeListener(event -> {
            if (event != null && event.getValue() != null) {
                Port vesselPort = event.getValue();
                destinationPortEta.setLabel("ETA " + vesselPort.getPortName());
            }
        });

        Button addButton = new Button("Add", e -> {
            Schedule schedule = new Schedule();
            schedule.setPortOfLoading(portOfLoading.getValue());
            schedule.setLoadingPortEta(polEta.getValue());
            schedule.setLoadingPortEtd(polEtd.getValue());
            schedule.setMvPortFeederEta(mvPortEta.getValue());

            schedule.setMotherVesselPort(motherVesselPort.getValue());
            schedule.setMotherVesselPortEta(motherVesselPortEta.getValue());

            schedule.setTsPort(tsPort.getValue());
            schedule.setTsPortEta(tsPortEta.getValue());

            schedule.setPortOfDestination(destinationPort.getValue());
            schedule.setDestinationPortEta(destinationPortEta.getValue());

            scheduleService.saveSchedule(schedule);
            Util.getNotificationForSuccess("Schedule Added!").open();
        });

        formLayout.add(feederVesselName, motherVesselName, portOfLoading, motherVesselPort, polEta, motherVesselPortEta,
                polEtd, mvPortEta, tsPort, tsPortEta, destinationPort, destinationPortEta);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(pageTitle, formLayout, addButton);
    }
}
