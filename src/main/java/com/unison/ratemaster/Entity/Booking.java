package com.unison.ratemaster.Entity;

import com.unison.ratemaster.Enum.ContainerSize;
import com.unison.ratemaster.Enum.ContainerType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "BOOKING")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;
    private String bookingNo;
    private String invoiceNo;
    private Integer numOfContainers;
    private ContainerType containerType;
    private ContainerSize containerSize;
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<FreightContainer> container;
    private LocalDate stuffingDate;
    private String stuffingDepot;
    private BigDecimal stuffingCostPerContainer;
    private LocalDateTime enteredOn;
}
