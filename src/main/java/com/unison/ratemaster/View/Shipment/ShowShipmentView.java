package com.unison.ratemaster.View.Shipment;

import com.unison.ratemaster.Entity.*;
import com.unison.ratemaster.Enum.ContainerSize;
import com.unison.ratemaster.Enum.ContainerType;
import com.unison.ratemaster.Enum.PackageUnit;
import com.unison.ratemaster.Enum.ShipmentStatus;
import com.unison.ratemaster.Service.*;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.*;
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
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;


@Route(value = "show-shipment", layout = MainView.class)
public class ShowShipmentView extends VerticalLayout {

    byte[] masterBl;

    public ShowShipmentView(@Autowired ShipmentService shipmentService,
                            @Autowired ClientService clientService,
                            @Autowired ScheduleService scheduleService,
                            @Autowired PortService portService,
                            @Autowired BookingService bookingService) {
        Grid<Shipment> grid = new Grid<>();
        grid.addColumn(Shipment::getBlNo).setHeader("B/L No.").setSortable(false).setFrozen(true).setAutoWidth(true);
        grid.addColumn(shipment -> shipment.getBooking().getBookingNo()).setHeader("Booking").setAutoWidth(true);
        grid.addColumn(Shipment::getInvoiceNo).setHeader("Invoice").setAutoWidth(true);
        grid.addColumn(shipment -> shipment.getShipper().getName(), "name").setHeader("Shipper")
                .setTooltipGenerator(shipment -> shipment.getShipper().getName());
        grid.addColumn(Shipment::getNumberOfContainer).setHeader("Containers").setAutoWidth(true);
        grid.addColumn(shipment -> shipment.getContainerSize().getContainerSize()).setHeader("Size").setAutoWidth(true);
        grid.addColumn(shipment -> shipment.getStatus().name()).setHeader("Status").setAutoWidth(true);
        grid.addColumn(shipment -> shipment.getCreatedOn().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setHeader("Created").setSortable(true);
        grid.addColumn(shipment -> shipment.getLastUpdated().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setHeader("Updated").setSortable(true);

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

        grid.addComponentColumn(shipment -> {
            Anchor anchor = new Anchor(new StreamResource(shipment.getBooking().getBookingNo() + ".pdf", (InputStreamFactory) () -> {
                try (FileInputStream stream = new FileInputStream("Reports/shipment_advice.jasper")) {
                    final Map<String, Object> parameters = prepareParams(shipment);
                    return new ByteArrayInputStream(JasperRunManager.runReportToPdf(stream, parameters, new JREmptyDataSource(1)));
                } catch (JRException | IOException e) {
                    throw new RuntimeException(e);
                }
            }), "");
            anchor.getElement().setAttribute("download", true);
            anchor.add(new Button(new Icon(VaadinIcon.DOWNLOAD)));
            return anchor;
        }).setHeader("Download Advice");

        grid.setItems(shipmentService.getAllShipments());
        GridContextMenu<Shipment> menu = grid.addContextMenu();
        menu.addItem("Edit Shipment", event -> {
            if (event.getItem().isPresent()) {
                createEditDialog(event.getItem().get(), clientService, shipmentService, scheduleService).open();
            }
        });
        menu.addItem("Edit Schedule", event -> {
            if (event.getItem().isPresent()) {
                createScheduleEditorDialog(event.getItem().get(), scheduleService, portService, shipmentService).open();
            }
        });
        menu.addItem("Edit Booking", event -> {
            if (event.getItem().isPresent()) {
                createBookingEditorDialog(event.getItem().get(), bookingService).open();
            }
        });
        menu.add(new Hr());
        menu.addItem("Delete", event -> {
            if (event.getItem().isEmpty()) return;
            shipmentService.deleteShipmentAndBooking(event.getItem().get());
            grid.setItems(shipmentService.getAllShipments());
        });


        VerticalLayout layout = new VerticalLayout(grid);
        layout.setPadding(false);
        add(layout);
    }

    private Map<String, Object> prepareParams(Shipment shipment) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("BL_NO", shipment.getBlNo());
        paramMap.put("BOOKING_NO", shipment.getBooking().getBookingNo());
        paramMap.put("INVOICE_NO", shipment.getInvoiceNo());
        paramMap.put("STUFFING_DATE", shipment.getBooking().getStuffingDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
        paramMap.put("SHIPPER_NAME", shipment.getShipper().getName());
        paramMap.put("CONSIGNEE", shipment.getConsignee().getName());
        paramMap.put("CONTAINER", shipment.getNumberOfContainer() + "x" + shipment.getContainerSize().getContainerSize());
        paramMap.put("COMMODITY", shipment.getCommodity().getName());
        paramMap.put("QUANTITY", "20 Bales");
        paramMap.put("GROSS_WEIGHT", "100,236 KGS");
        //paramMap.put("SEAL_NO", shipment.getBlNo());
        //paramMap.put("CONTAINERS", shipment.getBlNo());
        return paramMap;
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

        ComboBox<ShipmentStatus> statusComboBox = new ComboBox<>("Status");
        statusComboBox.setItems(ShipmentStatus.values());
        statusComboBox.setItemLabelGenerator(ShipmentStatus::name);
        statusComboBox.setValue(shipment.getStatus());

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
            shipment.setStatus(statusComboBox.getValue());
            if (this.masterBl != null) {
                shipment.setMasterBl(masterBl);
                this.masterBl = null;
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
        formLayout.setColspan(goodsDescription, 2);
        formLayout.setColspan(shipperMarks, 2);

        Button cancelButton = new Button("Close", e -> dialog.close());

        dialog.add(formLayout);
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        return dialog;
    }

    public Dialog createBookingEditorDialog(Shipment shipment, BookingService bookingService) {
        Dialog dialog = new Dialog();

        Booking booking = shipment.getBooking();
        Set<FreightContainer> containerList = booking.getContainer();

        H2 title = new H2("Create Booking");

        TextField bookingNo = new TextField("Booking No");
        bookingNo.setValue(booking.getBookingNo());

        Button addButton = new Button("Add");
        bookingNo.setValueChangeMode(ValueChangeMode.EAGER);
        bookingNo.addValueChangeListener(event -> {
            if (event.getSource().getValue() != null && !event.getSource().getValue().isEmpty()) {
                addButton.setEnabled(true);
            }
        });
        TextField invoiceNo = new TextField("Invoice No");
        invoiceNo.setValue(booking.getInvoiceNo());

        ComboBox<ContainerType> containerType = new ComboBox<>("Container Type");
        containerType.setItems(ContainerType.values());
        if (booking.getContainerType() != null) {
            containerType.setValue(booking.getContainerType());
        }

        IntegerField numOfCont = new IntegerField("Num Of Containers");
        numOfCont.setValue(booking.getNumOfContainers());

        ComboBox<ContainerSize> containerSize = new ComboBox<>("Container Size");
        containerSize.setItems(ContainerSize.values());
        containerSize.setValue(booking.getContainerSize());

        DatePicker stuffingDate = new DatePicker("Stuffing Date");
        stuffingDate.setValue(booking.getStuffingDate());

        TextField stuffingDepot = new TextField("Stuffing Depot");
        stuffingDepot.setValue(booking.getStuffingDepot());

        BigDecimalField stuffingCost = new BigDecimalField("Stuffing Cost/Container");
        stuffingCost.setValue(booking.getStuffingCostPerContainer());

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
        containerGrid.setItems(containerList);

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
            container.setBookingNo(bookingNo.getValue());
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
            booking.setBookingNo(bookingNo.getValue());
            booking.setContainerType(containerType.getValue());
            booking.setInvoiceNo(invoiceNo.getValue());
            booking.setStuffingDate(stuffingDate.getValue());
            booking.setStuffingDepot(stuffingDepot.getValue());
            booking.setNumOfContainers(numOfCont.getValue());
            booking.setStuffingCostPerContainer(stuffingCost.getValue());
            booking.setContainerSize(containerSize.getValue());
            booking.setContainer(containerList);
            booking.setEnteredOn(LocalDateTime.now());
            bookingService.saveBooking(booking);
            Util.getNotificationForSuccess("Booking Saved Successfully").open();
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
        dialog.add(title, formLayout);
        dialog.getFooter().add(saveButton);
        dialog.getFooter().add(new Button("Close", event -> dialog.close()));
        return dialog;
    }

    public Dialog createScheduleEditorDialog(Shipment shipment,ScheduleService scheduleService,
                                             PortService portService, ShipmentService shipmentService) {

        Dialog dialog = new Dialog();
        Schedule schedule;
        List<Port> portList = portService.getPorts();

        H3 pageTitle = new H3("Create Schedule");
        if (shipment.getSchedule() != null) {
            pageTitle = new H3("Edit Schedule");
            schedule = shipment.getSchedule();
        } else {
            schedule = null;
        }
        FormLayout formLayout = new FormLayout();
        formLayout.setMaxWidth("100%");

        TextField feederVesselName = new TextField("Feeder Vessel Name");

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
        motherVesselPort.setRequired(true);
        motherVesselPortEta.setRequired(true);
        motherVesselPort.addValueChangeListener(event -> {
            if (event != null && event.getValue() != null) {
                Port vesselPort = event.getValue();
                motherVesselPortEta.setLabel("ETA " + vesselPort.getPortShortCode());
            }
        });

        ComboBox<Port> tsPort = Util.getPortComboBoxByItemListAndTitle(portList, "Transshipment Port");
        DatePicker tsPortEta = new DatePicker(tsPort.getLabel() + " ETA");
        tsPort.addValueChangeListener(event -> {
            if (event != null && event.getValue() != null) {
                Port vesselPort = event.getValue();
                tsPortEta.setLabel("ETA " + vesselPort.getPortCity());
            }
        });

        ComboBox<Port> destinationPort = Util.getPortComboBoxByItemListAndTitle(portList, "Destination Port");
        DatePicker destinationPortEta = new DatePicker(destinationPort.getLabel() + " ETA");
        destinationPortEta.setRequired(true);
        destinationPort.setRequired(true);
        destinationPort.addValueChangeListener(event -> {
            if (event != null && event.getValue() != null) {
                Port vesselPort = event.getValue();
                destinationPortEta.setLabel("ETA " + vesselPort.getPortCity());
            }
        });

        Button addButton = new Button("Add");

        if (schedule != null) {
            feederVesselName.setValue(schedule.getFeederVesselName());
            portOfLoading.setValue(schedule.getPortOfLoading());
            polEta.setValue(schedule.getLoadingPortEta());
            polEtd.setValue(schedule.getLoadingPortEtd());

            motherVesselName.setValue(schedule.getMotherVesselName());
            motherVesselPort.setValue(schedule.getMotherVesselPort());
            motherVesselPortEta.setValue(schedule.getMotherVesselPortEta());
            tsPort.setValue(schedule.getTsPort());
            tsPortEta.setValue(schedule.getTsPortEta());
            destinationPort.setValue(schedule.getPortOfDestination());
            destinationPortEta.setValue(schedule.getDestinationPortEta());
            addButton.setText("Save");
        }

        addButton.addClickListener(e -> {
            Schedule newSchedule = Objects.requireNonNullElseGet(schedule, Schedule::new);
            newSchedule.setFeederVesselName(feederVesselName.getValue());
            newSchedule.setPortOfLoading(portOfLoading.getValue());
            newSchedule.setLoadingPortEta(polEta.getValue());
            newSchedule.setLoadingPortEtd(polEtd.getValue());

            newSchedule.setMotherVesselName(motherVesselName.getValue());
            newSchedule.setMotherVesselPort(motherVesselPort.getValue());
            newSchedule.setMotherVesselPortEta(motherVesselPortEta.getValue());

            newSchedule.setTsPort(tsPort.getValue());
            newSchedule.setTsPortEta(tsPortEta.getValue());

            newSchedule.setPortOfDestination(destinationPort.getValue());
            newSchedule.setDestinationPortEta(destinationPortEta.getValue());

            Schedule editedSchedule = scheduleService.saveSchedule(newSchedule);

            shipment.setSchedule(editedSchedule);
            shipmentService.saveEditedShipment(shipment);
            if (schedule != null) {
                Util.getNotificationForSuccess("Schedule Saved!").open();
            } else {
                Util.getNotificationForSuccess("Schedule Saved!").open();
            }
        });

        formLayout.add(feederVesselName, portOfLoading, polEta, polEtd, motherVesselName,
                motherVesselPort, motherVesselPortEta, tsPort, tsPortEta, destinationPort, destinationPortEta);
        formLayout.setColspan(motherVesselName, 2);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Close", e -> dialog.close());

        dialog.add(pageTitle, formLayout);
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(addButton);
        dialog.setMaxWidth("50%");
        return dialog;
    }
}
