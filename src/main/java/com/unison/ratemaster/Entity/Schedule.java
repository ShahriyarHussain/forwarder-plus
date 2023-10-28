package com.unison.ratemaster.Entity;

import com.unison.ratemaster.Enum.VesselType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "SCHEDULE")
@Getter
@Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String vesselName;
    private VesselType vesselType;
    private LocalDate portCutOff;
    private LocalDate vgmCutOff;
    private LocalDate loadingPortEtd;
    private LocalDate destinationPortEta;
    @OneToOne
    private Port portOfLoading;
    @OneToOne
    private Port portOfDestination;
    @OneToMany(cascade = CascadeType.REMOVE)
    private Set<Transshipment> transshipment;
}
