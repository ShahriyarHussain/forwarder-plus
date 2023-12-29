package com.unison.ratemaster.Service;

import com.unison.ratemaster.Entity.Carrier;
import com.unison.ratemaster.Repository.CarrierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarrierService {

    private final CarrierRepository carrierRepository;

    public List<Carrier> getAllCarriers() {
        return carrierRepository.findAll();
    }

    public void saveCarrier(Carrier carrier) {
        carrierRepository.save(carrier);
    }

    public void deleteCarrier(Carrier carrier) {
        carrierRepository.delete(carrier);
    }
}
