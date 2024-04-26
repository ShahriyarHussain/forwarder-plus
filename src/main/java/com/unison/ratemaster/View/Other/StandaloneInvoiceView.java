package com.unison.ratemaster.View.Other;

import com.unison.ratemaster.Dto.InvoiceItemDto;
import com.unison.ratemaster.Entity.*;
import com.unison.ratemaster.Enum.AmountCurrency;
import com.unison.ratemaster.Service.BankDetailsService;
import com.unison.ratemaster.Service.ContactDetailsService;
import com.unison.ratemaster.Util.Util;
import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@PermitAll
@PageTitle("Create Standalone Invoice")
@Route(value = "create-standalone-invoice", layout = MainView.class)
public class StandaloneInvoiceView extends VerticalLayout {

    public StandaloneInvoiceView(@Autowired BankDetailsService bankDetailsService,
                                 @Autowired ContactDetailsService contactDetailsService) {

        List<InvoiceItem> invoiceItems = new LinkedList<>();
        HorizontalLayout horizontalLayout = new HorizontalLayout();

        H3 title = new H3("Create Standalone Invoice");

        ComboBox<AmountCurrency> foreignCurrComboBox = new ComboBox<>("Carrier Currency");
        foreignCurrComboBox.setItems(AmountCurrency.values());
        foreignCurrComboBox.setItemLabelGenerator(curr -> curr.toString() + " - " + curr.getCurrencyName());

        ComboBox<AmountCurrency> localCurrencyComboBox = new ComboBox<>("Local Currency");
        localCurrencyComboBox.setItems(AmountCurrency.values());
        localCurrencyComboBox.setItemLabelGenerator(curr -> curr.toString() + " - " + curr.getCurrencyName());

        Text inWords = new Text("Zero");

        BigDecimalField total = new BigDecimalField("Total", BigDecimal.ZERO, "");
        total.setReadOnly(true);

        BigDecimalField conversionRate = new BigDecimalField("Conversion Rate", "");

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
                InvoiceItem.calculateItemTotal(rate.getValue(), quantity.getValue(), foreignCurrency.getValue(), conversionRate.getValue()))
        );
        quantity.addValueChangeListener(e -> itemTotalInLocalCurr.setValue(
                InvoiceItem.calculateItemTotal(rate.getValue(), quantity.getValue(), foreignCurrency.getValue(), conversionRate.getValue()))
        );
        foreignCurrency.addValueChangeListener(e -> itemTotalInLocalCurr.setValue(
                InvoiceItem.calculateItemTotal(rate.getValue(), quantity.getValue(), foreignCurrency.getValue(), conversionRate.getValue()))
        );
        conversionRate.addValueChangeListener(e -> itemTotalInLocalCurr.setValue(
                InvoiceItem.calculateItemTotal(rate.getValue(), quantity.getValue(), foreignCurrency.getValue(), conversionRate.getValue())));

        Grid<InvoiceItem> invoiceItemGrid = new Grid<>();

        invoiceItemGrid.setItems(invoiceItems);
        invoiceItemGrid.addColumn(InvoiceItem::getDescription).setHeader("Description");
        invoiceItemGrid.addColumn(InvoiceItem::getRate).setHeader("Rate");
        invoiceItemGrid.addColumn(InvoiceItem::getQuantity).setHeader("Quantity");
        invoiceItemGrid.addColumn(InvoiceItem::getItemUnit).setHeader("Unit");
        Grid.Column<InvoiceItem> foreignCurrTotal = invoiceItemGrid.addColumn(InvoiceItem::getTotalInForeignCurr)
                .setHeader(InvoiceItem.getTotalColumnName(foreignCurrComboBox.getValue()));
        Grid.Column<InvoiceItem> localCurrTotal = invoiceItemGrid.addColumn(InvoiceItem::getTotalInLocalCurr)
                .setHeader(InvoiceItem.getTotalColumnName(localCurrencyComboBox.getValue()));
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
                inWords.setText(InvoiceItem.addSuffixToWordAmountByCurrency(localCurrencyComboBox.getValue(),
                        Util.getAmountInWords(total.getValue())));
            });
            return deleteButton;
        });
        invoiceItemGrid.setMaxHeight(17, Unit.EM);
        invoiceItemGrid.setVisible(!invoiceItems.isEmpty());
        invoiceItemGrid.setItems(invoiceItems);

        foreignCurrComboBox.addValueChangeListener(event -> {
            foreignCurrTotal.setHeader(InvoiceItem.getTotalColumnName(event.getValue()));
            conversionRate.setValue(BigDecimal.ONE);
        });
        localCurrencyComboBox.addValueChangeListener(event -> {
            localCurrTotal.setHeader(InvoiceItem.getTotalColumnName(event.getValue()));
            conversionRate.setValue(BigDecimal.ONE);
        });

        ComboBox<BankDetails> bankDetailsComboBox = new ComboBox<>("Choose Bank Details");
        bankDetailsComboBox.setItems(bankDetailsService.getAllBankDetails());
        bankDetailsComboBox.setItemLabelGenerator(bankDetails -> bankDetails.getAccName() + " - " + bankDetails.getBankName()
                + " - " + bankDetails.getBranchName() + " - " + bankDetails.getAccNo());

        ComboBox<ContactDetails> contactDetailsComboBox = new ComboBox<>("Choose Contact Details");
        contactDetailsComboBox.setItems(contactDetailsService.getAllContactDetails());
        contactDetailsComboBox.setItemLabelGenerator(ContactDetails::getName);

        Button createDetailsButton = new Button("Add", new Icon(VaadinIcon.PLUS));
        createDetailsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createDetailsButton.addClickListener(event -> {
            try {
                InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setRate(rate.getValue());
                invoiceItem.setQuantity(quantity.getValue());
                invoiceItem.setDescription(description.getValue());
                invoiceItem.setItemUnit(itemUnit.getValue());
                invoiceItem.setForeignCurr(foreignCurrency.getValue());

                invoiceItem.setCurrency(foreignCurrency.getValue() ?
                        foreignCurrComboBox.getValue().toString() : localCurrencyComboBox.getValue().toString());
                invoiceItem.setTotalInForeignCurr(
                        InvoiceItem.calculateItemTotal(rate.getValue(), quantity.getValue(), false, BigDecimal.ONE));

                invoiceItem.setTotalInLocalCurr(itemTotalInLocalCurr.getValue());

                invoiceItems.add(invoiceItem);
                invoiceItemGrid.setItems(invoiceItems);
                invoiceItemGrid.setVisible(true);
                total.setValue(invoiceItems.stream()
                        .map(InvoiceItem::getTotalInLocalCurr)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                inWords.setText(InvoiceItem.addSuffixToWordAmountByCurrency(localCurrencyComboBox.getValue(),
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
        Anchor anchor = new Anchor(new StreamResource("Invoice-" + Util.formatDateTime("dd-MM-yyyy", LocalDate.now())
                + ".pdf", (InputStreamFactory) () -> {

            List<InvoiceItemDto> invoiceItemDtos = new LinkedList<>();
            invoiceItemDtos.add(new InvoiceItemDto()); // প্রথম খালি ভ্যালুটি MASTER_REPORT এর জন্য
            invoiceItemDtos.addAll(invoiceItems.stream()
                    .sorted(Comparator.comparing(InvoiceItem::getTotalInLocalCurr).reversed())
                    .map(InvoiceItemDto::new)
                    .collect(Collectors.toList()));

            for (int i = 0; i < invoiceItemDtos.size(); i++) {
                invoiceItemDtos.get(i).setSlNo(i);
                InvoiceItemDto item = invoiceItemDtos.get(i);
                if (item.getSubtotal() != null && !item.getSubtotal().isEmpty()) {
                    item.setSubtotal(localCurrencyComboBox.getValue().toString() + " " + item.getSubtotal());
                }
            }

            String reportName = "Invoice_standalone.jasper";

            final Map<String, Object> parameters = new HashMap<>();

            parameters.put("LOGO_URL", Util.imagePath);

            parameters.put("FOREIGN_CURRENCY", foreignCurrComboBox.getValue().toString());
            parameters.put("LOCAL_CURRENCY", localCurrencyComboBox.getValue().toString());
            parameters.put("CONVERSION_RATE", Util.getFormattedBigDecimal(
                    conversionRate.getValue().setScale(2, RoundingMode.UNNECESSARY)));

            BigDecimal grandTotal = invoiceItems.stream().map(InvoiceItem::getTotalInLocalCurr).reduce(BigDecimal.ZERO, BigDecimal::add);
            parameters.put("TOTAL", Util.getFormattedBigDecimal(grandTotal.setScale(1, RoundingMode.UNNECESSARY)));
            parameters.put("TOTAL_IN_WORD", InvoiceItem.addSuffixToWordAmountByCurrency(
                    localCurrencyComboBox.getValue(), Util.getAmountInWords(grandTotal)));

            BankDetails bankDetails = bankDetailsComboBox.getValue();
            parameters.put("BANK_NAME", bankDetails.getBankName());
            parameters.put("AC_NAME", bankDetails.getAccName());
            parameters.put("AC_NO", bankDetails.getAccNo());
            parameters.put("ROUTING_NO", bankDetails.getRoutingNo());
            parameters.put("BRANCH", bankDetails.getBranchName());

            ContactDetails contactDetails = contactDetailsComboBox.getValue();
            parameters.put("SIGNED_BY", contactDetails.getName());
            parameters.put("SIGNED_BY_EMAIL", contactDetails.getEmail());
            parameters.put("SIGNED_BY_CONTACT", contactDetails.getContactNo());

            JRDataSource dataSource = new JRBeanCollectionDataSource(invoiceItemDtos);
            parameters.put("COLLECTION_LIST", dataSource);

            try (InputStream stream = getClass().getResourceAsStream((Util.REPORTS_PATH + reportName))) {
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
        invoiceForm.add(foreignCurrComboBox, localCurrencyComboBox, conversionRate, gap0, line1, gap0,
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

        add(title, invoiceForm, contactForm, anchor);
    }

}
