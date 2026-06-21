package com.ujkz.memoire.GestionMemoiresBackend.controller;

import com.ujkz.memoire.GestionMemoiresBackend.dto.SubjectDTO;
import com.ujkz.memoire.GestionMemoiresBackend.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
@CrossOrigin(origins = "*")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllPublishedSubjects() {
        return ResponseEntity.ok(subjectService.getAllPublishedSubjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(subjectService.getSubjectsByTeacher(teacherId));
    }
}