package com.unison.ratemaster.View.RateViews;


import com.unison.ratemaster.Entity.Rate;
import com.unison.ratemaster.Service.PortService;
import com.unison.ratemaster.Service.RateService;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "show-rate", layout = MainView.class)
public class ShowRateView extends VerticalLayout {

    public ShowRateView(@Autowired PortService portService, @Autowired RateService rateService) {
        Grid<Rate> grid = new Grid<>();
        grid.addColumn(rate -> rate.getPortOfLoading().getPortShortCode(), "pol").setHeader("POL")
                .setTooltipGenerator(rate -> rate.getPortOfLoading().getPortCity() +", "+ rate.getPortOfLoading().getPortCountry());
        grid.addColumn(rate -> rate.getPortOfDestination().getPortShortCode(), "pod").setHeader("POD")
                .setTooltipGenerator(rate -> rate.getPortOfDestination().getPortCity() +", "+ rate.getPortOfDestination().getPortCountry());

        grid.addColumn(Rate::getCommodity, "commodity").setHeader("Commodity");
        grid.addColumn(Rate::getTerm, "term").setHeader("Term").setSortable(false);
        grid.addColumn(Rate::getTwentyFtRate, "rate20").setHeader("20' Rate");
        grid.addColumn(Rate::getFortyFtRate, "rate40").setHeader("40' Rate");
        grid.addColumn(Rate::getTwentyFtRate, "rate40h").setHeader("40' HC Rate");
        grid.addColumn(Rate::getValidity, "validity").setHeader("Validity");
        grid.addColumn(Rate::getRemarks, "remarks").setHeader("Remarks").setSortable(false);
        GridListDataView<Rate> dataView = grid.setItems(rateService.getValidRates());

        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(rate -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty()) return true;

            return searchTermMatchesFilterFields(searchTerm, rate);
        });

        VerticalLayout layout = new VerticalLayout(searchField, grid);
        layout.setPadding(false);
        add(layout);
    }

    private boolean searchTermMatchesFilterFields(String searchTerm, Rate rate) {
        boolean matchesPortCity = rate.getPortOfDestination().getPortCity().toLowerCase().contains(searchTerm.toLowerCase())
                || rate.getPortOfLoading().getPortCity().toLowerCase().contains(searchTerm.toLowerCase());
        boolean matchesShortCode = rate.getPortOfDestination().getPortShortCode().toLowerCase().contains(searchTerm.toLowerCase())
                || rate.getPortOfLoading().getPortShortCode().toLowerCase().contains(searchTerm.toLowerCase());
        boolean matchesPortCountry = rate.getPortOfDestination().getPortCountry().toLowerCase().contains(searchTerm.toLowerCase())
                || rate.getPortOfLoading().getPortCountry().toLowerCase().contains(searchTerm.toLowerCase());
        boolean matchesCarrier = rate.getCarrier().toLowerCase().contains(searchTerm.toLowerCase());
        boolean matchesCommodity = rate.getCommodity().toLowerCase().contains(searchTerm.toLowerCase());
        return matchesPortCity || matchesShortCode || matchesPortCountry || matchesCommodity || matchesCarrier;
    }
}
