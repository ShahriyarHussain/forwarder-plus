package com.unison.ratemaster.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "FREIGHT_CONTAINERS")
@IdClass(FreightContainerId.class)
public class FreightContainer {
    @Id
    private String bookingNo;
    @Id
    private String containerNo;
    @Id
    private String sealNo;
}
