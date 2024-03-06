package com.unison.ratemaster.View.Misc;


import com.unison.ratemaster.Entity.*;
import com.unison.ratemaster.Enum.ClientType;
import com.unison.ratemaster.Service.*;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.Unit;
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

import javax.annotation.security.PermitAll;
import java.util.List;
import java.util.Objects;

@PermitAll
@PageTitle("Other Management")
@Route(value = "other-management", layout = MainView.class)
public class MiscManagementView extends VerticalLayout {

    public MiscManagementView(@Autowired PortService portService,
                              @Autowired CommodityService commodityService,
                              @Autowired CarrierService carrierService,
                              @Autowired ClientService clientService,
                              @Autowired BankDetailsService bankDetailsService,
                              @Autowired ContactDetailsService contactDetailsService) {

        H2 title = new H2("Ports, Commodity, Carrier, Banks and Contact Management");
        HorizontalLayout hlayout = new HorizontalLayout();

        ComboBox<String> chooseType = new ComboBox<>();
        chooseType.setItems(List.of("Carriers", "Ports", "Commodities", "Clients", "Banking", "Contact"));

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
        carrierGrid.addComponentColumn(carrier -> {
            Button button = new Button(new Icon(VaadinIcon.TRASH), event -> {
                carrierService.deleteCarrier(carrier);
                carrierGrid.setItems(carrierService.getAllCarriers());
            });
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return button;
        }).setHeader("Delete");
        carrierGrid.setVisible(false);

        H4 portTitle = new H4("Ports");
        portTitle.setVisible(false);
        Grid<Port> portGrid = new Grid<>();
        portGrid.setItems(portService.getPorts());
        portGrid.addColumn(Port::getPortName).setHeader("Name").setAutoWidth(true).setSortable(true);
        portGrid.addColumn(Port::getPortCountry).setHeader("Country").setAutoWidth(true);
        portGrid.addColumn(Port::getPortShortCode).setHeader("Short Code").setAutoWidth(true).setSortable(true);
        portGrid.addComponentColumn(port -> {
            Button button = new Button(new Icon(VaadinIcon.TRASH), event -> {
                portService.deletePort(port);
                portGrid.setItems(portService.getPorts());
            });
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return button;
        }).setHeader("Delete");
        portGrid.setVisible(false);

        H4 commodityTitle = new H4("Commodities");
        commodityTitle.setVisible(false);
        Grid<Commodity> commodityGrid = new Grid<>();
        commodityGrid.setItems(commodityService.getAllCommodity());
        commodityGrid.addColumn(Commodity::getHscode).setHeader("HSCode").setSortable(true).setAutoWidth(true);
        commodityGrid.addColumn(Commodity::getName).setHeader("Name").setSortable(true).setAutoWidth(true);
        commodityGrid.addColumn(Commodity::getDescription).setHeader("Description").setSortable(true);
        commodityGrid.addColumn(commodity -> commodity.isDangerousGoods() ? "Yes" : "No").setHeader("DG").setAutoWidth(true);
        commodityGrid.addComponentColumn(commodity -> {
            Button button = new Button(new Icon(VaadinIcon.TRASH), event -> {
                commodityService.deleteCommodity(commodity);
                commodityGrid.setItems(commodityService.getAllCommodity());
            });
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return button;
        }).setHeader("Delete");
        commodityGrid.setVisible(false);

        H4 clientTitle = new H4("Clients");
        clientTitle.setVisible(false);
        Grid<Client> clientGrid = new Grid<>();
        clientGrid.setHeight(30, Unit.EM);
        clientGrid.setItems(clientService.getAllClients());
        clientGrid.addColumn(Client::getName).setHeader("Name").setAutoWidth(true);
        clientGrid.addColumn(Client::getType).setHeader("Type");
        clientGrid.addColumn(Client::getCountry).setHeader("Country");
        clientGrid.addComponentColumn(client -> {
            Button button = new Button(new Icon(VaadinIcon.PENCIL), event -> {
                openClientsDialogBox(clientService, client).open();
            });
            button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return button;
        }).setHeader("Edit");
        clientGrid.addComponentColumn(client -> {
            Button button = new Button(new Icon(VaadinIcon.TRASH), event -> {
                clientService.deleteClient(client);
                clientGrid.setItems(clientService.getAllClients());
            });
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return button;
        }).setHeader("Delete");
        clientGrid.setVisible(false);

        H4 bankTitle = new H4("Bank Details");
        bankTitle.setVisible(false);
        Grid<BankDetails> bankDetailsGrid = new Grid<>();
        bankDetailsGrid.setItems(bankDetailsService.getAllBankDetails());
        bankDetailsGrid.addColumn(BankDetails::getBankName).setHeader("Bank").setAutoWidth(true);
        bankDetailsGrid.addColumn(BankDetails::getAccName).setHeader("Acc Name").setTooltipGenerator(BankDetails::getAccName);
        bankDetailsGrid.addColumn(BankDetails::getAccNo).setHeader("Acc No").setAutoWidth(true);
        bankDetailsGrid.addColumn(BankDetails::getRoutingNo).setHeader("Routing No").setAutoWidth(true);
        bankDetailsGrid.addColumn(BankDetails::getBranchName).setHeader("Branch").setTooltipGenerator(BankDetails::getBranchName);
        bankDetailsGrid.addComponentColumn(bankDetails -> {
            Button button = new Button(new Icon(VaadinIcon.TRASH), event -> {
                bankDetailsService.deleteBankDetails(bankDetails);
                bankDetailsGrid.setItems(bankDetailsService.getAllBankDetails());
            });
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return button;
        }).setHeader("Delete");
        bankDetailsGrid.setVisible(false);

        H4 contactTitle = new H4("Contact Details");
        contactTitle.setVisible(false);
        Grid<ContactDetails> contactDetailsGrid = new Grid<>();
        contactDetailsGrid.setItems(contactDetailsService.getAllContactDetails());
        contactDetailsGrid.addColumn(ContactDetails::getName).setHeader("Contact").setAutoWidth(true);
        contactDetailsGrid.addColumn(ContactDetails::getEmail).setHeader("Email").setTooltipGenerator(ContactDetails::getEmail);
        contactDetailsGrid.addColumn(ContactDetails::getContactNo).setHeader("Acc No").setAutoWidth(true);
        contactDetailsGrid.addColumn(ContactDetails::getRemarks).setHeader("Branch").setTooltipGenerator(ContactDetails::getRemarks);
        contactDetailsGrid.addComponentColumn(contactDetails -> {
            Button button = new Button(new Icon(VaadinIcon.TRASH), event -> {
                contactDetailsService.deleteContact(contactDetails);
                contactDetailsGrid.setItems(contactDetailsService.getAllContactDetails());
            });
            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return button;
        }).setHeader("Delete");
        contactDetailsGrid.setVisible(false);

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
                bankTitle.setVisible(false);
                bankDetailsGrid.setVisible(false);
                contactTitle.setVisible(false);
                contactDetailsGrid.setVisible(false);
                editButton.setText("Add Carrier");
            } else if (chooseType.getValue().equals("Ports")) {
                carrierTitle.setVisible(false);
                carrierGrid.setVisible(false);
                portTitle.setVisible(true);
                portGrid.setVisible(true);
                portGrid.setItems(portService.getPorts());
                commodityTitle.setVisible(false);
                commodityGrid.setVisible(false);
                clientTitle.setVisible(false);
                clientGrid.setVisible(false);
                bankTitle.setVisible(false);
                bankDetailsGrid.setVisible(false);
                contactTitle.setVisible(false);
                contactDetailsGrid.setVisible(false);
                editButton.setText("Add Port");
            } else if (chooseType.getValue().equals("Commodities")) {
                carrierTitle.setVisible(false);
                carrierGrid.setVisible(false);
                portTitle.setVisible(false);
                portGrid.setVisible(false);
                commodityTitle.setVisible(true);
                commodityGrid.setVisible(true);
                commodityGrid.setItems(commodityService.getAllCommodity());
                clientTitle.setVisible(false);
                clientGrid.setVisible(false);
                bankTitle.setVisible(false);
                bankDetailsGrid.setVisible(false);
                contactTitle.setVisible(false);
                contactDetailsGrid.setVisible(false);
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
                clientGrid.setItems(clientService.getAllClients());
                bankTitle.setVisible(false);
                bankDetailsGrid.setVisible(false);
                contactTitle.setVisible(false);
                contactDetailsGrid.setVisible(false);
                editButton.setText("Add Clients");
            } else if (chooseType.getValue().equals("Banking")) {
                carrierTitle.setVisible(false);
                carrierGrid.setVisible(false);
                portTitle.setVisible(false);
                portGrid.setVisible(false);
                commodityTitle.setVisible(false);
                commodityGrid.setVisible(false);
                clientTitle.setVisible(false);
                clientGrid.setVisible(false);
                bankTitle.setVisible(true);
                bankDetailsGrid.setVisible(true);
                bankDetailsGrid.setItems(bankDetailsService.getAllBankDetails());
                contactTitle.setVisible(false);
                contactDetailsGrid.setVisible(false);
                editButton.setText("Add Bank Details");
            } else if (chooseType.getValue().equals("Contact")) {
                carrierTitle.setVisible(false);
                carrierGrid.setVisible(false);
                portTitle.setVisible(false);
                portGrid.setVisible(false);
                commodityTitle.setVisible(false);
                commodityGrid.setVisible(false);
                clientTitle.setVisible(false);
                clientGrid.setVisible(false);
                bankTitle.setVisible(false);
                bankDetailsGrid.setVisible(false);
                contactTitle.setVisible(true);
                contactDetailsGrid.setVisible(true);
                contactDetailsGrid.setItems(contactDetailsService.getAllContactDetails());
                editButton.setText("Add Contact Details");
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
                openClientsDialogBox(clientService, null).open();
            } else if (chooseType.getValue().equals("Banking")) {
                openBankDetailsDialog(bankDetailsService).open();
            } else if (chooseType.getValue().equals("Contact")) {
                openContactDetailsDialog(contactDetailsService).open();
            }
        });

        add(title, hlayout, carrierTitle, carrierGrid, portTitle, portGrid, commodityTitle, commodityGrid,
                clientTitle, clientGrid, bankTitle, bankDetailsGrid, contactTitle, contactDetailsGrid);

    }

    private Dialog openClientsDialogBox(ClientService clientService, Client client) {
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

        if (client != null) {
            partyName.setValue(client.getName());
            address.setValue(client.getAddress());
            city.setValue(client.getCity());
            country.setValue(client.getCountry());
            taxId.setValue(client.getTaxId());
            postCode.setValue(client.getPostCode());
            email.setValue(client.getEmail());
        }

        ComboBox<ClientType> partyType = new ComboBox<>("Party Type");
        partyType.setItems(ClientType.values());

        partyType.setItemLabelGenerator(ClientType::name);
        partyType.setRequired(true);
        partyType.setRequiredIndicatorVisible(true);

        String buttonLabel = client == null ? "Add" : "Save Changes";

        Button addButton = new Button(buttonLabel, e -> {
            Client newClient = Objects.requireNonNullElseGet(client, Client::new);

            newClient.setName(partyName.getValue());
            newClient.setType(partyType.getValue());
            newClient.setCity(city.getValue());
            newClient.setAddress(address.getValue());
            newClient.setCountry(country.getValue());
            newClient.setPostCode(postCode.getValue());
            newClient.setTaxId(taxId.getValue());
            newClient.setEmail(email.getValue());
            try {
                clientService.saveClient(newClient);
                Util.getNotificationForSuccess("Client Saved!").open();
            } catch (Exception ex) {
                ex.printStackTrace();
                Util.getNotificationForError("Error! " + ex.getMessage()).open();
            }
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

    private Dialog openBankDetailsDialog(BankDetailsService bankDetailsService) {
        Dialog dialog = new Dialog();
        H3 title = new H3("Add Banking Details");

        FormLayout formLayout = new FormLayout();

        TextField bankName = new TextField("Bank Name");
        TextField acNo = new TextField("A/C No");
        TextField acName = new TextField("A/C Name");
        TextField routingNo = new TextField("Routing No");
        TextField branchName = new TextField("Branch Name");


        Button addButton = new Button("Add", e -> {
            BankDetails bankDetails = new BankDetails();
            bankDetails.setBankName(bankName.getValue());
            bankDetails.setAccNo(acNo.getValue());
            bankDetails.setAccName(acName.getValue());
            bankDetails.setRoutingNo(routingNo.getValue());
            bankDetails.setBranchName(branchName.getValue());

            bankDetailsService.saveBankDetails(bankDetails);
            Util.getNotificationForSuccess("Bank Details Added!").open();
        });

        formLayout.add(bankName, acNo, acName, routingNo, branchName);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(title, formLayout);
        dialog.getFooter().add(addButton);
        dialog.getFooter().add(new Button("Close", event -> dialog.close()));
        return dialog;
    }

    private Dialog openContactDetailsDialog(ContactDetailsService contactDetailsService) {
        Dialog dialog = new Dialog();
        H3 title = new H3("Add Contact Details");

        FormLayout formLayout = new FormLayout();

        TextField contactName = new TextField("Contact Name");
        TextField contactNo = new TextField("Contact No");
        TextField email = new TextField("Contact Email");
        TextField remarks = new TextField("Remarks");


        Button addButton = new Button("Add", e -> {
            ContactDetails contactDetails = new ContactDetails();
            contactDetails.setName(contactName.getValue());
            contactDetails.setContactNo(contactNo.getValue());
            contactDetails.setEmail(email.getValue());
            contactDetails.setRemarks(remarks.getValue());

            contactDetailsService.saveContactDetails(contactDetails);
            Util.getNotificationForSuccess("Contact Details Added!").open();
        });

        formLayout.add(contactName, contactNo, email, remarks);
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(title, formLayout);
        dialog.getFooter().add(addButton);
        dialog.getFooter().add(new Button("Close", event -> dialog.close()));
        return dialog;
    }
}
