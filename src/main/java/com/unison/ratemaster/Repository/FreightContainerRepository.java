package com.unison.ratemaster.Repository;

import com.unison.ratemaster.Entity.FreightContainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreightContainerRepository extends JpaRepository<FreightContainer, Long> {
}
