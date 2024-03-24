package com.example.managercourse.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleRequest {

    @NotNull(message = "Class is not required")
    private Integer idClass;

    @NotNull(message = "Subject is not required")
    private Integer idSubject;

    @NotNull(message = "Class room is not required")
    private Integer idClassRoom;

    @NotNull(message = "Day is not required")
    private Integer day;

    @NotNull(message = "Start time is required")
    private LocalTime timeStart;

    @NotNull(message = "End time is required")
    private LocalTime timeEnd;
}
