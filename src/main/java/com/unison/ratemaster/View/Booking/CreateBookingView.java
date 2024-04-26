package com.unison.ratemaster.View.Booking;

import com.unison.ratemaster.Entity.Booking;
import com.unison.ratemaster.Entity.FreightContainer;
import com.unison.ratemaster.Enum.ContainerSize;
import com.unison.ratemaster.Enum.ContainerType;
import com.unison.ratemaster.Enum.PackageUnit;
import com.unison.ratemaster.Service.BookingService;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@PermitAll
@Route(value = "create-booking", layout = MainView.class)
public class CreateBookingView extends VerticalLayout {

    private final Set<FreightContainer> containerList = new HashSet<>();

    public CreateBookingView(@Autowired BookingService bookingService) {
        H2 title = new H2("Create Booking");

        TextField bookingNo = new TextField("Booking No");
        Button addButton = new Button("Add");
        bookingNo.setValueChangeMode(ValueChangeMode.EAGER);
        bookingNo.addValueChangeListener(event -> {
            if (event.getSource().getValue() != null && !event.getSource().getValue().isEmpty()) {
                addButton.setEnabled(true);
            }
        });
        TextField invoiceNo = new TextField("Invoice No");
        ComboBox<ContainerType> containerType = new ComboBox<>("Container Type");
        containerType.setItems(ContainerType.values());
        IntegerField numOfCont = new IntegerField("Num Of Containers");
        ComboBox<ContainerSize> containerSize = new ComboBox<>("Container Size");
        containerSize.setItems(ContainerSize.values());
        DatePicker stuffingDate = new DatePicker("Stuffing Date");
        TextField stuffingDepot = new TextField("Stuffing Depot");
        BigDecimalField stuffingCost = new BigDecimalField("Stuffing Cost/Container");

        Grid<FreightContainer> containerGrid = new Grid<>();
        containerGrid.setItems(containerList);
        containerGrid.addColumn(FreightContainer::getContainerNo).setHeader("Container No");
        containerGrid.addColumn(FreightContainer::getSealNo).setHeader("Seal No");
        containerGrid.addColumn(FreightContainer::getGrossWeight).setHeader("Gross Weight");
        containerGrid.addColumn(FreightContainer::getNoOfPackages).setHeader("Packages");
        containerGrid.addColumn(FreightContainer::getPackageUnit).setHeader("Packaging Unit");
        containerGrid.addComponentColumn(freightContainer ->
                new Button(new Icon(VaadinIcon.TRASH), event -> {
                    containerList.remove(freightContainer);
                    containerGrid.setVisible(!containerList.isEmpty());
                    containerGrid.setItems(containerList);
                }));
        containerGrid.setMaxHeight(20, Unit.EM);
        containerGrid.setVisible(!containerList.isEmpty());

        TextField containerNo = new TextField("Container No.");
        TextField sealNo = new TextField("Seal No.");
        BigDecimalField grossWeight = new BigDecimalField("Gross Weight");
        IntegerField noOfPackages = new IntegerField("No Packages");
        ComboBox<PackageUnit> packageUnitComboBox = new ComboBox<>("Package Unit");
        packageUnitComboBox.setItems(PackageUnit.values());
        addButton.setMaxWidth(1, Unit.EM);
        addButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        addButton.addClickListener(event -> {
            FreightContainer container = new FreightContainer();
            container.setContainerNo(containerNo.getValue());
            container.setSealNo(sealNo.getValue());
            container.setNoOfPackages(noOfPackages.getValue());
            container.setGrossWeight(grossWeight.getValue());
            container.setPackageUnit(packageUnitComboBox.getValue());
            containerList.add(container);
            containerGrid.setVisible(true);
            containerGrid.setItems(containerList);
        });
        addButton.setEnabled(bookingNo.getValue() != null && !bookingNo.getValue().isEmpty());

        Button saveButton = new Button("Save Booking");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> {
            Booking booking = new Booking();
            booking.setBookingNo(bookingNo.getValue());
            booking.setContainerType(containerType.getValue());
            booking.setInvoiceNo(invoiceNo.getValue());
            booking.setStuffingDate(stuffingDate.getValue());
            booking.setStuffingDepot(stuffingDepot.getValue());
            booking.setNumOfContainers(numOfCont.getValue());
            booking.setStuffingCostPerContainer(stuffingCost.getValue());
            booking.setContainerSize(containerSize.getValue());
            containerList.forEach(freightContainer -> freightContainer.setBooking(booking));
            booking.setContainer(containerList);
            booking.setEnteredOn(LocalDateTime.now());
            //bookingService.saveBooking(booking, containerList);
            Util.getPopUpNotification("Booking Saved Successfully", 2500, NotificationVariant.LUMO_SUCCESS).open();
        });
        Hr line = new Hr();
        line.setHeight("5px");
        line.setClassName("style=background-color:black");

        FormLayout formLayout = new FormLayout();
        formLayout.add(bookingNo, invoiceNo, containerType, numOfCont, containerSize, stuffingDate, stuffingCost, stuffingDepot,
                line, containerNo, sealNo, grossWeight, noOfPackages, packageUnitComboBox, addButton, containerGrid);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 3));
        formLayout.setColspan(line, 4);
        formLayout.setColspan(containerGrid, 4);
        add(title, formLayout, saveButton);


    }
}
