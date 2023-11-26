package com.unison.ratemaster.Repository;

import com.unison.ratemaster.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    @Query("select b from Booking b order by b.enteredOn desc")
    List<Booking> getLatestBookings();
}
