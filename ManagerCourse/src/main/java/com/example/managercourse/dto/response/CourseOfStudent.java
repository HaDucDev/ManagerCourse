package com.example.managercourse.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseOfStudent {

    private String subjectCode;

    private String subjectName;

    private String teacherName;

    private Integer learningMode;

    private Integer status;

    private Integer numberOfSessions;
}
