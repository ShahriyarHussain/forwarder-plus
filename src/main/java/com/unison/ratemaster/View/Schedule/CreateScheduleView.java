package com.unison.ratemaster.View.Schedule;

import com.unison.ratemaster.Entity.Port;
import com.unison.ratemaster.Entity.Schedule;
import com.unison.ratemaster.Entity.Transshipment;
import com.unison.ratemaster.Enum.VesselType;
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

import java.util.LinkedList;
import java.util.List;

@Route(value = "create-schedule", layout = MainView.class)
public class CreateScheduleView extends VerticalLayout {

    List<Transshipment> transshipmentList = new LinkedList<>();

    public CreateScheduleView(@Autowired ScheduleService scheduleService,
                              @Autowired PortService portService) {

        H3 pageTitle = new H3("Create Schedule");
        FormLayout formLayout = new FormLayout();
        formLayout.setMaxWidth("50%");

        TextField vesselName = new TextField("Vessel Name");
        ComboBox<VesselType> vesselType = new ComboBox<>("Vessel Type");
        vesselType.setItems(VesselType.values());


        DatePicker vgmCutOff = new DatePicker("VGM CutOff");
        DatePicker portCutOff = new DatePicker("Port CutOff");
        portCutOff.setRequired(true);
        DatePicker loadingPortETD = new DatePicker("ETD (Loading Port)");
        loadingPortETD.setRequired(true);
        DatePicker destinationPortETA = new DatePicker("ETA Destination");
        destinationPortETA.setRequired(true);

        List<Port> portList = portService.getPorts();
        ComboBox<Port> portOfLoading = Util.getPortComboBoxByItemListAndTitle(portList, "Port Of Loading");
        portOfLoading.setRequired(true);
        ComboBox<Port> portOfDestination = Util.getPortComboBoxByItemListAndTitle(portList, "Port Of Destination");
        portOfDestination.setRequired(true);


        Button addButton = new Button("Add", e -> {
            Schedule schedule = new Schedule();
            schedule.setPortOfLoading(portOfLoading.getValue());
            schedule.setPortOfDestination(portOfDestination.getValue());
            schedule.setPortCutOff(portCutOff.getValue());
            if (!vgmCutOff.isEmpty()) {
                schedule.setVgmCutOff(vgmCutOff.getValue());
            }
            schedule.setLoadingPortEtd(loadingPortETD.getValue());
            schedule.setDestinationPortEta(destinationPortETA.getValue());
            scheduleService.saveSchedule(schedule);
            Util.getNotificationForSuccess("Schedule Added!").open();
        });

        formLayout.add(vesselName, vesselType, portCutOff, vgmCutOff, loadingPortETD, destinationPortETA, portOfLoading, portOfDestination);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(pageTitle, formLayout, addButton);
    }
}
