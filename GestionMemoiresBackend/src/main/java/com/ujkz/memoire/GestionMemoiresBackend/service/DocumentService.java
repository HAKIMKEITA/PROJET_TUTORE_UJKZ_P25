package com.ujkz.memoire.GestionMemoiresBackend.service;

import com.ujkz.memoire.GestionMemoiresBackend.dto.DocumentDTO;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Document;
import com.ujkz.memoire.GestionMemoiresBackend.enums.DocumentType;

import java.util.List;

public interface DocumentService {
    List<DocumentDTO> getDocumentsByMemoire(Long memoireId);
    List<DocumentDTO> getDocumentsByType(Long memoireId, DocumentType type);
    DocumentDTO uploadDocument(DocumentDTO documentDTO, String userEmail);
    DocumentDTO validateDocument(Long documentId, String userEmail);
    void deleteDocument(Long documentId, String userEmail);
    Document getDocumentEntity(Long id);
}