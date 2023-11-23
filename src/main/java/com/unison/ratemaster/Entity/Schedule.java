package com.unison.ratemaster.Entity;

import com.unison.ratemaster.Util.Util;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "SCHEDULE")
@Getter
@Setter
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String feederVesselName;

    @OneToOne
    private Port portOfLoading;
    private LocalDate loadingPortEta;
    private LocalDate loadingPortEtd;

    private String motherVesselName;

    @OneToOne
    private Port motherVesselPort;
    private LocalDate motherVesselPortEta;

    @OneToOne
    private Port tsPort;
    private LocalDate tsPortEta;

    @OneToOne
    private Port portOfDestination;
    private LocalDate destinationPortEta;

//    @OneToMany(cascade = CascadeType.REMOVE)
//    private Set<Transshipment> transshipment;

    public String getScheduleSummary() {
        return this.getPortOfLoading().getPortName() + " to "
                + this.getPortOfDestination().getPortName() + " | ETD: "
                + Util.formatDateTime("dd/MM/yyyy", this.getLoadingPortEtd()) + ", ETA: "
                + Util.formatDateTime("dd/MM/yyyy", this.getDestinationPortEta());
    }
}
