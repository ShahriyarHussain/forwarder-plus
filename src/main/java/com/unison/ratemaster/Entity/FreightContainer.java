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
public class FreightContainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne
    private Booking booking;

    private String containerNo;
    private String sealNo;
    private BigDecimal grossWeight;
    private Integer noOfPackages;
    private PackageUnit packageUnit;
}
