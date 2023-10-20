package com.unison.ratemaster.Entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "PORT")
@Getter
@Setter
public class Port {
    @Id
    private String portShortCode;
    @NotEmpty
    @Length(max = 100)
    private String portName;
    @NotEmpty
    @Length(max = 100)
    private String portCountry;
    private String portCity;

    public String getPortLabel() {
        return getPortShortCode() + " - " + getPortName() + ", " + getPortCountry();
    }
}
