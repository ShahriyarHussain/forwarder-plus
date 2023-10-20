package com.unison.ratemaster.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "TRANSSHIPMENT")
@Getter
@Setter
public class Transshipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate portArrival;
    private LocalDate portDeparture;
    @ManyToOne
    private Schedule schedule;
    @OneToOne
    private Port transshipmentPort;
}
