package com.unison.ratemaster.Entity;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "COMMODITY")
public class Commodity {
    @Id
    private String hscode;
    @NotEmpty
    @Length(max = 100)
    private String name;
    @Length(max = 300)
    private String description;
    private boolean isDangerousGoods;
}
