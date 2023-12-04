package com.unison.ratemaster.View.Client;

import com.unison.ratemaster.Entity.Client;
import com.unison.ratemaster.Enum.ClientType;
import com.unison.ratemaster.Service.ClientService;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "manage-party", layout = MainView.class)
public class ManagePartyView extends VerticalLayout {

    public ManagePartyView(@Autowired ClientService clientService) {
        H2 title = new H2("Manage Parties");

        FormLayout formLayout = new FormLayout();
        formLayout.setMaxWidth("50%");

        TextField partyName = new TextField("Name");
        TextArea address = new TextArea("Address");
        TextField city = new TextField("City");
        TextField country = new TextField("Country");
        TextField taxId = new TextField("Country");
        TextField postCode = new TextField("Post/Zip Code");

        ComboBox<ClientType> partyType = new ComboBox<>("Party Type");
        partyType.setItems(ClientType.values());

        partyType.setItemLabelGenerator(ClientType::name);
        partyType.setRequired(true);
        partyType.setRequiredIndicatorVisible(true);

        Grid<Client> clientGrid = new Grid<>();
        clientGrid.addColumn(Client::getName).setHeader("Name").setAutoWidth(true);
        clientGrid.addColumn(Client::getType).setHeader("Type");
        clientGrid.addColumn(Client::getCountry).setHeader("Country");
        clientGrid.addColumn(Client::getTaxId).setHeader("Tax ID");
        clientGrid.addComponentColumn(client -> {
            Button button = new Button(new Icon(VaadinIcon.TRASH), event -> {
                clientService.deleteClient(client);
                clientGrid.setItems(clientService.getAllClients());
            });
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return button;
        }).setHeader("Delete");
        clientGrid.setMaxHeight(20, Unit.EM);
        clientGrid.setItems(clientService.getAllClients());


        Button addButton = new Button("Add", e -> {
            Client client = new Client();
            client.setName(partyName.getValue());
            client.setType(partyType.getValue());
            client.setCity(city.getValue());
            client.setAddress(address.getValue());
            client.setCountry(country.getValue());
            client.setPostCode(postCode.getValue());
            client.setTaxId(taxId.getValue());

            clientService.saveClient(client);
            Util.getNotificationForSuccess("Client Added!").open();
            clientGrid.setItems(clientService.getAllClients());
        });

        formLayout.add(partyName, partyType, address, city, country, postCode);
        formLayout.setColspan(address, 2);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(title, formLayout, addButton, clientGrid);
    }
}
