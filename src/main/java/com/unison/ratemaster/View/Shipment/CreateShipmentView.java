package com.unison.ratemaster.View.Shipment;

import com.unison.ratemaster.Entity.*;
import com.unison.ratemaster.Enum.ContainerSize;
import com.unison.ratemaster.Enum.ContainerType;
import com.unison.ratemaster.Enum.ShipmentStatus;
import com.unison.ratemaster.Service.*;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Route(value = "create-shipment", layout = MainView.class)
public class CreateShipmentView extends VerticalLayout {

    byte[] masterBl;
    Booking booking;

    public CreateShipmentView(@Autowired ShipmentService shipmentService,
                              @Autowired ClientService clientService,
                              @Autowired ScheduleService scheduleService,
                              @Autowired CommodityService commodityService,
                              @Autowired BookingService bookingService) {

        H2 title = new H2("Create Shipment");

        ComboBox<Booking> bookings = new ComboBox<>("Choose Booking");
        bookings.setItems(bookingService.getLatestBooking());
        bookings.setItemLabelGenerator(booking -> booking.getBookingNo() + "-"
                + booking.getNumOfContainers() + " x " + booking.getContainerSize().getContainerSize());
        bookings.setWidthFull();

        TextField name = new TextField("Shipment Name");
        name.setRequired(true);

        TextField blNo = new TextField("B/L No");
        TextArea goodsDescription = new TextArea("Goods Description");
        TextArea shipperMarks = new TextArea("Shipper Marks");

        TextField bookingNo = new TextField("Booking No");
        TextField invoiceNo = new TextField("Invoice No");

        ComboBox<ContainerType> containerType = new ComboBox<>("Container Type:");
        containerType.setItems(ContainerType.values());
        containerType.setItemLabelGenerator(ContainerType::getContainerSize);

        ComboBox<ContainerSize> containerSize = new ComboBox<>("Container Size");
        containerSize.setItems(ContainerSize.values());
        containerSize.setItemLabelGenerator(ContainerSize::getContainerSize);

        DatePicker stuffingDate = new DatePicker("Stuffing Date");
        TextField stuffingDepot = new TextField("Stuffing Depot");
        IntegerField numOfContainers = new IntegerField("Number of Containers");
        BigDecimalField ratePerContainer = new BigDecimalField("Rate per Container");

        List<Client> clients = clientService.getAllClients();
        ComboBox<Client> shipper = new ComboBox<>("Shipper");
        shipper.setItems(clients);
        shipper.setItemLabelGenerator(Client::getName);

        ComboBox<Client> consignee = new ComboBox<>("Consignee");
        consignee.setItems(clients);
        consignee.setItemLabelGenerator(Client::getName);

        ComboBox<Client> notifyParty = new ComboBox<>("Notify Party");
        notifyParty.setItems(clients);
        notifyParty.setItemLabelGenerator(Client::getName);

        ComboBox<Schedule> scheduleComboBox = new ComboBox<>("Schedule");
        scheduleComboBox.setItems(scheduleService.getValidSchedules());
        scheduleComboBox.setItemLabelGenerator(Schedule::getScheduleSummary);

        ComboBox<Commodity> commodities = new ComboBox<>("Commodity");
        commodities.setItems(commodityService.getAllCommodity());
        commodities.setItemLabelGenerator(Commodity::getCommoditySummary);

        bookings.addValueChangeListener(event -> {
            this.booking = event.getSource().getValue();
            if (this.booking != null) {
                bookingNo.setValue(this.booking.getBookingNo());
                containerType.setValue(this.booking.getContainerType());
                invoiceNo.setValue(this.booking.getInvoiceNo());
                stuffingDate.setValue(this.booking.getStuffingDate());
                stuffingDepot.setValue(this.booking.getStuffingDepot());
                numOfContainers.setValue(this.booking.getNumOfContainers());
                ratePerContainer.setValue(this.booking.getStuffingCostPerContainer());
            }
        });

        Upload upload = getUploadComponent();

        Button saveButton = new Button("Save", event -> {
            Shipment shipment = new Shipment();
            shipment.setName(name.getValue());
            shipment.setBlNo(blNo.getValue());
            shipment.setNumberOfContainer(numOfContainers.getValue());
            shipment.setInvoiceNo(invoiceNo.getValue());
            shipment.setGoodsDescription(goodsDescription.getValue());
            shipment.setShipperMarks(shipperMarks.getValue());
            shipment.setShipper(shipper.getValue());
            shipment.setConsignee(consignee.getValue());
            shipment.setNotifyParty(notifyParty.getValue());
            shipment.setStatus(ShipmentStatus.NEW);
            shipment.setMasterBl(this.masterBl);
            shipment.setCommodity(commodities.getValue());
            shipment.setContainerSize(containerSize.getValue());
            shipment.setMasterBl(this.masterBl);
            shipment.setCreatedOn(LocalDateTime.now());
            shipment.setLastUpdated(LocalDateTime.now());


            if (bookings.getValue() == null) {
                this.booking = new Booking();
            } else {
                this.booking = bookings.getValue();
            }

            booking.setBookingNo(bookingNo.getValue());
            booking.setContainerType(containerType.getValue());
            booking.setInvoiceNo(invoiceNo.getValue());
            booking.setStuffingDate(stuffingDate.getValue());
            booking.setStuffingDepot(stuffingDepot.getValue());
            booking.setNumOfContainers(numOfContainers.getValue());
            booking.setStuffingCostPerContainer(ratePerContainer.getValue());

            try {
                shipmentService.createNewShipment(shipment, booking);
                Util.getNotificationForSuccess("Shipment Created Successfully!").open();
            } catch (Exception e) {
                e.printStackTrace();
                Util.getNotificationForError("Unexpected Error: " + e.getMessage()).open();
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        FormLayout formLayout = new FormLayout();
        formLayout.add(name, blNo, invoiceNo, bookingNo, containerType, numOfContainers, containerSize, ratePerContainer,
                stuffingDate, stuffingDepot, commodities, scheduleComboBox, goodsDescription, shipperMarks,
                shipper, consignee, notifyParty, upload);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));
        formLayout.setColspan(goodsDescription,2);
        formLayout.setColspan(shipperMarks,2);

        add(title, bookings, formLayout, saveButton);
    }

    private Upload getUploadComponent() {
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload upload = new Upload(memoryBuffer);
        upload.setDropAllowed(true);
        upload.setAcceptedFileTypes("application/pdf", ".pdf");
        upload.setMaxFiles(1);

        upload.addSucceededListener(event -> {
            //String fileName = event.getFileName();
            InputStream inputStream = memoryBuffer.getInputStream();
            try {
                this.masterBl = inputStream.readAllBytes();
            } catch (IOException e) {
                e.printStackTrace();
                Util.getNotificationForError("Error: " + e.getMessage()).open();
            }
        });

        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            Util.getNotificationForError(errorMessage).open();
        });
        return upload;
    }


}
