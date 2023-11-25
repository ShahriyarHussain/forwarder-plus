package com.unison.ratemaster.Entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FreightContainerId implements Serializable {

    private String bookingNo;
    private String containerNo;
    private String sealNo;
}
