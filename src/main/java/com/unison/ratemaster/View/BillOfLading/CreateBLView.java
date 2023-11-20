package com.unison.ratemaster.View.BillOfLading;

import com.unison.ratemaster.View.MainView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Route(value = "create-bl", layout = MainView.class)
public class CreateBLView extends VerticalLayout {

    @Value("${reports.folder}")
    private String reportFolder;

    public CreateBLView() throws JRException, FileNotFoundException {
        H3 pageTitle = new H3("Create Bill Of Lading");
        // Entry form section
        FormLayout formLayout = new FormLayout();

        TextField blNo = new TextField("B/L No.");
        TextField bookingNo = new TextField("Booking No.");
        TextField shipper = new TextField("Shipper");
        TextField consignee = new TextField("Consignee");
        TextField notifyParty = new TextField("Notify Party");
        TextField alsoNotifyParty = new TextField("Also Notify Party");
        TextField deliveryAgent = new TextField("Delivery Agent");
        TextField exportRef = new TextField("Export Reference");
        TextField placeOfReceipt = new TextField("Place Of Receipt");
        TextField placeOfDischarge = new TextField("Place Of Discharge");
        TextField placeOfDelivery = new TextField("Place of Delivery");
        TextField vessel = new TextField("Vessel/Voyage");
        TextField containerSealNo = new TextField("Container/Seal No");
        TextField noOfPkgs = new TextField("No Of Pkgs");
        TextArea goodsDesc = new TextArea("Goods Description");
        goodsDesc.setHeight(15, Unit.EM);
        goodsDesc.setMaxLength(500);
        goodsDesc.setValueChangeMode(ValueChangeMode.EAGER);
        goodsDesc.addValueChangeListener(e -> e.getSource().setHelperText(e.getValue().length() + "/" + 500));
        TextField prepaidAt = new TextField("Prepaid At");
        TextField payableAt = new TextField("Payable At");
        TextField noOfOriginalBl = new TextField("Original B/L No.");
        TextField cargoReceiptDate = new TextField("Cargo Receipt Date");;
        TextField ladenDate = new TextField("Bill Laden Date");

        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
        radioGroup.setLabel("Freight Term");
        radioGroup.setItems("Prepaid", "Collect");


        Anchor anchor = new Anchor(new StreamResource("bl_new.pdf", (InputStreamFactory) () -> {
            try (FileInputStream stream = new FileInputStream("Reports/Blank_A4.jasper");){
                final Map<String, Object> parameters = new HashMap<>();
                return new ByteArrayInputStream(JasperRunManager.runReportToPdf(stream, parameters));
            } catch (JRException | IOException e) {
                throw new RuntimeException(e);
            }
        }), "");
        anchor.getElement().setAttribute("download", true);
        anchor.add(new Button("Download"));


        formLayout.add(blNo, bookingNo, shipper, consignee, notifyParty, alsoNotifyParty, deliveryAgent, exportRef,
                placeOfReceipt, placeOfDischarge, placeOfDelivery, vessel, containerSealNo, noOfPkgs, prepaidAt, payableAt,
                noOfOriginalBl, cargoReceiptDate, ladenDate,  goodsDesc);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 5));
        formLayout.setColspan(goodsDesc,5);

        add(pageTitle, formLayout, anchor);
    }
}
