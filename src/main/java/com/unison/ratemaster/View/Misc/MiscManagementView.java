package com.unison.ratemaster.View.Misc;


import com.unison.ratemaster.Entity.Carrier;
import com.unison.ratemaster.Entity.Client;
import com.unison.ratemaster.Entity.Commodity;
import com.unison.ratemaster.Entity.Port;
import com.unison.ratemaster.Enum.ClientType;
import com.unison.ratemaster.Service.CarrierService;
import com.unison.ratemaster.Service.ClientService;
import com.unison.ratemaster.Service.CommodityService;
import com.unison.ratemaster.Service.PortService;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PageTitle("Other Management")
@Route(value = "other-management", layout = MainView.class)
public class MiscManagementView extends VerticalLayout {

    public MiscManagementView(@Autowired PortService portService,
                              @Autowired CommodityService commodityService,
                              @Autowired CarrierService carrierService,
                              @Autowired ClientService clientService) {

        H2 title = new H2("Ports, Commodity, Carrier Management");
        HorizontalLayout hlayout = new HorizontalLayout();

        ComboBox<String> chooseType = new ComboBox<>();
        chooseType.setItems(List.of("Carriers", "Ports", "Commodities", "Clients"));

        Button viewButton = new Button("View Data");
        viewButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        Button editButton = new Button("Add Data");
        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        hlayout.add(chooseType, viewButton, editButton);

        H4 carrierTitle = new H4("Carriers");
        carrierTitle.setVisible(false);
        Grid<Carrier> carrierGrid = new Grid<>();
        carrierGrid.setItems(carrierService.getAllCarriers());
        carrierGrid.addColumn(Carrier::getName).setHeader("Name").setAutoWidth(true).setSortable(true);
        carrierGrid.addColumn(Carrier::getCountry).setHeader("Country").setAutoWidth(true);
        carrierGrid.setVisible(false);

        H4 portTitle = new H4("Ports");
        portTitle.setVisible(false);
        Grid<Port> portGrid = new Grid<>();
        portGrid.setItems(portService.getPorts());
        portGrid.addColumn(Port::getPortName).setHeader("Name").setAutoWidth(true).setSortable(true);
        portGrid.addColumn(Port::getPortCountry).setHeader("Country").setAutoWidth(true);
        portGrid.addColumn(Port::getPortShortCode).setHeader("Short Code").setAutoWidth(true).setSortable(true);
        portGrid.setVisible(false);

        H4 commodityTitle = new H4("Commodities");
        commodityTitle.setVisible(false);
        Grid<Commodity> commodityGrid = new Grid<>();
        commodityGrid.setItems(commodityService.getAllCommodity());
        commodityGrid.addColumn(Commodity::getHscode).setHeader("HSCode").setSortable(true).setAutoWidth(true);
        commodityGrid.addColumn(Commodity::getName).setHeader("Name").setSortable(true).setAutoWidth(true);
        commodityGrid.addColumn(Commodity::getDescription).setHeader("Description").setSortable(true);
        commodityGrid.addColumn(commodity -> commodity.isDangerousGoods() ? "Yes" : "No").setHeader("DG").setAutoWidth(true);
        commodityGrid.setVisible(false);

        H4 clientTitle = new H4("Clients");
        clientTitle.setVisible(false);
        Grid<Client> clientGrid = new Grid<>();
        clientGrid.setItems(clientService.getAllClients());
        clientGrid.addColumn(Client::getName).setHeader("Name").setAutoWidth(true);
        clientGrid.addColumn(Client::getType).setHeader("Type");
        clientGrid.addColumn(Client::getCountry).setHeader("Country");
        clientGrid.addComponentColumn(client -> {
            Button button = new Button(new Icon(VaadinIcon.TRASH), event -> {
                clientService.deleteClient(client);
                clientGrid.setItems(clientService.getAllClients());
            });
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return button;
        }).setHeader("Delete");
        clientGrid.setVisible(false);

        viewButton.addClickListener(event -> {
            if (chooseType.getValue().equals("Carriers")) {
                carrierTitle.setVisible(true);
                carrierGrid.setVisible(true);
                carrierGrid.setItems(carrierService.getAllCarriers());
                portTitle.setVisible(false);
                portGrid.setVisible(false);
                commodityTitle.setVisible(false);
                commodityGrid.setVisible(false);
                clientTitle.setVisible(false);
                clientGrid.setVisible(false);
                editButton.setText("Add Carrier");
            } else if (chooseType.getValue().equals("Ports")) {
                carrierTitle.setVisible(false);
                carrierGrid.setVisible(false);
                portTitle.setVisible(true);
                portGrid.setVisible(true);
                commodityTitle.setVisible(false);
                commodityGrid.setVisible(false);
                clientTitle.setVisible(false);
                clientGrid.setVisible(false);
                editButton.setText("Add Port");
            } else if (chooseType.getValue().equals("Commodities")) {
                carrierTitle.setVisible(false);
                carrierGrid.setVisible(false);
                portTitle.setVisible(false);
                portGrid.setVisible(false);
                commodityTitle.setVisible(true);
                commodityGrid.setVisible(true);
                clientTitle.setVisible(false);
                clientGrid.setVisible(false);
                editButton.setText("Add Commodity");
            } else if (chooseType.getValue().equals("Clients")) {
                carrierTitle.setVisible(false);
                carrierGrid.setVisible(false);
                portTitle.setVisible(false);
                portGrid.setVisible(false);
                commodityTitle.setVisible(false);
                commodityGrid.setVisible(false);
                clientTitle.setVisible(true);
                clientGrid.setVisible(true);
                editButton.setText("Add Clients");
            } else {
                carrierTitle.setVisible(false);
                carrierGrid.setVisible(false);
                portTitle.setVisible(false);
                portGrid.setVisible(false);
                commodityTitle.setVisible(false);
                commodityGrid.setVisible(false);
                clientTitle.setVisible(false);
                clientGrid.setVisible(false);
                editButton.setText("Add Data");
            }
        });

        editButton.addClickListener(event -> {
            if (chooseType.getValue().equals("Carriers")) {
                openCarrierDialogBox(carrierService).open();
            } else if (chooseType.getValue().equals("Ports")) {
                openPortsDialogBox(portService).open();
            } else if (chooseType.getValue().equals("Commodities")) {
                openCommoditiesDialogBox(commodityService).open();
            } else if (chooseType.getValue().equals("Clients")) {
                openClientsDialogBox(clientService).open();
            }
        });

        add(title, hlayout, carrierTitle, carrierGrid, portTitle, portGrid, commodityTitle, commodityGrid, clientTitle, clientGrid);

    }

    private Dialog openClientsDialogBox(ClientService clientService) {
        Dialog dialog = new Dialog();
        H3 title = new H3("Add Client");

        FormLayout formLayout = new FormLayout();

        TextField partyName = new TextField("Name");
        TextArea address = new TextArea("Address");
        TextField city = new TextField("City");
        TextField country = new TextField("Country");
        TextField taxId = new TextField("Country");
        TextField postCode = new TextField("Post/Zip Code");
        TextField email = new TextField("Email");

        ComboBox<ClientType> partyType = new ComboBox<>("Party Type");
        partyType.setItems(ClientType.values());

        partyType.setItemLabelGenerator(ClientType::name);
        partyType.setRequired(true);
        partyType.setRequiredIndicatorVisible(true);

        Button addButton = new Button("Add", e -> {
            Client client = new Client();
            client.setName(partyName.getValue());
            client.setType(partyType.getValue());
            client.setCity(city.getValue());
            client.setAddress(address.getValue());
            client.setCountry(country.getValue());
            client.setPostCode(postCode.getValue());
            client.setTaxId(taxId.getValue());
            client.setEmail(email.getValue());
            clientService.saveClient(client);
            Util.getNotificationForSuccess("Client Added!").open();
        });

        formLayout.add(partyName, partyType, address, city, country, postCode, email);
        formLayout.setColspan(address, 2);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(title, formLayout);
        dialog.getFooter().add(addButton);
        dialog.getFooter().add(new Button("Close", event -> dialog.close()));
        return dialog;
    }

    private Dialog openCommoditiesDialogBox(CommodityService commodityService) {
        Dialog dialog = new Dialog();
        H3 title = new H3("Add Commodity");

        FormLayout formLayout = new FormLayout();

        TextField commodityName = new TextField("Name");
        TextField hsCode = new TextField("HSCode");
        Checkbox isDangerousGoods = new Checkbox("Is Dangerous Goods");


        Button addButton = new Button("Add", e -> {
            Commodity commodity = new Commodity();
            commodity.setName(commodityName.getValue());
            commodity.setHscode(hsCode.getValue());
            commodity.setDangerousGoods(isDangerousGoods.isEnabled());

            commodityService.saveCommodity(commodity);
            Util.getNotificationForSuccess("Commodity Added!").open();
        });

        formLayout.add(commodityName, hsCode, isDangerousGoods);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(title, formLayout);
        dialog.getFooter().add(addButton);
        dialog.getFooter().add(new Button("Close", event -> dialog.close()));
        return dialog;
    }

    private Dialog openPortsDialogBox(PortService portService) {
        Dialog dialog = new Dialog();
        H3 title = new H3("Add Port");

        FormLayout formLayout = new FormLayout();

        Label label = new Label("Go to https://photius.com/seaports/?ref=dizinler.com to find short codes for your desired ports");

        TextField portShortCode = new TextField("Port Short Code (Must Be Unique)");
        TextField portName = new TextField("Port Name");
        TextField portCountry = new TextField("Port Country");
        TextField portCity = new TextField("Port City");


        Button addButton = new Button("Add", e -> {
            Port port = new Port();
            port.setPortName(portName.getValue());
            port.setPortShortCode(portShortCode.getValue());
            port.setPortCountry(portCountry.getValue());
            port.setPortCity(portCity.getValue());

            portService.savePort(port);
            Util.getNotificationForSuccess("Port Added!").open();
        });

        formLayout.add(label, portShortCode, portName, portCountry, portCity);
        formLayout.setColspan(label, 2);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(title, formLayout);
        dialog.getFooter().add(addButton);
        dialog.getFooter().add(new Button("Close", event -> dialog.close()));
        return dialog;
    }

    private Dialog openCarrierDialogBox(CarrierService carrierService) {
        Dialog dialog = new Dialog();
        H3 title = new H3("Add Shipping Line/Carrier");

        FormLayout formLayout = new FormLayout();

        TextField carrierName = new TextField("Carrier Name");
        TextField carrierCountry = new TextField("Carrier Country");


        Button addButton = new Button("Add", e -> {
            Carrier carrier = new Carrier();
            carrier.setName(carrierName.getValue());
            carrier.setCountry(carrierCountry.getValue());

            carrierService.saveCarrier(carrier);
            Util.getNotificationForSuccess("Carrier Added!").open();
        });

        formLayout.add(carrierName, carrierCountry);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(title, formLayout);
        dialog.getFooter().add(addButton);
        dialog.getFooter().add(new Button("Close", event -> dialog.close()));
        return dialog;
    }
}
