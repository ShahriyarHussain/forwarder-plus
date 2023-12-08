package com.unison.ratemaster.Repository;

import com.unison.ratemaster.Entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {

    @Query(value = "SELECT nextval('INVOICE_NO_SEQ')", nativeQuery = true)
    String getInvoiceNoSequence();
}
