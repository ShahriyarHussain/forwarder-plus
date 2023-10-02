package com.unison.ratemaster.Repository;

import com.unison.ratemaster.Entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {

    List<Rate> findAllByValidityGreaterThan(LocalDate today);
}
