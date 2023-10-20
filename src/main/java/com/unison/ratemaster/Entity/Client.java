package com.unison.ratemaster.Entity;

import com.unison.ratemaster.Enum.ClientType;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Length(max = 100)
    private String name;
    @Length(max = 500)
    private String address1;
    @Length(max = 500)
    private String address2;
    private ClientType type;
}
