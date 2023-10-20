package com.unison.ratemaster.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "BL_ENTRY")
@Getter
@Setter
public class BLEntries {

    @Id
    private String blNo;
    private String bookingNo;
    private String shipper;
    private String consignee;
    private String notifyParty;
    private String alsoNotifyParty;
    private String deliveryAgent;
    private String exportRef;
    private String placeOfReceipt;
    private String portOfLoading;
    private String placeOfDischarge;
    private String placeOfDelivery;
    private String vessel;
    private String containerSealNo;
    private String noOfPkgs;
    private String goodsDesc;
    private Long grossWeight;
    private Long measurement;
    private boolean prepaid;
    private boolean collect;
    private LocalDate cargoReceiptDate;
    private LocalDate ladenDate;
    private String prepaidAt;
    private String payableAt;
    private String noOfOriginalBl;
    private LocalDateTime entryDate;
    private String entryBy;
    private LocalDateTime editDate;
    private String editBy;
    @OneToOne
    private Shipment ShipmentId;
}
