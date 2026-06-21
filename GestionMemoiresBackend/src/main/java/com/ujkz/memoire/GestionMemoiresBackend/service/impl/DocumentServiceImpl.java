package com.ujkz.memoire.GestionMemoiresBackend.service.impl;

import com.ujkz.memoire.GestionMemoiresBackend.dto.DocumentDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Document;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.enums.DocumentType;
import com.ujkz.memoire.GestionMemoiresBackend.exception.BusinessException;
import com.ujkz.memoire.GestionMemoiresBackend.exception.ResourceNotFoundException;
import com.ujkz.memoire.GestionMemoiresBackend.repository.DocumentRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.MemoireRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.UserRepository;
import com.ujkz.memoire.GestionMemoiresBackend.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private MemoireRepository memoireRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<DocumentDTO> getDocumentsByMemoire(Long memoireId) {
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new ResourceNotFoundException("Mémoire non trouvé"));
        
        return documentRepository.findByMemoire(memoire).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentDTO> getDocumentsByType(Long memoireId, DocumentType type) {
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new ResourceNotFoundException("Mémoire non trouvé"));
        
        return documentRepository.findByMemoireAndType(memoire, type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentDTO uploadDocument(DocumentDTO documentDTO, String userEmail) {
        Memoire memoire = memoireRepository.findById(documentDTO.getMemoireId())
                .orElseThrow(() -> new ResourceNotFoundException("Mémoire non trouvé"));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Vérifier les droits
        boolean isStudent = memoire.getStudent().getUser().getId().equals(currentUser.getId());
        boolean isTeacher = memoire.getSubject().getEncadrant().getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMINISTRATEUR");

        if (!isStudent && !isTeacher && !isAdmin) {
            throw new BusinessException("Vous n'avez pas le droit de déposer un document pour ce mémoire");
        }

        Document document = new Document();
        document.setMemoire(memoire);
        document.setType(documentDTO.getType());
        document.setNomFichier(documentDTO.getNomFichier());
        document.setCheminFichier(documentDTO.getCheminFichier());
        document.setDescription(documentDTO.getDescription());
        document.setValideParEncadrant(false);
        document.setVersion(documentDTO.getVersion() != null ? documentDTO.getVersion() : 1);

        Document saved = documentRepository.save(document);
        return convertToDTO(saved);
    }

    @Override
    public DocumentDTO validateDocument(Long documentId, String userEmail) {
        Document document = getDocumentEntity(documentId);
        
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        boolean isTeacher = document.getMemoire().getSubject().getEncadrant().getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMINISTRATEUR");

        if (!isTeacher && !isAdmin) {
            throw new BusinessException("Seul l'encadrant peut valider un document");
        }

        document.setValideParEncadrant(true);
        document.setDateValidation(LocalDateTime.now());
        document.setValidePar(currentUser);

        Document saved = documentRepository.save(document);
        return convertToDTO(saved);
    }

    @Override
    public void deleteDocument(Long documentId, String userEmail) {
        Document document = getDocumentEntity(documentId);
        
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        boolean isOwner = document.getMemoire().getStudent().getUser().getId().equals(currentUser.getId());
        boolean isTeacher = document.getMemoire().getSubject().getEncadrant().getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMINISTRATEUR");

        if (!isOwner && !isTeacher && !isAdmin) {
            throw new BusinessException("Vous n'avez pas le droit de supprimer ce document");
        }

        documentRepository.delete(document);
    }

    @Override
    public Document getDocumentEntity(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document non trouvé avec l'ID: " + id));
    }

    private DocumentDTO convertToDTO(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setMemoireId(document.getMemoire().getId());
        dto.setType(document.getType());
        dto.setTypeLibelle(document.getType().getLibelle());
        dto.setNomFichier(document.getNomFichier());
        dto.setCheminFichier(document.getCheminFichier());
        dto.setDateDepot(document.getDateDepot());
        dto.setVersion(document.getVersion());
        dto.setDescription(document.getDescription());
        dto.setValideParEncadrant(document.getValideParEncadrant());
        dto.setDateValidation(document.getDateValidation());
        if (document.getValidePar() != null) {
            dto.setValidePar(document.getValidePar().getPrenom() + " " + document.getValidePar().getNom());
        }
        return dto;
    }
}