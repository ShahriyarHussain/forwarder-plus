package com.unison.ratemaster.View.Shipment;

import com.unison.ratemaster.Entity.*;
import com.unison.ratemaster.Enum.ContainerType;
import com.unison.ratemaster.Enum.ShipmentStatus;
import com.unison.ratemaster.Service.ClientService;
import com.unison.ratemaster.Service.CommodityService;
import com.unison.ratemaster.Service.RateService;
import com.unison.ratemaster.Service.ShipmentService;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
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
import java.util.List;

@Route(value = "create-shipment", layout = MainView.class)
public class CreateShipmentView extends VerticalLayout {

    byte[] masterBl;

    public CreateShipmentView(@Autowired ShipmentService shipmentService,
                              @Autowired ClientService clientService,
                              @Autowired RateService rateService,
                              @Autowired CommodityService commodityService) {

        H2 title = new H2("Create Shipment");

        TextField name = new TextField("Shipment Name");
        name.setRequired(true);

        TextField blNo = new TextField("B/L No");
        TextArea description = new TextArea("Goods Description");
        TextArea shipperMarks = new TextArea("Goods Description");

        TextField bookingNo = new TextField("Booking No");
        TextField invoiceNo = new TextField("Invoice No");
        ComboBox<ContainerType> containerType = new ComboBox<>("Container Type:");
        containerType.setItems(ContainerType.values());
        containerType.setItemLabelGenerator(ContainerType::getContainerSize);
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

        ComboBox<Rate> rates = new ComboBox<>("Rate");
        rates.setItems(rateService.getValidRates());
        rates.setItemLabelGenerator(rate -> rate.getRateId() + "- " + rate.getPortOfLoading().getPortName()
                + " to " + rate.getPortOfDestination().getPortName());

        ComboBox<Commodity> commodities = new ComboBox<>("Commodity");
        commodities.setItems(commodityService.getAllCommodity());
        commodities.setItemLabelGenerator(Commodity::getCommoditySummary);

        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload upload = new Upload(memoryBuffer);
        upload.setDropAllowed(true);
        upload.setAcceptedFileTypes("application/pdf", ".pdf");
        upload.setMaxFiles(1);

        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
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

        Button saveButton = new Button("Save", event -> {
            Shipment shipment = new Shipment();
            shipment.setName(name.getValue());
            shipment.setBlNo(blNo.getValue());
            shipment.setShipperMarks(shipperMarks.getValue());
            shipment.setShipper(shipper.getValue());
            shipment.setConsignee(consignee.getValue());
            shipment.setNotifyParty(notifyParty.getValue());
            shipment.setStatus(ShipmentStatus.NEW);
            shipment.setRate(rates.getValue());
            shipment.setMasterBl(this.masterBl);

            Booking booking = new Booking();
            booking.setBookingNo(bookingNo.getValue());
            booking.setContainerType(containerType.getValue());
            booking.setInvoiceNo(invoiceNo.getValue());
            booking.setStuffingDate(stuffingDate.getValue());
            booking.setStuffingDepot(stuffingDepot.getValue());
            booking.setNumOfContainers(numOfContainers.getValue());
            booking.setStuffingCostPerContainer(ratePerContainer.getValue());
        });
    }
}
