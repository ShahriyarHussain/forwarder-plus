package com.unison.ratemaster.Service;

import com.unison.ratemaster.Entity.Booking;
import com.unison.ratemaster.Repository.BookingRepository;
import com.unison.ratemaster.Repository.FreightContainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FreightContainerRepository freightContainerRepository;

    @Transactional
    public Booking saveBooking(Booking booking) {
        freightContainerRepository.saveAll(booking.getContainer());
        return bookingRepository.save(booking);
    }

    public List<Booking> getLatestBooking() {
        return bookingRepository.getLatestBookings();
    }
}
