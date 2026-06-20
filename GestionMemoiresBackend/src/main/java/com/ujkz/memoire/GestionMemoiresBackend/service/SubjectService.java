package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.SubjectDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Subject;
import com.ujkz.memoire.GestionMemoiresBackend.enums.SubjectStatus;

import java.util.List;

public interface SubjectService {
    List<SubjectDTO> getAllPublishedSubjects();
    SubjectDTO getSubjectById(Long id);
    List<SubjectDTO> getSubjectsByTeacher(Long teacherId);
    Subject getSubjectEntity(Long id);
    SubjectDTO createSubject(SubjectDTO subjectDTO);
    SubjectDTO updateSubject(Long id, SubjectDTO subjectDTO);
    void deleteSubject(Long id);
    SubjectDTO changeSubjectStatus(Long id, SubjectStatus status);
    SubjectDTO togglePublication(Long id);
}