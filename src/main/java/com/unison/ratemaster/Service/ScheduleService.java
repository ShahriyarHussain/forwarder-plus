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

    public void saveSchedule(Schedule schedule) {
        scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllSchedule() {
        return scheduleRepository.getValidSchedules(LocalDate.now());
    }
}
