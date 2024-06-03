package com.unison.ratemaster.Service;

import com.unison.ratemaster.Entity.Schedule;
import com.unison.ratemaster.Repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public Schedule saveSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getValidSchedules() {
        return scheduleRepository.getScheduleByDate(LocalDate.now().minusDays(3));
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.getAllSchedules();
    }
}
