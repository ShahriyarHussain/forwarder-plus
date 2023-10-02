package com.unison.ratemaster.Service;

import com.unison.ratemaster.Entity.Rate;
import com.unison.ratemaster.Repository.RateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RateService {

    private final RateRepository rateRepository;

    public void saveRate(Rate rate) {
        rateRepository.save(rate);
    }

    public List<Rate> getValidRates() {
        return rateRepository.findAllByValidityGreaterThan(LocalDate.now());
    }
}
