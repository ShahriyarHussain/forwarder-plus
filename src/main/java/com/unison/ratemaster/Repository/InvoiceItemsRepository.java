package com.unison.ratemaster.Repository;

import com.unison.ratemaster.Entity.FreightDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceItemsRepository extends JpaRepository<FreightDetails, Long> {

}
