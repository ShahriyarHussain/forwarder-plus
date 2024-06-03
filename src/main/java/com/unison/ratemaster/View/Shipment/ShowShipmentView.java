package com.unison.ratemaster.View.Shipment;

import com.unison.ratemaster.Dto.ContainerDto;
import com.unison.ratemaster.Dto.InvoiceItemDto;
import com.unison.ratemaster.Dto.TSReportDto;
import com.unison.ratemaster.Entity.*;
import com.unison.ratemaster.Enum.*;
import com.unison.ratemaster.Repository.InvoiceItemsService;
import com.unison.ratemaster.Service.*;
import com.unison.ratemaster.Util.ShipmentFilter;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
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
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.NotificationVariant;
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
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.annotation.security.PermitAll;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@PageTitle("View Shipments")
@PermitAll
@Route(value = "show-shipment", layout = MainView.class)

public class ShowShipmentView extends VerticalLayout {

    private final String REPORTS_PATH = "/Reports/";
    private final ShipmentService shipmentService;
    private final ClientService clientService;
    private final ScheduleService scheduleService;
    private final PortService portService;
    private final BookingService bookingService;
    private final CommodityService commodityService;
    private final InvoiceService invoiceService;
    private final CarrierService carrierService;
    private final BankDetailsService bankDetailsService;
    private final ContactDetailsService contactDetailsService;
    private final InvoiceItemsService invoiceItemsService;

    private byte[] masterBl;
    private final Grid<Shipment> grid;

    public ShowShipmentView(ShipmentService shipmentService, ClientService clientService, ScheduleService scheduleService,
                            PortService portService, BookingService bookingService, CommodityService commodityService,
                            InvoiceService invoiceService, CarrierService carrierService, BankDetailsService bankDetailsService,
                            ContactDetailsService contactDetailsService, InvoiceItemsService invoiceItemsService) {

        this.shipmentService = shipmentService;
        this.clientService = clientService;
        this.scheduleService = scheduleService;
        this.portService = portService;
        this.bookingService = bookingService;
        this.commodityService = commodityService;
        this.invoiceService = invoiceService;
        this.carrierService = carrierService;
        this.bankDetailsService = bankDetailsService;
        this.contactDetailsService = contactDetailsService;
        this.invoiceItemsService = invoiceItemsService;


        H2 title = new H2("View Shipment");

        grid = new Grid<>();
        grid.setMinHeight(35, Unit.EM);

        Grid.Column<Shipment> blColumn = grid.addColumn(Shipment::getBlNo).setSortable(false).setFrozen(true)
                .setAutoWidth(true).setResizable(true);;
        Grid.Column<Shipment> bookingColumn = grid.addColumn(shipment -> shipment.getBooking().getBookingNo())
                .setAutoWidth(true).setResizable(true);;
        Grid.Column<Shipment> shipperInvoiceColumn = grid.addColumn(Shipment::getInvoiceNo).setAutoWidth(true).setResizable(true);;
        Grid.Column<Shipment> invoiceColumn = grid.addColumn(shipment -> shipment.getInvoice().getInvoiceNo())
                .setAutoWidth(true).setResizable(true);;

        Grid.Column<Shipment> shipperColumn = grid.addColumn(shipment -> shipment.getShipper() == null ? "" : shipment.getShipper().getName())
                .setTooltipGenerator(shipment -> shipment.getShipper() == null ? "" : shipment.getShipper().getName())
                .setWidth("11em").setResizable(true);;

        Grid.Column<Shipment> numOfContainerColumn = grid.addColumn(shipment -> shipment.getBooking().getNumOfContainers() + "x" +
                shipment.getBooking().getContainerSize().getContainerSize()).setAutoWidth(true).setResizable(true);;

        Grid.Column<Shipment> statusColumn = grid.addColumn(shipment -> shipment.getStatus().name())
                .setTooltipGenerator(shipment -> shipment.getStatus().toString()).setAutoWidth(true)
                .setSortable(true).setResizable(true);;

        Grid.Column<Shipment> createdColumn = grid.addColumn(shipment -> shipment.getCreatedOn()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setSortable(true)
                .setComparator(Shipment::getCreatedOn).setAutoWidth(true).setResizable(true);;

        Grid.Column<Shipment> lastUpdateColumn = grid.addColumn(shipment -> shipment.getLastUpdated()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).setSortable(true)
                .setComparator(Shipment::getLastUpdated).setAutoWidth(true).setResizable(true);

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
        }).setTooltipGenerator(shipment -> shipment.getMasterBl() == null ? "B/L Not Found" : "")
                .setAutoWidth(true).setResizable(true);;

        grid.setRowsDraggable(true);
        grid.setColumnReorderingAllowed(true);


        List<Shipment> shipments = shipmentService.getAllShipments();
        GridListDataView<Shipment> dataView = grid.setItems(shipments);
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
                createNonFilterHeader("Container"));
        headerRow.getCell(statusColumn).setComponent(
                createNonFilterHeader("Status"));
        headerRow.getCell(createdColumn).setComponent(
                createNonFilterHeader("Created"));
        headerRow.getCell(lastUpdateColumn).setComponent(
                createNonFilterHeader("Updated"));
        headerRow.getCell(mblColumn).setComponent(
                createNonFilterHeader("MB/L"));


        GridContextMenu<Shipment> menu = grid.addContextMenu();
        menu.addItem("Edit Shipment", event -> {
            if (event.getItem().isPresent()) {
                createEditDialog(event.getItem().get()).open();
            }
        });
        menu.addItem("Edit Schedule", event -> {
            if (event.getItem().isPresent()) {
                createScheduleEditorDialog(event.getItem().get()).open();
            }
        });
        menu.addItem("Edit Booking", event -> {
            if (event.getItem().isPresent()) {
                addContainerDetailsDialog(event.getItem().get()).open();
            }
        });
        menu.add(new Hr());
        menu.addItem("Shipment Advice", event -> {
            if (event.getItem().isPresent()) {
                validateParams(event.getItem().get()).open();
            }
        });
        menu.addItem("Create Invoice", event -> {
            if (event.getItem().isPresent()) {
                createInvoiceMakerDialog(event.getItem().get()).open();
            }
        });
        menu.add(new Hr());
        menu.addItem("Delete", event -> {
            if (event.getItem().isEmpty()) return;
            shipmentService.deleteShipmentAndBooking(event.getItem().get());
            grid.setItems(shipmentService.getAllShipments());
        });

        Label itemCount = new Label("Total Shipments: " + shipments.size());

        VerticalLayout layout = new VerticalLayout(grid, itemCount);
        layout.setPadding(false);
        add(title, layout);
    }

    private Dialog validateParams(Shipment shipment) {

        List<String> errorList = new LinkedList<>();
        Dialog dialog = new Dialog();
        H4 title = new H4();
        ListBox<String> listBox = new ListBox<>();

        dialog.setCloseOnEsc(true);
        dialog.getFooter().add(new Button("Close", event -> dialog.close()));
        dialog.add(title, listBox);

        //  শিপমেন্ট এর তথ্য যাচাইকরণ
        if (shipment.getBlNo() == null) {
            errorList.add("B/L No. Not Provided!");
        }
        if (shipment.getInvoiceNo() == null) {
            errorList.add("Invoice No. Not Provided!");
        }
        if (shipment.getShipper() == null || shipment.getShipper().getName() == null) {
            errorList.add("Shipper Missing");
        }
        if (shipment.getNotifyParty() == null || shipment.getNotifyParty().getName() == null) {
            errorList.add("Notify Party Missing");
        }


        // শিপমেন্ট সংলগ্ন বুকিং এর তথ্য যাচাইকরণ । যদি না থাকে তবে আর কোনও যাচাইকরণ প্রয়োজন নেই
        if (shipment.getBooking() == null) {
            errorList.add("No Booking Found for shipment. Please create a new or choose existing!");
            title.setText("Please make corrections for following " + errorList.size() + " fields");
            listBox.setItems(errorList);
            return dialog;
        }
        if (shipment.getBooking().getBookingNo() == null) {
            errorList.add("Booking No. Not Provided!");
        }
        if (shipment.getBooking().getStuffingDate() == null) {
            errorList.add("Stuffing Date Not Provided!");
        }
        if (shipment.getCommodity().getName() == null) {
            errorList.add("Please Enter COMMODITY value");
        }
        if (shipment.getBooking().getContainer().stream().anyMatch(Objects::isNull)) {
            errorList.add("Container data has invalid value, Please make corrections and try again.");
        }
        if (shipment.getBooking().getContainerSize().getContainerSize() == null) {
            errorList.add("Please provide container size");
        }
        if (shipment.getBooking().getNumOfContainers() == null) {
            errorList.add("Please provide how many containers");
        }


        // শিডিউল যাচাইকরণ । যদি না থাকে তবে আর কোনও যাচাইকরণ প্রয়োজন নেই
        if (shipment.getSchedule() == null) {
            errorList.add("No Schedule Found for shipment. Please create a new or choose existing!");
            title.setText("Please make corrections for following " + errorList.size() + " fields");
            listBox.setItems(errorList);
            return dialog;
        }
        if (shipment.getSchedule().getPortOfLoading() == null) {
            errorList.add("Please provide Port Of Loading!");
        }
        if (shipment.getSchedule().getLoadingPortEta() == null) {
            errorList.add("Please provide Port Of Loading!");
        }
        if (shipment.getSchedule().getLoadingPortEtd() == null) {
            errorList.add("Please provide Port Of Loading!");
        }
        if (shipment.getSchedule().getPolVesselName() == null) {
            errorList.add("Please provide Feeder Vessel Name!");
        }
        if (shipment.getSchedule().getMvPortFeederEta() == null) {
            errorList.add("Please provide Port Of Loading!");
        }

        if (shipment.getSchedule().getPortOfDestination() == null) {
            errorList.add("Please provide Port Of Loading!");
        }
        if (shipment.getSchedule().getDestinationPortEta() == null) {
            errorList.add("Please provide Port Of Loading!");
        }


        // যদি ট্রান্সশিপমেন্ট থাকে তবে এর তথ্য যাচাইকরণ
        Set<Transshipment> transshipments = shipment.getSchedule().getTransshipment();
        if (transshipments != null && !transshipments.isEmpty()) {
            transshipments.forEach(ts -> {
                if (ts.getPort().getPortName() == null) {
                    errorList.add("Please provide Transhipment Port Name!");
                }
                if (ts.getPortArrival() == null) {
                    errorList.add("Please provide Transhipment Port ETA");
                }
            });
        }

        if (errorList.isEmpty()) {
            title.setText("ALL OK!");
            ComboBox<ContactDetails> contactDetailsComboBox = new ComboBox<>("Choose Contact Details");
            contactDetailsComboBox.setItems(contactDetailsService.getAllContactDetails());
            contactDetailsComboBox.setItemLabelGenerator(ContactDetails::getName);
            contactDetailsComboBox.addValueChangeListener(e -> {
                if (e.getValue() != null) {
                    contactDetailsComboBox.setLabel("You can now download!");
                    dialog.getFooter().add(getShipmentAdviceDownloadButton(shipment, contactDetailsComboBox.getValue()));
                }
            });
            dialog.add(contactDetailsComboBox);
        } else {
            listBox.setItems(errorList);
            title.setText("Please make corrections for following " + errorList.size() + " fields");
        }
        dialog.setResizable(true);
        dialog.setMinWidth(10, Unit.EM);
        return dialog;
    }

    private Anchor getShipmentAdviceDownloadButton(Shipment shipment, ContactDetails contactDetails) {
        Anchor anchor = new Anchor(new StreamResource("Shipment_Advice_" + shipment.getBooking().getBookingNo() + ".pdf", (InputStreamFactory) () -> {
            Map<String, Object> parameters;
            String report = "shipment_advice.jasper";
            parameters = prepareParamsForShipmentAdvice(shipment, contactDetails);
            List<TSReportDto> tsReportDtoList = new LinkedList<>();

            Schedule shipmentSchedule = shipment.getSchedule();

            List<Transshipment> tsList = shipmentSchedule.getTransshipment().stream()
                    .sorted(Comparator.comparing(Transshipment::getPortArrival)).collect(Collectors.toList());
            for (int i = 0, count = 1; i < tsList.size(); i++) {
                Transshipment transshipment = tsList.get(i);
                if (transshipment.getVesselName() != null && !transshipment.getVesselName().isEmpty()) {
                    tsReportDtoList.add(new TSReportDto("Vessel TS" + count++, transshipment.getVesselName()));
                }
                tsReportDtoList.add(new TSReportDto("ETA " + transshipment.getPort().getPortName(),
                        Util.formatDateTime(Util.GENERIC_DATE_PATTERN, transshipment.getPortArrival())));
            }

            tsReportDtoList.add(new TSReportDto(
                    "ETA Dest. " + System.lineSeparator() + "(" + shipmentSchedule.getPortOfDestination().getPortName() + ")",
                    Util.formatDateTime(Util.GENERIC_DATE_PATTERN, shipmentSchedule.getDestinationPortEta())));

            JRDataSource dataSource = new JRBeanCollectionDataSource(tsReportDtoList);
            parameters.put("COLLECTION_LIST", dataSource);

            try (InputStream stream = getClass().getResourceAsStream(REPORTS_PATH + report)) {
                return new ByteArrayInputStream(JasperRunManager
                        .runReportToPdf(stream, parameters, new JREmptyDataSource(1)));
            } catch (JRException | IOException e) {
                throw new RuntimeException(e);
            }
        }), "");

        Button downloadButton = new Button(new Icon(VaadinIcon.DOWNLOAD_ALT));
        downloadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        downloadButton.setText("Download");

        anchor.getElement().setAttribute("download", true);
        anchor.add(downloadButton);
        return anchor;
    }

    private Map<String, Object> prepareParamsForShipmentAdvice(Shipment shipment, ContactDetails contactDetails) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("LOGO_URL", Util.imagePath);

        paramMap.put("ADVICE_DATE", Util.formatDateTime(Util.GENERIC_DATE_PATTERN, LocalDate.now()));
        paramMap.put("BL_NO", shipment.getBlNo());
        paramMap.put("BOOKING_NO", shipment.getBooking().getBookingNo());
        paramMap.put("SHIPPER_INVOICE_NO", shipment.getInvoiceNo());

        paramMap.put("STUFFING_DATE", Util.formatDateTime(Util.GENERIC_DATE_PATTERN, shipment.getBooking().getStuffingDate()));
        paramMap.put("STUFFING_DEPOT", shipment.getBooking().getStuffingDepot());
        paramMap.put("SHIPPER_NAME", shipment.getShipper().getName());
        paramMap.put("CONSIGNEE", shipment.getNotifyParty().getName());

        paramMap.put("NUM_OF_CONTAINER", shipment.getBooking().getNumOfContainers() + "x" +
                shipment.getBooking().getContainerSize().getContainerSize());
        paramMap.put("COMMODITY", shipment.getCommodity().getName());
        paramMap.put("QUANTITY", calculateQuantity(shipment.getBooking()));
        paramMap.put("GROSS_WEIGHT", calculateWeight(shipment.getBooking()));

        paramMap.put("PORT_OF_LOADING", shipment.getSchedule().getPortOfLoading().getPortShortCode());
        paramMap.put("MV_CONNECT_PORT", shipment.getSchedule().getMotherVesselPort().getPortShortCode());
        paramMap.put("FEEDER", shipment.getSchedule().getPolVesselName());

        paramMap.put("POL_ETA", Util.formatDateTime(Util.GENERIC_DATE_PATTERN, shipment.getSchedule().getLoadingPortEta()));
        paramMap.put("POL_ETD", Util.formatDateTime(Util.GENERIC_DATE_PATTERN, shipment.getSchedule().getLoadingPortEtd()));
        paramMap.put("MV_PORT_FEEDER_ETA", Util.formatDateTime(Util.GENERIC_DATE_PATTERN, shipment.getSchedule().getMvPortFeederEta()));

        paramMap.put("SEAL_NO", getSealNo(shipment.getBooking()));
        paramMap.put("CONTAINERS", getContainers(shipment.getBooking()));

        paramMap.put("SIGNED_BY", contactDetails.getName());
        paramMap.put("SIGNED_BY_EMAIL", contactDetails.getEmail());
        paramMap.put("SIGNED_BY_CONTACT", contactDetails.getContactNo());

        return paramMap;
    }

    private String calculateWeight(Booking booking) {
        if (booking.getContainer() == null || booking.getContainer().isEmpty()) {
            return "";
        }
        BigDecimal grossWeight = new BigDecimal("0");
        for (FreightContainer container : booking.getContainer()) {
            grossWeight = grossWeight.add(container.getGrossWeight());
        }
        return Util.getFormattedBigDecimal(grossWeight) + " KGS";
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

    private Component createFilterHeader(String labelText, Consumer<String> filterChangeConsumer) {
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

    private Dialog createEditDialog(Shipment shipment) {

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
        carrierComboBox.setValue(shipment.getCarrier());

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
            shipment.setStatus(statusComboBox.getValue());

            shipment.getBooking().setBookingNo(bookingNo.getValue());
            shipment.getBooking().setContainerType(containerType.getValue());
            shipment.getBooking().setContainerSize(containerSize.getValue());
            shipment.getBooking().setInvoiceNo(invoiceNo.getValue());
            shipment.getBooking().setNumOfContainers(numOfContainers.getValue());

            try {
                shipmentService.saveEditedShipment(shipment);
                Util.getPopUpNotification("Shipment Saved Successfully!", 2500,
                        NotificationVariant.LUMO_SUCCESS).open();
                grid.setItems(shipmentService.getAllShipments());
                dialog.close();
            } catch (Exception e) {
                e.printStackTrace();
                Util.getPopUpNotification("Unexpected Error: " + e.getMessage(), 3500,
                        NotificationVariant.LUMO_ERROR).open();
            }
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        formLayout.add(name, blNo, invoiceNo, bookingNo, containerType, numOfContainers, containerSize,
                commodities, scheduleComboBox, shipper, consignee, notifyParty, goodsDescription, shipperMarks,
                carrierComboBox, upload, statusComboBox);
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
                Util.getPopUpNotification("Error: " + e.getMessage(), 3500, NotificationVariant.LUMO_ERROR).open();
            }
        });

        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();
            Util.getPopUpNotification(errorMessage, 3500, NotificationVariant.LUMO_ERROR).open();
        });
        upload.setUploadButton(new Button("Upload MB/L"));
        return upload;
    }

    public Dialog addContainerDetailsDialog(Shipment shipment) {
        Dialog dialog = new Dialog();

        Booking booking = shipment.getBooking();
        Set<FreightContainer> containerList = booking.getContainer();

        H2 title = new H2("Add Container Details");

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
        containerGrid.addComponentColumn(freightContainer -> {
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(event -> {
                containerList.remove(freightContainer);
                containerGrid.setVisible(!containerList.isEmpty());
                containerGrid.setItems(containerList);
            });
            return deleteButton;
        });
        containerGrid.setMaxHeight(20, Unit.EM);
        containerGrid.setVisible(!containerList.isEmpty());
        containerGrid.setItems(containerList);

        Label totalContainers = new Label();
        totalContainers.setVisible(!containerList.isEmpty());
        totalContainers.setText("( " + containerList.size() + " Containers Added)");


        TextField containerNo = new TextField("Container No.");
        TextField sealNo = new TextField("Seal No.");

        TextArea bulkContainerNo = new TextArea("Enter All Containers (Separated by ',' or '|' or line break)");
        bulkContainerNo.setVisible(false);
        TextArea bulkSealNo = new TextArea("Enter All Seal Nos. (Separated by ',' or '|' or line break)");
        bulkSealNo.setVisible(false);

        Checkbox bulkEntryCheckBox = new Checkbox("Bulk Entry Container Info ?");
        bulkEntryCheckBox.setValue(false);
        bulkEntryCheckBox.addValueChangeListener(e -> {
            boolean isBulkEntry = e.getValue();
            containerNo.setVisible(!isBulkEntry);
            sealNo.setVisible(!isBulkEntry);
            bulkContainerNo.setVisible(isBulkEntry);
            bulkSealNo.setVisible(isBulkEntry);
        });

        BigDecimalField grossWeight = new BigDecimalField("Gross Weight");
        IntegerField noOfPackages = new IntegerField("No Packages");
        ComboBox<PackageUnit> packageUnitComboBox = new ComboBox<>("Package Unit");
        packageUnitComboBox.setItems(PackageUnit.values());

        addButton.setMaxWidth(1, Unit.EM);
        addButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        addButton.addClickListener(event -> {
            if (bulkEntryCheckBox.getValue()) {
                if (bulkContainerNo.getValue() == null || bulkContainerNo.getValue().isEmpty()
                        || bulkSealNo.getValue() == null || bulkSealNo.getValue().isEmpty()) {
                    Util.getPopUpNotification("Please provide Correct Data!", 3000,
                            NotificationVariant.LUMO_ERROR).open();
                    return;
                }
                List<ContainerDto> containerDtos = getContainerDtosFromBulkEntry(bulkContainerNo.getValue(), bulkSealNo.getValue());
                containerList.addAll(containerDtos.stream().map(dto -> {
                    FreightContainer freightContainer = new FreightContainer();
                    freightContainer.setNoOfPackages(noOfPackages.getValue());
                    freightContainer.setGrossWeight(grossWeight.getValue());
                    freightContainer.setPackageUnit(packageUnitComboBox.getValue());

                    freightContainer.setContainerNo(dto.getContainerNo());
                    freightContainer.setSealNo(dto.getSealNo());
                    return freightContainer;
                }).collect(Collectors.toList()));
            } else {
                if (containerNo.getValue() == null || containerNo.getValue().isEmpty()
                        || sealNo.getValue() == null || sealNo.getValue().isEmpty()) {
                    Util.getPopUpNotification("Please provide Correct Data!", 3000,
                            NotificationVariant.LUMO_ERROR).open();
                    return;
                }
                FreightContainer container = new FreightContainer();
                container.setContainerNo(containerNo.getValue());
                container.setSealNo(sealNo.getValue());
                container.setNoOfPackages(noOfPackages.getValue());
                container.setGrossWeight(grossWeight.getValue());
                container.setPackageUnit(packageUnitComboBox.getValue());
                containerList.add(container);
            }
            containerGrid.setVisible(true);
            containerGrid.setItems(containerList);
            totalContainers.setVisible(!containerList.isEmpty());
            totalContainers.setText("( " + containerList.size() + " Containers Added)");
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

            if (containerList.size() != booking.getNumOfContainers()) {
                new ConfirmDialog("Data Mismatch Warning!",
                        "Your Booking is SAVED. But Number of Containers Does Not Match With Entered Containers."
                                + System.lineSeparator() + "Entered Container = " + containerList.size() + ", "
                                + " Number of Containers = " + booking.getNumOfContainers(), "Ok",
                        e -> getChildren().close()).open();

            } else {
                Util.getPopUpNotification("Booking Saved Successfully", 2500, NotificationVariant.LUMO_SUCCESS).open();
            }
        });

        Hr line = new Hr();
        line.setHeight("5px");
        line.setClassName("style=background-color:black");

        FormLayout formLayout = new FormLayout();
        formLayout.add(bookingNo, invoiceNo, containerType, numOfCont, containerSize, stuffingDate, stuffingCost, stuffingDepot,
                line, bulkEntryCheckBox, containerNo, sealNo, bulkContainerNo, bulkSealNo, grossWeight, noOfPackages,
                packageUnitComboBox, addButton, totalContainers);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));
        formLayout.setColspan(line, 4);
        formLayout.setColspan(containerGrid, 4);
        dialog.add(title, formLayout, containerGrid);
        dialog.getFooter().add(saveButton);
        dialog.getFooter().add(new Button("Close", event -> dialog.close()));
        return dialog;
    }

    private List<ContainerDto> getContainerDtosFromBulkEntry(String containerNo, String sealNo) throws RuntimeException {
        List<String> containerNos = getBulkEntryItemsAsListFromString(containerNo);
        List<String> sealNos = getBulkEntryItemsAsListFromString(sealNo);
        if (containerNos.isEmpty() || sealNos.isEmpty()) return new LinkedList<>();

        int length = Integer.min(containerNos.size(), sealNos.size());
        if (containerNos.size() != sealNos.size()) {
            Util.getPopUpNotification("Number of Containers does not match number of seal no.", 3000,
                    NotificationVariant.LUMO_CONTRAST).open();
        }

        List<ContainerDto> containerDtos = new LinkedList<>();
        for (int i = 0; i < length; i++) {
            containerDtos.add(new ContainerDto(containerNos.get(i), sealNos.get(i)));
        }
        return containerDtos;
    }

    private List<String> getBulkEntryItemsAsListFromString(String items) {
        char LINE_BREAK = '\n', COMMA = ',', PIPE = '|';
        List<String> itemList = new LinkedList<>();

        StringBuilder entry = new StringBuilder();
        for (char c : items.toCharArray()) {
            if (c == LINE_BREAK || c == COMMA || c == PIPE) {
                if (!entry.toString().trim().isEmpty()) {
                    itemList.add(entry.toString().trim());
                }
                entry = new StringBuilder();
                continue;
            }
            entry.append(c);
        }
        if (!entry.toString().trim().isEmpty()) {
            itemList.add(entry.toString().trim());
        }
        return itemList;
    }

    public Dialog createScheduleEditorDialog(Shipment shipment) {

        Dialog dialog = new Dialog();
        Schedule schedule;
        Set<Transshipment> transshipments;
        List<Port> portList = portService.getPorts();
        Label carrierLabel = new Label("Carrier: N/A");


        H3 pageTitle = new H3("Create Schedule");
        if (shipment.getSchedule() != null) {
            pageTitle = new H3("Edit Schedule");
            schedule = shipment.getSchedule();
            transshipments = schedule.getTransshipment();
            if (schedule.getCarrier() != null) {
                carrierLabel.setText("Carrier: " + schedule.getCarrier());
            }
        } else {
            schedule = null;
            transshipments = new HashSet<>();
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

        ComboBox<Port> motherVesselPort = Util.getPortComboBoxByItemListAndTitle(portList, "Mother Vessel Connect Port");
        DatePicker mvPortEta = new DatePicker("Feeder Connect ETA");
        motherVesselPort.setRequired(true);
        motherVesselPort.addValueChangeListener(event -> {
            if (event != null && event.getValue() != null) {
                Port vesselPort = event.getValue();
                mvPortEta.setLabel("Feeder ETA" + vesselPort.getPortShortCode());
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

        TextField transhipmentVessel = new TextField("Transhipment Vessel:");
        ComboBox<Port> tsPort = Util.getPortComboBoxByItemListAndTitle(portList, "Transshipment Port");
        DatePicker tsPortEta = new DatePicker(tsPort.getLabel() + " ETA");
        tsPort.addValueChangeListener(event -> {
            if (event != null && event.getValue() != null) {
                Port vesselPort = event.getValue();
                tsPortEta.setLabel("ETA " + vesselPort.getPortCity());
            }
        });

        Grid<Transshipment> transshipmentGrid = new Grid<>();

        transshipmentGrid.setItems(transshipments);
        transshipmentGrid.addColumn(Transshipment::getVesselName).setHeader("Vessel");
        transshipmentGrid.addColumn(ts -> ts.getPort().getPortName()).setHeader("Port");
        transshipmentGrid.addColumn(ts -> Util.formatDateTime(Util.GENERIC_DATE_PATTERN, ts.getPortArrival())).setHeader("ETA");
        transshipmentGrid.addComponentColumn(ts -> {
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(event -> {
                transshipments.remove(ts);
                transshipmentGrid.setVisible(!transshipments.isEmpty());
                transshipmentGrid.setItems(transshipments);
            });
            return deleteButton;
        }).setHeader("Delete");
        transshipmentGrid.setMaxHeight(15, Unit.EM);
        transshipmentGrid.setVisible(!transshipments.isEmpty());

        Button addTsButton = new Button("Add", new Icon(VaadinIcon.PLUS));
        addTsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addTsButton.addClickListener(event -> {
            if (tsPort.getValue() == null || tsPortEta.getValue() == null) {
                Util.getPopUpNotification("Please fill up the values correctly!", 3500, NotificationVariant.LUMO_CONTRAST).open();
                return;
            }
            Transshipment ts = new Transshipment();
            ts.setPort(tsPort.getValue());
            ts.setVesselName(transhipmentVessel.getValue());
            ts.setPortArrival(tsPortEta.getValue());
            transshipments.add(ts);
            transshipmentGrid.setVisible(true);
            transshipmentGrid.setItems(transshipments);
        });


        Button addButton = new Button("Add Schedule");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        if (schedule != null) {
            feederVesselName.setValue(schedule.getPolVesselName());
            portOfLoading.setValue(schedule.getPortOfLoading());
            polEta.setValue(schedule.getLoadingPortEta());
            polEtd.setValue(schedule.getLoadingPortEtd());
            motherVesselPort.setValue(schedule.getMotherVesselPort());
            mvPortEta.setValue(schedule.getMvPortFeederEta());
            transshipmentGrid.setItems(schedule.getTransshipment());
            destinationPort.setValue(schedule.getPortOfDestination());
            destinationPortEta.setValue(schedule.getDestinationPortEta());
            addButton.setText("Save");
        }

        addButton.addClickListener(e -> {
            Schedule newSchedule = Objects.requireNonNullElseGet(schedule, Schedule::new);
            newSchedule.setPolVesselName(feederVesselName.getValue());
            newSchedule.setPortOfLoading(portOfLoading.getValue());
            newSchedule.setLoadingPortEta(polEta.getValue());
            newSchedule.setLoadingPortEtd(polEtd.getValue());

            newSchedule.setMvPortFeederEta(mvPortEta.getValue());
            newSchedule.setMotherVesselPort(motherVesselPort.getValue());

            newSchedule.setPortOfDestination(destinationPort.getValue());
            newSchedule.setDestinationPortEta(destinationPortEta.getValue());

            newSchedule.setTransshipment(transshipments);
            newSchedule.setCarrier(shipment.getCarrier().getName());

            Schedule editedSchedule = scheduleService.saveSchedule(newSchedule);
            shipment.setSchedule(editedSchedule);
            shipmentService.saveEditedShipment(shipment);
            if (schedule != null) {
                Util.getPopUpNotification("Schedule Saved!", 2500, NotificationVariant.LUMO_SUCCESS).open();
            } else {
                Util.getPopUpNotification("Schedule Saved!", 2500, NotificationVariant.LUMO_SUCCESS).open();
            }
        });

        HorizontalLayout tsLayout = new HorizontalLayout();
        tsLayout.setVerticalComponentAlignment(Alignment.END);
        tsLayout.setAlignItems(Alignment.END);
        tsLayout.add(transhipmentVessel, tsPort, tsPortEta, addTsButton);

        formLayout.add(feederVesselName, portOfLoading, polEta, polEtd, motherVesselPort, mvPortEta,
                destinationPort, destinationPortEta, transhipmentVessel, tsPort, tsPortEta, addTsButton);
        Button cancelButton = new Button("Close", e -> dialog.close());

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));

        dialog.add(pageTitle, carrierLabel, formLayout, transshipmentGrid);
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(addButton);
        dialog.setMaxWidth("75%");
        return dialog;
    }

    public Dialog createInvoiceMakerDialog(Shipment shipment) {

        Invoice invoice = shipment.getInvoice();
        Set<InvoiceItem> invoiceItems = invoice.getInvoiceItems();

        Dialog dialog = new Dialog();
        HorizontalLayout horizontalLayout = new HorizontalLayout();

        H3 title = new H3("Prepare Invoice");

        IntegerField numOfContainer = new IntegerField("Containers");
        numOfContainer.setValue(shipment.getBooking().getNumOfContainers() == null
                ? 0 : shipment.getBooking().getNumOfContainers());

        TextField invoiceNo = new TextField("Invoice No");
        invoiceNo.setValue(invoice.getInvoiceNo());

        TextField expNo = new TextField("Exp No");
        expNo.setValue(Objects.requireNonNullElse(invoice.getExpNo(), ""));

        DatePicker expDate = new DatePicker("Exp Date");
        expDate.setValue(invoice.getExpDate());

        ComboBox<AmountCurrency> foreignCurrComboBox = new ComboBox<>("Carrier Currency");
        foreignCurrComboBox.setItems(AmountCurrency.values());
        foreignCurrComboBox.setItemLabelGenerator(curr -> curr.toString() + " - " + curr.getCurrencyName());
        foreignCurrComboBox.setValue(invoice.getForeignCurrency());

        ComboBox<AmountCurrency> localCurrencyComboBox = new ComboBox<>("Local Currency");
        localCurrencyComboBox.setItems(AmountCurrency.values());
        localCurrencyComboBox.setItemLabelGenerator(curr -> curr.toString() + " - " + curr.getCurrencyName());
        localCurrencyComboBox.setValue(invoice.getLocalCurrency());

        Text inWords = new Text("Zero");
        inWords.setText(addSuffixToWordAmountByCurrency(localCurrencyComboBox.getValue(),
                Util.getAmountInWords(invoice.getGrandTotal())));

        BigDecimalField total = new BigDecimalField("Total", BigDecimal.ZERO, "");
        total.setValue(invoice.getGrandTotal() == null ? BigDecimal.ZERO : invoice.getGrandTotal());
        total.setReadOnly(true);

        BigDecimalField conversionRate = new BigDecimalField("Conversion Rate", "");
        conversionRate.setValue(invoice.getConversionRate() == null ? BigDecimal.ONE : invoice.getConversionRate());

        TextField description = new TextField("Description");
        BigDecimalField rate = new BigDecimalField("Rate Per Product");
        IntegerField quantity = new IntegerField("Quantity");
        TextField itemUnit = new TextField("Unit");
        BigDecimalField itemTotalInLocalCurr = new BigDecimalField("Sub Total");
        Checkbox foreignCurrency = new Checkbox("Foreign Currency ?");

        rate.setValueChangeMode(ValueChangeMode.ON_BLUR);
        conversionRate.setValueChangeMode(ValueChangeMode.ON_BLUR);
        quantity.setValueChangeMode(ValueChangeMode.EAGER);

        rate.addValueChangeListener(e -> itemTotalInLocalCurr.setValue(
                calculateItemTotal(rate.getValue(), quantity.getValue(), foreignCurrency.getValue(), conversionRate.getValue()))
        );
        quantity.addValueChangeListener(e -> itemTotalInLocalCurr.setValue(
                calculateItemTotal(rate.getValue(), quantity.getValue(), foreignCurrency.getValue(), conversionRate.getValue()))
        );
        foreignCurrency.addValueChangeListener(e -> itemTotalInLocalCurr.setValue(
                calculateItemTotal(rate.getValue(), quantity.getValue(), foreignCurrency.getValue(), conversionRate.getValue()))
        );
        conversionRate.addValueChangeListener(e -> itemTotalInLocalCurr.setValue(
                calculateItemTotal(rate.getValue(), quantity.getValue(), foreignCurrency.getValue(), conversionRate.getValue())));

//        Checkbox includeContainers = new Checkbox("Include Containers ?");

        Grid<InvoiceItem> invoiceItemGrid = new Grid<>();

        invoiceItemGrid.setItems(invoiceItems);
        invoiceItemGrid.addColumn(InvoiceItem::getDescription).setHeader("Description");
        invoiceItemGrid.addColumn(InvoiceItem::getRate).setHeader("Rate");
        invoiceItemGrid.addColumn(InvoiceItem::getQuantity).setHeader("Quantity");
        invoiceItemGrid.addColumn(InvoiceItem::getItemUnit).setHeader("Unit");
        Grid.Column<InvoiceItem> foreignCurrTotal = invoiceItemGrid.addColumn(InvoiceItem::getTotalInForeignCurr)
                .setHeader(getTotalColumnName(foreignCurrComboBox.getValue()));
        Grid.Column<InvoiceItem> localCurrTotal = invoiceItemGrid.addColumn(InvoiceItem::getTotalInLocalCurr)
                .setHeader(getTotalColumnName(localCurrencyComboBox.getValue()));
        invoiceItemGrid.addComponentColumn(invoiceItem -> {
            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(event -> {
                invoiceItems.remove(invoiceItem);
                invoiceItemGrid.setVisible(!invoiceItems.isEmpty());
                invoiceItemGrid.setItems(invoiceItems);
                total.setValue(invoiceItems.stream()
                        .map(InvoiceItem::getTotalInLocalCurr)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                inWords.setText(addSuffixToWordAmountByCurrency(localCurrencyComboBox.getValue(),
                        Util.getAmountInWords(total.getValue())));
            });
            return deleteButton;
        });
        invoiceItemGrid.setMaxHeight(17, Unit.EM);
        invoiceItemGrid.setVisible(!invoiceItems.isEmpty());
        invoiceItemGrid.setItems(invoiceItems);

        foreignCurrComboBox.addValueChangeListener(event -> {
            foreignCurrTotal.setHeader(getTotalColumnName(event.getValue()));
            conversionRate.setValue(BigDecimal.ONE);
        });
        localCurrencyComboBox.addValueChangeListener(event -> {
            localCurrTotal.setHeader(getTotalColumnName(event.getValue()));
            conversionRate.setValue(BigDecimal.ONE);
        });

        ComboBox<BankDetails> bankDetailsComboBox = new ComboBox<>("Choose Bank Details");
        bankDetailsComboBox.setItems(bankDetailsService.getAllBankDetails());
        bankDetailsComboBox.setItemLabelGenerator(bankDetails -> bankDetails.getAccName() + " - " + bankDetails.getBankName()
                + " - " + bankDetails.getBranchName() + " - " + bankDetails.getAccNo());
        bankDetailsComboBox.setValue(invoice.getBankDetails());

        ComboBox<ContactDetails> contactDetailsComboBox = new ComboBox<>("Choose Contact Details");
        contactDetailsComboBox.setItems(contactDetailsService.getAllContactDetails());
        contactDetailsComboBox.setItemLabelGenerator(ContactDetails::getName);
        contactDetailsComboBox.setValue(invoice.getContactDetails());


        Button saveInvoiceButton = new Button("Save Invoice", new Icon(VaadinIcon.DATABASE));
        saveInvoiceButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveInvoiceButton.addClickListener(event -> {
            invoice.setGrandTotal(total.getValue());
            invoice.setExpNo(expNo.getValue());
            invoice.setExpDate(expDate.getValue());
            invoice.setConversionRate(conversionRate.getValue());
            invoice.setForeignCurrency(foreignCurrComboBox.getValue());
            invoice.setLocalCurrency(localCurrencyComboBox.getValue());
            invoice.setBankDetails(bankDetailsComboBox.getValue());
            invoice.setContactDetails(contactDetailsComboBox.getValue());

            try {
                invoice.setInvoiceItems(invoiceItems);
                invoiceItemsService.saveInvoiceItems(invoiceItems);
                invoiceService.saveInvoice(invoice);
                Util.getPopUpNotification("Saved Successfully", 2500, NotificationVariant.LUMO_SUCCESS).open();
            } catch (Exception e) {
                e.printStackTrace();
                Util.getPopUpNotification("Error!", 3500, NotificationVariant.LUMO_ERROR).open();
            }

        });

        Button createDetailsButton = new Button("Add", new Icon(VaadinIcon.PLUS));
        createDetailsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createDetailsButton.addClickListener(event -> {
            try {
                InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setInvoice(invoice);
                invoiceItem.setRate(rate.getValue());
                invoiceItem.setQuantity(quantity.getValue());
                invoiceItem.setDescription(description.getValue());
                invoiceItem.setItemUnit(itemUnit.getValue());
                invoiceItem.setForeignCurr(foreignCurrency.getValue());

                invoiceItem.setCurrency(foreignCurrency.getValue() ?
                        foreignCurrComboBox.getValue().toString() : localCurrencyComboBox.getValue().toString());
                invoiceItem.setTotalInForeignCurr(
                        calculateItemTotal(rate.getValue(), quantity.getValue(), false, BigDecimal.ONE));

                invoiceItem.setTotalInLocalCurr(itemTotalInLocalCurr.getValue());

                invoiceItems.add(invoiceItem);
                invoiceItemGrid.setItems(invoiceItems);
                invoiceItemGrid.setVisible(true);
                total.setValue(invoiceItems.stream()
                        .map(InvoiceItem::getTotalInLocalCurr)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                inWords.setText(addSuffixToWordAmountByCurrency(localCurrencyComboBox.getValue(),
                        Util.getAmountInWords(total.getValue())));
            } catch (Exception e) {
                Util.getPopUpNotification("Error Occurred", 2500, NotificationVariant.LUMO_SUCCESS).open();
                e.printStackTrace();
            }
        });

        HorizontalLayout invoiceItemSubLayout = new HorizontalLayout();
        invoiceItemSubLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        invoiceItemSubLayout.add(foreignCurrency, createDetailsButton);

        Button prepareInvoiceButton = new Button("Download Invoice", new Icon(VaadinIcon.DOWNLOAD));
        prepareInvoiceButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Anchor anchor = new Anchor(new StreamResource("Invoice-" + shipment.getBlNo() + ".pdf", (InputStreamFactory) () -> {

            List<InvoiceItemDto> invoiceItemDtos = new LinkedList<>();
            invoiceItemDtos.add(new InvoiceItemDto()); // প্রথম খালি ভ্যালুটি MASTER_REPORT এর জন্য
            invoiceItemDtos.addAll(invoice.getInvoiceItems().stream()
                    .sorted(Comparator.comparing(InvoiceItem::getTotalInLocalCurr).reversed())
                    .map(InvoiceItemDto::new)
                    .collect(Collectors.toList()));

            for (int i = 0; i < invoiceItemDtos.size(); i++) {
                invoiceItemDtos.get(i).setSlNo(i);
            }

            String reportName = "invoice.jasper";

            final Map<String, Object> parameters = getInvoiceReportParam(shipment);
            JRDataSource dataSource = new JRBeanCollectionDataSource(invoiceItemDtos);
            parameters.put("COLLECTION_LIST", dataSource);

            try (InputStream stream = getClass().getResourceAsStream((REPORTS_PATH + reportName))) {
                return new ByteArrayInputStream(JasperRunManager.runReportToPdf(stream, parameters, dataSource));
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
        invoiceForm.add(foreignCurrComboBox, localCurrencyComboBox, conversionRate, expNo, expDate, gap0, line1, gap0,
                description, rate, quantity, itemUnit, itemTotalInLocalCurr, invoiceItemSubLayout,
                invoiceItemGrid,
                horizontalLayout, gap5, total);

        invoiceForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 6));
        invoiceForm.setColspan(line1, 5);
        invoiceForm.setColspan(line2, 5);
        invoiceForm.setColspan(gap1, 3);
        invoiceForm.setColspan(gap2, 3);
        invoiceForm.setColspan(line3, 5);
        invoiceForm.setColspan(gap3, 3);
        invoiceForm.setColspan(gap4, 3);
        invoiceForm.setColspan(gap5, 2);
        invoiceForm.setColspan(horizontalLayout, 2);
        invoiceForm.setColspan(invoiceItemGrid, 6);

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

    private Map<String, Object> getInvoiceReportParam(Shipment shipment) {

        final Map<String, Object> parameters = new HashMap<>();

        Invoice invoice = shipment.getInvoice();

        parameters.put("LOGO_URL", Util.imagePath);

        parameters.put("INVOICE_NO", invoice.getInvoiceNo());
        parameters.put("INVOICE_DATE", Util.formatDateTime(Util.GENERIC_DATE_PATTERN, LocalDate.now()));

        parameters.put("SHIPPER_NAME", shipment.getShipper().getName());
        parameters.put("BL_NO", shipment.getBlNo());

        parameters.put("ADDRESS", shipment.getShipper().getAddress());
        parameters.put("VESSEL", shipment.getSchedule().getPolVesselName());

        parameters.put("SHIPPER_EMAIL", shipment.getShipper().getEmail());
        parameters.put("POL_ETD", Util.formatDateTime(Util.GENERIC_DATE_PATTERN, shipment.getSchedule().getLoadingPortEta()));

        parameters.put("COMMODITY", shipment.getCommodity().getName());
        parameters.put("DEST_ETA", Util.formatDateTime(Util.GENERIC_DATE_PATTERN, shipment.getSchedule().getDestinationPortEta()));

        parameters.put("PORT_OF_LOADING", shipment.getSchedule().getPortOfLoading().getPortName() + ", "
                + shipment.getSchedule().getPortOfLoading().getPortCountry());
        parameters.put("DEST_PORT", shipment.getSchedule().getPortOfDestination().getPortName() + ", "
                + shipment.getSchedule().getPortOfDestination().getPortCountry());

        int numOfContainers = shipment.getBooking().getNumOfContainers();
        if (numOfContainers > 9) {
            parameters.put("CONTAINERS", numOfContainers + "x" +
                    shipment.getBooking().getContainerSize().getContainerSize());
        } else {
            parameters.put("CONTAINERS", getContainers(shipment.getBooking()));
        }
        parameters.put("SHIPPER_INV_NO", shipment.getInvoiceNo());

        parameters.put("EXP_NO", invoice.getExpNo());
        parameters.put("EXP_DATE", Util.formatDateTime("dd-MM-yyyy", invoice.getExpDate()));

        parameters.put("FOREIGN_CURRENCY", invoice.getForeignCurrency().toString());
        parameters.put("LOCAL_CURRENCY", invoice.getLocalCurrency().toString());
        parameters.put("CONVERSION_RATE", Util.getFormattedBigDecimal(invoice.getConversionRate().setScale(2, RoundingMode.UNNECESSARY)));

        BigDecimal grandTotal = invoice.getInvoiceItems().stream().map(InvoiceItem::getTotalInLocalCurr).reduce(BigDecimal.ZERO, BigDecimal::add);
        parameters.put("TOTAL", Util.getFormattedBigDecimal(grandTotal.setScale(1, RoundingMode.UNNECESSARY)));
        parameters.put("TOTAL_IN_WORD",
                addSuffixToWordAmountByCurrency(invoice.getLocalCurrency(), Util.getAmountInWords(grandTotal)));

        BankDetails bankDetails = invoice.getBankDetails();
        parameters.put("BANK_NAME", bankDetails.getBankName());
        parameters.put("AC_NAME", bankDetails.getAccName());
        parameters.put("AC_NO", bankDetails.getAccNo());
        parameters.put("ROUTING_NO", bankDetails.getRoutingNo());
        parameters.put("BRANCH", bankDetails.getBranchName());

        ContactDetails contactDetails = invoice.getContactDetails();
        parameters.put("SIGNED_BY", contactDetails.getName());
        parameters.put("SIGNED_BY_EMAIL", contactDetails.getEmail());
        parameters.put("SIGNED_BY_CONTACT", contactDetails.getContactNo());

        return parameters;
    }

    private String getTotalColumnName(AmountCurrency currency) {
        if (currency == null) {
            return "Total";
        }
        return "Total in " + currency;
    }

    private BigDecimal calculateItemTotal(BigDecimal rate, Integer quantity, Boolean isForeignCurr, BigDecimal convRate) {
        if (rate == null || quantity == null || convRate == null) {
            System.out.println("NULL FOUND IN CALCULATION");
            return BigDecimal.ZERO;
        }
        BigDecimal conversionRate = isForeignCurr ? convRate : BigDecimal.ONE;
        return rate.multiply(new BigDecimal(quantity)).multiply(conversionRate);
    }

    private String addSuffixToWordAmountByCurrency(AmountCurrency currency, String amount) {
        if (currency == null) {
            currency = AmountCurrency.BDT;
        }
        return amount + " " + currency.getCurrencyName() + " Only";
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
