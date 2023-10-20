package com.unison.ratemaster.Entity;

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
    private LocalDate portCutOff;
    private LocalDate vgmCutOff;
    private LocalDate loadingPortETD;
    private LocalDate destinationPortETA;
    @OneToOne
    private Port portOfLoading;
    @OneToOne
    private Port portOfDestination;
    @OneToMany(cascade = CascadeType.REMOVE)
    private Set<Transshipment> transshipment;
}
