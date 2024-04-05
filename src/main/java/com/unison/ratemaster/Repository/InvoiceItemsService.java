package com.unison.ratemaster.Repository;

import com.unison.ratemaster.Entity.FreightDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class InvoiceItemsService {
    private final InvoiceItemsRepository invoiceItemsRepository;

    public void saveInvoiceItems(Set<FreightDetails> freightDetails) {
        invoiceItemsRepository.saveAll(freightDetails);
    }
}
