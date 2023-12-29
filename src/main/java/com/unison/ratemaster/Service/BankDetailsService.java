package com.unison.ratemaster.Service;

import com.unison.ratemaster.Entity.BankDetails;
import com.unison.ratemaster.Repository.BankDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankDetailsService {
    private final BankDetailsRepository bankDetailsRepository;

    public List<BankDetails> getAllBankDetails() {
        return bankDetailsRepository.findAll();
    }

    public void saveBankDetails(BankDetails bankDetails) {
        bankDetailsRepository.save(bankDetails);
    }

    public void deleteBankDetails(BankDetails bankDetails) {
        bankDetailsRepository.delete(bankDetails);
    }
}
