package com.unison.ratemaster.Entity;

import com.unison.ratemaster.Enum.ContainerType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "BOOKING")
public class Booking {
    @Id
    @NotEmpty
    @Length(max = 50)
    private String bookingNo;
    @Length(max = 50)
    private String invoiceNo;
    private Integer numOfContainers;
    private ContainerType containerType;
    @OneToMany(mappedBy = "bookingNo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<FreightContainer> container;
    private LocalDate stuffingDate;
    private String stuffingDepot;
    private BigDecimal stuffingCostPerContainer;
}
