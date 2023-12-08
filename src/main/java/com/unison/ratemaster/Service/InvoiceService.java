package com.unison.ratemaster.Service;

import com.unison.ratemaster.Entity.Invoice;
import com.unison.ratemaster.Repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public String getInvoiceNo() {
        return "USL-EXP-" + invoiceRepository.getInvoiceNoSequence() + "-" + LocalDate.now().getYear();
    };

    public Invoice saveInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }
}
