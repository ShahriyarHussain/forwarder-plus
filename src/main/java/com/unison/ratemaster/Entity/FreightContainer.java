package com.unison.ratemaster.Entity;

import com.unison.ratemaster.Enum.PackageUnit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "FREIGHT_CONTAINERS")
@IdClass(FreightContainerId.class)
public class FreightContainer {
    @Id
    private Long bookingId;
    @Id
    private String containerNo;
    @Id
    private String sealNo;
    private BigDecimal grossWeight;
    private Integer noOfPackages;
    private PackageUnit packageUnit;
    @ManyToOne
    @JoinColumn(name = "bookingId", insertable = false, updatable = false)
    private Booking booking;
}
