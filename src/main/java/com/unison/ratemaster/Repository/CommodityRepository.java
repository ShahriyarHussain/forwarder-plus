package com.unison.ratemaster.Repository;

import com.unison.ratemaster.Entity.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommodityRepository extends JpaRepository<Commodity, String> {
}
