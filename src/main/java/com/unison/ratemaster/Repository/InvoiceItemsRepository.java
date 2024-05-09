package com.unison.ratemaster.Repository;

import com.unison.ratemaster.Entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceItemsRepository extends JpaRepository<InvoiceItem, Long> {

}
