package com.unison.ratemaster.Service;

import com.unison.ratemaster.Entity.ContactDetails;
import com.unison.ratemaster.Repository.ContactDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactDetailsService {
    private final ContactDetailsRepository contactDetailsRepository;

    public List<ContactDetails> getAllContactDetails() {
        return contactDetailsRepository.findAll();
    }

    public void saveContactDetails(ContactDetails contactDetails) {
        contactDetailsRepository.save(contactDetails);
    }

    public void deleteContact(ContactDetails contactDetails) {
        contactDetailsRepository.delete(contactDetails);
    }
}
