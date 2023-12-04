package com.unison.ratemaster.View.Rate;

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
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
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
        H2 pageTitle = new H2("Create Rate");

        // Entry form section
        FormLayout formLayout = new FormLayout();

        List<Port> portList = portService.getPorts();
        List<Carrier> carrierList = carrierService.getAllCarriers();
        List<Commodity> commodityList = commodityService.getAllCommodity();

        ComboBox<Port> portOfLoading = Util.getPortComboBoxByItemListAndTitle(portList, "Port Of Loading");
        ComboBox<Port> portOfDestination = Util.getPortComboBoxByItemListAndTitle(portList, "Port Of Destination");

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
        factoryLocation.setHeight(10, Unit.EM);
        factoryLocation.setMaxLength(500);
        factoryLocation.setValueChangeMode(ValueChangeMode.EAGER);
        factoryLocation.addValueChangeListener(e -> e.getSource().setHelperText(e.getValue().length() + "/" + 500));

        TextArea remarks = new TextArea("Remarks");
        remarks.setHeight(10, Unit.EM);
        remarks.setMaxLength(200);
        remarks.setValueChangeMode(ValueChangeMode.EAGER);
        remarks.addValueChangeListener(e -> e.getSource().setHelperText(e.getValue().length() + "/" + 200));

        formLayout.add(portOfLoading, portOfDestination, commodityComboBox, carrierComboBox, twentyFtRate, fortyFtRate,
                fortyHQRate, exwRate, validity, term, new Hr(), factoryLocation, remarks);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));
        formLayout.setColspan(remarks,2);
        formLayout.setColspan(factoryLocation,2);

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

}