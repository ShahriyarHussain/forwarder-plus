package com.unison.ratemaster.Entity;

import com.unison.ratemaster.Util.Util;
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

    @OneToOne
    private Port portOfLoading;
    private String polVesselName;
    private LocalDate loadingPortEta;
    private LocalDate loadingPortEtd;
    private LocalDate mvPortFeederEta;
    @OneToOne
    private Port motherVesselPort;

    @OneToOne
    private Port portOfDestination;
    private LocalDate destinationPortEta;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Transshipment> transshipment;

    public String getScheduleSummary() {
        return this.getPortOfLoading().getPortName() + " to "
                + this.getPortOfDestination().getPortName() + " | ETD: "
                + Util.formatDateTime("dd/MM/yyyy", this.getLoadingPortEtd()) + ", ETA: "
                + Util.formatDateTime("dd/MM/yyyy", this.getDestinationPortEta());
    }
}
