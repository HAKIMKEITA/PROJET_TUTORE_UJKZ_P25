package com.ujkz.memoire.GestionMemoiresBackend.repository;

import com.ujkz.memoire.GestionMemoiresBackend.entity.Document;
import com.ujkz.memoire.GestionMemoiresBackend.entity.Memoire;
import com.ujkz.memoire.GestionMemoiresBackend.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByMemoire(Memoire memoire);
    List<Document> findByMemoireAndType(Memoire memoire, DocumentType type);
    List<Document> findByMemoireAndValideParEncadrantFalse(Memoire memoire);
}