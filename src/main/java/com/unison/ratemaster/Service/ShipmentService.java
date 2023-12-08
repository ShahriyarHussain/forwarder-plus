package com.unison.ratemaster.Service;

import com.unison.ratemaster.Entity.Booking;
import com.unison.ratemaster.Entity.Shipment;
import com.unison.ratemaster.Repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final BookingService bookingService;

    @Transactional
    public void createNewShipment(Shipment shipment, Booking booking) {
        Booking savedBooking = bookingService.saveBooking(booking);
        shipment.setBooking(savedBooking);
        shipmentRepository.save(shipment);
    }


    @Transactional
    public void saveEditedShipment(Shipment shipment) {
        shipmentRepository.save(shipment);
    }


    @Transactional
    public void deleteShipmentAndBooking(Shipment shipment) {
        shipmentRepository.delete(shipment);
    }


    @Transactional
    public List<Shipment> getAllShipments() {
        return shipmentRepository.getAllShipmentOrderedByCreateDate();
    }


    @Transactional
    public byte[] getPdf(String blNo) {
        return shipmentRepository.getPdf(blNo);
    }

    public String getInvoiceNo() {
        return "USL-EXP-" + shipmentRepository.getInvoiceNoSequence() + "-" + LocalDate.now().getYear();
    };
}
