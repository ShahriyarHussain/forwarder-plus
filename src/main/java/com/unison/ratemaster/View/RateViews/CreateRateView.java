package com.unison.ratemaster.View.RateViews;

import com.unison.ratemaster.Entity.Port;
import com.unison.ratemaster.Entity.Rate;
import com.unison.ratemaster.Enum.ShippingTerm;
import com.unison.ratemaster.Service.PortService;
import com.unison.ratemaster.Service.RateService;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "create-rate", layout = MainView.class)
public class CreateRateView extends VerticalLayout {

    public CreateRateView(@Autowired PortService portService, @Autowired RateService rateService) {
        H3 pageTitle = new H3("Create Rate");
        // Entry form section
        FormLayout formLayout = new FormLayout();

        List<Port> portList = portService.getPorts();

        ComboBox<Port> portOfLoading = new ComboBox<>("Port Of Loading");
        portOfLoading.setItems(portList);
        portOfLoading.setAllowCustomValue(true);
        portOfLoading.setItemLabelGenerator(Util::getPortLabel);
        portOfLoading.setRequired(true);
        portOfLoading.setRequiredIndicatorVisible(true);

        ComboBox<Port> portOfDestination = new ComboBox<>("Port Of Destination");
        portOfDestination.setItems(portList);
        portOfDestination.setAllowCustomValue(true);
        portOfDestination.setItemLabelGenerator(Util::getPortLabel);
        portOfDestination.setRequired(true);
        portOfDestination.setRequiredIndicatorVisible(true);

        //title
        TextField commodity = new TextField("Commodity");
        commodity.setRequired(true); commodity.setRequiredIndicatorVisible(true);

        Select<ShippingTerm> term = new Select<>();
        term.setLabel("Term");
        term.setItems(ShippingTerm.values());

        TextField carrier = new TextField("Carrier");
        carrier.setRequired(true); carrier.setRequiredIndicatorVisible(true);
        //Amount
        BigDecimalField twentyFtRate = new BigDecimalField("20' Rate (USD)");
        twentyFtRate.setRequiredIndicatorVisible(false);
        BigDecimalField fortyFtRate = new BigDecimalField("40' Rate (USD)");
        fortyFtRate.setRequiredIndicatorVisible(false);
        BigDecimalField fortyHQRate = new BigDecimalField("40' HC Rate (USD)");
        fortyHQRate.setRequiredIndicatorVisible(false);


        DatePicker validity = new DatePicker("Validity");

        TextArea remarks = new TextArea("Remarks");
        remarks.setHeight(5, Unit.EM);
        remarks.setMaxLength(200);
        remarks.setValueChangeMode(ValueChangeMode.EAGER);
        remarks.addValueChangeListener(e -> e.getSource().setHelperText(e.getValue().length() + "/" + 200));

        formLayout.add(portOfLoading, portOfDestination, commodity, validity, twentyFtRate, fortyFtRate, fortyHQRate, carrier, remarks, term);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));
        formLayout.setColspan(remarks,2);
        // Form section end

        Button saveButton = new Button("Save Rate", event -> {
            Rate rate = new Rate();
            rate.setValidity(validity.getValue());
            rate.setFortyFtHQRate(fortyHQRate.getValue());
            rate.setCommodity(commodity.getValue());
            rate.setCarrier(carrier.getValue());
            rate.setFortyFtRate(fortyFtRate.getValue());
            rate.setTwentyFtRate(twentyFtRate.getValue());
            rate.setTerm(term.getValue());
            rate.setPortOfDestination(portOfDestination.getValue());
            rate.setPortOfLoading(portOfLoading.getValue());
            rate.setRemarks(remarks.getValue());
            rateService.saveRate(rate);
            Util.getNotificationForSuccess("Rate Saved!").open();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(pageTitle, formLayout, saveButton);
    }
}