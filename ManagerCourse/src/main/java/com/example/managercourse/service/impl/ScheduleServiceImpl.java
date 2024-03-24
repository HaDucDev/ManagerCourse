package com.example.managercourse.service.impl;

import com.example.managercourse.dto.request.ScheduleRequest;
import com.example.managercourse.dto.request.ScheduleUpdateRequest;
import com.example.managercourse.dto.response.MessageResponse;
import com.example.managercourse.dto.response.ScheduleDetailResponse;
import com.example.managercourse.dto.response.ScheduleResponse;
import com.example.managercourse.entity.Class;
import com.example.managercourse.entity.ClassRoom;
import com.example.managercourse.entity.Schedule;
import com.example.managercourse.entity.Subject;
import com.example.managercourse.repository.ClassRepository;
import com.example.managercourse.repository.ClassRoomRepository;
import com.example.managercourse.repository.ScheduleRepository;
import com.example.managercourse.repository.SubjectRepository;
import com.example.managercourse.service.ScheduleService;
import jakarta.persistence.EntityNotFoundException;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ClassRoomRepository classRoomRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public List<ScheduleResponse> findAllSchedule(Integer pageNumber, Integer pageSize, Integer id, String subjectName) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ScheduleResponse> scheduleResponses = scheduleRepository.findAllSchedule(id, subjectName, pageable);
        return scheduleResponses.getContent();
    }

    @Override
    @Transactional
    public MessageResponse createSchedule(ScheduleRequest scheduleRequest) {
        Class aClass = classRepository.findById(scheduleRequest.getIdClass())
                .orElseThrow(() -> new EntityNotFoundException("Class not found"));
        Subject subject = subjectRepository.findById(scheduleRequest.getIdSubject())
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));
        ClassRoom classRoom = classRoomRepository.findById(scheduleRequest.getIdClassRoom())
                .orElseThrow(() -> new EntityNotFoundException("ClassRoom not found"));

        LocalDate startDate = subject.getStudyTimeStart();
        LocalDate endDate = subject.getStudyTimeEnd();
        int selectedValue = scheduleRequest.getDay();

        List<LocalDate> studyDays = getStudyDays(selectedValue, startDate, endDate);
        List<Schedule> schedules = new ArrayList<>();
        for (LocalDate localDate: studyDays) {
            Schedule lessonSchedule = createScheduleForDay(aClass, subject, classRoom, scheduleRequest);
            lessonSchedule.setDateLearn(localDate);
            schedules.add(lessonSchedule);
        }

        scheduleRepository.saveAll(schedules);

        return MessageResponse.builder().message("Thêm thành công").build();
    }

    @Override
    public ScheduleDetailResponse detail(Integer id) {
        return scheduleRepository.detail(id);
    }

    @Override
    @Transactional
    public MessageResponse updateSchedule(Integer id, ScheduleUpdateRequest scheduleUpdateRequest) {
        Optional<Schedule> scheduleOptional = scheduleRepository.findById(id);
        ClassRoom classRoom = classRoomRepository.findById(scheduleUpdateRequest.getIdClassRoom()).orElseThrow(() -> new EntityNotFoundException("ClassRoom not found"));
        scheduleOptional.ifPresent(schedule -> {
            schedule.setStartDate(scheduleUpdateRequest.getTimeStart());
            schedule.setEndDate(scheduleUpdateRequest.getTimeEnd());
            schedule.setClassRoom(classRoom);
            scheduleRepository.save(schedule);
        });
        return MessageResponse.builder().message("Update thành công").build();
    }

    @Override
    public List<ScheduleResponse> findAllScheduleByCourse(Integer pageNumber, Integer pageSize, Integer id, String username) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ScheduleResponse> scheduleResponses = scheduleRepository.findAllScheduleByCourse(id, pageable, username);
        return scheduleResponses.getContent();
    }

    private Schedule createScheduleForDay(Class aClass, Subject subject, ClassRoom classRoom, ScheduleRequest scheduleRequest) {
        return Schedule.builder().aClass(aClass).subject(subject).classRoom(classRoom).day(scheduleRequest.getDay()).endDate(scheduleRequest.getTimeEnd()).startDate(scheduleRequest.getTimeStart()).status(1).build();
    }

    public static List<LocalDate> getStudyDays(int selectedValue, LocalDate startDate, LocalDate endDate) {
        List<LocalDate> studyDays = new ArrayList<>();
        LocalDate currentDate = startDate;

        // Nếu ngày bắt đầu là một ngày học được chọn, chuyển sang ngày học tiếp theo
        if (selectedValue == 1 && (currentDate.getDayOfWeek() == DayOfWeek.MONDAY || currentDate.getDayOfWeek() == DayOfWeek.WEDNESDAY || currentDate.getDayOfWeek() == DayOfWeek.FRIDAY)) {
            currentDate = currentDate.plusDays(1);
        } else if (selectedValue == 2 && (currentDate.getDayOfWeek() == DayOfWeek.TUESDAY || currentDate.getDayOfWeek() == DayOfWeek.THURSDAY || currentDate.getDayOfWeek() == DayOfWeek.SATURDAY)) {
            currentDate = currentDate.plusDays(1);
        }

        // Kiểm tra từng ngày từ ngày bắt đầu đến ngày kết thúc
        while (!currentDate.isAfter(endDate)) {
            // Kiểm tra ngày thứ và selectedValue
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            boolean shouldAdd = false;
            if (selectedValue == 1) {
                shouldAdd = (dayOfWeek == DayOfWeek.MONDAY || dayOfWeek == DayOfWeek.WEDNESDAY || dayOfWeek == DayOfWeek.FRIDAY);
            } else if (selectedValue == 2) {
                shouldAdd = (dayOfWeek == DayOfWeek.TUESDAY || dayOfWeek == DayOfWeek.THURSDAY || dayOfWeek == DayOfWeek.SATURDAY);
            }

            if (shouldAdd) {
                studyDays.add(currentDate);
            }

            // Di chuyển tới ngày tiếp theo
            currentDate = currentDate.plusDays(1);
        }

        return studyDays;
    }


}
