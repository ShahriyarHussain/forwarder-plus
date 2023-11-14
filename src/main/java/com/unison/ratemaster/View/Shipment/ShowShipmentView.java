package com.unison.ratemaster.View.Shipment;

import com.unison.ratemaster.Entity.Client;
import com.unison.ratemaster.Entity.Schedule;
import com.unison.ratemaster.Entity.Shipment;
import com.unison.ratemaster.Enum.ContainerSize;
import com.unison.ratemaster.Enum.ContainerType;
import com.unison.ratemaster.Enum.ShipmentStatus;
import com.unison.ratemaster.Service.ClientService;
import com.unison.ratemaster.Service.ScheduleService;
import com.unison.ratemaster.Service.ShipmentService;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;


@Route(value = "show-shipment", layout = MainView.class)
public class ShowShipmentView extends VerticalLayout {

    byte[] masterBl;

    public ShowShipmentView(@Autowired ShipmentService shipmentService,
                            @Autowired ClientService clientService,
                            @Autowired ScheduleService scheduleService) {
        Grid<Shipment> grid = new Grid<>();
        grid.addColumn(Shipment::getBlNo).setHeader("B/L No.").setSortable(false).setFrozen(true).setAutoWidth(true);
        grid.addColumn(shipment -> shipment.getBooking().getBookingNo()).setHeader("Booking").setAutoWidth(true);
        grid.addColumn(Shipment::getInvoiceNo).setHeader("Invoice").setAutoWidth(true);
//        grid.addColumn(Shipment::getName, "name").setHeader("Name");
        grid.addColumn(shipment -> shipment.getShipper().getName(), "name").setHeader("Shipper")
                .setTooltipGenerator(shipment -> shipment.getShipper().getName());
        grid.addColumn(Shipment::getNumberOfContainer).setHeader("Containers").setAutoWidth(true);
        grid.addColumn(shipment -> shipment.getContainerSize().getContainerSize()).setHeader("Size").setAutoWidth(true);
        grid.addColumn(shipment -> shipment.getStatus().name()).setHeader("Status").setAutoWidth(true);
        grid.addColumn(shipment -> shipment.getCreatedOn().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setHeader("Created").setSortable(true);
        grid.addColumn(shipment -> shipment.getLastUpdated().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setHeader("Updated").setSortable(true);
//        grid.addComponentColumn(shipment -> new Button(new Icon(VaadinIcon.PENCIL), event -> {
//            try {
//                createEditDialog(shipment, clientService, shipmentService, scheduleService).open();
//            } catch (Exception e) {
//                e.printStackTrace();
//                Util.getNotificationForError("Unexpected Error: " + e.getMessage()).open();
//            }
//        })).setHeader("Edit");
        grid.addComponentColumn(shipment -> {
            Button downloadButton = new Button(new Icon(VaadinIcon.DOWNLOAD));
            Anchor anchor = new Anchor(new StreamResource(shipment.getBlNo() + ".pdf", (InputStreamFactory) () -> {
                try {
                    return new ByteArrayInputStream(shipmentService.getPdf(shipment.getBlNo()));
                } catch (Exception e) {
                    downloadButton.setTooltipText("BL Not Found");
                    downloadButton.setEnabled(false);
                    Util.getNotificationForError("BL Not Found").open();
                    return null;
                }
            }), "");
            anchor.getElement().setAttribute("download", true);
            anchor.add(downloadButton);
            return anchor;
        }).setHeader("Download B/L");
        grid.setItems(shipmentService.getAllShipments());
        GridContextMenu<Shipment> menu = grid.addContextMenu();
        menu.addItem("Edit", event -> {
            if (event.getItem().isPresent()) {
                createEditDialog(event.getItem().get(), clientService, shipmentService, scheduleService).open();
            }
        });
        menu.addItem("Delete", event -> {
            if (event.getItem().isEmpty()) return;
            shipmentService.deleteShipmentAndBooking(event.getItem().get());
            grid.setItems(shipmentService.getAllShipments());
        });

        VerticalLayout layout = new VerticalLayout(grid);
        layout.setPadding(false);
        add(layout);
    }

    private Component createFilterHeader(String labelText,
                                                Consumer<String> filterChangeConsumer) {
        Label label = new Label(labelText);
        label.getStyle().set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-xs)");
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setWidthFull();
        textField.getStyle().set("max-width", "100%");
        textField.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));
        VerticalLayout layout = new VerticalLayout(label, textField);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }


    private Dialog createEditDialog(Shipment shipment, ClientService clientService,
                                           ShipmentService shipmentService, ScheduleService scheduleService) {

        Dialog dialog = new Dialog();
        boolean newFileUploaded = false;

        FormLayout formLayout = new FormLayout();

        TextField name = new TextField("Shipment Name");
        name.setValue(shipment.getName());
        name.setRequired(true);

        TextField blNo = new TextField("B/L No");
        blNo.setValue(shipment.getBlNo());
        TextArea goodsDescription = new TextArea("Goods Description");
        goodsDescription.setValue(shipment.getGoodsDescription());
        TextArea shipperMarks = new TextArea("Shipper Marks");
        shipperMarks.setValue(shipment.getShipperMarks());

        TextField bookingNo = new TextField("Booking No");
        bookingNo.setValue(shipment.getBooking().getBookingNo());
        TextField invoiceNo = new TextField("Invoice No");
        invoiceNo.setValue(shipment.getInvoiceNo());

        ComboBox<ContainerType> containerType = new ComboBox<>("Container Type:");
        containerType.setItems(ContainerType.values());
        containerType.setItemLabelGenerator(ContainerType::getContainerSize);
        containerType.setValue(shipment.getBooking().getContainerType());

        ComboBox<ContainerSize> containerSize = new ComboBox<>("Container Size");
        containerSize.setItems(ContainerSize.values());
        containerSize.setItemLabelGenerator(ContainerSize::getContainerSize);
        containerSize.setValue(shipment.getContainerSize());

        DatePicker stuffingDate = new DatePicker("Stuffing Date");
        stuffingDate.setValue(shipment.getBooking().getStuffingDate());

        TextField stuffingDepot = new TextField("Stuffing Depot");
        stuffingDepot.setValue(shipment.getBooking().getStuffingDepot());

        IntegerField numOfContainers = new IntegerField("Number of Containers");
        numOfContainers.setValue(shipment.getNumberOfContainer());

        BigDecimalField ratePerContainer = new BigDecimalField("Rate per Container");
        ratePerContainer.setValue(shipment.getBooking().getStuffingCostPerContainer());

        List<Client> clients = clientService.getAllClients();
        ComboBox<Client> shipper = new ComboBox<>("Shipper");
        shipper.setItems(clients);
        shipper.setItemLabelGenerator(Client::getName);
        shipper.setValue(shipment.getShipper());

        ComboBox<Client> consignee = new ComboBox<>("Consignee");
        consignee.setItems(clients);
        consignee.setItemLabelGenerator(Client::getName);
        consignee.setValue(shipment.getConsignee());

        ComboBox<Client> notifyParty = new ComboBox<>("Notify Party");
        notifyParty.setItems(clients);
        notifyParty.setItemLabelGenerator(Client::getName);
        notifyParty.setValue(shipment.getNotifyParty());

        ComboBox<Schedule> scheduleComboBox = new ComboBox<>("Schedule");
        scheduleComboBox.setItems(scheduleService.getValidSchedules());
        scheduleComboBox.setItemLabelGenerator(Schedule::getScheduleSummary);

//        ComboBox<Commodity> commodities = new ComboBox<>("Commodity");
//        commodities.setItems(commodityService.getAllCommodity());
//        commodities.setItemLabelGenerator(Commodity::getCommoditySummary);
//        byte[] masterBl;

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
                e.printStackTrace();
                Util.getNotificationForError("Error: " + e.getMessage()).open();
            }
        });

        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            Util.getNotificationForError(errorMessage).open();
        });

        Button saveButton = new Button("Save", event -> {
            shipment.setName(name.getValue());
            shipment.setBlNo(blNo.getValue());
            shipment.setInvoiceNo(invoiceNo.getValue());
            shipment.setGoodsDescription(goodsDescription.getValue());
            shipment.setShipperMarks(shipperMarks.getValue());
            shipment.setShipper(shipper.getValue());
            shipment.setConsignee(consignee.getValue());
            shipment.setNotifyParty(notifyParty.getValue());
            shipment.setStatus(ShipmentStatus.NEW);
            if (this.masterBl != null) {
                shipment.setMasterBl(masterBl);
            }
           // editedShipment.setCommodity(commodities.getValue());
            shipment.setContainerSize(containerSize.getValue());
            shipment.setCreatedOn(LocalDateTime.now());
            shipment.setLastUpdated(LocalDateTime.now());

            shipment.getBooking().setBookingNo(bookingNo.getValue());
            shipment.getBooking().setContainerType(containerType.getValue());
            shipment.getBooking().setInvoiceNo(invoiceNo.getValue());
            shipment.getBooking().setStuffingDate(stuffingDate.getValue());
            shipment.getBooking().setStuffingDepot(stuffingDepot.getValue());
            shipment.getBooking().setNumOfContainers(numOfContainers.getValue());
            shipment.getBooking().setStuffingCostPerContainer(ratePerContainer.getValue());

            try {
                shipmentService.saveEditedShipment(shipment);
                Util.getNotificationForSuccess("Shipment Saved Successfully!").open();
                dialog.close();
            } catch (Exception e) {
                e.printStackTrace();
                Util.getNotificationForError("Unexpected Error: " + e.getMessage()).open();
            }
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        formLayout.add(name, blNo, invoiceNo, bookingNo, containerType, numOfContainers, containerSize, ratePerContainer,
                stuffingDate, stuffingDepot, scheduleComboBox, goodsDescription, shipperMarks, shipper, consignee, notifyParty, upload);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));
        formLayout.setColspan(goodsDescription,2);
        formLayout.setColspan(shipperMarks,2);

        Button cancelButton = new Button("Close", e -> dialog.close());

        dialog.add(formLayout);
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        return dialog;
    }
}
