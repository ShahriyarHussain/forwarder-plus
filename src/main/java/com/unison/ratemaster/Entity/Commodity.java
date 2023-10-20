package com.unison.ratemaster.Entity;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "COMMODITY")
public class Commodity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String hscode;
    @NotEmpty
    @Length(max = 300)
    private String name;
    @Length(max = 500)
    private String description;
    private boolean isDangerousGoods;

    public String getCommoditySummary() {
        StringBuilder summary = new StringBuilder();
        if (hscode != null && !hscode.isEmpty()) {
            summary.append(hscode).append(" - ");
        }
        summary.append(name);
        if (isDangerousGoods) {
            summary.append(" (Hazardous)");
        }
        return summary.toString();
    }
}
