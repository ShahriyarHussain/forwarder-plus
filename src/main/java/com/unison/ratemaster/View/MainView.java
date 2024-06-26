package com.unison.ratemaster.View;

import com.unison.ratemaster.Enum.View;
import com.unison.ratemaster.View.Misc.MiscManagementView;
import com.unison.ratemaster.View.Other.StandaloneInvoiceView;
import com.unison.ratemaster.View.Rate.CreateRateView;
import com.unison.ratemaster.View.Rate.ShowRateView;
import com.unison.ratemaster.View.Shipment.CreateShipmentView;
import com.unison.ratemaster.View.Shipment.ShowShipmentView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;


@PageTitle("Main")
@Route(value = "home")

public class MainView extends AppLayout {

    public MainView() {
        createDrawer();
    }

    public void createDrawer() {
        DrawerToggle toggle = new DrawerToggle();

        H2 title = new H2("Unison Shipping Ltd.");
        Tabs tabs = getTabs();

        addToDrawer(tabs);
        addToNavbar(toggle, title);
    }

    private Tabs getTabs() {
        Tabs tabs = new Tabs();
        tabs.add(createTab(VaadinIcon.BOOK, View.CREATE_RATE),
                createTab(VaadinIcon.SEARCH, View.VIEW_RATE),
                createTab(VaadinIcon.PLUS, View.CREATE_SHIPMENT),
                createTab(VaadinIcon.GLOBE, View.VIEW_SHIPMENT),
                createTab(VaadinIcon.DATABASE, View.MISC_MANAGEMENT),
                createTab(VaadinIcon.BRIEFCASE, View.CREATE_INVOICE)
        );
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        return tabs;
    }

    private Tab createTab(VaadinIcon viewIcon, View view) {
        Icon icon = viewIcon.create();
        icon.getStyle().set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("margin-inline-start", "var(--lumo-space-xs)")
                .set("padding", "var(--lumo-space-xs)");

        RouterLink link = new RouterLink();
        link.add(icon, new Span(view.getViewName()));
        switch (view) {
            case CREATE_RATE:
                link.setRoute(CreateRateView.class);
                break;
            case VIEW_RATE:
                link.setRoute(ShowRateView.class);
                break;
            case CREATE_SHIPMENT:
                link.setRoute(CreateShipmentView.class);
                break;
            case VIEW_SHIPMENT:
                link.setRoute(ShowShipmentView.class);
                break;
            case MISC_MANAGEMENT:
                link.setRoute(MiscManagementView.class);
                break;
            case CREATE_INVOICE:
                link.setRoute(StandaloneInvoiceView.class);
                break;
//            case CREATE_BILL_OF_LADING:
//                link.setRoute(CreateBLView.class);
//                break;
            default:
                break;
        }
        link.setTabIndex(-1);
        return new Tab(link);
    }
}
