package com.unison.ratemaster.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "CARRIER")
@Getter
@Setter
public class Carrier {
    @Id
    private String name;
    @NotEmpty
    private String Country;
}
