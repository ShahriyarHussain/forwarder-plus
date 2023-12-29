package com.unison.ratemaster.View.Shipment;

import com.unison.ratemaster.Entity.*;
import com.unison.ratemaster.Enum.*;
import com.unison.ratemaster.Service.*;
import com.unison.ratemaster.Util.ShipmentFilter;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;


@PageTitle("View Shipments")
@Route(value = "show-shipment", layout = MainView.class)
public class ShowShipmentView extends VerticalLayout {

    byte[] masterBl;

    private final String REPORTS_PATH = "Reports/";

    public ShowShipmentView(@Autowired ShipmentService shipmentService,
                            @Autowired ClientService clientService,
                            @Autowired ScheduleService scheduleService,
                            @Autowired PortService portService,
                            @Autowired BookingService bookingService,
                            @Autowired CommodityService commodityService,
                            @Autowired InvoiceService invoiceService,
                            @Autowired CarrierService carrierService,
                            @Autowired BankDetailsService bankDetailsService,
                            @Autowired ContactDetailsService contactDetailsService) {

        H2 title = new H2("View Shipment");

        Grid<Shipment> grid = new Grid<>();
        Grid.Column<Shipment> blColumn = grid.addColumn(Shipment::getBlNo).setSortable(false).setFrozen(true).setAutoWidth(true);
        Grid.Column<Shipment> bookingColumn = grid.addColumn(shipment -> shipment.getBooking().getBookingNo()).setAutoWidth(true);
        Grid.Column<Shipment> shipperInvoiceColumn = grid.addColumn(Shipment::getInvoiceNo).setAutoWidth(true);
        Grid.Column<Shipment> invoiceColumn = grid.addColumn(shipment -> shipment.getInvoice().getInvoiceNo()).setAutoWidth(true);
        Grid.Column<Shipment> shipperColumn = grid.addColumn(shipment ->
                        shipment.getShipper() == null ? "" : shipment.getShipper().getName())
                .setTooltipGenerator(shipment -> shipment.getShipper() == null ? "" : shipment.getShipper().getName());
        Grid.Column<Shipment> numOfContainerColumn = grid.addColumn(shipment -> shipment.getBooking().getNumOfContainers() + "x" +
                shipment.getBooking().getContainerSize().getContainerSize()).setAutoWidth(true);
        Grid.Column<Shipment> statusColumn = grid.addColumn(shipment -> shipment.getStatus().name()).setAutoWidth(true);
        Grid.Column<Shipment> createdColumn = grid.addColumn(shipment -> shipment.getCreatedOn()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setSortable(true).setAutoWidth(true);
        Grid.Column<Shipment> lastUpdateColumn = grid.addColumn(shipment -> shipment.getLastUpdated()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setSortable(true).setAutoWidth(true);

        Grid.Column<Shipment> mblColumn = grid.addComponentColumn(shipment -> {
            Button downloadButton = new Button(new Icon(VaadinIcon.DOWNLOAD));
            Anchor anchor = new Anchor(new StreamResource(shipment.getBlNo() + ".pdf", (InputStreamFactory) () -> {
                try {
                    return new ByteArrayInputStream(shipmentService.getPdf(shipment.getBlNo()));
                } catch (Exception e) {
                    downloadButton.setTooltipText("BL Not Found");
                    downloadButton.setEnabled(false);
                    return null;
                }
            }), "");
            anchor.getElement().setAttribute("download", true);
            anchor.add(downloadButton);
            return anchor;
        }).setTooltipGenerator(shipment -> shipment.getMasterBl() == null ? "B/L Not Found" : "").setAutoWidth(true);

        Grid.Column<Shipment>  shipmentAdvColumn = grid.addComponentColumn(shipment -> {
            Anchor anchor = new Anchor(new StreamResource(shipment.getBooking().getBookingNo() + ".pdf", (InputStreamFactory) () -> {
                final Map<String, Object> parameters = prepareParamsForShipmentAdvice(shipment);
                String report = "shipment_advice.jasper";
                if (parameters.get("TS2_PORT") != null) {
                    report = "shipment_advice_ts.jasper";
                }
                try (FileInputStream stream = new FileInputStream(REPORTS_PATH + report)) {
                    return new ByteArrayInputStream(JasperRunManager.runReportToPdf(stream, parameters, new JREmptyDataSource(1)));
                } catch (JRException | IOException e) {
                    throw new RuntimeException(e);
                }
            }), "");
            anchor.getElement().setAttribute("download", true);
            anchor.add(new Button(new Icon(VaadinIcon.DOWNLOAD)));
            return anchor;
        }).setAutoWidth(true);

        GridListDataView<Shipment> dataView = grid.setItems(shipmentService.getAllShipments());
        ShipmentFilter shipmentFilter = new ShipmentFilter(dataView);

        grid.getHeaderRows().clear();
        HeaderRow headerRow = grid.appendHeaderRow();

        headerRow.getCell(blColumn).setComponent(
                createFilterHeader("BL No", shipmentFilter::setBlNo));
        headerRow.getCell(bookingColumn).setComponent(
                createFilterHeader("Booking No", shipmentFilter::setBookingNo));
        headerRow.getCell(shipperInvoiceColumn).setComponent(
                createFilterHeader("Shipper Invoice No", shipmentFilter::setShipperInvoiceNo));
        headerRow.getCell(invoiceColumn).setComponent(
                createFilterHeader("Invoice No", shipmentFilter::setInvoiceNo));
        headerRow.getCell(shipperColumn).setComponent(
                createNonFilterHeader("Shipper"));
        headerRow.getCell(numOfContainerColumn).setComponent(
                createNonFilterHeader("Booking No"));
        headerRow.getCell(statusColumn).setComponent(
                createNonFilterHeader("Shipper Invoice No"));
        headerRow.getCell(createdColumn).setComponent(
                createNonFilterHeader("Created"));
        headerRow.getCell(lastUpdateColumn).setComponent(
                createNonFilterHeader("Updated"));
        headerRow.getCell(mblColumn).setComponent(
                createNonFilterHeader("MB/L"));
        headerRow.getCell(shipmentAdvColumn).setComponent(
                createNonFilterHeader("Ship. Adv."));


        GridContextMenu<Shipment> menu = grid.addContextMenu();
        menu.addItem("Edit Shipment", event -> {
            if (event.getItem().isPresent()) {
                createEditDialog(event.getItem().get(), clientService, shipmentService, scheduleService,
                        commodityService, carrierService).open();
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
        menu.addItem("Create Invoice", event -> {
            if (event.getItem().isPresent()) {
                createInvoiceMakerDialog(event.getItem().get(), invoiceService, bankDetailsService, contactDetailsService).open();
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
        add(title, layout);
    }

    private Map<String, Object> prepareParamsForShipmentAdvice(Shipment shipment) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ADVICE_DATE", getFormattedDate(LocalDate.now()));
        paramMap.put("BL_NO", shipment.getBlNo());
        paramMap.put("BOOKING_NO", shipment.getBooking().getBookingNo());
        paramMap.put("INVOICE_NO", shipment.getInvoiceNo());
        paramMap.put("STUFFING_DATE", shipment.getBooking().getStuffingDate().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
        paramMap.put("SHIPPER_NAME", shipment.getShipper().getName());
        paramMap.put("CONSIGNEE", shipment.getNotifyParty().getName());
        paramMap.put("CONTAINER", shipment.getBooking().getNumOfContainers() + "x"
                + shipment.getBooking().getContainerSize().getContainerSize());
        paramMap.put("COMMODITY", shipment.getCommodity().getName());
        paramMap.put("QUANTITY", calculateQuantity(shipment.getBooking()));
        paramMap.put("GROSS_WEIGHT", calculateWeight(shipment.getBooking()));
        paramMap.put("LOGO_URL", REPORTS_PATH + Util.imagePath);
        paramMap.put("PORT_OF_LOADING", shipment.getSchedule().getPortOfLoading().getPortShortCode());
        paramMap.put("MV_CONNECT_PORT", shipment.getSchedule().getMotherVesselPort().getPortShortCode());
        paramMap.put("TS_PORT", shipment.getSchedule().getTsPort().getPortName());
        paramMap.put("DEST_PORT", shipment.getSchedule().getPortOfDestination().getPortName());
        paramMap.put("POL_ETA", getFormattedDate(shipment.getSchedule().getLoadingPortEta()));
        paramMap.put("POL_ETD", getFormattedDate(shipment.getSchedule().getLoadingPortEtd()));
        paramMap.put("MV_PORT_FEEDER_ETA", getFormattedDate(shipment.getSchedule().getMvPortFeederEta()));
        paramMap.put("MV_ETA", getFormattedDate(shipment.getSchedule().getMotherVesselPortEta()));
        paramMap.put("TS_ETA", getFormattedDate(shipment.getSchedule().getTsPortEta()));
        paramMap.put("DEST_ETA", getFormattedDate(shipment.getSchedule().getDestinationPortEta()));
        paramMap.put("FEEDER", shipment.getSchedule().getFeederVesselName());
        paramMap.put("MOTHER", shipment.getSchedule().getMotherVesselName());

        //TS2 Addition
        if (shipment.getSchedule().getTs2Port() != null) {
            paramMap.put("TS2_PORT", shipment.getSchedule().getTs2Port().getPortName());
            paramMap.put("TS2_ETA", getFormattedDate(shipment.getSchedule().getTs2PortEta()));
            paramMap.put("TS_MOTHER", shipment.getSchedule().getTsVesselName());
        }

        paramMap.put("SEAL_NO", getSealNo(shipment.getBooking()));
        paramMap.put("CONTAINERS", getContainers(shipment.getBooking()));

        return paramMap;
    }

    private Object calculateWeight(Booking booking) {
        if (booking.getContainer() == null || booking.getContainer().isEmpty()) {
            return "";
        }
        BigDecimal grossWeight = new BigDecimal("0");
        for (FreightContainer container : booking.getContainer()) {
            grossWeight = grossWeight.add(container.getGrossWeight());
        }
        return grossWeight + " KGS";
    }

    private String calculateQuantity(Booking booking) {
        if (booking.getContainer() == null || booking.getContainer().isEmpty()) {
            return "";
        }
        int quantity = 0;
        String unit = "";
        for (FreightContainer container : booking.getContainer()) {
            quantity += container.getNoOfPackages();
            unit = container.getPackageUnit().toString();
        }
        return quantity + " " + unit;
    }

    private String getContainers(Booking booking) {
        if (booking.getContainer() == null || booking.getContainer().isEmpty()) {
            return "";
        }
        StringBuilder containers = new StringBuilder();
        for (FreightContainer container : booking.getContainer()) {
            containers.append(container.getContainerNo()).append(", ");
        }
        containers.replace(containers.length() - 2, containers.length() - 1, "");
        return containers.toString();
    }

    private String getSealNo(Booking booking) {
        if (booking.getContainer() == null || booking.getContainer().isEmpty()) {
            return "";
        }
        StringBuilder sealNo = new StringBuilder();
        for (FreightContainer container : booking.getContainer()) {
            sealNo.append(container.getSealNo()).append(", ");
        }
        sealNo.replace(sealNo.length() - 2, sealNo.length() - 1, "");
        return sealNo.toString();
    }

    private String getFormattedDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    private Component createFilterHeader(String labelText,
                                         Consumer<String> filterChangeConsumer) {
        Label label = new Label(labelText);
        label.getStyle().set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-s)");
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

    private Component createNonFilterHeader(String labelText) {
        Label label = new Label(labelText);
        label.getStyle().set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-m)");
        VerticalLayout layout = new VerticalLayout(label);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");
        return layout;
    }


    private Dialog createEditDialog(Shipment shipment, ClientService clientService,
                                    ShipmentService shipmentService, ScheduleService scheduleService,
                                    CommodityService commodityService, CarrierService carrierService) {

        Dialog dialog = new Dialog();

        FormLayout formLayout = new FormLayout();

        TextField name = new TextField("Shipment Name");
        name.setValue(shipment.getName());
        name.setRequired(true);

        TextField blNo = new TextField("B/L No");
        blNo.setValue(shipment.getBlNo());
        TextArea goodsDescription = new TextArea("Goods Description");
        goodsDescription.setValue(shipment.getGoodsDescription());
        goodsDescription.setHeight(10, Unit.EM);
        TextArea shipperMarks = new TextArea("Shipper Marks");
        shipperMarks.setValue(shipment.getShipperMarks());
        shipperMarks.setHeight(10, Unit.EM);

        TextField bookingNo = new TextField("Booking No");
        bookingNo.setValue(shipment.getBooking().getBookingNo());
        TextField invoiceNo = new TextField("Shipper Invoice No");
        invoiceNo.setValue(shipment.getInvoiceNo());

        ComboBox<ContainerType> containerType = new ComboBox<>("Container Type:");
        containerType.setItems(ContainerType.values());
        containerType.setItemLabelGenerator(ContainerType::getContainerSize);
        containerType.setValue(shipment.getBooking().getContainerType());

        ComboBox<ContainerSize> containerSize = new ComboBox<>("Container Size");
        containerSize.setItems(ContainerSize.values());
        containerSize.setItemLabelGenerator(ContainerSize::getContainerSize);
        containerSize.setValue(shipment.getBooking().getContainerSize());

        IntegerField numOfContainers = new IntegerField("Number of Containers");
        numOfContainers.setValue(shipment.getBooking().getNumOfContainers());

        ComboBox<Commodity> commodities = new ComboBox<>("Commodity");
        commodities.setItems(commodityService.getAllCommodity());
        commodities.setItemLabelGenerator(Commodity::getCommoditySummary);
        commodities.setValue(shipment.getCommodity());

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
        scheduleComboBox.setValue(shipment.getSchedule());

        ComboBox<ShipmentStatus> statusComboBox = new ComboBox<>("Status");
        statusComboBox.setItems(ShipmentStatus.values());
        statusComboBox.setItemLabelGenerator(ShipmentStatus::name);
        statusComboBox.setValue(shipment.getStatus());

        ComboBox<Carrier> carrierComboBox = new ComboBox<>("Carrier");
        carrierComboBox.setItems(carrierService.getAllCarriers());
        carrierComboBox.setItemLabelGenerator(Carrier::getName);

        Upload upload = getUpload();

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
            shipment.setCommodity(commodities.getValue());
            if (masterBl != null) {
                shipment.setMasterBl(masterBl);
                this.masterBl = null;
            }
            shipment.setCreatedOn(LocalDateTime.now());
            shipment.setLastUpdated(LocalDateTime.now());
            shipment.setSchedule(scheduleComboBox.getValue());

            shipment.getBooking().setBookingNo(bookingNo.getValue());
            shipment.getBooking().setContainerType(containerType.getValue());
            shipment.getBooking().setContainerSize(containerSize.getValue());
            shipment.getBooking().setInvoiceNo(invoiceNo.getValue());
            shipment.getBooking().setNumOfContainers(numOfContainers.getValue());

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
        formLayout.add(name, blNo, invoiceNo, bookingNo, containerType, numOfContainers, containerSize,
                commodities, scheduleComboBox, shipper, consignee, notifyParty, carrierComboBox, upload,
                goodsDescription, shipperMarks);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));

        formLayout.setColspan(goodsDescription, 2);
        formLayout.setColspan(shipperMarks, 2);

        Button cancelButton = new Button("Close", e -> dialog.close());

        dialog.add(formLayout);
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        return dialog;
    }

    private Upload getUpload() {
        MemoryBuffer memoryBuffer = new MemoryBuffer();
        Upload upload = new Upload(memoryBuffer);
        upload.setDropAllowed(true);
        upload.setAcceptedFileTypes("application/pdf", ".pdf");
        upload.setMaxFiles(1);


        upload.addSucceededListener(event -> {
            InputStream inputStream = memoryBuffer.getInputStream();
            try {
                masterBl = inputStream.readAllBytes();

            } catch (IOException e) {
                e.printStackTrace();
                Util.getNotificationForError("Error: " + e.getMessage()).open();
            }
        });

        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            Util.getNotificationForError(errorMessage).open();
        });
        upload.setUploadButton(new Button("Upload MB/L"));
        return upload;
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
        containerSize.setItemLabelGenerator(ContainerSize::getContainerSize);

        DatePicker stuffingDate = new DatePicker("Stuffing Date");
        stuffingDate.setValue(booking.getStuffingDate());

        TextField stuffingDepot = new TextField("Stuffing Depot");
        stuffingDepot.setValue(booking.getStuffingDepot() == null ? "" : booking.getStuffingDepot());

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
            container.setBooking(booking);
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
            containerList.forEach(container -> container.setBookingId(booking.getBookingId()));
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

    public Dialog createScheduleEditorDialog(Shipment shipment, ScheduleService scheduleService,
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
        DatePicker mvPortEta = new DatePicker("Feeder Connect ETA");
        motherVesselPort.setRequired(true);
        motherVesselPortEta.setRequired(true);
        motherVesselPort.addValueChangeListener(event -> {
            if (event != null && event.getValue() != null) {
                Port vesselPort = event.getValue();
                motherVesselPortEta.setLabel("Mother ETA " + vesselPort.getPortShortCode());
                mvPortEta.setLabel("Feeder ETA " + vesselPort.getPortShortCode());
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

        TextField ts2VesselName = new TextField("TS2 Vessel Name");
        ComboBox<Port> ts2Port = Util.getPortComboBoxByItemListAndTitle(portList, "Transshipment 2 Port");
        DatePicker ts2PortEta = new DatePicker(ts2Port.getLabel() + " ETA");
        ts2Port.addValueChangeListener(event -> {
            ts2VesselName.setRequired(false);
            if (event != null && event.getValue() != null) {
                Port vesselPort = event.getValue();
                ts2VesselName.setRequired(true);
                ts2PortEta.setLabel("ETA " + vesselPort.getPortCity());
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
            mvPortEta.setValue(schedule.getMvPortFeederEta());
            motherVesselPortEta.setValue(schedule.getMotherVesselPortEta());
            tsPort.setValue(schedule.getTsPort());
            tsPortEta.setValue(schedule.getTsPortEta());
            destinationPort.setValue(schedule.getPortOfDestination());
            destinationPortEta.setValue(schedule.getDestinationPortEta());
            if (schedule.getTs2Port() != null) {
                ts2VesselName.setValue(schedule.getTsVesselName());
                ts2PortEta.setValue(schedule.getTs2PortEta());
                ts2Port.setValue(schedule.getTs2Port());
            }
            addButton.setText("Save");
        }

        addButton.addClickListener(e -> {
            Schedule newSchedule = Objects.requireNonNullElseGet(schedule, Schedule::new);
            newSchedule.setFeederVesselName(feederVesselName.getValue());
            newSchedule.setPortOfLoading(portOfLoading.getValue());
            newSchedule.setLoadingPortEta(polEta.getValue());
            newSchedule.setLoadingPortEtd(polEtd.getValue());
            newSchedule.setMvPortFeederEta(mvPortEta.getValue());

            newSchedule.setMotherVesselName(motherVesselName.getValue());
            newSchedule.setMotherVesselPort(motherVesselPort.getValue());
            newSchedule.setMotherVesselPortEta(motherVesselPortEta.getValue());

            newSchedule.setTsPort(tsPort.getValue());
            newSchedule.setTsPortEta(tsPortEta.getValue());

            newSchedule.setPortOfDestination(destinationPort.getValue());
            newSchedule.setDestinationPortEta(destinationPortEta.getValue());

            newSchedule.setTsVesselName(ts2VesselName.getValue());
            newSchedule.setTs2Port(ts2Port.getValue());
            newSchedule.setTs2PortEta(ts2PortEta.getValue());

            Schedule editedSchedule = scheduleService.saveSchedule(newSchedule);

            shipment.setSchedule(editedSchedule);
            shipmentService.saveEditedShipment(shipment);
            if (schedule != null) {
                Util.getNotificationForSuccess("Schedule Saved!").open();
            } else {
                Util.getNotificationForSuccess("Schedule Saved!").open();
            }
        });

        formLayout.add(feederVesselName, motherVesselName, portOfLoading, motherVesselPort, polEta, motherVesselPortEta,
                polEtd, mvPortEta, tsPort, tsPortEta, ts2VesselName, new Hr(), ts2Port, ts2PortEta, destinationPort, destinationPortEta);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Close", e -> dialog.close());

        dialog.add(pageTitle, formLayout);
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(addButton);
        dialog.setMaxWidth("50%");
        return dialog;
    }

    public Dialog createInvoiceMakerDialog(Shipment shipment, InvoiceService invoiceService,
                                           BankDetailsService bankDetailsService,
                                           ContactDetailsService contactDetailsService) {

        Invoice invoice = shipment.getInvoice();

        Dialog dialog = new Dialog();
        HorizontalLayout horizontalLayout = new HorizontalLayout();

        H3 title = new H3("Prepare Invoice");

        IntegerField numOfContainer = new IntegerField("Containers");
        numOfContainer.setValue(shipment.getBooking().getNumOfContainers());

        TextField invoiceNo = new TextField("Invoice No");
        invoiceNo.setValue(invoice.getInvoiceNo());

        TextField expNo = new TextField("Exp No");
        expNo.setValue(Objects.requireNonNullElse(invoice.getExpNo(), ""));

        DatePicker expDate = new DatePicker("Exp Date");
        expDate.setValue(invoice.getExpDate());

        Text inWords = new Text("Zero");
        inWords.setText(Util.getAmountInWords(invoice.getSubTotal()));

        BigDecimalField total = new BigDecimalField("Total", BigDecimal.ZERO, "");
        total.setValue(invoice.getSubTotal());
        total.setReadOnly(true);

        BigDecimalField freightInBDT = new BigDecimalField("Freight in BDT", BigDecimal.ZERO, "");
        freightInBDT.setValue(invoice.getFreightTotalInLocalCurr());
        freightInBDT.setReadOnly(true);

        BigDecimalField totalFreight = new BigDecimalField("Total Freight", BigDecimal.ZERO, "");
        totalFreight.setValue(invoice.getTotalFreight());
        totalFreight.setReadOnly(true);

        BigDecimalField ratePerContField = new BigDecimalField("Rate Per Container", BigDecimal.ZERO, "");
        ratePerContField.setValue(invoice.getRatePerContainer());
        ratePerContField.setValueChangeMode(ValueChangeMode.EAGER);

        BigDecimalField conversionRate = new BigDecimalField("Conversion Rate", BigDecimal.ZERO, "");
        conversionRate.setValue(invoice.getConversionRate());

        ComboBox<AmountCurrency> currComboBox = new ComboBox<>("Currency");
        currComboBox.setItems(AmountCurrency.values());
        currComboBox.setItemLabelGenerator(curr -> curr.toString() + " - " + curr.getCurrencyName());
        currComboBox.setValue(invoice.getCurrency());

        TextField goodDescription = new TextField("Goods Description");
        goodDescription.setValue(Objects.requireNonNullElse(invoice.getGoodsDescription(), ""));

        TextField otherField1 = new TextField("Other Cost Name 1:");
        otherField1.setValue(Objects.requireNonNullElse(invoice.getOtherDesc1(), ""));

        BigDecimalField otherCost1Amt = new BigDecimalField("Other Cost 1 Amount:", BigDecimal.ZERO, "Cannot be Empty");
        otherCost1Amt.setValueChangeMode(ValueChangeMode.EAGER);
        otherCost1Amt.setValue(invoice.getOther1Amt());

        TextField otherField2 = new TextField("Other Cost Name 2:");
        otherField2.setValue(Objects.requireNonNullElse(invoice.getOtherDesc2(), ""));

        BigDecimalField otherCost2Amt = new BigDecimalField("Other Cost 2 Amount:", BigDecimal.ZERO, "Cannot be Empty");
        otherCost2Amt.setValueChangeMode(ValueChangeMode.EAGER);
        otherCost2Amt.setValue(invoice.getOther2Amt());

        TextField otherField3 = new TextField("Other Cost Name 3:");
        otherField3.setValue(Objects.requireNonNullElse(invoice.getOtherDesc3(), ""));

        BigDecimalField otherCost3Amt = new BigDecimalField("Other Cost 3 Amount:", BigDecimal.ZERO, "Cannot be Empty");
        otherCost3Amt.setValueChangeMode(ValueChangeMode.EAGER);
        otherCost3Amt.setValue(invoice.getOther3Amt());

        TextField otherField4 = new TextField("Other Cost Name 4:");
        otherField4.setValue(Objects.requireNonNullElse(invoice.getOtherDesc4(), ""));

        BigDecimalField otherCost4Amt = new BigDecimalField("Other Cost 4 Amount:", BigDecimal.ZERO, "Cannot be Empty");
        otherCost4Amt.setValueChangeMode(ValueChangeMode.EAGER);
        otherCost4Amt.setValue(invoice.getOther4Amt());

        ComboBox<BankDetails> bankDetailsComboBox = new ComboBox<>("Choose Bank Details");
        bankDetailsComboBox.setItems(bankDetailsService.getAllBankDetails());
        bankDetailsComboBox.setItemLabelGenerator(bankDetails -> bankDetails.getAccName() + " - " + bankDetails.getBankName()
                + " - " + bankDetails.getBranchName() + " - " + bankDetails.getAccNo());
        bankDetailsComboBox.setValue(invoice.getBankDetails());

        ComboBox<ContactDetails> contactDetailsComboBox = new ComboBox<>("Choose Contact Details");
        contactDetailsComboBox.setItems(contactDetailsService.getAllContactDetails());
        contactDetailsComboBox.setItemLabelGenerator(ContactDetails::getName);
        contactDetailsComboBox.setValue(invoice.getContactDetails());

        otherCost1Amt.addValueChangeListener(e -> {
            if (otherCost1Amt.getValue() == null) {
                otherCost1Amt.setValue(BigDecimal.ZERO);
            }
                    total.setValue(freightInBDT.getValue().add(otherCost1Amt.getValue())
                            .add(otherCost2Amt.getValue()).add(otherCost3Amt.getValue()).add(otherCost4Amt.getValue()));
                    inWords.setText(Util.getAmountInWords(total.getValue()));
                }
        );
        otherCost2Amt.addValueChangeListener(e -> {
            if (otherCost2Amt.getValue() == null) {
                otherCost2Amt.setValue(BigDecimal.ZERO);
            }
            total.setValue(freightInBDT.getValue().add(otherCost1Amt.getValue())
                    .add(otherCost2Amt.getValue()).add(otherCost3Amt.getValue()).add(otherCost4Amt.getValue()));
                    inWords.setText(Util.getAmountInWords(total.getValue()));
                }
        );
        otherCost3Amt.addValueChangeListener(e -> {
            if (otherCost3Amt.getValue() == null) {
                otherCost3Amt.setValue(BigDecimal.ZERO);
            }
            total.setValue(freightInBDT.getValue().add(otherCost1Amt.getValue())
                    .add(otherCost2Amt.getValue()).add(otherCost3Amt.getValue()).add(otherCost4Amt.getValue()));
                    inWords.setText(Util.getAmountInWords(total.getValue()));
                }
        );
        otherCost4Amt.addValueChangeListener(e -> {
            if (otherCost4Amt.getValue() == null) {
                otherCost4Amt.setValue(BigDecimal.ZERO);
            }
            total.setValue(freightInBDT.getValue().add(otherCost1Amt.getValue())
                    .add(otherCost2Amt.getValue()).add(otherCost3Amt.getValue()).add(otherCost4Amt.getValue()));
                    inWords.setText(Util.getAmountInWords(total.getValue()));
                }
        );
        ratePerContField.addValueChangeListener(e -> {
                    totalFreight.setValue(e.getValue()
                            .multiply(BigDecimal.valueOf(numOfContainer.getValue())));
                    freightInBDT.setValue(totalFreight.getValue().multiply(conversionRate.getValue()));
                    total.setValue(total.getValue().add(freightInBDT.getValue()).add(otherCost1Amt.getValue())
                            .add(otherCost2Amt.getValue()).add(otherCost1Amt.getValue()).add(otherCost2Amt.getValue()));
                    inWords.setText(Util.getAmountInWords(total.getValue()));
                }
        );

        Button saveInvoiceButton = new Button("Save Invoice", new Icon(VaadinIcon.DATABASE));
        saveInvoiceButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveInvoiceButton.addClickListener(event -> {
            invoice.setFreightTotalInLocalCurr(freightInBDT.getValue());
            invoice.setSubTotal(total.getValue());
            invoice.setExpNo(expNo.getValue());
            invoice.setExpDate(expDate.getValue());
            invoice.setRatePerContainer(ratePerContField.getValue());
            invoice.setTotalFreight(totalFreight.getValue());
            invoice.setConversionRate(conversionRate.getValue());
            invoice.setCurrency(currComboBox.getValue());
            invoice.setGoodsDescription(goodDescription.getValue());

            invoice.setOtherDesc1(otherField1.getValue());
            invoice.setOther1Amt(otherCost1Amt.getValue());
            invoice.setOtherDesc2(otherField2.getValue());
            invoice.setOther2Amt(otherCost2Amt.getValue());
            invoice.setOtherDesc3(otherField3.getValue());
            invoice.setOther3Amt(otherCost3Amt.getValue());
            invoice.setOtherDesc4(otherField4.getValue());
            invoice.setOther4Amt(otherCost4Amt.getValue());

            invoice.setBankDetails(bankDetailsComboBox.getValue());
            invoice.setContactDetails(contactDetailsComboBox.getValue());
            invoiceService.saveInvoice(invoice);
            Util.getNotificationForSuccess("Saved Successfully").open();
        });

        Button prepareInvoiceButton = new Button("Download Invoice", new Icon(VaadinIcon.DOWNLOAD));
        prepareInvoiceButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Anchor anchor = new Anchor(new StreamResource("Invoice" + shipment.getBlNo() + ".pdf", (InputStreamFactory) () -> {
            final Map<String, Object> parameters = new HashMap<>();

            String reportName = "Invoice.jasper";

            parameters.put("BL_NO", shipment.getBlNo());
            parameters.put("ADDRESS", shipment.getShipper().getAddress());
            parameters.put("INVOICE_NO", shipment.getInvoice().getInvoiceNo());
            parameters.put("SHIPPER_NAME", shipment.getShipper().getName());
            parameters.put("CONTAINER", numOfContainer.getValue() + "x" + shipment.getBooking().getContainerSize().getContainerSize());
            parameters.put("COMMODITY", shipment.getCommodity().getName());
            parameters.put("CONTAINERS", getContainers(shipment.getBooking()));
            parameters.put("INVOICE_DATE", getFormattedDate(LocalDate.now()));
            parameters.put("PORT_OF_LOADING", shipment.getSchedule().getPortOfLoading().getPortName() + ", "
                    + shipment.getSchedule().getPortOfLoading().getPortCountry());
            parameters.put("DEST_PORT", shipment.getSchedule().getPortOfDestination().getPortName() + ", "
                    + shipment.getSchedule().getPortOfDestination().getPortCountry());
            parameters.put("POL_ETD", getFormattedDate(shipment.getSchedule().getLoadingPortEta()));
            parameters.put("DEST_ETA", getFormattedDate(shipment.getSchedule().getDestinationPortEta()));
            parameters.put("SHIPPER_EMAIL", shipment.getShipper().getEmail());
            parameters.put("LOGO_URL", REPORTS_PATH + Util.imagePath);
            parameters.put("SHIPPER_INV_NO", shipment.getInvoiceNo());
            parameters.put("FREIGHT_CURRENCY", currComboBox.getValue().toString());
            parameters.put("CONVERSION_RATE", conversionRate.getValue().setScale(2, RoundingMode.UNNECESSARY).toPlainString());
            parameters.put("FREIGHT_RATE", ratePerContField.getValue().setScale(2, RoundingMode.UNNECESSARY).toPlainString());
            parameters.put("TOTAL_FREIGHT", totalFreight.getValue().setScale(2, RoundingMode.UNNECESSARY).toPlainString());
            parameters.put("FREIGHT_LOCAL", freightInBDT.getValue().setScale(2, RoundingMode.UNNECESSARY).toPlainString());
            parameters.put("GOODS_DESCRIPTION", goodDescription.getValue());
            parameters.put("DESC_2", otherField1.getValue());
            parameters.put("DESC_2_AMT", otherCost1Amt.getValue().setScale(2, RoundingMode.UNNECESSARY).toPlainString());
            parameters.put("DESC_3", otherField2.getValue());
            parameters.put("DESC_3_AMT", otherCost2Amt.getValue().setScale(2, RoundingMode.UNNECESSARY).toPlainString());

            if (otherField3.getValue() != null && !otherField3.getValue().isEmpty()) {
                reportName = "Invoice_row_3.jasper";
                parameters.put("DESC_4", otherField3.getValue());
                parameters.put("DESC_4_AMT", otherCost3Amt.getValue().setScale(2, RoundingMode.UNNECESSARY).toPlainString());
            }
            if (otherField4.getValue() != null && !otherField4.getValue().isEmpty()) {
                reportName = "Invoice_row_4.jasper";
                parameters.put("DESC_5", otherField4.getValue());
                parameters.put("DESC_5_AMT", otherCost4Amt.getValue().setScale(2, RoundingMode.UNNECESSARY).toPlainString());
            }

            parameters.put("TOTAL", total.getValue().setScale(2, RoundingMode.UNNECESSARY).toPlainString());
            parameters.put("TOTAL_IN_WORD", inWords.getText());
            parameters.put("BANK_NAME", bankDetailsComboBox.getValue().getBankName());
            parameters.put("AC_NAME", bankDetailsComboBox.getValue().getAccName());
            parameters.put("AC_NO", bankDetailsComboBox.getValue().getAccNo());
            parameters.put("ROUTING_NO", bankDetailsComboBox.getValue().getRoutingNo());
            parameters.put("BRANCH", bankDetailsComboBox.getValue().getBranchName());
            parameters.put("SIGNED_BY", contactDetailsComboBox.getValue().getName());
            parameters.put("SIGNED_BY_EMAIL", contactDetailsComboBox.getValue().getEmail());
            parameters.put("SIGNED_BY_CONTACT", contactDetailsComboBox.getValue().getContactNo());
            parameters.put("EXP_NO", expNo.getValue());
            parameters.put("EXP_DATE", getFormattedDate(expDate.getValue()));
            parameters.put("VESSEL", shipment.getSchedule().getFeederVesselName());


            try (FileInputStream stream = new FileInputStream(REPORTS_PATH + reportName)) {
                return new ByteArrayInputStream(JasperRunManager.runReportToPdf(stream, parameters, new JREmptyDataSource(1)));
            } catch (JRException | IOException e) {
                throw new RuntimeException(e);
            }
        }), "");
        anchor.getElement().setAttribute("download", true);
        anchor.add(prepareInvoiceButton);


        Hr line1 = new Hr();
        Hr line2 = new Hr();
        Hr gap0 = new Hr();
        gap0.setMaxHeight("0");
        Hr gap1 = new Hr();
        gap1.setMaxHeight("0");
        Hr gap2 = new Hr();
        gap2.setMaxHeight("0");
        Hr gap3 = new Hr();
        gap3.setMaxHeight("0");
        Hr gap4 = new Hr();
        gap4.setMaxHeight("0");
        Hr line3 = new Hr();
        Hr gap5 = new Hr();
        gap4.setMaxHeight("0");

        horizontalLayout.add(inWords);
        FormLayout invoiceForm = new FormLayout();
        invoiceForm.add(currComboBox, conversionRate, expNo, expDate, gap0, line1, goodDescription, numOfContainer,
                ratePerContField, totalFreight, freightInBDT, otherField1, gap1, otherCost1Amt, otherField2, gap2,
                otherCost2Amt, otherField3, gap3, otherCost3Amt, otherField4, gap4, otherCost4Amt, line3,
                horizontalLayout, gap5, total);

        invoiceForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 5));
        //invoiceForm.setColspan(gap0, 1);
        invoiceForm.setColspan(line1, 5);
        invoiceForm.setColspan(line2, 5);
        invoiceForm.setColspan(gap1, 3);
        invoiceForm.setColspan(gap2, 3);
        invoiceForm.setColspan(line3, 5);
        invoiceForm.setColspan(gap3, 3);
        invoiceForm.setColspan(gap4, 3);
        invoiceForm.setColspan(gap5, 2);
        invoiceForm.setColspan(horizontalLayout, 2);

        FormLayout contactForm = new FormLayout();
        contactForm.add(bankDetailsComboBox, contactDetailsComboBox);
        contactForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        contactForm.setMaxWidth("50%");

        Button closeButton = getConfirmDialogButton(dialog);
        closeButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        dialog.add(title, invoiceForm, contactForm);
        dialog.getFooter().add(closeButton);
        dialog.getFooter().add(saveInvoiceButton);
        dialog.getFooter().add(anchor);

        return dialog;
    }

    private static Button getConfirmDialogButton(Dialog dialog) {
        ConfirmDialog confirmDialog = new ConfirmDialog();
        confirmDialog.setHeader("Unsaved changes");
        confirmDialog.setText(
                "Edited data will be lost. Are you sure you want to close ?");

        confirmDialog.setCancelable(true);
        confirmDialog.setCancelButtonTheme("primary");
        confirmDialog.addCancelListener(event -> confirmDialog.close());

        confirmDialog.setConfirmText("Close Invoice Maker");
        confirmDialog.setConfirmButtonTheme("error primary");
        confirmDialog.addConfirmListener(event -> dialog.close());

        return new Button("Close", e -> confirmDialog.open());
    }
}
