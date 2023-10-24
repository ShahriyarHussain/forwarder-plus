package com.unison.ratemaster.Repository;

import com.unison.ratemaster.Entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("select s from Schedule s where s.portCutOff > :cutOffDate")
    List<Schedule> getValidSchedules(@Param("cutOffDate") LocalDate cutOffDate);
}
