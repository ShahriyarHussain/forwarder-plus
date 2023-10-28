package com.unison.ratemaster.View.Client;

import com.unison.ratemaster.Entity.Client;
import com.unison.ratemaster.Enum.ClientType;
import com.unison.ratemaster.Service.ClientService;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "manage-party", layout = MainView.class)
public class ManagePartyView extends VerticalLayout {

    public ManagePartyView(@Autowired ClientService clientService) {
        H3 title = new H3("Manage Parties");

        FormLayout formLayout = new FormLayout();
        formLayout.setMaxWidth("50%");

        TextField partyName = new TextField("Name");
        TextArea address = new TextArea("Address");
        TextField city = new TextField("City");
        TextField country = new TextField("Country");
        TextField postCode = new TextField("Post/Zip Code");

        ComboBox<ClientType> partyType = new ComboBox<>("Party Type");
        partyType.setItems(ClientType.values());

        partyType.setItemLabelGenerator(ClientType::name);
        partyType.setRequired(true);
        partyType.setRequiredIndicatorVisible(true);


        Button addButton = new Button("Add", e -> {
            Client client = new Client();
            client.setName(partyName.getValue());
            client.setCity(city.getValue());
            client.setAddress(address.getValue());
            client.setCountry(country.getValue());
            client.setPostCode(postCode.getValue());

            clientService.saveClient(client);
            Util.getNotificationForSuccess("Client Added!").open();
        });

        formLayout.add(partyName, partyType, address, city, country, postCode);
        formLayout.setColspan(address, 2);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(title, formLayout, addButton);
    }
}
