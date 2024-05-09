package com.unison.ratemaster.View.Shipment;

import com.unison.ratemaster.Entity.*;
import com.unison.ratemaster.Enum.ContainerSize;
import com.unison.ratemaster.Enum.ContainerType;
import com.unison.ratemaster.Enum.ShipmentStatus;
import com.unison.ratemaster.Service.*;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@PermitAll
@PageTitle("New Shipment")
@Route(value = "create-shipment", layout = MainView.class)
public class CreateShipmentView extends VerticalLayout {

    private byte[] masterBl;
    private final TextField name;
    private final TextField blNo;
    private final TextArea goodsDescription;
    private final TextArea shipperMarks;
    private final TextField bookingNo;
    private final TextField shipperInvoiceNo;
    private final ComboBox<ContainerType> containerType;
    private final ComboBox<ContainerSize> containerSize;
    private final IntegerField numOfContainers;
    private final ComboBox<Client> shipper;
    private final ComboBox<Client> consignee;
    private final ComboBox<Client> notifyParty;
    private final ComboBox<Commodity> commodities;
    private final ComboBox<Carrier> carrierComboBox;
    private final ShipmentService shipmentService;


    public CreateShipmentView(ShipmentService shipmentService, @Autowired ClientService clientService,
                              @Autowired ScheduleService scheduleService, @Autowired CommodityService commodityService,
                              @Autowired InvoiceService invoiceService, @Autowired CarrierService carrierService) {

        this.shipmentService = shipmentService;

        H2 title = new H2("Create Shipment");

        name = new TextField("Shipment Name");
        name.setRequired(true);

        blNo = new TextField("B/L No");

        goodsDescription = new TextArea("Goods Description");
        goodsDescription.setHeight(10, Unit.EM);

        shipperMarks = new TextArea("Shipper Marks");
        shipperMarks.setHeight(10, Unit.EM);

        bookingNo = new TextField("Booking No");

        shipperInvoiceNo = new TextField("Shipper Invoice No");
        shipperInvoiceNo.setRequired(true);

        containerType = new ComboBox<>("Container Type:");
        containerType.setItems(ContainerType.values());
        containerType.setItemLabelGenerator(ContainerType::getContainerSize);
        containerType.setRequired(true);

        containerSize = new ComboBox<>("Container Size");
        containerSize.setItems(ContainerSize.values());
        containerSize.setItemLabelGenerator(ContainerSize::getContainerSize);
        containerSize.setRequired(true);

        numOfContainers = new IntegerField("Number of Containers");
        numOfContainers.setValue(0);

        List<Client> clients = clientService.getAllClients();

        shipper = new ComboBox<>("Shipper");
        shipper.setItems(clients);
        shipper.setItemLabelGenerator(Client::getName);
        shipper.setRequired(true);

        consignee = new ComboBox<>("Consignee");
        consignee.setItems(clients);
        consignee.setItemLabelGenerator(Client::getName);

        notifyParty = new ComboBox<>("Notify Party");
        notifyParty.setItems(clients);
        notifyParty.setItemLabelGenerator(Client::getName);

        ComboBox<Schedule> scheduleComboBox = new ComboBox<>("Schedule");
        scheduleComboBox.setItems(scheduleService.getValidSchedules());
        scheduleComboBox.setItemLabelGenerator(Schedule::getScheduleSummary);

        commodities = new ComboBox<>("Commodity");
        commodities.setItems(commodityService.getAllCommodity());
        commodities.setItemLabelGenerator(Commodity::getCommoditySummary);
        commodities.setRequired(true);

        Upload upload = getUploadComponent();

        carrierComboBox = new ComboBox<>("Carrier");
        carrierComboBox.setItems(carrierService.getAllCarriers());
        carrierComboBox.setItemLabelGenerator(Carrier::getName);
        carrierComboBox.setRequired(true);

        Button saveButton = new Button("Save", event -> {
            if (doesFormContainInvalidData()) {
                Util.getPopUpNotification("Please provide valid data", 3500, NotificationVariant.LUMO_ERROR).open();
                return;
            }
            if (isShipperInvoiceAlreadyExists()) {
                Util.getPopUpNotification("A shipment invoice already exists with the number " +
                        shipperInvoiceNo.getValue() , 3500, NotificationVariant.LUMO_ERROR).open();
                return;
            }
            Shipment shipment = new Shipment();
            shipment.setName(name.getValue());
            shipment.setBlNo(blNo.getValue());
            shipment.setInvoiceNo(shipperInvoiceNo.getValue());
            shipment.setGoodsDescription(goodsDescription.getValue());
            shipment.setShipperMarks(shipperMarks.getValue());
            shipment.setShipper(shipper.getValue());
            shipment.setConsignee(consignee.getValue());
            shipment.setNotifyParty(notifyParty.getValue());
            shipment.setStatus(ShipmentStatus.NEW);
            shipment.setMasterBl(masterBl);
            shipment.setCommodity(commodities.getValue());
            shipment.setMasterBl(masterBl);
            shipment.setCreatedOn(LocalDateTime.now());
            shipment.setLastUpdated(LocalDateTime.now());
            shipment.setCarrier(carrierComboBox.getValue());

            Invoice invoice = new Invoice();
            invoice.setInvoiceNo(invoiceService.getInvoiceNo());
            invoice = invoiceService.saveInvoice(invoice);
            shipment.setInvoice(invoice);

            Booking booking = new Booking();
            booking.setBookingNo(bookingNo.getValue());
            booking.setNumOfContainers(numOfContainers.getValue());
            booking.setContainerType(containerType.getValue());
            booking.setInvoiceNo(shipperInvoiceNo.getValue());
            booking.setStuffingCostPerContainer(BigDecimal.ZERO);
            booking.setNumOfContainers(numOfContainers.getValue());
            booking.setContainerSize(containerSize.getValue());
            booking.setContainer(new HashSet<>());
            booking.setEnteredOn(LocalDateTime.now());

            try {
                shipmentService.createNewShipment(shipment, booking);
                Util.getPopUpNotification("Shipment Created Successfully!", 2500,
                        NotificationVariant.LUMO_SUCCESS).open();
            } catch (Exception e) {
                Util.getPopUpNotification("Unexpected Error: " + e.getMessage(), 3500,
                        NotificationVariant.LUMO_ERROR).open();
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        FormLayout formLayout = new FormLayout();
        formLayout.add(name, blNo, shipperInvoiceNo, bookingNo, containerType, numOfContainers, containerSize,
                commodities, scheduleComboBox, shipper, consignee, notifyParty, goodsDescription, shipperMarks,
                carrierComboBox, upload);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));
        formLayout.setColspan(goodsDescription,2);
        formLayout.setColspan(shipperMarks,2);

        add(title, formLayout, saveButton);
    }

    private boolean isShipperInvoiceAlreadyExists() {
        if (shipper != null && shipper.getValue() != null &&
                shipmentService.isShipmentExistsByShipperAndShipperInvoice(shipperInvoiceNo.getValue(), shipper.getValue())) {
            shipperInvoiceNo.setInvalid(true);
            shipperInvoiceNo.setErrorMessage("Invoice No. for Shipper already exists");
            shipper.setInvalid(true);
            shipper.setErrorMessage(shipperInvoiceNo.getErrorMessage());
            return true;
        }
        return false;
    }

    private boolean doesFormContainInvalidData() {
        if (!StringUtils.isNotBlank(name.getValue())) {
            name.setInvalid(true);
            name.setErrorMessage("Cannot be empty");
            return true;
        }
        if (!StringUtils.isNotBlank(shipperInvoiceNo.getValue())) {
            shipperInvoiceNo.setInvalid(true);
            shipperInvoiceNo.setErrorMessage("Cannot be empty");
            return true;
        }
        if (containerType.getValue() == null) {
            containerType.setInvalid(true);
            containerType.setErrorMessage("Please select Container Type");
            return true;
        }
        if (numOfContainers.getValue() < 1) {
            numOfContainers.setInvalid(true);
            numOfContainers.setErrorMessage("Cannot be less than 1");
            return true;
        }
        if (containerSize.getValue() == null) {
            containerSize.setInvalid(true);
            containerSize.setErrorMessage("Please select Container Size");
            return true;
        }
        if (commodities.getValue() == null) {
            commodities.setInvalid(true);
            commodities.setErrorMessage("Please select a Commodity");
            return true;
        }
        if (shipper.getValue() == null) {
            shipper.setInvalid(true);
            shipper.setErrorMessage("Please select a Shipper");
            return true;
        }
        if (carrierComboBox.getValue() == null) {
            carrierComboBox.setInvalid(true);
            carrierComboBox.setErrorMessage("Please select a Carrier");
            return true;
        }
        return false;
    }

    private Upload getUploadComponent() {
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload upload = new Upload(memoryBuffer);
        upload.setDropAllowed(true);
        upload.setAcceptedFileTypes("application/pdf", ".pdf");
        upload.setMaxFiles(1);

        upload.addSucceededListener(event -> {
            InputStream inputStream = memoryBuffer.getInputStream();
            try {
                this.masterBl = inputStream.readAllBytes();
            } catch (IOException e) {
                Util.getPopUpNotification("Error: " + e.getMessage(), 3500,
                        NotificationVariant.LUMO_ERROR).open();
            }
        });

        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            Util.getPopUpNotification(errorMessage, 3500, NotificationVariant.LUMO_ERROR).open();
        });
        upload.setUploadButton(new Button("Upload MB/L"));
        return upload;
    }
}
