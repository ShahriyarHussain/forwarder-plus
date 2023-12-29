package com.unison.ratemaster.Service;

import com.unison.ratemaster.Entity.Commodity;
import com.unison.ratemaster.Repository.CommodityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommodityService {

    private final CommodityRepository commodityRepository;

    public List<Commodity> getAllCommodity() {
        return commodityRepository.findAll();
    }

    public void saveCommodity(Commodity commodity) {
        commodityRepository.save(commodity);
    }

    public void deleteCommodity(Commodity commodity) {
        commodityRepository.delete(commodity);
    }
}
