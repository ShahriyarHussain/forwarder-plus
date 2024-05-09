package com.unison.ratemaster.Repository;

import com.unison.ratemaster.Entity.InvoiceItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class InvoiceItemsService {
    private final InvoiceItemsRepository invoiceItemsRepository;

    public void saveInvoiceItems(Set<InvoiceItem> invoiceItems) {
        invoiceItemsRepository.saveAll(invoiceItems);
    }
}
