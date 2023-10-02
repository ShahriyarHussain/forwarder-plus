package com.unison.ratemaster.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "PORT")
@Getter
@Setter
public class Port {
    @Id
    private String portShortCode;
    private String portName;
    private String portCountry;
    private String portCity;
}
