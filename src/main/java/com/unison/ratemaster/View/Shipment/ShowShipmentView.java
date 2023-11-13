package com.unison.ratemaster.View.Shipment;

import com.unison.ratemaster.Entity.Rate;
import com.unison.ratemaster.Entity.Shipment;
import com.unison.ratemaster.Service.ShipmentService;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.function.Consumer;


@Route(value = "show-shipment", layout = MainView.class)
public class ShowShipmentView extends VerticalLayout {

    public ShowShipmentView(@Autowired ShipmentService shipmentService) {
        Grid<Shipment> grid = new Grid<>();
        grid.addColumn(Shipment::getBlNo).setHeader("B/L No").setSortable(false).setFrozen(true);
        grid.addColumn(shipment -> shipment.getBooking().getBookingNo()).setHeader("Booking No").setSortable(false);
        grid.addColumn(Shipment::getInvoiceNo).setHeader("Invoice").setSortable(false);
        grid.addColumn(Shipment::getName, "name").setHeader("Name").setSortable(false);
        grid.addColumn(shipment -> shipment.getShipper().getName(), "name").setHeader("ShipperName")
                .setSortable(false);
        grid.addColumn(shipment -> shipment.getContainerSize().getContainerSize()).setHeader("Container");
        grid.addColumn(shipment -> shipment.getStatus().name()).setHeader("Status");
        grid.addColumn(Shipment::getCreatedOn).setHeader("Created").setSortable(true);
        grid.addColumn(Shipment::getLastUpdated).setHeader("Updated").setSortable(true);
        grid.addComponentColumn(shipment -> {
            Anchor anchor = new Anchor(new StreamResource("MEDUD0884798.pdf", (InputStreamFactory) () -> {
                try {
                    return new ByteArrayInputStream(shipmentService.getPdf());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }), "");
            anchor.getElement().setAttribute("download", true);
            anchor.add(new Button("Download"));
            return anchor;
        }).setHeader("Master BL");
        grid.setItems(shipmentService.getAllShipments());



//        add(pdfViewer);





//        HeaderRow headerRow = grid.appendHeaderRow();
//
//        headerRow.getCell(blNoColumn).setComponent(
//                createFilterHeader("Name", ::setFullName));
//        headerRow.getCell(bookingNoColumn).setComponent(
//                createFilterHeader("Email", personFilter::setEmail));
//        headerRow.getCell(invoiceColumn).setComponent(
//                createFilterHeader("Profession", personFilter::setProfession));
//
//        TextField searchField = new TextField();
//        searchField.setWidth("50%");
//        searchField.setPlaceholder("Search");
//        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
//        searchField.setValueChangeMode(ValueChangeMode.EAGER);
//        searchField.addValueChangeListener(e -> dataView.refreshAll());
//
//        dataView.addFilter(rate -> {
//            String searchTerm = searchField.getValue().trim();
//            if (searchTerm.isEmpty()) return true;
//            return searchTermMatchesFilterFields(searchTerm, rate);
//        });

        VerticalLayout layout = new VerticalLayout(grid);
        layout.setPadding(false);
        add(layout);
    }

    private static Component createFilterHeader(String labelText,
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

    private boolean searchTermMatchesFilterFields(String searchTerm, Rate rate) {
        boolean matchesPortCity = rate.getPortOfDestination().getPortCity().toLowerCase().contains(searchTerm.toLowerCase())
                || rate.getPortOfLoading().getPortCity().toLowerCase().contains(searchTerm.toLowerCase());
        boolean matchesShortCode = rate.getPortOfDestination().getPortShortCode().toLowerCase().contains(searchTerm.toLowerCase())
                || rate.getPortOfLoading().getPortShortCode().toLowerCase().contains(searchTerm.toLowerCase());
        boolean matchesPortCountry = rate.getPortOfDestination().getPortCountry().toLowerCase().contains(searchTerm.toLowerCase())
                || rate.getPortOfLoading().getPortCountry().toLowerCase().contains(searchTerm.toLowerCase());
//        boolean matchesCarrier = rate.getCarrier().toLowerCase().contains(searchTerm.toLowerCase());
//        boolean matchesCommodity = rate.getCommodity().toLowerCase().contains(searchTerm.toLowerCase());
        return matchesPortCity || matchesShortCode || matchesPortCountry;// || matchesCommodity || matchesCarrier;
    }
}
