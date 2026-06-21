package com.ujkz.memoire.GestionMemoiresBackend.controller;

import com.ujkz.memoire.GestionMemoiresBackend.dto.DocumentDTO;
import com.ujkz.memoire.GestionMemoiresBackend.dto.MessageResponse;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Document;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.entity.User;
import com.ujkz.memoire.GestionMemoiresBackend.enums.DocumentType;
import com.ujkz.memoire.GestionMemoiresBackend.repository.DocumentRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.MemoireRepository;
import com.ujkz.memoire.GestionMemoiresBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private MemoireRepository memoireRepository;

    @Autowired
    private UserRepository userRepository;

    // GET - Récupérer tous les documents d'un mémoire
    @GetMapping("/memoire/{memoireId}")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByMemoire(@PathVariable Long memoireId) {
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new RuntimeException("Mémoire non trouvé"));

        List<Document> documents = documentRepository.findByMemoire(memoire);
        List<DocumentDTO> dtos = documents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET - Récupérer les documents par type
    @GetMapping("/memoire/{memoireId}/type/{type}")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByType(@PathVariable Long memoireId,
                                                                 @PathVariable DocumentType type) {
        Memoire memoire = memoireRepository.findById(memoireId)
                .orElseThrow(() -> new RuntimeException("Mémoire non trouvé"));

        List<Document> documents = documentRepository.findByMemoireAndType(memoire, type);
        List<DocumentDTO> dtos = documents.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // POST - Déposer un document
    @PostMapping
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<?> uploadDocument(@RequestBody DocumentDTO documentDTO) {
        Memoire memoire = memoireRepository.findById(documentDTO.getMemoireId())
                .orElseThrow(() -> new RuntimeException("Mémoire non trouvé"));

        // Vérifier si l'utilisateur a le droit de déposer un document
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Un étudiant ne peut déposer que pour son propre mémoire
        if (currentUser.getRole().name().equals("ETUDIANT")) {
            if (!memoire.getStudent().getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(new MessageResponse("Vous n'avez pas le droit de déposer un document pour ce mémoire"));
            }
        }

        Document document = new Document();
        document.setMemoire(memoire);
        document.setType(documentDTO.getType());
        document.setNomFichier(documentDTO.getNomFichier());
        document.setCheminFichier(documentDTO.getCheminFichier());
        document.setDescription(documentDTO.getDescription());
        document.setValideParEncadrant(false);

        // Incrémenter la version si c'est une mise à jour
        if (documentDTO.getVersion() != null) {
            document.setVersion(documentDTO.getVersion());
        }

        documentRepository.save(document);

        return ResponseEntity.ok(new MessageResponse("Document déposé avec succès"));
    }

    // PATCH - Valider un document par l'encadrant
    @PatchMapping("/{documentId}/validate")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<?> validateDocument(@PathVariable Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        document.setValideParEncadrant(true);
        document.setDateValidation(LocalDateTime.now());

        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        document.setValidePar(currentUser);

        documentRepository.save(document);

        return ResponseEntity.ok(new MessageResponse("Document validé avec succès"));
    }

    // DELETE - Supprimer un document
    @DeleteMapping("/{documentId}")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ADMINISTRATEUR')")
    public ResponseEntity<?> deleteDocument(@PathVariable Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        // Vérifier les droits
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Seul l'étudiant propriétaire, l'encadrant ou l'admin peuvent supprimer
        boolean isOwner = document.getMemoire().getStudent().getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMINISTRATEUR");
        boolean isTeacher = currentUser.getRole().name().equals("ENSEIGNANT");

        if (!isOwner && !isAdmin && !isTeacher) {
            return ResponseEntity.status(403).body(new MessageResponse("Vous n'avez pas le droit de supprimer ce document"));
        }

        documentRepository.delete(document);
        return ResponseEntity.ok(new MessageResponse("Document supprimé avec succès"));
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