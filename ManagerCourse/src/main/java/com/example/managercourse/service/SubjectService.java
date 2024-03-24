package com.example.managercourse.service;

import com.example.managercourse.dto.request.SubjectCreateRequest;
import com.example.managercourse.dto.request.UpdateSubjectRequest;
import com.example.managercourse.dto.response.MessageResponse;
import com.example.managercourse.dto.response.SubjectResponse;
import com.example.managercourse.dto.response.SubjectUpdateResponse;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubjectService {

    List<SubjectResponse> getListSubjectName(Integer pageNumber, Integer pageSize);

    List<SubjectResponse> getListSubjectByCourse(Integer pageNumber, Integer pageSize, Integer id);

    MessageResponse createSubject(SubjectCreateRequest subjectCreateRequest);

    SubjectUpdateResponse subjectDetail(Integer id);

    MessageResponse updateSubject(Integer id, UpdateSubjectRequest updateSubjectRequest);

    List<SubjectResponse> searchListSubject(Integer pageNumber, Integer pageSize, String subjectName);

    List<SubjectResponse> getListSubjectByCourse(String courseName);

    List<SubjectResponse> getListSubjectByClass(Integer id, Integer pageNumber, Integer pageSize);

    List<SubjectResponse> getListSubjectName(Integer id);

    List<SubjectResponse> getListSubjectAddCourse(Integer id);

}
