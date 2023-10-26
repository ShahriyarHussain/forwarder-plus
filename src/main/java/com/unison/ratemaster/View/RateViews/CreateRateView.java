package com.unison.ratemaster.View.RateViews;

import com.unison.ratemaster.Entity.*;
import com.unison.ratemaster.Enum.ShippingTerm;
import com.unison.ratemaster.Service.*;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "create-rate", layout = MainView.class)
public class CreateRateView extends VerticalLayout {

    Schedule schedule;

    public CreateRateView(@Autowired PortService portService,
                          @Autowired RateService rateService,
                          @Autowired CarrierService carrierService,
                          @Autowired CommodityService commodityService,
                          @Autowired ScheduleService scheduleService) {
        H3 pageTitle = new H3("Create Rate");

        // Entry form section
        FormLayout formLayout = new FormLayout();

        List<Port> portList = portService.getPorts();
        List<Carrier> carrierList = carrierService.getAllCarriers();
        List<Commodity> commodityList = commodityService.getAllCommodity();

        ComboBox<Port> portOfLoading = getPortComboBoxByItemListAndTitle(portList, "Port Of Loading");
        ComboBox<Port> portOfDestination = getPortComboBoxByItemListAndTitle(portList, "Port Of Destination");

        ComboBox<Carrier> carrierComboBox = new ComboBox<>("Carrier");
        carrierComboBox.setItems(carrierList);
        carrierComboBox.setAllowCustomValue(true);
        carrierComboBox.setItemLabelGenerator(Carrier::getName);
        carrierComboBox.setRequired(true);
        carrierComboBox.setRequiredIndicatorVisible(true);

        ComboBox<Commodity> commodityComboBox  = new ComboBox<>("Commodity");
        commodityComboBox.setItems(commodityList);
        commodityComboBox.setAllowCustomValue(true);
        commodityComboBox.setItemLabelGenerator(Commodity::getCommoditySummary);
        commodityComboBox.setRequired(true);
        commodityComboBox.setRequiredIndicatorVisible(true);

        Select<ShippingTerm> term = new Select<>();
        term.setLabel("Term");
        term.setItems(ShippingTerm.values());

        //Amount
        BigDecimalField twentyFtRate = new BigDecimalField("20' Rate (USD)");
        twentyFtRate.setRequiredIndicatorVisible(false);
        BigDecimalField fortyFtRate = new BigDecimalField("40' Rate (USD)");
        fortyFtRate.setRequiredIndicatorVisible(false);
        BigDecimalField fortyHQRate = new BigDecimalField("40' HC Rate (USD)");
        fortyHQRate.setRequiredIndicatorVisible(false);
        BigDecimalField exwRate = new BigDecimalField("EXW Rate (USD)");
        fortyHQRate.setRequiredIndicatorVisible(false);


        DatePicker validity = new DatePicker("Validity");

        TextArea factoryLocation = new TextArea("Factory Location");
        factoryLocation.setHeight(5, Unit.EM);
        factoryLocation.setMaxLength(500);
        factoryLocation.setValueChangeMode(ValueChangeMode.EAGER);
        factoryLocation.addValueChangeListener(e -> e.getSource().setHelperText(e.getValue().length() + "/" + 500));

        TextArea remarks = new TextArea("Remarks");
        remarks.setHeight(5, Unit.EM);
        remarks.setMaxLength(200);
        remarks.setValueChangeMode(ValueChangeMode.EAGER);
        remarks.addValueChangeListener(e -> e.getSource().setHelperText(e.getValue().length() + "/" + 200));


        /// SAVE SCHEDULE

        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Add Schedule");

        VerticalLayout dialogLayout = createDialogLayout(scheduleService, portList);
        dialog.add(dialogLayout);

        //Button saveButton = createSaveButton(dialog);
        Button cancelButton = new Button("Close", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        //dialog.getFooter().add(saveButton);

        Button addScheduleButton = new Button("Add Schedule", e -> dialog.open());
        if (this.schedule != null) {
            addScheduleButton.setText("Edit Schedule");
        }

        /// SAVE SCHEDULE

        formLayout.add(portOfLoading, portOfDestination, commodityComboBox, carrierComboBox, twentyFtRate, fortyFtRate,
                fortyHQRate, exwRate, validity, term, factoryLocation, remarks, addScheduleButton);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));
        formLayout.setColspan(remarks,2);
        // Form section end

        Button saveButton = new Button("Save Rate", event -> {
            Rate rate = new Rate();
            rate.setValidity(validity.getValue());
            rate.setFortyFtHQRate(fortyHQRate.getValue());
            rate.setCommodity(commodityComboBox.getValue());
            rate.setCarrier(carrierComboBox.getValue());
            rate.setFortyFtRate(fortyFtRate.getValue());
            rate.setTwentyFtRate(twentyFtRate.getValue());
            rate.setTerm(term.getValue());
            rate.setPortOfDestination(portOfDestination.getValue());
            rate.setPortOfLoading(portOfLoading.getValue());
            rate.setRemarks(remarks.getValue());
            rate.setTruckingRate(exwRate.getValue());
            rate.setFactoryLocation(factoryLocation.getValue());
            rateService.saveRate(rate);
            Util.getNotificationForSuccess("Rate Saved!").open();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(pageTitle, formLayout, saveButton);
    }

    private VerticalLayout createDialogLayout(ScheduleService scheduleService, List<Port> portList) {

        VerticalLayout verticalLayout = new VerticalLayout();
        List<Schedule> scheduleList = scheduleService.getValidSchedules();

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        ComboBox<Schedule> scheduleComboBox  = new ComboBox<>("Tag Existing Schedule:");
        scheduleComboBox.setItems(scheduleList);
        scheduleComboBox.setAllowCustomValue(false);
        scheduleComboBox.setItemLabelGenerator(this::getScheduleSummary);
        scheduleComboBox.setRequired(true);
        scheduleComboBox.setRequiredIndicatorVisible(true);

        Button addExistingButton = new Button("Tag", event -> {
            if (scheduleComboBox.isEmpty()) {
                Util.getNotificationForError("No Schedule Selected!").open();
            } else {
                this.schedule = scheduleComboBox.getValue();
            }
        });

        horizontalLayout.add(scheduleComboBox, addExistingButton);
        horizontalLayout.setAlignItems(Alignment.END);

        FormLayout formLayout = new FormLayout();
        DatePicker portCutOff = new DatePicker("Port CutOff");
        portCutOff.setRequired(true);
        DatePicker vgmCutOff = new DatePicker("VGM CutOff");
        DatePicker loadingPortETD = new DatePicker("ETD (Loading Port)");
        loadingPortETD.setRequired(true);
        DatePicker destinationPortETA = new DatePicker("ETA Destination");
        destinationPortETA.setRequired(true);
        ComboBox<Port> portOfLoading = getPortComboBoxByItemListAndTitle(portList, "Port Of Loading");
        portOfLoading.setRequired(true);
        ComboBox<Port> portOfDestination = getPortComboBoxByItemListAndTitle(portList, "Port Of Destination");
        portOfDestination.setRequired(true);

        formLayout.add(portCutOff, vgmCutOff, loadingPortETD, portOfLoading, destinationPortETA, portOfDestination);

        verticalLayout.add(horizontalLayout, formLayout);
        return verticalLayout;
    }

    private String getScheduleSummary(Schedule schedule) {
        return schedule.getPortOfLoading().getPortShortCode() + " to "
                + schedule.getPortOfDestination().getPortShortCode() + " | " + schedule.getLoadingPortETD() + schedule.getDestinationPortETA();
    }

    private static ComboBox<Port> getPortComboBoxByItemListAndTitle(List<Port> portList, String title) {
        ComboBox<Port> portOfLoading = new ComboBox<>(title);
        portOfLoading.setItems(portList);
        portOfLoading.setAllowCustomValue(true);
        portOfLoading.setItemLabelGenerator(Port::getPortLabel);
        portOfLoading.setRequired(true);
        portOfLoading.setRequiredIndicatorVisible(true);
        return portOfLoading;
    }
}