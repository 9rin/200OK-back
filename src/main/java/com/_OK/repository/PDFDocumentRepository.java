package com._OK.repository;


import com._OK.domain.PDFDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PDFDocumentRepository extends JpaRepository<PDFDocument, Long> {
}
