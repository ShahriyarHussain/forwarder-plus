package com.unison.ratemaster.Entity;


import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public class FreightContainerId implements Serializable {

    private String bookingNo;
    private String containerNo;
    private String sealNo;
}
