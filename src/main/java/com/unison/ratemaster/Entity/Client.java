package com.unison.ratemaster.Entity;

import com.unison.ratemaster.Enum.ClientType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter
@Setter
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    @Length(max = 100)
    private String name;
    @Length(max = 500)
    private String address;
    @Length(max = 50)
    private String email;
    @Length(max = 20)
    private String postCode;
    @Length(max = 20)
    private String taxId;
    @Length(max = 500)
    private String city;
    @Length(max = 500)
    private String country;
    private ClientType type;
}
