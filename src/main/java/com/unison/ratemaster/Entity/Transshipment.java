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
    private String vesselName;
    private LocalDate portArrival;
    @OneToOne
    private Port port;
//    @ManyToOne
//    private Schedule schedule;
//    @OneToOne
//    private Port transshipmentPort;
}
