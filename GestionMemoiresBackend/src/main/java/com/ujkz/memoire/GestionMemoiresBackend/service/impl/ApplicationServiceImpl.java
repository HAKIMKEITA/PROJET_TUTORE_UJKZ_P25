package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.ApplicationDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.ApplicationRequest;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Application;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Student;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Subject;
import com.ujkz.memoire.GestionMemoiresBackend.enums.ApplicationStatus;
import com.ujkz.memoire.GestionMemoiresBackend.enums.SubjectStatus;
import com.ujkz.memoire.GestionMemoiresBackend.exception.BusinessException;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.ApplicationRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.StudentRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.SubjectRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public Application applyToSubject(ApplicationRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Sujet non trouvé"));

        if (!subject.isPublie() || subject.getStatut() != SubjectStatus.OUVERT) {
            throw new BusinessException("Ce sujet n'est pas disponible");
        }

        boolean hasPendingOrAccepted = applicationRepository.existsByStudentAndStatut(student, ApplicationStatus.PENDING) ||
                                       applicationRepository.existsByStudentAndStatut(student, ApplicationStatus.ACCEPTED);
        if (hasPendingOrAccepted) {
            throw new BusinessException("Vous avez déjà une candidature en attente ou acceptée");
        }

        long candidaturesActuelles = applicationRepository.countBySubjectAndStatut(subject, ApplicationStatus.ACCEPTED);
        if (candidaturesActuelles >= subject.getCapaciteMax()) {
            throw new BusinessException("Ce sujet a atteint sa capacité maximale");
        }

        Application application = new Application();
        application.setStudent(student);
        application.setSubject(subject);
        application.setMotivation(request.getMotivation());
        application.setStatut(ApplicationStatus.PENDING);

        return applicationRepository.save(application);
    }

    @Override
    public List<ApplicationDTO> getApplicationsByStudent(Long studentId) {
        return applicationRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO> getApplicationsBySubject(Long subjectId) {
        return applicationRepository.findBySubjectId(subjectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationDTO acceptApplication(Long applicationId, String commentaire) {
        Application application = getApplicationEntity(applicationId);

        if (application.getStatut() != ApplicationStatus.PENDING) {
            throw new BusinessException("Cette candidature n'est plus en attente");
        }

        Subject subject = application.getSubject();
        long nbAcceptes = applicationRepository.countBySubjectAndStatut(subject, ApplicationStatus.ACCEPTED);
        if (nbAcceptes >= subject.getCapaciteMax()) {
            throw new BusinessException("Ce sujet a atteint sa capacité maximale");
        }

        application.setStatut(ApplicationStatus.ACCEPTED);
        application.setCommentaireEncadrant(commentaire);
        Application saved = applicationRepository.save(application);

        if (nbAcceptes + 1 >= subject.getCapaciteMax()) {
            subject.setStatut(SubjectStatus.FERME);
            subjectRepository.save(subject);
        }

        return convertToDTO(saved);
    }

    @Override
    public ApplicationDTO rejectApplication(Long applicationId, String commentaire) {
        Application application = getApplicationEntity(applicationId);

        if (application.getStatut() != ApplicationStatus.PENDING) {
            throw new BusinessException("Cette candidature n'est plus en attente");
        }

        application.setStatut(ApplicationStatus.REJECTED);
        application.setCommentaireEncadrant(commentaire);
        Application saved = applicationRepository.save(application);

        return convertToDTO(saved);
    }

    @Override
    public Application getApplicationEntity(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature non trouvée avec l'ID: " + id));
    }

    @Override
    public List<ApplicationDTO> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ApplicationDTO convertToDTO(Application application) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(application.getId());
        dto.setStudentId(application.getStudent().getId());
        dto.setStudentNom(application.getStudent().getUser().getNom());
        dto.setStudentPrenom(application.getStudent().getUser().getPrenom());
        dto.setStudentMatricule(application.getStudent().getMatricule());
        dto.setSubjectId(application.getSubject().getId());
        dto.setSubjectTitre(application.getSubject().getTitre());
        dto.setMotivation(application.getMotivation());
        dto.setStatut(application.getStatut());
        dto.setCommentaireEncadrant(application.getCommentaireEncadrant());
        dto.setDateCandidature(application.getDateCandidature());
        return dto;
    }
}